package cl.duoc.tienda.dto;

import cl.duoc.tienda.model.Boleta;
import java.time.LocalDateTime;
import java.util.List;


public record BoletaResponse(
  Long numero,
  LocalDateTime fecha,
  String estado,
  Cliente cliente,
  Envio envio,
  List<Item> items,
  Integer total,
  Pago pago
) {
  public record Cliente(String nombre, String apellidos, String correo) {}
  public record Envio(String calle, String departamento, String region, String comuna, String indicaciones) {}
  public record Item(String codigo, String nombre, Integer precioUnitario, Integer cantidad,
    Integer subtotal, String imagen) {}
  public record Pago(String medio, String ultimosDigitos, String codigoAutorizacion, String motivo) {}

  public static BoletaResponse desde(Boleta b) {
    List<Item> items = b.getDetalles().stream()
      .map(d -> new Item(d.getProducto().getCodigo(), d.getNombreProducto(),
        d.getPrecioUnitario(), d.getCantidad(), d.getSubtotal(), d.getProducto().getImagen()))
      .toList();
    return new BoletaResponse(b.getNumero(), b.getFecha(), b.getEstado(),
      new Cliente(b.getClienteNombre(), b.getClienteApellidos(), b.getUsuarioCorreo()),
      new Envio(b.getEnvioCalle(), b.getEnvioDepartamento(), b.getEnvioRegion(),
        b.getEnvioComuna(), b.getEnvioIndicaciones()),
      items, b.getTotal(),
      new Pago(b.getPagoMedio(), b.getPagoUltimosDigitos(), b.getPagoCodigoAutorizacion(),
        b.getPagoMotivo()));
  }
}