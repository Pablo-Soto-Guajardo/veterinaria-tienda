package cl.duoc.tienda.dto;

import jakarta.validation.constraints.*;

public record CategoriaRequest(
  @NotBlank @Size(max = 50) String nombre,
  @Size(max = 200) String descripcion,
  @Size(max = 500) String imagen
) {}