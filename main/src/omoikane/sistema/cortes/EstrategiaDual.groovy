
package omoikane.sistema.cortes

import groovy.sql.Sql
import omoikane.entities.Corte
import omoikane.entities.CorteImpuesto
import omoikane.nadesicoiLegacy.Consola
import omoikane.nadesicoiLegacy.Db
import omoikane.principal.Principal;
import omoikane.sistema.Nadesico;
import omoikane.sistema.Comprobantes
import org.apache.log4j.Logger

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.EntityTransaction;
/**
 *
 * @author Octavio
 */

public class EstrategiaDual extends EstrategiasCorte {

    public def yaSumado = false
    public def laSuma   = null
    public Logger                logger                  = Logger.getLogger(EstrategiaDual.class);

    public def obtenerSumaCaja(IDCaja, horaAbierta, horaCerrada) {
        try {

            def ventas= sumaDual(IDCaja, horaAbierta, horaCerrada)

            yaSumado = true
            laSuma   = ventas
            return ventas

        } catch(Exception e) {
            omoikane.sistema.Dialogos.error("Error al obtener suma para corte de caja", e)
        }

    }
    public def hacerCorteCaja(IDCaja, IDAlmacen, subtotal, impuestos, descuento, total,
                               nVentas, desde, hasta, depositos, retiros, impuestosList)
    {
        try {
            if(!yaSumado) { throw new Exception("Error. No se realizó \"obtenerSumaCaja\"") }

                def newCorte = addCorteDual(IDCaja, IDAlmacen, subtotal, laSuma.subtotalDual, impuestos,
                    laSuma.impuestosDual, descuento, laSuma.descuentoDual, total,
                    laSuma.totalDual, nVentas, desde, hasta, depositos, retiros, impuestosList)


            omoikane.sistema.Dialogos.lanzarAlerta(newCorte.mensaje)
            def comprobante = new Comprobantes()
            comprobante.Corte(newCorte.IDCorte)                    //imprimir ticket
            comprobante.probar()
            Thread.sleep(6000);                                   // auch que feo parche >__<
            comprobante.CorteLegacy(newCorte.IDCorteDual, "cortes_dual") //imprimir corte dual
            comprobante.probar()//imprimir ticket

            return newCorte

        } catch(Exception e) {
            omoikane.sistema.Dialogos.error("Error al generar corte de caja", e)
        }
    }
    public def hacerCorteSucursal(IDAlmacen) {
        try {
            def serv  = Nadesico.conectar()
            def salida= serv.corteSucursal(IDAlmacen)
            serv.desconectar()
            return salida
        } catch(Exception e) {
            omoikane.sistema.Dialogos.error("Error al generar corte de sucursal", e)
        }
    }

    public def obtenerSumaSucursal(IDAlmacen, IDCorte) {
        try {
            def serv    = Nadesico.conectar()
            def salida  = serv.getSumaCorteSucursal(IDAlmacen, IDCorte)
            salida.dual = serv.getSumaCorteSucursalDual(IDAlmacen, IDCorte)

            serv.desconectar()
            return salida
        } catch(Exception e) {
            omoikane.sistema.Dialogos.error("Error al obtener suma de corte de sucursal", e)
        }
    }

    public def imprimirCorteSucursal(IDAlmacen, IDCorte) {
        try {
            def comprobante = new Comprobantes()

            def serv    = Nadesico.conectar()
            def data   = serv.getSumaCorteSucursalDual(IDAlmacen, IDCorte)
            data      += serv.getCorteSucursal(IDAlmacen, IDCorte)
            serv.desconectar()

            (comprobante.CorteSucursal(IDAlmacen, IDCorte)) //imprimir ticket
            (comprobante.probar())                          //* Aqui tambien mandar a imprimir*/
            Thread.sleep(6000);                            // auch que feo parche >__<

            (comprobante.CorteSucursalAvanzado(data))       //imprimir ticket
            (comprobante.probar())                          //* Aqui tambien mandar a imprimir*/
        } catch(Exception e) {
            omoikane.sistema.Dialogos.error("Error al imprimir corte de sucursal", e)
        }
    }

    def sumaDualFunc (IDCaja, desde, hasta) {    sumaDual(IDCaja, desde, hasta)    }

