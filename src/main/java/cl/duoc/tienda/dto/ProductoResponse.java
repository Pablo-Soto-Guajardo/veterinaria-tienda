package cl.duoc.tienda.dto;

import cl.duoc.tienda.model.Producto;


public record ProductoResponse(
  String codigo,
  String nombre,
  String categoria,
  String categoriaNombre,
  String presentacion,
  String descripcion,
  Integer precio,
  Integer precioOferta,
  Integer stock,
  Integer stockCritico,
  String imagen
) {
  public static ProductoResponse desde(Producto p) {
    return new ProductoResponse(p.getCodigo(), p.getNombre(), p.getCategoria().getId(),
      p.getCategoria().getNombre(), p.getPresentacion(), p.getDescripcion(), p.getPrecio(),
      p.getPrecioOferta(), p.getStock(), p.getStockCritico(), p.getImagen());
  }
}