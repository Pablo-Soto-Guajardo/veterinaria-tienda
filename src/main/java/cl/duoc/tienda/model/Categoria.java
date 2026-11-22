package cl.duoc.tienda.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "categoria")
@Getter @Setter @NoArgsConstructor
public class Categoria {
  @Id
  @Column(length = 60)
  private String id;

  @Column(nullable = false, unique = true, length = 50)
  private String nombre;

  @Column(length = 200)
  private String descripcion;

  @Column(length = 500)
  private String imagen;
}
