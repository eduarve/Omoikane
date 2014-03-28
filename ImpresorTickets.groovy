import groovy.sql.*

import omoikane.nadesicoiLegacy.Db;

/**
* Parámetros
*/
def fi = '2012-12-24'; //Fecha inicial
def ff = '2012-12-25'; //Fecha final
def soloLectura = true;

/**
* Programa
*/

def db   = Db.connect()

def cajas  = db.rows("SELECT id_caja FROM cajas")
def resultado = []; //Lista que contiene todas las ventas a corregir

for(def caja : cajas) {
    def cortes  = db.rows("SELECT * FROM cortes WHERE fecha_hora BETWEEN ? AND ? AND id_caja = ?", [fi, ff, caja.id_caja])
    for(def corte : cortes) {
        //sVentas = suma manual de ventas
        def sVentas = db.firstRow("""SELECT count(id_venta) as nVentas, sum(subtotal) as subtotal, sum(impuestos) as impuestos,
                                    sum(descuento) as descuento, sum(total) as total FROM ventas WHERE id_caja = ?
                                    AND fecha_hora BETWEEN ? AND ? AND completada = 1""", [caja.id_caja, corte.desde, corte.hasta])
        //println corte
        //println sVentas
        def difs = [
            'subtotal': sVentas.subtotal - corte.subtotal, 
            'impuestos': sVentas.impuestos - corte.impuestos,
            'descuentos': sVentas.descuento - corte.descuentos,
            'total': sVentas.total - corte.total
            ] //Diferencias
        println "Diferencias: "+difs
        def vCambios = [] //Ventas con cambios
        def cCambios = ['subtotal': 0, 'impuestos': 0, 'descuentos': 0, 'total': 0] //Total acumulado de cambios
        def ventas = db.rows("""SELECT * FROM ventas WHERE fecha_hora BETWEEN ? AND ? AND id_caja = ?""", [corte.desde, corte.hasta, caja.id_caja]);
        
        for(def venta : ventas) {
            def deleteRows = []
            def updateRows = []
            def ventasDetalles = db.rows("""SELECT * FROM ventas_detalles WHERE id_venta = ?""", [venta.id_venta])
            
            if(ventasDetalles.size() <= 2) { 
                //println "SKIP venta por renglones insuficientes: " + ventasDetalles.size(); 
                continue; 
            }
           
            //Recorrer detalles de venta para reducir renglones 
            for(int i = 2; i < ventasDetalles.size(); i++) {
                def vd = ventasDetalles[i];
                //Reducir eliminando
                if(vd.impuestos == 0 && vd.total <= (difs.total - cCambios.total) && vd.descuento == 0) {
                    //Eliminando renglon   
                    cCambios.total     += vd.total;
                    cCambios.impuestos += vd.impuestos; //Para comprobar que no se modificaron renglones con impuestos
                    cCambios.descuentos+= vd.descuento; //Para comprobar que no se modificaron renglones con descuentos
                    cCambios.subtotal  += vd.subtotal;
                    deleteRows << vd
                }
                //Reducir modificando, sólo para los últimos centavos, sólo se quita a productos de precio mayor a 10, sin impuestos, con cantidad 1 y sin descuento
                def difFaltante = difs.total - cCambios.total;
                if(difFaltante < 1 && difFaltante >= 0.01 && vd.total > difFaltante + 10 && vd.impuestos == 0 && vd.cantidad == 1 && vd.descuento == 0) {
                    vd.precio      -= difFaltante;
                    vd.subtotal    -= difFaltante;
                    vd.total       -= difFaltante;
                    cCambios.descuentos += vd.descuento; //Para comprobar que no se modificaron renglones con descuento
                    cCambios.impuestos  += vd.impuestos;  //Para comprobar que no se modificacion renglones con impuestos
                    cCambios.subtotal   += difFaltante;
                    cCambios.total      += difFaltante;
                    
                    updateRows << vd;
                }
            }
            
            venta.deleteRows = deleteRows;
            venta.updateRows = updateRows;
            
            resultado << venta;
        }
        
        println "----[Caja: ${caja.id_caja}]------"+cCambios+".vs."+difs
    }
}


println "# Ventas: "+resultado.size();
def nRenglonesDelete = 0;
def nRenglonesUpdate = 0;

for(def venta : resultado) {
    nRenglonesDelete += venta.deleteRows.size();
    nRenglonesUpdate += venta.updateRows.size();
    
    venta.deleteRows.each {
        query(db, "DELETE FROM ventas_detalles WHERE id_renglon = ${it.id_renglon};")
    }
    venta.updateRows.each {
        query(db, "UPDATE ventas_detalles SET subtotal = ${it.subtotal}, precio = ${it.precio}, total = ${it.total} WHERE id_renglon = ${it.id_renglon};")
    }
}

println "nRenglonesDelete: "+nRenglonesDelete
println "nRenglonesUpdate: "+nRenglonesUpdate

// Paso 2, resumar ventar corregidas
for(def venta : resultado) {
    def modVenta = [subtotal:0, descuento:0,impuestos:0,total:0,cambio:0]
    def ventaDetalles = db.rows("""SELECT * FROM ventas_detalles WHERE id_venta = ?""", [venta.id_venta])
    for(def vd : ventaDetalles) {
        def tmpDescuento    = vd.descuento * vd.cantidad;
        modVenta.total     += vd.total; 
        modVenta.descuento += tmpDescuento;  
        
        def tmpSubtotal     = vd.subtotal / ((vd.impuestos/100)+1)
        def tmpImpuestos    = tmpSubtotal * (vd.impuestos/100);
        
        modVenta.impuestos += tmpImpuestos
        modVenta.subtotal  += tmpSubtotal     
    
    }
    modVenta.subtotal = round(modVenta.subtotal)
    modVenta.descuento= round(modVenta.descuento)
    modVenta.impuestos= round(modVenta.impuestos);
    modVenta.total    = round(modVenta.total)  
    modVenta.cambio   = round(venta.efectivo - modVenta.total);
  
    //Comprobación con 5 centésimos de tolerancia
    assert Math.abs( modVenta.subtotal + modVenta.impuestos - modVenta.descuento - modVenta.total ) <= 0.05 ;
    assert Math.abs( venta.efectivo - modVenta.cambio       - modVenta.total ) <= 0.05 ;
    
    query( db, """UPDATE ventas 
                SET subtotal = ${modVenta.subtotal}, descuento = ${modVenta.descuento}, impuestos = ${modVenta.impuestos}, total = ${modVenta.total}, cambio = ${modVenta.cambio}
                WHERE id_venta = ${venta.id_venta};""" );
}             
             
db.close()

def round(def x) {
    return Math.floor(x * 10000) / 10000
}

def query(sql, String query) {
    def soloLectura = false;
    if(!soloLectura) {
        //sql.execute(query)
    } else {
        //println query;
    }
}