
 /* Author Phesus        //////////////////////////////
 *  ORC,ACR             /////////////
 *                     /////////////
 *                    /////////////
 *                   /////////////
 * //////////////////////////////                   */

package omoikane.sistema

 import groovy.sql.Sql
 import omoikane.entities.Corte
 import omoikane.entities.VentaDetalleImpuesto
 import omoikane.nadesicoiLegacy.Db
 import omoikane.principal.Principal
 import omoikane.repository.VentaRepo

 import javax.persistence.EntityManagerFactory
 import java.io.*;
 import groovy.text.GStringTemplateEngine

 import java.text.NumberFormat
 import java.text.SimpleDateFormat
 import groovy.inspect.swingui.*
 import javax.persistence.PersistenceContext
 import javax.persistence.EntityManager
 import omoikane.entities.LegacyVenta
 import omoikane.entities.Caja
 import omoikane.entities.Usuario
 import omoikane.entities.LegacyVentaDetalle
 import javax.persistence.TypedQuery
 import org.springframework.beans.factory.annotation.Autowired
 import omoikane.repository.ProductoRepo
 import org.apache.log4j.Logger

 class Comprobantes {

    def data
    def generado
    def impresora = omoikane.principal.Principal.impresoraActiva
    def protocolo = omoikane.principal.Principal.puertoImpresion
    public Logger logger = Logger.getLogger(Comprobantes.class);

     @PersistenceContext
     EntityManager entityManager;

     @Autowired
     ProductoRepo productoRepo;

     @Autowired
     VentaRepo ventaRepo;

    /**
     * Método que utiliza hibernate para acceder a los datos de la venta en lugar de nadesico (versión antigua ticket())
     */
    public void ticketVenta( LegacyVenta lv, Long idVenta ) {
        LegacyVenta venta = lv;
        data              = venta.properties;
        data.date         = data.fechaHora
        data.caja         = entityManager.find(Caja.class, data.idCaja as Integer).properties;
        data.usuario      = entityManager.find(Usuario.class, data.idUsuario as Long);
        if(data.usuario == null) { throw new Exception("Usuario inválido"); }
        data.usuario = data.usuario.properties;

        data.detalles   = []
        data.id_almacen = data.idAlmacen;
        data.id_caja    = data.idCaja;
        data.id_venta   = venta.getId();

        List<LegacyVentaDetalle> detalles = venta.getItems();
        def impuestosMap = [:]
        for( LegacyVentaDetalle lvd : detalles) {
            final temp = [:];
            omoikane.producto.Articulo art = productoRepo.readByPrimaryKey( lvd.idArticulo as Long );
            temp             = lvd.properties;
            temp.descripcion = art.getDescripcion();
            for(VentaDetalleImpuesto vim : lvd.getVentaDetalleImpuestos()) {
                if(impuestosMap[vim.getDescripcion()]==null) impuestosMap[vim.getDescripcion()] = [importe:0d];
                impuestosMap[vim.getDescripcion()].importe += vim.total.doubleValue();
            }
            data.detalles << temp;
        }
        data.impuestosMap = impuestosMap;
        generado          = generarTicket()
    }

    def ticket(IDAlmacen, Long IDVenta) {
        LegacyVenta lv = ventaRepo.readByPrimaryKey(IDVenta as Long);
        return ticketVenta( lv, IDVenta as Long )
    }

    def Corte(ID) {
        return Corte(ID, "cortes")
    }
    def Corte(ID, tabla) {
        def serv = new Nadesico().conectar()
        Sql db = Db.connect();
        try {
            //TODO ********* Sustituir getCorteWhereFrom por una consulta con JPA de la entidad corte **********
            //Cargar mediante JPA
            EntityManagerFactory emf = (EntityManagerFactory) Principal.applicationContext.getBean("entityManagerFactory");
            EntityManager em = emf.createEntityManager();
            Corte corte        = (Corte)em.find(Corte.class, ID)
            data = [:]

            //<-- Mapeo de atributos para compatibilidad con código legado
            data.id_caja       = corte.idCaja;
            data.id_almacen    = corte.sucursalId;
            data.id_corte      = corte.id;
            data.n_ventas      = corte.nVentas;
            data.folio_inicial = corte.folioInicial;
            data.folio_final   = corte.folioFinal;
            data.abierto       = corte.abierto;
            data.impuestoList  = corte.corteImpuestoList;
            data.desde         = corte.desde
            data.hasta         = corte.hasta
            data.depositos     = corte.depositos
            data.descuentos    = corte.descuentos
            data.impuestos     = corte.impuestos
            data.retiros       = corte.retiros
            data.subtotal      = corte.subtotal
            data.total         = corte.total
            data.fecha_hora    = corte.fechaHora
            //Fin mapeo -->

            //data             = serv.getCorteWhereFrom(" cortes.id_corte=$ID", tabla)
            data.movsCaja    = db.rows("SELECT tipo, concepto, importe FROM movimientos_cortes WHERE id_caja = ? AND momento >= ? AND momento <= ?",[data.id_caja, data.desde, data.hasta])
            data.caja        = serv.getCaja(data.id_caja)
            data.leyenda     = "C O R T E   D E   C A J A"
            data.isCorteCaja = true
            generado         = generarCorte(ID)
        } catch(e) {
            throw e
        } finally {
            serv.desconectar()
            db?.close();
        }
    }

    def CorteLegacy(ID, tabla) {
         def serv = new Nadesico().conectar()
         Sql db = Db.connect();
         try {
             data             = serv.getCorteWhereFrom(" cortes.id_corte=$ID", tabla)
             data.movsCaja    = db.rows("SELECT tipo, concepto, importe FROM movimientos_cortes WHERE id_caja = ? AND momento >= ? AND momento <= ?",[data.id_caja, data.desde, data.hasta])
             data.caja        = serv.getCaja(data.id_caja)
             data.leyenda     = "C O R T E   D E   C A J A"
             data.isCorteCaja = true
             data.impuestoList= []
             generado         = generarCorte(ID)
         } catch(e) {
             throw e
         } finally {
             serv.desconectar()
             db?.close();
         }
     }

    def CorteSucursal(IDAlmacen, IDCorte) {
        def serv = new Nadesico().conectar()
        try {
            data   = serv.getSumaCorteSucursal(IDAlmacen, IDCorte)
            data   += serv.getCorteSucursal(IDAlmacen, IDCorte)
            return CorteSucursalAvanzado(data)
        } catch(e) {
            omoikane.sistema.Dialogos.error("Ha ocurrido un error al realizar comprobante de corte de sucursal", e)
        }finally {
            serv.desconectar()
        }
    }

    def CorteSucursalAvanzado(data) {
        try {
            data.descripcion = "CORTE DEL DIA"
            data.leyenda= "C O R T E   D E   S U C U R S A L"
            this.data = data
            data.isCorteCaja = false
            generado = generarCorteSucursal()

        } catch(Exception e) {
            omoikane.sistema.Dialogos.error("Ha ocurrido un error al realizar comprobante de corte de sucursal", e)
        }
    }

    def movimiento(ID,tipo) {
        def serv = new Nadesico().conectar()
        def temp
        try {
            data         = serv.getDoMovimiento(ID,tipo)
            temp         = serv.getCaja(data.id_caja)
            data.caja    =temp.descripcion
            temp         = serv.getUsuario(data.id_cajero,data.id_almacen)
            data.cajero = temp.nombre
            temp         = serv.getUsuario(data.id_usuario,data.id_almacen)
            data.usuario = temp.nombre
            generado = generarMovimiento()
        } catch(e) {
            throw e
        }finally {
        serv.desconectar()
        }
    }

    def generarTicket() {
        def plantilla = new File("Plantillas/FormatoTicket.txt").getText('UTF-8') as String
        //def plantilla   = getClass().getResourceAsStream("/omoikane/reportes/FormatoTicket.txt").getText('UTF-8') as String
        def sdfFecha    = new SimpleDateFormat("dd-MM-yyyy")
        def sdfHora     = new SimpleDateFormat("hh:mm a")
        def binding     = data
        binding.fecha   = sdfFecha.format(data.date)
        binding.hora    = sdfHora.format(data.date)
        binding.folio   = "${data.id_almacen}-${data.id_caja}-${data.folio}"
		binding.idFolio = "${data.id_almacen}-${data.id_caja}-${data.id_venta}"
        binding.cajero= data.usuario.nombre
        def engine = new GStringTemplateEngine()
        def template = engine.createTemplate(plantilla).make(binding)
        template.toString()
    }

    def generarCorte(id) {
        def plantilla = new File("Plantillas/FormatoCorte.txt").getText('UTF-8') as String
        //def plantilla = getClass().getResourceAsStream("/omoikane/reportes/FormatoCorte.txt").getText('UTF-8') as String
        def sdfFecha = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a")
        def sdfHora  = new SimpleDateFormat("hh:mm a")
        def sdfDia  = new SimpleDateFormat("EEEEEEEEEE dd-MMM-yyyy  ")
        def binding = data
        binding.fecha        = sdfFecha.format(data.fecha_hora)
        binding.descripcion  = data.caja.descripcion
        binding.dia          = sdfDia.format(data.desde)
        binding.desde        = sdfHora.format(data.desde)
        binding.hasta        = sdfHora.format(data.hasta)
		binding.id_caja      = data.id_caja
		binding.id_almacen  = data.id_almacen
		binding.prefijoFolio = data.id_almacen+"-"+data.id_caja
		binding.folioInicial = binding.prefijoFolio + "-" + data.folio_inicial
		binding.folioFinal   = binding.prefijoFolio + "-" + data.folio_final
		binding.folios       = "Folios desde ${binding.folioInicial} hasta ${binding.folioFinal}"
        binding.depositos    = binding.depositos ?: 0.0f
        binding.retiros      = binding.retiros ?: 0.0f

        binding.devoluciones = 0.0f
        def engine = new GStringTemplateEngine()
        def template = engine.createTemplate(plantilla).make(binding)
        template.toString()
    }

    def generarCorteSucursal() {
        def plantilla = new File("Plantillas/FormatoCorte.txt").getText('UTF-8') as String
        //def plantilla = getClass().getResourceAsStream("/omoikane/reportes/FormatoCorte.txt").getText('UTF-8') as String
        def sdfFecha = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a")
        def sdfHora  = new SimpleDateFormat("hh:mm a")
        def sdfDia  = new SimpleDateFormat("EEEEEEEEEE dd-MMM-yyyy  ")
        def binding = data
        binding.fecha  = sdfFecha.format(data.hasta)
        binding.dia    = sdfDia.format(data.desde)
        binding.desde  = sdfHora.format(data.desde)
        binding.hasta  = sdfHora.format(data.hasta)
        binding.devoluciones = 0.0f
        binding.depositos = 0.0f
        binding.retiros = 0.0f

		binding.folios       = ""
        def engine = new GStringTemplateEngine()
        def template = engine.createTemplate(plantilla).make(binding)
        template.toString()
    }

    def generarMovimiento() {
        def plantilla = new File("Plantillas/FormatoMovimiento.txt").getText('UTF-8') as String
        //def plantilla = getClass().getResourceAsStream("/omoikane/reportes/FormatoMovimiento.txt").getText('UTF-8') as String
        def sdfFecha = new SimpleDateFormat("dd-MM-yyyy")
        def sdfHora  = new SimpleDateFormat("hh:mm a")
        def binding = data
        binding.fecha = sdfFecha.format(data.momento)
        binding.hora  = sdfHora.format(data.momento)
        binding.folio = "${data.id_almacen}-${data.id_caja}-${data.id}"
        def engine = new GStringTemplateEngine()
        def template = engine.createTemplate(plantilla).make(binding)
        template.toString()
    }

    def setComprobante(String filePath, Map data) {
        def plantilla = new File(filePath).getText('UTF-8') as String
        def binding = data;
        def engine = new GStringTemplateEngine()
        def template = engine.createTemplate(plantilla).make(binding)
        generado = template;
    }
	/* 
	 * Imprime el comprobante generado, alias de probar() 
	 */
    def imprimir() { return probar() }
    def probar() {
        Thread.start {
            if (impresora)
            {
            try {
                FileOutputStream os = new FileOutputStream("$protocolo");
                PrintStream ps = new PrintStream(os);
                ps.println(generado);
                ps.close();
            } catch (FileNotFoundException fnf) { Dialogos.error("Error al imprimir al puerto $protocolo", fnf); }
            }
            else
            {
                try {
                    //logger.debug(generado)
                    println generado
                } catch (e) { logger.info("Error al mandar a la consola"); }
            }
        }
    }

    public void abrirCajon() {
        generado = "" + (27 as char)+(112 as char)+(0 as char)+(25 as char)+(250 as char);

        imprimir();
    }

}
  
