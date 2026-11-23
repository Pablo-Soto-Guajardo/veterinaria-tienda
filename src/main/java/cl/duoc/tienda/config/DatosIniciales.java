package cl.duoc.tienda.config;

import cl.duoc.tienda.model.*;
import cl.duoc.tienda.repository.*;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;

@Configuration
public class DatosIniciales {
  @Bean CommandLineRunner catalogoInicial(CategoriaRepository categorias,
      ProductoRepository productos, BoletaRepository boletas) {
    return args -> {
      if (categorias.count() > 0) return;

      categoria(categorias, "antiparasitarios", "Antiparasitarios", "Control de pulgas, garrapatas y parásitos internos.", "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=600&auto=format&fit=crop&q=60");
      categoria(categorias, "antibioticos", "Antibióticos", "Tratamientos bajo receta para infecciones bacterianas.", "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600&auto=format&fit=crop&q=60");
      categoria(categorias, "dermatologia", "Dermatología", "Productos para piel, pelaje y alergias.", "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?w=600&auto=format&fit=crop&q=60");
      categoria(categorias, "alimentos", "Alimentos clínicos", "Dietas veterinarias para necesidades específicas.", "https://images.unsplash.com/photo-1568640347023-a616a30bc3bd?w=600&auto=format&fit=crop&q=60");
      categoria(categorias, "higiene", "Higiene y cuidado", "Accesorios de higiene dental, ótica y cuidado diario.", "https://images.unsplash.com/photo-1548767797-d8c844163c4c?w=600&auto=format&fit=crop&q=60");
      categoria(categorias, "educacion", "Educación", "Material gratuito para tutores responsables.", "https://images.unsplash.com/photo-1601758228041-f3b2795255f1?w=600&auto=format&fit=crop&q=60");

      producto(productos, categorias, "ME004", "Nexgard (Afoxolaner)", "antiparasitarios", "Masticable 1 unid. (Perro)",
        "Tratamiento y prevención de infestaciones por pulgas y garrapatas en perros.", 9500, null, 60, 10,
        "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=600&auto=format&fit=crop&q=60");
      producto(productos, categorias, "ME005", "Bravecto (Fluralaner)", "antiparasitarios", "Masticable 1 unid. (Perro)",
        "Protección continua por hasta 12 semanas contra pulgas y garrapatas.", 18900, 16900, 8, 10,
        "https://images.unsplash.com/photo-1568640347023-a616a30bc3bd?w=600&auto=format&fit=crop&q=60");
      producto(productos, categorias, "ME006", "Drontal Plus (Prazicuantel)", "antiparasitarios", "Comprimido 1 unid. (Perro)",
        "Antiparasitario interno de amplio espectro contra nematodos y cestodos.", 3900, null, 40, 10,
        "https://images.unsplash.com/photo-1548767797-d8c844163c4c?w=600&auto=format&fit=crop&q=60");
      producto(productos, categorias, "ME007", "Revolution Gatos (Selamectina)", "antiparasitarios", "Pipeta 0,75 ml (Gato)",
        "Pipeta mensual contra pulgas, ácaros del oído y parásitos intestinales.", 12500, 10900, 15, 5,
        "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=600&auto=format&fit=crop&q=60");
      producto(productos, categorias, "ME001", "Amoxibay 250mg (Amoxicilina)", "antibioticos", "Blíster 10 comp. (Perro / Gato)",
        "Antibiótico bactericida de amplio espectro para infecciones respiratorias y dérmicas.", 4200, null, 45, 10,
        "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600&auto=format&fit=crop&q=60");
      producto(productos, categorias, "ME002", "Enrofloxacino 50mg", "antibioticos", "Blíster 10 comp. (Perro / Gato)",
        "Antibiótico de la familia de las quinolonas para infecciones urinarias y digestivas.", 6900, null, 4, 8,
        "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600&auto=format&fit=crop&q=60");
      producto(productos, categorias, "ME003", "Cefalexina 250mg/5ml", "antibioticos", "Suspensión oral 100 ml",
        "Antibiótico en suspensión, de fácil dosificación para cachorros y gatos.", 7800, null, 25, 6,
        "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600&auto=format&fit=crop&q=60");
      producto(productos, categorias, "ME011", "Clorhexidina shampoo 2%", "dermatologia", "Frasco 250 ml (Perro / Gato)",
        "Antiséptico de uso tópico para higiene y tratamiento de piodermas y dermatitis.", 8900, null, 0, 5,
        "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?w=600&auto=format&fit=crop&q=60");
      producto(productos, categorias, "ME013", "Apoquel 16mg (Oclacitinib)", "dermatologia", "Blíster 10 comp. (Perro)",
        "Control rápido y seguro del prurito asociado a dermatitis alérgica en caninos.", 22000, 19800, 18, 5,
        "https://images.unsplash.com/photo-1548767797-d8c844163c4c?w=600&auto=format&fit=crop&q=60");
      producto(productos, categorias, "ME014", "Crema cicatrizante con alantoína", "dermatologia", "Pomo 50 g",
        "Favorece la regeneración de la piel en heridas superficiales y rozaduras.", 5400, null, 30, 6,
        "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?w=600&auto=format&fit=crop&q=60");
      producto(productos, categorias, "AL001", "Dieta renal felina", "alimentos", "Saco 2 kg (Gato)",
        "Alimento clínico bajo en fósforo para apoyar la función renal de gatos adultos.", 24990, null, 12, 5,
        "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=600&auto=format&fit=crop&q=60");
      producto(productos, categorias, "AL002", "Dieta gastrointestinal canina", "alimentos", "Saco 3,85 kg (Perro)",
        "Alimento de alta digestibilidad para perros con trastornos digestivos.", 32990, 28990, 9, 5,
        "https://images.unsplash.com/photo-1568640347023-a616a30bc3bd?w=600&auto=format&fit=crop&q=60");
      producto(productos, categorias, "AL003", "Lata de recuperación", "alimentos", "Lata 400 g (Perro / Gato)",
        "Alimento húmedo hipercalórico para pacientes en convalecencia.", 3200, null, 50, 12,
        "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=600&auto=format&fit=crop&q=60");
      producto(productos, categorias, "HI001", "Kit cepillo dental doble", "higiene", "Unidad (Perro / Gato)",
        "Cepillo de doble cabezal y dedal para la higiene dental diaria.", 2990, 2490, 70, 10,
        "https://images.unsplash.com/photo-1548767797-d8c844163c4c?w=600&auto=format&fit=crop&q=60");
      producto(productos, categorias, "HI002", "Limpiador ótico", "higiene", "Frasco 120 ml",
        "Solución para la limpieza suave del conducto auditivo externo.", 6500, null, 22, 6,
        "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=600&auto=format&fit=crop&q=60");
      producto(productos, categorias, "EDU001", "Guía de primeros auxilios y vacunación", "educacion", "Documento digital PDF",
        "Manual preventivo con protocolos de urgencia para tutores responsables.", 0, null, 200, 10,
        "https://images.unsplash.com/photo-1601758228041-f3b2795255f1?w=600&auto=format&fit=crop&q=60");

      Boleta b1 = boleta("2026-09-20T12:32:00", EstadoBoleta.PAGADA, "Los Crisantemos 120, Edificio Norte",
        "Depto 603", "Dejar con conserjería.", "1111", "AUT-584211", null);
      detalle(b1, productos, "ME004", 9500, 2);
      detalle(b1, productos, "HI001", 2490, 1);
      boletas.save(b1);

      Boleta b2 = boleta("2026-10-01T09:05:00", EstadoBoleta.ENVIADA, "Los Crisantemos 120, Edificio Norte",
        "Depto 603", "", "1111", "AUT-610934", null);
      detalle(b2, productos, "AL002", 28990, 1);
      detalle(b2, productos, "EDU001", 0, 1);
      boletas.save(b2);

      Boleta b3 = boleta("2026-10-03T16:47:00", EstadoBoleta.RECHAZADA, "Los Crisantemos 120, Edificio Norte",
        "", "", "0002", null, "Tarjeta rechazada por el emisor.");
      detalle(b3, productos, "ME013", 19800, 1);
      boletas.save(b3);
    };
  }