    def sumaDual ( IDCaja, desde, hasta ) {
        def salida = ""
        try {
            def db     = Db.connect()
            def ventas = db.firstRow("""SELECT count(id_venta) as nVentas, sum(subtotal) as subtotal, sum(impuestos) as impuestos,
                                    sum(descuento) as descuento, sum(total) as total FROM ventas WHERE id_caja = ?
                                    AND fecha_hora >= ? AND fecha_hora <= ?""", [IDCaja, desde, hasta])
            def ventasNoReg = db.firstRow("SELECT sum(ventas_detalles.subtotal) as subtotal,sum(ventas_detalles.impuestos) as impuestos,"+
                    "sum(ventas_detalles.descuento) as descuento,sum(ventas_detalles.total) as total FROM ventas,ventas_detalles,"+
                    "lineas_dual WHERE ventas.id_caja = ? AND ventas.fecha_hora >= ? AND ventas.fecha_hora <= ?"+
                    "and ventas.id_venta = ventas_detalles.id_venta and ventas.id_caja=ventas_detalles.id_caja and lineas_dual.id_linea=ventas_detalles.id_linea", [IDCaja, desde, hasta])
            def impuestos = db.rows("""SELECT vdi.descripcion, sum(vdi.total) as importe
                                       FROM
                                            ventas v
                                            JOIN ventas_detalles vd ON v.id_venta = vd.id_venta
                                            JOIN ventas_detalles_impuestos vdi ON vd.id_renglon = vdi.id_renglon
                                       WHERE
                                                vd.id_linea NOT IN (SELECT id_linea FROM lineas_dual)
                                            AND
                                                v.id_caja = ? AND v.fecha_hora >= ? AND v.fecha_hora <= ?
                                       GROUP BY vdi.impuestoId
                                    """, [IDCaja, desde, hasta])
            def impuestosDual = db.rows("""SELECT vdi.descripcion, sum(vdi.total) as importe
                                       FROM
                                            ventas v
                                            JOIN ventas_detalles vd ON v.id_venta = vd.id_venta
                                            JOIN ventas_detalles_impuestos vdi ON vd.id_renglon = vdi.id_renglon
                                       WHERE
                                                vd.id_linea IN (SELECT id_linea FROM lineas_dual)
                                            AND
                                                v.id_caja = ? AND v.fecha_hora >= ? AND v.fecha_hora <= ?
                                       GROUP BY vdi.impuestoId
                                    """, [IDCaja, desde, hasta])
            sanitizeVentas(ventas)
            sanitizeVentas(ventasNoReg)

            ventas.total=ventas.total-ventasNoReg.total
            ventas.subtotal=ventas.subtotal-ventasNoReg.subtotal
            ventas.descuento=ventas.descuento-ventasNoReg.descuento
            ventas.impuestos=ventas.impuestos-ventasNoReg.impuestos
            ventas.totalDual=ventasNoReg.total
            ventas.subtotalDual=ventasNoReg.subtotal
            ventas.descuentoDual=ventasNoReg.descuento
            ventas.impuestosDual=ventasNoReg.impuestos
            ventas.impuestosList = impuestos!=null?impuestos:[];
            def depos  = db.firstRow("""SELECT sum(importe) as total FROM movimientos_cortes WHERE id_caja = ? AND momento >= ? AND momento <= ?
                                AND tipo = 'deposito'""",[IDCaja, desde, hasta])

            def retiros= db.firstRow("""SELECT sum(importe) as total FROM movimientos_cortes WHERE id_caja = ? AND momento >= ? AND momento <= ?
                                AND tipo = 'retiro'""",[IDCaja, desde, hasta])
            ventas.depositos = depos.total!=null?depos.total:0.0
            ventas.retiros   = retiros.total!=null?retiros.total:0.0
            if(ventas.total == null) { ventas = 0 }
            salida = ventas


        } catch(e) { println "[Error]"; Consola.error("[Error: ${e.message}]",e); throw new Exception("Error al consultar la suma de ventas")}
        return salida
    }

