package cl.duoc.tienda;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

// Prueba de integración: levanta el backend Tienda con H2 y lo llama por HTTP.
// Los JWT se firman aquí con el mismo secreto de prueba, simulando al backend Auth.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TiendaApiIntegrationTest {
  @Value("${local.server.port}") int puerto;
  @Value("${app.jwt.secret}") String secreto;
  private final HttpClient cliente = HttpClient.newHttpClient();

  private String token(String correo, String rol, String issuer) {
    Date ahora = new Date();
    return Jwts.builder().issuer(issuer).subject(correo).claim("rol", rol)
      .issuedAt(ahora).expiration(new Date(ahora.getTime() + 60_000))
      .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto))).compact();
  }

  private String token(String correo, String rol) {
    return token(correo, rol, "veterinaria-auth");
  }

  private HttpResponse<String> enviar(String metodo, String ruta, String json, String token)
      throws Exception {
    HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta))
      .header("Content-Type", "application/json")
      .method(metodo, json == null ? HttpRequest.BodyPublishers.noBody()
        : HttpRequest.BodyPublishers.ofString(json));
    if (token != null) builder.header("Authorization", "Bearer " + token);
    return cliente.send(builder.build(), HttpResponse.BodyHandlers.ofString());
  }

  private static final String PRODUCTO_NUEVO = """
    {"codigo":"%s","nombre":"Producto de prueba","categoria":"higiene","presentacion":"Unidad",
     "descripcion":"Solo pruebas","precio":5000,"precioOferta":null,"stock":10,"stockCritico":2,"imagen":""}
    """;

  private static final String COMPRA = """
    {"cliente":{"nombre":"Javiera","apellidos":"Rojas"},
     "envio":{"calle":"Los Crisantemos 120","departamento":"","region":"RM","comuna":"Providencia","indicaciones":""},
     "items":[{"codigo":"HI002","cantidad":1}],
     "numeroTarjeta":"4111 1111 1111 1111"}
    """;

  @Test
  void catalogoEsPublico() throws Exception {
    var r = enviar("GET", "/api/v1/productos", null, null);
    assertEquals(200, r.statusCode());
    assertTrue(r.body().contains("ME004"));
    assertEquals(200, enviar("GET", "/api/v1/categorias", null, null).statusCode());
  }

  @Test
  void crearProductoSinTokenEntrega401() throws Exception {
    assertEquals(401, enviar("POST", "/api/v1/productos", PRODUCTO_NUEVO.formatted("PR901"), null).statusCode());
  }

  @Test
  void crearProductoComoVendedorEntrega403() throws Exception {
    String t = token("vendedor@duoc.cl", "VENDEDOR");
    assertEquals(403, enviar("POST", "/api/v1/productos", PRODUCTO_NUEVO.formatted("PR902"), t).statusCode());
  }

  @Test
  void crearProductoComoAdminEntrega201() throws Exception {
    String t = token("admin@duoc.cl", "ADMIN");
    var r = enviar("POST", "/api/v1/productos", PRODUCTO_NUEVO.formatted("PR903"), t);
    assertEquals(201, r.statusCode());
    assertEquals(200, enviar("GET", "/api/v1/productos/PR903", null, null).statusCode());
    assertEquals(204, enviar("DELETE", "/api/v1/productos/PR903", null, t).statusCode());
  }

  @Test
  void vendedorPuedeVerBoletasYClienteNo() throws Exception {
    assertEquals(200, enviar("GET", "/api/v1/boletas", null, token("vendedor@duoc.cl", "VENDEDOR")).statusCode());
    assertEquals(403, enviar("GET", "/api/v1/boletas", null, token("cliente@gmail.com", "CLIENTE")).statusCode());
  }

  @Test
  void vendedorNoPuedeCambiarEstadoDeBoletas() throws Exception {
    var r = enviar("PUT", "/api/v1/boletas/1/estado", "{\"estado\":\"Enviada\"}",
      token("vendedor@duoc.cl", "VENDEDOR"));
    assertEquals(403, r.statusCode());
  }

  @Test
  void clienteCompraYVeSuBoletaPeroNoLaDeOtro() throws Exception {
    String t = token("cliente@gmail.com", "CLIENTE");
    var compra = enviar("POST", "/api/v1/boletas", COMPRA, t);
    assertEquals(201, compra.statusCode());
    assertTrue(compra.body().contains("\"estado\":\"Pagada\""));

    assertEquals(200, enviar("GET", "/api/v1/boletas/mias", null, t).statusCode());
    // La boleta 1 de los datos iniciales es de cliente@gmail.com; otro cliente no puede verla.
    assertEquals(200, enviar("GET", "/api/v1/boletas/1", null, t).statusCode());
    assertEquals(403, enviar("GET", "/api/v1/boletas/1", null, token("otro@gmail.com", "CLIENTE")).statusCode());
  }

  @Test
  void tokenDeOtroEmisorEntrega401() throws Exception {
    String falso = token("admin@duoc.cl", "ADMIN", "otro-emisor");
    assertEquals(401, enviar("GET", "/api/v1/boletas", null, falso).statusCode());
  }

  @Test
  void eliminarCategoriaConProductosEntrega409() throws Exception {
    String t = token("admin@duoc.cl", "ADMIN");
    var r = enviar("DELETE", "/api/v1/categorias/antiparasitarios", null, t);
    assertEquals(409, r.statusCode());
    assertTrue(r.body().contains("producto"));
  }
}