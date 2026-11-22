package cl.duoc.tienda.service;

import cl.duoc.tienda.dto.*;
import cl.duoc.tienda.exception.*;
import cl.duoc.tienda.model.Categoria;
import cl.duoc.tienda.repository.CategoriaRepository;
import cl.duoc.tienda.repository.ProductoRepository;
import java.text.Normalizer;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {
  private final CategoriaRepository categorias;
  private final ProductoRepository productos;

  public CategoriaService(CategoriaRepository categorias, ProductoRepository productos) {
    this.categorias = categorias;
    this.productos = productos;
  }

  @Transactional(readOnly = true)
  public List<CategoriaResponse> listar() {
    return categorias.findAll(Sort.by("nombre")).stream().map(CategoriaResponse::desde).toList();
  }

  @Transactional(readOnly = true)
  public CategoriaResponse buscar(String id) {
    return CategoriaResponse.desde(buscarEntidad(id));
  }

  Categoria buscarEntidad(String id) {
    return categorias.findById(id)
      .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada: " + id));
  }

  @Transactional
  public CategoriaResponse crear(CategoriaRequest request) {
    String id = crearSlug(request.nombre());
    if (categorias.existsById(id) || categorias.existsByNombreIgnoreCase(request.nombre().trim())) {
      throw new ReglaNegocioException("Ya existe la categoría " + request.nombre().trim());
    }
    Categoria categoria = new Categoria();
    categoria.setId(id);
    copiar(categoria, request);
    return CategoriaResponse.desde(categorias.save(categoria));
  }

  @Transactional
  public CategoriaResponse actualizar(String id, CategoriaRequest request) {
    Categoria categoria = buscarEntidad(id);
    copiar(categoria, request);
    return CategoriaResponse.desde(categoria);
  }


  @Transactional
  public void eliminar(String id) {
    Categoria categoria = buscarEntidad(id);
    long enUso = productos.countByCategoriaId(id);
    if (enUso > 0) {
      throw new ReglaNegocioException("No se puede eliminar: " + enUso + " producto(s) usan esta categoría");
    }
    categorias.delete(categoria);
  }

  private void copiar(Categoria categoria, CategoriaRequest request) {
    categoria.setNombre(request.nombre().trim());
    categoria.setDescripcion(texto(request.descripcion()));
    categoria.setImagen(texto(request.imagen()));
  }

  static String texto(String valor) {
    return valor == null ? "" : valor.trim();
  }

  static String crearSlug(String texto) {
    String sinTildes = Normalizer.normalize(texto.trim().toLowerCase(), Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "");
    return sinTildes.replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
  }
}
