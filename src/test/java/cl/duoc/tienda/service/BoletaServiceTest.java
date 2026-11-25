package cl.duoc.tienda.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import cl.duoc.tienda.dto.BoletaRequest;
import cl.duoc.tienda.dto.BoletaResponse;
import cl.duoc.tienda.exception.ReglaNegocioException;
import cl.duoc.tienda.model.*;
import cl.duoc.tienda.repository.BoletaRepository;
import cl.duoc.tienda.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class BoletaServiceTest {
  @Mock BoletaRepository boletas;
  @Mock ProductoRepository productos;
  @Mock PagoService pagos;
  @InjectMocks BoletaService service;

  Producto bravecto;

  @BeforeEach
  void preparar() {
    Categoria categoria = new Categoria();
    categoria.setId("antiparasitarios");
    categoria.setNombre("Antiparasitarios");
    bravecto = new Producto();
    bravecto.setCodigo("ME005");
    bravecto.setNombre("Bravecto");
    bravecto.setCategoria(categoria);
    bravecto.setPrecio(18900);
    bravecto.setPrecioOferta(16900);
    bravecto.setStock(8);
    bravecto.setStockCritico(10);
  }

  private BoletaRequest compra(int cantidad, String tarjeta) {
    return new BoletaRequest(
      new BoletaRequest.Cliente("Javiera", "Rojas"),
      new BoletaRequest.Envio("Calle 1", "", "RM", "Providencia", ""),
      List.of(new BoletaRequest.Item("ME005", cantidad)),
      tarjeta);
  }

  @Test
  void compraAprobadaUsaPrecioOfertaYDescuentaStock() {
    when(productos.bloquearPorCodigos(anyCollection())).thenReturn(List.of(bravecto));
    when(pagos.cobrar(anyString(), anyInt()))
      .thenReturn(new PagoService.ResultadoPago(true, "AUT-123456", null));
    when(boletas.save(any(Boleta.class))).thenAnswer(inv -> inv.getArgument(0));

    BoletaResponse boleta = service.crear(compra(2, "4111111111111111"), "cliente@gmail.com");

    assertEquals("Pagada", boleta.estado());
    assertEquals(33800, boleta.total());           // 2 x 16.900 (precio oferta)
    assertEquals(6, bravecto.getStock());          // 8 - 2
    assertEquals("1111", boleta.pago().ultimosDigitos());
    assertEquals("cliente@gmail.com", boleta.cliente().correo());
  }

  @Test
  void sinStockSuficienteLaBoletaQuedaRechazadaYNoSeCobra() {
    when(productos.bloquearPorCodigos(anyCollection())).thenReturn(List.of(bravecto));
    when(boletas.save(any(Boleta.class))).thenAnswer(inv -> inv.getArgument(0));

    BoletaResponse boleta = service.crear(compra(20, "4111111111111111"), "cliente@gmail.com");

    assertEquals("Rechazada", boleta.estado());
    assertTrue(boleta.pago().motivo().contains("Stock insuficiente"));
    assertEquals(8, bravecto.getStock());
    verify(pagos, never()).cobrar(anyString(), anyInt());
  }

  @Test
  void pagoRechazadoNoDescuentaStock() {
    when(productos.bloquearPorCodigos(anyCollection())).thenReturn(List.of(bravecto));
    when(pagos.cobrar(anyString(), anyInt()))
      .thenReturn(new PagoService.ResultadoPago(false, null, "Tarjeta rechazada por el emisor."));
    when(boletas.save(any(Boleta.class))).thenAnswer(inv -> inv.getArgument(0));

    BoletaResponse boleta = service.crear(compra(1, "4000000000000002"), "cliente@gmail.com");

    assertEquals("Rechazada", boleta.estado());
    assertEquals(8, bravecto.getStock());
  }

  @Test
  void anularUnaVentaDevuelveElStock() {
    Boleta boleta = new Boleta();
    boleta.setNumero(1L);
    boleta.setEstado(EstadoBoleta.PAGADA);
    DetalleBoleta d = new DetalleBoleta();
    d.setProducto(bravecto);
    d.setCantidad(3);
    boleta.agregarDetalle(d);
    when(boletas.findById(1L)).thenReturn(Optional.of(boleta));

    service.cambiarEstado(1L, EstadoBoleta.ANULADA);

    assertEquals(EstadoBoleta.ANULADA, boleta.getEstado());
    assertEquals(11, bravecto.getStock());
  }

  @Test
  void noPermiteCambiosDeEstadoInvalidos() {
    Boleta boleta = new Boleta();
    boleta.setNumero(2L);
    boleta.setEstado(EstadoBoleta.RECHAZADA);
    when(boletas.findById(2L)).thenReturn(Optional.of(boleta));

    assertThrows(ReglaNegocioException.class,
      () -> service.cambiarEstado(2L, EstadoBoleta.ENVIADA));
  }
}