  private void categoria(CategoriaRepository repo, String id, String nombre,
      String descripcion, String imagen) {
    Categoria c = new Categoria();
    c.setId(id);
    c.setNombre(nombre);
    c.setDescripcion(descripcion);
    c.setImagen(imagen);
    repo.save(c);
  }

  private void producto(ProductoRepository repo, CategoriaRepository categorias, String codigo,
      String nombre, String categoria, String presentacion, String descripcion, int precio,
      Integer precioOferta, int stock, int stockCritico, String imagen) {
    Producto p = new Producto();
    p.setCodigo(codigo);
    p.setNombre(nombre);
    p.setCategoria(categorias.findById(categoria).orElseThrow());
    p.setPresentacion(presentacion);
    p.setDescripcion(descripcion);
    p.setPrecio(precio);
    p.setPrecioOferta(precioOferta);
    p.setStock(stock);
    p.setStockCritico(stockCritico);
    p.setImagen(imagen);
    repo.save(p);
  }

  private Boleta boleta(String fecha, String estado, String calle, String depto,
      String indicaciones, String digitos, String autorizacion, String motivo) {
    Boleta b = new Boleta();
    b.setFecha(LocalDateTime.parse(fecha));
    b.setEstado(estado);
    b.setUsuarioCorreo("cliente@gmail.com");
    b.setClienteNombre("Javiera");
    b.setClienteApellidos("Rojas Fuentes");
    b.setEnvioCalle(calle);
    b.setEnvioDepartamento(depto);
    b.setEnvioRegion("RM");
    b.setEnvioComuna("Providencia");
    b.setEnvioIndicaciones(indicaciones);
    b.setPagoMedio("Tarjeta");
    b.setPagoUltimosDigitos(digitos);
    b.setPagoCodigoAutorizacion(autorizacion);
    b.setPagoMotivo(motivo);
    b.setTotal(0);
    return b;
  }

  private void detalle(Boleta boleta, ProductoRepository productos, String codigo,
      int precio, int cantidad) {
    Producto producto = productos.findById(codigo).orElseThrow();
    DetalleBoleta d = new DetalleBoleta();
    d.setProducto(producto);
    d.setNombreProducto(producto.getNombre());
    d.setPrecioUnitario(precio);
    d.setCantidad(cantidad);
    d.setSubtotal(precio * cantidad);
    boleta.agregarDetalle(d);
    boleta.setTotal(boleta.getTotal() + d.getSubtotal());
  }
}