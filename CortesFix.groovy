
import omoikane.sistema.cortes.*;
import java.text.*
import omoikane.principal.*

//**
//* Éste script está diseñado para generar un corte de caja previo a la sesión actual sin cerrar la caja
//**

EstrategiaEstandar ed = new EstrategiaEstandar();

NumberFormat mf = NumberFormat.getCurrencyInstance();
NumberFormat nf = NumberFormat.getNumberInstance();
nf.setMinimumFractionDigits(2);
nf.setMaximumFractionDigits(2);
nf.setGroupingUsed(true);
SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy @ hh:mm:ss a ");
Calendar         fecha= Calendar.getInstance()

horas = ["horaAbierta":"2015-08-07 6:00:00", "horaCerrada":"2015-08-07 23:59:59"];
def IDCaja = 3;
def idAlmacen = 1;
def cortar = true;

//def ventas= serv.sumaVentas(IDCaja, sdf.format(horas.horaAbierta), sdf.format(horas.horaCerrada))
def instanciaCortes = ContextoCorte.instanciar()
def ventas= instanciaCortes.obtenerSumaCaja(IDCaja, horas.horaAbierta, horas.horaCerrada)
def form  = Cortes.lanzarVentanaDetalles()

def desde = horas.horaAbierta
def hasta = horas.horaCerrada
form.setTxtDescuento     nf.format ( ventas.descuento )
form.setTxtDesde         (desde as String)
//form.setTxtFecha         (sdf2.format(fecha.getTime()) as String)
form.setTxtHasta         (hasta as String)
form.setTxtIDAlmacen     (idAlmacen as String)
form.setTxtIDCaja        (IDCaja as String)
form.setTxtImpuesto      nf.format ( ventas.impuestos )
form.setTxtNumeroVenta   (ventas.nVentas as String)
form.setTxtSubtotal      nf.format ( ventas.subtotal  )
form.setTxtDeposito      nf.format ( ventas.depositos )
form.setTxtRetiro        nf.format ( ventas.retiros   )
form.setTxtTotal         mf.format ( ventas.total     )
def dinero = ventas['total']-ventas.retiros+ventas.depositos
form.setTxtEfectivo      mf.format ( dinero )

desde = (java.sql.Timestamp) new java.sql.Timestamp( sdf.parse(desde).getTime() )
hasta = (java.sql.Timestamp) new java.sql.Timestamp( sdf.parse(hasta).getTime() )

if(cortar) {
    newCorte     = instanciaCortes.hacerCorteCaja(IDCaja, idAlmacen, ventas.subtotal, ventas.impuestos, ventas.descuento, ventas.total, ventas.nVentas, desde, hasta,ventas.depositos,ventas.retiros, ventas.impuestosList)
    
    form.ID=newCorte.IDCorte
    form.setTxtIDCorte ( newCorte.IDCorte as String )
}