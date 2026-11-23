package cl.duoc.tienda.dto;

import jakarta.validation.constraints.*;


public record ProductoRequest(
  @NotBlank @Size(min = 3, max = 20) String codigo,
  @NotBlank @Size(max = 100) String nombre,
  @NotBlank String categoria,
  @Size(max = 100) String presentacion,
  @Size(max = 500) String descripcion,
  @NotNull @PositiveOrZero Integer precio,
  @PositiveOrZero Integer precioOferta,
  @NotNull @PositiveOrZero Integer stock,
  @PositiveOrZero Integer stockCritico,
  @Size(max = 500) String imagen
) {}