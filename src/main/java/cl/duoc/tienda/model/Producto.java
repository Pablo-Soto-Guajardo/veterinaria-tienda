package cl.duoc.tienda.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "producto")
@Getter @Setter @NoArgsConstructor
public class Producto {
  @Id
  @Column(length = 20)
  private String codigo;

  @Column(nullable = false, length = 100)
  private String nombre;

  @ManyToOne(optional = false)
  @JoinColumn(name = "categoria_id")
  private Categoria categoria;

  @Column(length = 100)
  private String presentacion;

  @Column(length = 500)
  private String descripcion;

  @Column(nullable = false)
  private Integer precio;


  private Integer precioOferta;

  @Column(nullable = false)
  private Integer stock;

  @Column(nullable = false)
  private Integer stockCritico;

  @Column(length = 500)
  private String imagen;

  public int precioVigente() {
    return precioOferta != null && precioOferta < precio ? precioOferta : precio;
  }

  public boolean esCritico() {
    return stock <= stockCritico;
  }
}
