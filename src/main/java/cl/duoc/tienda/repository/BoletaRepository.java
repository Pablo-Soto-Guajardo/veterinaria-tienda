package cl.duoc.tienda.repository;

import cl.duoc.tienda.model.Boleta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {
  List<Boleta> findAllByOrderByNumeroDesc();
  List<Boleta> findByUsuarioCorreoIgnoreCaseOrderByNumeroDesc(String correo);
}