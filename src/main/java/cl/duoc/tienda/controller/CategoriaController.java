package cl.duoc.tienda.controller;

import cl.duoc.tienda.dto.*;
import cl.duoc.tienda.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorías", description = "Lectura pública; escritura solo ADMIN")
public class CategoriaController {
  private final CategoriaService service;
  public CategoriaController(CategoriaService service) { this.service = service; }

  @GetMapping
  @Operation(summary = "Lista las categorías (público)")
  public List<CategoriaResponse> listar() { return service.listar(); }

  @GetMapping("/{id}")
  @Operation(summary = "Busca una categoría por id (público)")
  public CategoriaResponse buscar(@PathVariable String id) { return service.buscar(id); }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Crea una categoría; el id se genera desde el nombre (ADMIN)")
  public CategoriaResponse crear(@Valid @RequestBody CategoriaRequest request) {
    return service.crear(request);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Actualiza una categoría (ADMIN)")
  public CategoriaResponse actualizar(@PathVariable String id,
      @Valid @RequestBody CategoriaRequest request) {
    return service.actualizar(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Elimina una categoría sin productos (ADMIN)")
  public void eliminar(@PathVariable String id) { service.eliminar(id); }
}
