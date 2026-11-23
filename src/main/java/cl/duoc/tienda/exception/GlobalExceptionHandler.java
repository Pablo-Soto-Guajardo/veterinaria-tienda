package cl.duoc.tienda.exception;

import java.util.Map;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

// Traduce las excepciones a respuestas JSON { "error": "mensaje" }.
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(RecursoNoEncontradoException.class)
  ResponseEntity<Map<String, String>> noEncontrado(RecursoNoEncontradoException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
  }

  @ExceptionHandler(ReglaNegocioException.class)
  ResponseEntity<Map<String, String>> reglaNegocio(ReglaNegocioException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
  }

  @ExceptionHandler(AccesoDenegadoException.class)
  ResponseEntity<Map<String, String>> accesoDenegado(AccesoDenegadoException e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String, String>> datosInvalidos(MethodArgumentNotValidException e) {
    String mensaje = e.getBindingResult().getFieldErrors().stream()
      .findFirst().map(error -> error.getField() + ": " + error.getDefaultMessage())
      .orElse("Datos inválidos");
    return ResponseEntity.badRequest().body(Map.of("error", mensaje));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<Map<String, String>> jsonInvalido() {
    return ResponseEntity.badRequest().body(Map.of("error", "El cuerpo de la solicitud no es válido"));
  }
}