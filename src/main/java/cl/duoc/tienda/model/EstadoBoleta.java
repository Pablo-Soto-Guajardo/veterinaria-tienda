package cl.duoc.tienda.model;

import java.util.List;
import java.util.Map;
import java.util.Set;


public final class EstadoBoleta {
  public static final String PAGADA = "Pagada";
  public static final String ENVIADA = "Enviada";
  public static final String ENTREGADA = "Entregada";
  public static final String RECHAZADA = "Rechazada";
  public static final String ANULADA = "Anulada";

  public static final List<String> TODOS = List.of(PAGADA, ENVIADA, ENTREGADA, RECHAZADA, ANULADA);

  
  private static final Map<String, Set<String>> TRANSICIONES = Map.of(
    PAGADA, Set.of(ENVIADA, ANULADA),
    ENVIADA, Set.of(ENTREGADA, ANULADA),
    ENTREGADA, Set.of(),
    RECHAZADA, Set.of(),
    ANULADA, Set.of());

  private EstadoBoleta() {}

  public static boolean puedeCambiar(String desde, String hacia) {
    return TRANSICIONES.getOrDefault(desde, Set.of()).contains(hacia);
  }

  
  public static boolean esVenta(String estado) {
    return PAGADA.equals(estado) || ENVIADA.equals(estado) || ENTREGADA.equals(estado);
  }
}