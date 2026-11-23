package cl.duoc.tienda.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity @Table(name = "detalle_boleta")
@Getter @Setter @NoArgsConstructor
public class DetalleBoleta {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "boleta_numero")
  private Boleta boleta;

  @ManyToOne(optional = false)
  @JoinColumn(name = "producto_codigo")
  private Producto producto;

  @Column(nullable = false, length = 100)
  private String nombreProducto;

  @Column(nullable = false)
  private Integer precioUnitario;

  @Column(nullable = false)
  private Integer cantidad;

  @Column(nullable = false)
  private Integer subtotal;
}