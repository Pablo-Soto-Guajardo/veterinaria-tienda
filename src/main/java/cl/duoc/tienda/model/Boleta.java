package cl.duoc.tienda.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity @Table(name = "boleta")
@Getter @Setter @NoArgsConstructor
public class Boleta {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long numero;

  @Column(nullable = false)
  private LocalDateTime fecha;

  @Column(nullable = false, length = 20)
  private String estado;

  @Column(name = "usuario_correo", nullable = false, length = 100)
  private String usuarioCorreo;

  @Column(nullable = false, length = 50)
  private String clienteNombre;

  @Column(nullable = false, length = 100)
  private String clienteApellidos;

  @Column(nullable = false, length = 300)
  private String envioCalle;

  @Column(length = 50)
  private String envioDepartamento;

  @Column(nullable = false, length = 10)
  private String envioRegion;

  @Column(nullable = false, length = 60)
  private String envioComuna;

  @Column(length = 300)
  private String envioIndicaciones;

  @Column(nullable = false)
  private Integer total;

  @Column(nullable = false, length = 20)
  private String pagoMedio;


  @Column(length = 4)
  private String pagoUltimosDigitos;

  @Column(length = 20)
  private String pagoCodigoAutorizacion;

  @Column(length = 200)
  private String pagoMotivo;


  @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DetalleBoleta> detalles = new ArrayList<>();

  public void agregarDetalle(DetalleBoleta detalle) {
    detalle.setBoleta(this);
    detalles.add(detalle);
  }
}