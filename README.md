# Veterinaria San Marcos – Tienda API

Backend de **catálogo y ventas** de la tienda Veterinaria San Marcos
(DSY1104 Desarrollo FullStack II – Evaluación 3, Grupo 5).

| Dato | Valor |
|------|-------|
| Puerto | `8082` |
| Base de datos | MySQL `veterinaria_tienda` |
| Versión de la API | `/api/v1` |
| Swagger | http://localhost:8082/swagger-ui/index.html (botón **Authorize** para pegar el JWT) |
| Tecnologías | Java 17, Spring Boot 4.1.2, Spring Security, Spring Data JPA, JJWT 0.12.6, springdoc 3.2.0 |

No guarda usuarios ni contraseñas: valida el JWT emitido por
[veterinaria-auth](../veterinaria-auth) con el mismo `JWT_SECRET` y el emisor `veterinaria-auth` (Guía 17).

## Modelo de datos (Anexo 1)

```
USUARIO ┄┄> BOLETA ──> DETALLE_BOLETA <── PRODUCTO <── CATEGORIA
(Auth)      (usuario_correo)    (boleta_numero, producto_codigo)   (categoria_id)
```

- `categoria` (id, nombre, descripcion, imagen)
- `producto` (codigo, nombre, categoria_id → categoria, presentacion, descripcion, precio, precio_oferta, stock, stock_critico, imagen)
- `boleta` (numero, fecha, estado, usuario_correo, datos del cliente, dirección de envío, total, datos del pago)
- `detalle_boleta` (id, boleta_numero → boleta, producto_codigo → producto, nombre_producto, precio_unitario, cantidad, subtotal)

La relación USUARIO → BOLETA es **lógica** (línea punteada del Anexo 1): el usuario vive en otra
base, por eso la boleta guarda el correo que llega firmado dentro del JWT.

## Lógica de negocio

- **Compra** (`POST /api/v1/boletas`), en una sola transacción:
  1. Agrupa ítems repetidos y **bloquea** las filas de esos productos (evita vender el mismo stock dos veces).
  2. Calcula precios y total **desde la base** (usa el precio de oferta si existe); el cliente solo envía códigos y cantidades.
  3. Si falta stock, la boleta queda `Rechazada` con el motivo.
  4. Cobra con la pasarela **simulada** (tarjeta `4000 0000 0000 0002` = rechazada).
  5. Si se aprueba, **descuenta el stock** y la boleta queda `Pagada`. Solo se guardan los últimos 4 dígitos.
- **Estados**: `Pagada → Enviada → Entregada`, o `Anulada` (al anular se devuelve el stock).
- No se elimina una categoría con productos, ni un producto con ventas (`409`).
- Solo se eliminan boletas `Rechazada` o `Anulada`.

## Endpoints y permisos

| Método | Ruta | ADMIN | VENDEDOR | CLIENTE | Sin token |
|--------|------|:-----:|:--------:|:-------:|:---------:|
| GET | `/api/v1/categorias`, `/api/v1/categorias/{id}` | ✔ | ✔ | ✔ | ✔ |
| POST/PUT/DELETE | `/api/v1/categorias/**` | ✔ | 403 | 403 | 401 |
| GET | `/api/v1/productos` (`?categoria=`, `?oferta=true`, `?criticos=true`), `/api/v1/productos/{codigo}` | ✔ | ✔ | ✔ | ✔ |
| POST/PUT/DELETE | `/api/v1/productos/**` | ✔ | 403 | 403 | 401 |
| POST | `/api/v1/boletas` | ✔ | 403 | ✔ | 401 |
| GET | `/api/v1/boletas` (`?correo=`) | ✔ | ✔ | 403 | 401 |
| GET | `/api/v1/boletas/mias` | ✔ | ✔ | ✔ | 401 |
| GET | `/api/v1/boletas/{numero}` | ✔ | ✔ | solo las suyas | 401 |
| PUT | `/api/v1/boletas/{numero}/estado` | ✔ | 403 | 403 | 401 |
| DELETE | `/api/v1/boletas/{numero}` | ✔ | 403 | 403 | 401 |

## Ejecutar en Windows (PowerShell)

Primero inicia **veterinaria-auth** (para obtener tokens). En otra terminal:

```powershell
$env:JWT_SECRET = "EL_MISMO_SECRETO_QUE_USASTE_EN_AUTH"
$env:TIENDA_DB_USER = "root"
$env:TIENDA_DB_PASSWORD = "TU_CLAVE_MYSQL"
.\mvnw.cmd spring-boot:run
```

Al iniciar por primera vez se cargan 6 categorías, 16 productos y 3 boletas de ejemplo.

## Pruebas

```powershell
.\mvnw.cmd test
```

- `BoletaServiceTest`: compra aprobada (precio de oferta y descuento de stock), sin stock,
  tarjeta rechazada, anulación con devolución de stock y transición inválida (**Mockito**).
- `TiendaApiIntegrationTest`: backend completo con H2; matriz 200 / 201 / 401 / 403 / 409 por rol,
  token de otro emisor y boleta de otro cliente.

En la carpeta `postman/` hay una colección con todos los endpoints.