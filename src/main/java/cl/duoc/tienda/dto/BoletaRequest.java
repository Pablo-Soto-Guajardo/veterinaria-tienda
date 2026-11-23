package cl.duoc.tienda.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;


public record BoletaRequest(
  @NotNull @Valid Cliente cliente,
  @NotNull @Valid Envio envio,
  @NotEmpty @Valid List<Item> items,
  @NotBlank @Pattern(regexp = "^[0-9 ]{16,19}$", message = "la tarjeta debe tener 16 dígitos") String numeroTarjeta
) {
  public record Cliente(
    @NotBlank @Size(max = 50) String nombre,
    @NotBlank @Size(max = 100) String apellidos
  ) {}

  public record Envio(
    @NotBlank @Size(max = 300) String calle,
    @Size(max = 50) String departamento,
    @NotBlank String region,
    @NotBlank String comuna,
    @Size(max = 300) String indicaciones
  ) {}

  public record Item(
    @NotBlank String codigo,
    @NotNull @Min(1) Integer cantidad
  ) {}
}