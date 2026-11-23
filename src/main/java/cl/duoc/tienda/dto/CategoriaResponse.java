package cl.duoc.tienda.dto;

import cl.duoc.tienda.model.Categoria;

public record CategoriaResponse(String id, String nombre, String descripcion, String imagen) {
  public static CategoriaResponse desde(Categoria c) {
    return new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion(), c.getImagen());
  }
}