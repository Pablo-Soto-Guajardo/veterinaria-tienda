package cl.duoc.tienda.service;

import java.security.SecureRandom;
import org.springframework.stereotype.Service;


@Service
public class PagoService {
  public static final String TARJETA_RECHAZADA = "4000000000000002";
  private final SecureRandom aleatorio = new SecureRandom();

  public record ResultadoPago(boolean aprobado, String codigoAutorizacion, String motivo) {}

  public ResultadoPago cobrar(String numeroTarjeta, int total) {
    String numero = numeroTarjeta.replace(" ", "");
    if (numero.length() != 16) {
      return new ResultadoPago(false, null, "La tarjeta debe tener 16 dígitos.");
    }
    if (numero.equals(TARJETA_RECHAZADA)) {
      return new ResultadoPago(false, null, "Tarjeta rechazada por el emisor.");
    }
    String codigo = String.format("AUT-%06d", aleatorio.nextInt(1_000_000));
    return new ResultadoPago(true, codigo, null);
  }
}