package cl.duoc.tienda.controller;

import cl.duoc.tienda.dto.*;
import cl.duoc.tienda.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Lectura pública; escritura solo ADMIN")
public class ProductoController {
  private final ProductoService service;
  public ProductoController(ProductoService service) { this.service = service; }

  @GetMapping
  @Operation(summary = "Lista productos con filtros opcionales: categoria, oferta, criticos (público)")
  public List<ProductoResponse> listar(
      @RequestParam(required = false) String categoria,
      @RequestParam(defaultValue = "false") boolean oferta,
      @RequestParam(defaultValue = "false") boolean criticos) {
    return service.listar(categoria, oferta, criticos);
  }

  @GetMapping("/{codigo}")
  @Operation(summary = "Busca un producto por código (público)")
  public ProductoResponse buscar(@PathVariable String codigo) { return service.buscar(codigo); }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Crea un producto (ADMIN)")
  public ProductoResponse crear(@Valid @RequestBody ProductoRequest request) {
    return service.crear(request);
  }

  @PutMapping("/{codigo}")
  @Operation(summary = "Actualiza un producto (ADMIN)")
  public ProductoResponse actualizar(@PathVariable String codigo,
      @Valid @RequestBody ProductoRequest request) {
    return service.actualizar(codigo, request);
  }

  @DeleteMapping("/{codigo}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Elimina un producto sin ventas (ADMIN)")
  public void eliminar(@PathVariable String codigo) { service.eliminar(codigo); }
}
