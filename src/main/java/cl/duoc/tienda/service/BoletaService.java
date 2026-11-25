package cl.duoc.tienda.service;

import cl.duoc.tienda.dto.*;
import cl.duoc.tienda.exception.*;
import cl.duoc.tienda.model.*;
import cl.duoc.tienda.repository.BoletaRepository;
import cl.duoc.tienda.repository.ProductoRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoletaService {
  private final BoletaRepository boletas;
  private final ProductoRepository productos;
  private final PagoService pagos;

  public BoletaService(BoletaRepository boletas, ProductoRepository productos, PagoService pagos) {
    this.boletas = boletas;
    this.productos = productos;
    this.pagos = pagos;
  }

  
  @Transactional
  public BoletaResponse crear(BoletaRequest request, String correoUsuario) {
    Map<String, Integer> cantidades = new LinkedHashMap<>();
    for (BoletaRequest.Item item : request.items()) {
      cantidades.merge(item.codigo().trim().toUpperCase(), item.cantidad(), Integer::sum);
    }

    Map<String, Producto> encontrados = productos.bloquearPorCodigos(cantidades.keySet()).stream()
      .collect(Collectors.toMap(Producto::getCodigo, Function.identity()));

    Boleta boleta = new Boleta();
    boleta.setFecha(LocalDateTime.now());
    boleta.setUsuarioCorreo(correoUsuario.toLowerCase());
    boleta.setClienteNombre(request.cliente().nombre().trim());
    boleta.setClienteApellidos(request.cliente().apellidos().trim());
    boleta.setEnvioCalle(request.envio().calle().trim());
    boleta.setEnvioDepartamento(CategoriaService.texto(request.envio().departamento()));
    boleta.setEnvioRegion(request.envio().region());
    boleta.setEnvioComuna(request.envio().comuna());
    boleta.setEnvioIndicaciones(CategoriaService.texto(request.envio().indicaciones()));
    boleta.setPagoMedio("Tarjeta");
    String digitos = request.numeroTarjeta().replace(" ", "");
    boleta.setPagoUltimosDigitos(digitos.substring(Math.max(0, digitos.length() - 4)));

    int total = 0;
    String motivoStock = null;
    for (Map.Entry<String, Integer> entrada : cantidades.entrySet()) {
      Producto producto = encontrados.get(entrada.getKey());
      if (producto == null) {
        throw new RecursoNoEncontradoException("Producto no encontrado: " + entrada.getKey());
      }
      int cantidad = entrada.getValue();
      if (motivoStock == null && producto.getStock() < cantidad) {
        motivoStock = "Stock insuficiente para " + producto.getNombre() + ".";
      }
      DetalleBoleta detalle = new DetalleBoleta();
      detalle.setProducto(producto);
      detalle.setNombreProducto(producto.getNombre());
      detalle.setPrecioUnitario(producto.precioVigente());
      detalle.setCantidad(cantidad);
      detalle.setSubtotal(producto.precioVigente() * cantidad);
      boleta.agregarDetalle(detalle);
      total += detalle.getSubtotal();
    }
    boleta.setTotal(total);

    if (motivoStock != null) {
      rechazar(boleta, motivoStock);
    } else {
      PagoService.ResultadoPago pago = pagos.cobrar(request.numeroTarjeta(), total);
      if (pago.aprobado()) {
        for (DetalleBoleta d : boleta.getDetalles()) {
          d.getProducto().setStock(d.getProducto().getStock() - d.getCantidad());
        }
        boleta.setEstado(EstadoBoleta.PAGADA);
        boleta.setPagoCodigoAutorizacion(pago.codigoAutorizacion());
      } else {
        rechazar(boleta, pago.motivo());
      }
    }
    return BoletaResponse.desde(boletas.save(boleta));
  }

  private void rechazar(Boleta boleta, String motivo) {
    boleta.setEstado(EstadoBoleta.RECHAZADA);
    boleta.setPagoMotivo(motivo);
  }

  
  @Transactional(readOnly = true)
  public List<BoletaResponse> listar(String correo) {
    List<Boleta> lista = (correo == null || correo.isBlank())
      ? boletas.findAllByOrderByNumeroDesc()
      : boletas.findByUsuarioCorreoIgnoreCaseOrderByNumeroDesc(correo.trim());
    return lista.stream().map(BoletaResponse::desde).toList();
  }

  @Transactional(readOnly = true)
  public List<BoletaResponse> listarPropias(String correo) {
    return listar(correo);
  }

  
  @Transactional(readOnly = true)
  public BoletaResponse buscar(Long numero, Authentication auth) {
    Boleta boleta = buscarEntidad(numero);
    boolean esPersonal = tieneRol(auth, "ROLE_ADMIN") || tieneRol(auth, "ROLE_VENDEDOR");
    if (!esPersonal && !boleta.getUsuarioCorreo().equalsIgnoreCase(auth.getName())) {
      throw new AccesoDenegadoException("No puedes ver boletas de otros clientes");
    }
    return BoletaResponse.desde(boleta);
  }

  
  @Transactional
  public BoletaResponse cambiarEstado(Long numero, String nuevoEstado) {
    Boleta boleta = buscarEntidad(numero);
    if (!EstadoBoleta.TODOS.contains(nuevoEstado)) {
      throw new ReglaNegocioException("Estado no válido: " + nuevoEstado);
    }
    if (!EstadoBoleta.puedeCambiar(boleta.getEstado(), nuevoEstado)) {
      throw new ReglaNegocioException("No se puede pasar de " + boleta.getEstado() + " a " + nuevoEstado);
    }
    if (EstadoBoleta.ANULADA.equals(nuevoEstado) && EstadoBoleta.esVenta(boleta.getEstado())) {
      for (DetalleBoleta d : boleta.getDetalles()) {
        d.getProducto().setStock(d.getProducto().getStock() + d.getCantidad());
      }
    }
    boleta.setEstado(nuevoEstado);
    return BoletaResponse.desde(boleta);
  }

  
  @Transactional
  public void eliminar(Long numero) {
    Boleta boleta = buscarEntidad(numero);
    if (EstadoBoleta.esVenta(boleta.getEstado())) {
      throw new ReglaNegocioException("Solo se pueden eliminar boletas rechazadas o anuladas. "
        + "Primero anula la venta.");
    }
    boletas.delete(boleta);
  }

  private Boleta buscarEntidad(Long numero) {
    return boletas.findById(numero)
      .orElseThrow(() -> new RecursoNoEncontradoException("Boleta no encontrada: " + numero));
  }

  private static boolean tieneRol(Authentication auth, String rol) {
    return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(rol));
  }
}