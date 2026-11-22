package cl.duoc.tienda.service;

import cl.duoc.tienda.dto.*;
import cl.duoc.tienda.exception.*;
import cl.duoc.tienda.model.Producto;
import cl.duoc.tienda.repository.DetalleBoletaRepository;
import cl.duoc.tienda.repository.ProductoRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {
  private final ProductoRepository productos;
  private final DetalleBoletaRepository detalles;
  private final CategoriaService categorias;

  public ProductoService(ProductoRepository productos, DetalleBoletaRepository detalles,
      CategoriaService categorias) {
    this.productos = productos;
    this.detalles = detalles;
    this.categorias = categorias;
  }

  @Transactional(readOnly = true)
  public List<ProductoResponse> listar(String categoria, boolean oferta, boolean criticos) {
    Stream<Producto> lista = (categoria == null || categoria.isBlank())
      ? productos.findAll().stream()
      : productos.findByCategoriaId(categoria).stream();
    if (oferta) {
      lista = lista.filter(p -> p.getPrecioOferta() != null && p.getPrecioOferta() < p.getPrecio());
    }
    if (criticos) {
      lista = lista.filter(Producto::esCritico);
    }
    return lista.sorted(Comparator.comparing(Producto::getCodigo))
      .map(ProductoResponse::desde).toList();
  }

  @Transactional(readOnly = true)
  public ProductoResponse buscar(String codigo) {
    return ProductoResponse.desde(buscarEntidad(codigo));
  }

  private Producto buscarEntidad(String codigo) {
    return productos.findById(codigo.trim().toUpperCase())
      .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + codigo));
  }

  @Transactional
  public ProductoResponse crear(ProductoRequest request) {
    String codigo = request.codigo().trim().toUpperCase();
    if (productos.existsById(codigo)) {
      throw new ReglaNegocioException("Ya existe un producto con el código " + codigo);
    }
    Producto producto = new Producto();
    producto.setCodigo(codigo);
    copiar(producto, request);
    return ProductoResponse.desde(productos.save(producto));
  }

  @Transactional
  public ProductoResponse actualizar(String codigo, ProductoRequest request) {
    Producto producto = buscarEntidad(codigo);
    copiar(producto, request); // el código identifica al producto y no cambia
    return ProductoResponse.desde(producto);
  }

  @Transactional
  public void eliminar(String codigo) {
    Producto producto = buscarEntidad(codigo);
    if (detalles.existsByProductoCodigo(producto.getCodigo())) {
      throw new ReglaNegocioException("No se puede eliminar: el producto tiene ventas registradas. "
        + "Puedes dejar su stock en 0.");
    }
    productos.delete(producto);
  }

  private void copiar(Producto producto, ProductoRequest request) {
    if (request.precioOferta() != null && request.precioOferta() >= request.precio()) {
      throw new ReglaNegocioException("La oferta debe ser menor que el precio normal");
    }
    producto.setNombre(request.nombre().trim());
    producto.setCategoria(categorias.buscarEntidad(request.categoria()));
    producto.setPresentacion(CategoriaService.texto(request.presentacion()));
    producto.setDescripcion(CategoriaService.texto(request.descripcion()));
    producto.setPrecio(request.precio());
    producto.setPrecioOferta(request.precioOferta());
    producto.setStock(request.stock());
    producto.setStockCritico(request.stockCritico() == null ? 0 : request.stockCritico());
    producto.setImagen(CategoriaService.texto(request.imagen()));
  }
}
