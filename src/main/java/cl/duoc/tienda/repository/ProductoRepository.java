package cl.duoc.tienda.repository;

import cl.duoc.tienda.model.Producto;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, String> {
  List<Producto> findByCategoriaId(String categoriaId);
  long countByCategoriaId(String categoriaId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select p from Producto p where p.codigo in :codigos")
  List<Producto> bloquearPorCodigos(@Param("codigos") Collection<String> codigos);
}
