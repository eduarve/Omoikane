/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package omoikane.sistema.cortes

import omoikane.entities.Corte
import omoikane.entities.CorteImpuesto
import omoikane.nadesicoiLegacy.Consola
import omoikane.nadesicoiLegacy.Db
import omoikane.principal.Principal;
import omoikane.sistema.Nadesico;
import omoikane.sistema.Comprobantes

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.EntityTransaction;
/**
 *
 * @author Octavio
 */
public class EstrategiaEstandar extends EstrategiasCorte {

    @Override
    public def obtenerSumaCaja(IDCaja, horaAbierta, horaCerrada) {
        try {
            def ventas= sumaVentas(IDCaja, horaAbierta, horaCerrada)
            return ventas

        } catch(Exception e) {
            omoikane.sistema.Dialogos.error("Error al obtener suma para corte de caja", e)
        }

    }
    @Override
    public def hacerCorteCaja(IDCaja, IDAlmacen, subtotal, impuestos, descuento, total,
                               nVentas, desde, hasta, depositos, retiros, impuestoList)
    {
        try {
            def newCorte = addCorte(IDCaja, IDAlmacen, subtotal, impuestos, descuento, total,
                                   nVentas, desde, hasta, depositos, retiros, impuestoList)

            omoikane.sistema.Dialogos.lanzarAlerta(newCorte.mensaje)
            def comprobante = new Comprobantes()
            comprobante.Corte(newCorte.IDCorte)//imprimir ticket
            comprobante.probar()//imprimir ticket

            return newCorte

        } catch(Exception e) {
            omoikane.sistema.Dialogos.error("Error al generar corte de caja", e)
        }
    }
    @Override
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

    @Override
    public def obtenerSumaSucursal(IDAlmacen, IDCorte) {
        try {
            def serv  = Nadesico.conectar()
            def salida= serv.getSumaCorteSucursal(IDAlmacen, IDCorte)
            serv.desconectar()
            return salida
        } catch(Exception e) {
            omoikane.sistema.Dialogos.error("Error al obtener suma de corte de sucursal", e)
        }
    }

    @Override
    public def imprimirCorteSucursal(IDAlmacen, IDCorte) {
        try {
            def comprobante = new Comprobantes()
            (comprobante.CorteSucursal(IDAlmacen, IDCorte))//imprimir ticket
            (comprobante.probar())//* Aqui tambien mandar a imprimir*/
        } catch(Exception e) {
            omoikane.sistema.Dialogos.error("Error al imprimir corte de sucursal", e)
        }
    }

    def saluda() { println "Hola desde estrategia estándar"}

    public def sumaVentas ( IDCaja, desde, hasta ) {
        def salida = ""
        def db;
        try {
            db = Db.connect()
            def ventas = db.firstRow("""SELECT count(id_venta) as nVentas, sum(subtotal) as subtotal, sum(impuestos) as impuestos,
                                    sum(descuento) as descuento, sum(total) as total FROM ventas WHERE id_caja = ?
                                    AND fecha_hora >= ? AND fecha_hora <= ? AND completada = 1""", [IDCaja, desde, hasta])
            def depos  = db.firstRow("""SELECT sum(importe) as total FROM movimientos_cortes WHERE id_caja = ? AND momento >= ? AND momento <= ?
                                AND tipo = 'deposito'""",[IDCaja, desde, hasta])
            def retiros= db.firstRow("""SELECT sum(importe) as total FROM movimientos_cortes WHERE id_caja = ? AND momento >= ? AND momento <= ?
                                AND tipo = 'retiro'""",[IDCaja, desde, hasta])
            def impuestos = db.rows("""SELECT vdi.descripcion, sum(vdi.total) as importe
                                       FROM ventas v, ventas_detalles vd, ventas_detalles_impuestos vdi
                                       WHERE
                                                v.id_caja = ? AND v.fecha_hora >= ? AND v.fecha_hora <= ?
                                            AND
                                                v.id_venta = vd.id_venta
                                            AND
                                                vd.id_renglon = vdi.id_renglon
                                       GROUP BY vdi.impuestoId
                                    """, [IDCaja, desde, hasta])
            ventas.depositos = depos.total!=null?depos.total:0.0
            ventas.retiros   = retiros.total!=null?retiros.total:0.0
            ventas.impuestosList = impuestos!=null?impuestos:[];
            if(ventas == null) { ventas.total = 0; ventas.nVentas = 0; ventas.impuestos = 0; ventas.subtotal = 0; ventas.descuento = 0; }

            salida = ventas

        }
            catch(e)
        {
            throw new Exception("Error al consultar la suma de ventas", e)
        } finally {
            if(db != null) db.close();
        }
        salida
    }

    def addCorte ( IDCaja, IDAlmacen, subtotal, impuestos, descuentos, total, nVentas, desde, hasta , depositos , retiros, impuestosList ) {
        def db
        try {
            db = Db.connect()
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

                EntityTransaction tx = em.getTransaction();
                tx.begin();
                em.persist(corte);
                tx.commit();

                def IDCorte = corte.getId();

                return [IDCorte:IDCorte,mensaje:"Corte hecho."]
            } catch(Exception e) {
                //db.rollback()
                if(e.message.contains("Duplicate entry")) { return "Corte que intenta capturar ya exíste" }
                Consola.error("[Excepcion al addCorte:${e.message}]", e)
                throw new Exception("Error al enviar a la base de datos. Corte no se registró.", e)
            } finally {
                db.close()
            }
        } catch(e) {
            Consola.error("[Err addCorte:${e.message}]", e);
            throw new Exception("Error en la conexión del servidor con su base de datos")
        }
    }
}