    def sanitizeVentas(ventas) {
        ventas.total         = ventas.total     ?: 0;
        ventas.subtotal      = ventas.subtotal  ?: 0;
        ventas.descuento     = ventas.descuento ?: 0;
        ventas.impuestos     = ventas.impuestos ?: 0;
        ventas.totalDual     = ventas.total     ?: 0;
        ventas.subtotalDual  = ventas.subtotal  ?: 0;
        ventas.descuentoDual = ventas.descuento ?: 0;
        ventas.impuestosDual = ventas.impuestos ?: 0;
    }

    def addCorteDual ( IDCaja, IDAlmacen, subtotal,subtotalDual, impuestos,impuestoDual, descuentos,descuentoDual,total, totalDual, nVentas, desde, hasta , depositos , retiros, impuestosList ) {
        Sql db;
        EntityTransaction tx;
        try {
            db = Db.connect()
            db.connection.autoCommit = false
            try {
                def folios  = [ini:0,fin:0]

                folios.ini  = db.firstRow('SELECT min(folio) as fol FROM ventas WHERE fecha_hora >= ? and fecha_hora <= ? and id_caja = ? and id_almacen = ?', [desde, hasta, IDCaja, IDAlmacen]).fol
                folios.fin  = db.firstRow('SELECT max(folio) as fol FROM ventas WHERE fecha_hora >= ? and fecha_hora <= ? and id_caja = ? and id_almacen = ?', [desde, hasta, IDCaja, IDAlmacen]).fol

                //Persistir mediante JPA
                EntityManagerFactory emf = (EntityManagerFactory) Principal.applicationContext.getBean("entityManagerFactory");
                EntityManager em = emf.createEntityManager();

                Corte corte = new Corte();
                corte.setSubtotal( subtotal );
                corte.setImpuestos( impuestos );
                corte.setDescuentos( descuentos );
                corte.setTotal( total );
                corte.setnVentas( nVentas as Integer );
                corte.setDesde( desde );
                corte.setHasta( hasta );
                corte.setIdCaja( IDCaja );
                corte.setSucursalId( IDAlmacen as int );
                corte.setDepositos( depositos );
                corte.setRetiros( retiros );
                corte.setFolioInicial( folios.ini as Long);
                corte.setFolioFinal( folios.fin as Long);
                corte.setAbierto(false);

                List<CorteImpuesto> corteImpuestoList = []
                impuestosList.each {
                    CorteImpuesto corteImpuesto = new CorteImpuesto();
                    corteImpuesto.setDescripcion( it.descripcion )
                    corteImpuesto.setImporte( it.importe )
                    corteImpuestoList.add(corteImpuesto)
                }
                corte.setCorteImpuestoList( corteImpuestoList )

                tx = em.getTransaction();
                tx.begin();
                em.persist(corte);


                /*
                def IDCorte = db.executeInsert("INSERT INTO cortes SET subtotal = ?, impuestos = ?, descuentos = ?, total = ? "+
                        ", n_ventas = ?, desde = ?, hasta = ?, id_caja = ?, id_almacen = ?, depositos = ?, retiros = ?, folio_inicial = ?, folio_final = ?"
                        , [subtotal, impuestos, descuentos, total, nVentas, desde, hasta, IDCaja, IDAlmacen , depositos , retiros, folios.ini, folios.fin])
                        */
                def i = 0
                def IDCorteDual = db.executeInsert("INSERT INTO cortes_dual SET subtotal = ?, impuestos = ?, descuentos = ?, total = ? "+
                        ", n_ventas = ?, desde = ?, hasta = ?, id_caja = ?, id_almacen = ?, depositos = ?, retiros = ?, folio_inicial = ?, folio_final = ?"
                        , [subtotalDual, impuestoDual, descuentoDual,totalDual,i,desde,hasta,IDCaja, IDAlmacen,i,i, folios.ini, folios.fin])
                def IDCorte = corte.id
                IDCorteDual = IDCorteDual[0][0]
                db.commit();
                tx.commit();
                return [IDCorte:IDCorte,IDCorteDual:IDCorteDual,mensaje:"Corte hecho."]
            } catch(Exception e) {
                logger.error("Error generando corte.", e);
                db.rollback()
                tx.rollback();
                logger.info("Corte no se registró");
            } finally {
                db.close()
            }
        } catch(e) {
            Consola.error("[Error addCorte:${e.message}]", e);
            throw new Exception("Error en la conexión del servidor con su base de datos", e)
        }
    }

}
