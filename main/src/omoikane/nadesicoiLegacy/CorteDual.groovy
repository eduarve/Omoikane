/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package omoikane.nadesicoiLegacy

/**
 *
 * @author Adan
 */
import groovy.sql.*;


class CorteDual {

     static def asignarA(serv) {
        serv.getSumaCorteSucursalDual=getSumaCorteSucursalDual
        serv.getCorteWhereFrom=getCorteWhereFrom
    }

    static def getSumaCorteSucursalDual = { IDAlmacen, IDCorteSucursal ->
        def salida = ""
        try {
            def db       = Db.connect()
            def corteSuc = db.firstRow("SELECT desde, hasta FROM cortes_sucursal WHERE id_corte = ? AND id_almacen = ?", [IDCorteSucursal, IDAlmacen])

            if(corteSuc==null) { throw new Exception("Error al consultar la tabla cortes_sucursal, resultado de la consulta: "+corteSuc.inspect()) }

            salida       = db.firstRow("SELECT sum(subtotal) as subtotal, sum(impuestos) as impuestos, sum(descuentos) as descuentos, sum(total) as total,"+
            "sum(depositos) as depositos, sum(retiros)as retiros, sum(n_ventas) as n_ventas FROM cortes_dual WHERE id_almacen = ? AND desde >= ? AND hasta <= ?", [IDAlmacen, corteSuc.desde, corteSuc.hasta])
            salida.impuestoList = []
            db.close()
        } catch(e) { Consola.error("Error al consultar corte de sucursal", e); throw new Exception("Error al consultar corte de sucursal") }
        salida
    }

    static def getCorteWhereFrom = { where,tabla ->
        def salida = ""
        try {
        def db   = Db.connect()
        String query= "SELECT * FROM $tabla as cortes WHERE "+where

        def corte= db.firstRow(query)
            db.close()
            salida = corte
        } catch(e) { Consola.error("Error al consultar corte", e); throw new Exception("Error al consultar corte") }
        salida
    }


}

