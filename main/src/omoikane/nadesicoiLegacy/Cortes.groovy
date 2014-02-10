/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package omoikane.nadesicoiLegacy

import groovy.sql.*
import omoikane.entities.Corte
import omoikane.entities.CorteImpuesto
import omoikane.principal.Principal

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.EntityTransaction;

/**
 *
 * @author OCTAVIOOO!!
 */
class Cortes {
    static def asignarA(serv) {
        serv.getCorteWhere        = getCorteWhere
        serv.getCorte             = getCorte
        serv.getSumaCorteSucursal = getSumaCorteSucursal
        serv.getCorteSucursal     = getCorteSucursal
    }

    static def getCorteWhere = { where ->
        def salida = ""
        try {
        def db   = Db.connect()
        def where2 = "id_caja = 1 AND desde = '2009-01-08 23:35:35' AND hasta = '2009-01-10 09:22:48'"
        String query= "SELECT * FROM cortes WHERE "+where

        def corte= db.firstRow(query)
            db.close()
            salida = corte
        } catch(e) { Consola.error("Error al consultar corte", e); throw new Exception("Error al consultar corte") }
        salida
    }
    static def getCorte = { id ->
        getCorteWhere(" cortes.id_corte = "+id)
    }

    static def getCorteSucursal = { IDAlmacen, IDCorteSucursal ->
        def corteSuc = ""
        try {
            def db       = Db.connect()
            corteSuc = db.firstRow("SELECT desde, hasta FROM cortes_sucursal WHERE id_corte = ? AND id_almacen = ?", [IDCorteSucursal, IDAlmacen])

            if(corteSuc==null) { throw new Exception("Error al consultar la tabla cortes_sucursal, resultado de la consulta: "+corteSuc.inspect()) }
        } catch(e) { Consola.error("Error al consultar corte de sucursal", e); throw new Exception("Error al consultar corte de sucursal") }
        corteSuc
    }

    static def getSumaCorteSucursal = { IDAlmacen, IDCorteSucursal ->
        def salida = ""
        try {
            def db       = Db.connect()
            def corteSuc = db.firstRow("SELECT desde, hasta FROM cortes_sucursal WHERE id_corte = ? AND id_almacen = ?", [IDCorteSucursal, IDAlmacen])
            
            if(corteSuc==null) { throw new Exception("Error al consultar la tabla cortes_sucursal, resultado de la consulta: "+corteSuc.inspect()) }

            salida       = db.firstRow("SELECT sum(subtotal) as subtotal, sum(impuestos) as impuestos, sum(descuentos) as descuentos, sum(total) as total, sum(depositos) as depositos, sum(retiros) as retiros, sum(n_ventas) as n_ventas FROM cortes "+
                                       "WHERE id_almacen = ? AND desde >= ? AND hasta <= ?", [IDAlmacen, corteSuc.desde, corteSuc.hasta])
            salida.impuestoList = db.rows("""
                    SELECT ci.descripcion, sum(ci.importe) as importe
                    FROM cortes c JOIN corte_impuesto ci ON c.id_corte = ci.id_corte
                    WHERE
                        desde >= ? AND hasta <= ?
                    GROUP BY ci.descripcion""", [corteSuc.desde, corteSuc.hasta])

            db.close()
        } catch(e) { Consola.error("Error al consultar corte de sucursal", e); throw new Exception("Error al consultar corte de sucursal") }
        salida
    }
}

