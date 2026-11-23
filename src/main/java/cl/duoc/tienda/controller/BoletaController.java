package cl.duoc.tienda.controller;

import cl.duoc.tienda.dto.*;
import cl.duoc.tienda.service.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/boletas")
@Tag(name = "Boletas", description = "Compras de la tienda y gestión de órdenes")
public class BoletaController {
  private final BoletaService service;
  public BoletaController(BoletaService service) { this.service = service; }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Registra una compra: valida stock, cobra y descuenta inventario (CLIENTE, ADMIN)")
  public BoletaResponse crear(@Valid @RequestBody BoletaRequest request, Authentication auth) {
    return service.crear(request, auth.getName());
  }

  @GetMapping
  @Operation(summary = "Lista todas las boletas o las de un cliente con ?correo= (ADMIN, VENDEDOR)")
  public List<BoletaResponse> listar(@RequestParam(required = false) String correo) {
    return service.listar(correo);
  }

  @GetMapping("/mias")
  @Operation(summary = "Boletas del usuario dueño del token")
  public List<BoletaResponse> mias(Authentication auth) {
    return service.listarPropias(auth.getName());
  }

  @GetMapping("/{numero}")
  @Operation(summary = "Detalle de una boleta (ADMIN, VENDEDOR o el cliente dueño)")
  public BoletaResponse buscar(@PathVariable Long numero, Authentication auth) {
    return service.buscar(numero, auth);
  }

  @PutMapping("/{numero}/estado")
  @Operation(summary = "Cambia el estado: Pagada → Enviada → Entregada, o Anulada (ADMIN)")
  public BoletaResponse cambiarEstado(@PathVariable Long numero,
      @Valid @RequestBody EstadoRequest request) {
    return service.cambiarEstado(numero, request.estado());
  }

  @DeleteMapping("/{numero}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Elimina una boleta rechazada o anulada (ADMIN)")
  public void eliminar(@PathVariable Long numero) { service.eliminar(numero); }
}