
 /* Author Phesus        //////////////////////////////
 *  ORC,ACR             /////////////
 *                     /////////////
 *                    /////////////
 *                   /////////////
 * //////////////////////////////                   */

package omoikane.sistema

import static omoikane.sistema.Usuarios.*;

class Permisos {

    static Float PMA_ABRIRALMACEN      = SUPERVISOR
    static Float PMA_DETALLESALMACEN   = SUPERVISOR
    static Float PMA_MODIFICARALMACEN  = ADMINISTRADOR
    static Float PMA_ELIMINARALMACEN   = PROPIETARIO

    static Float PMA_ABRIRARTICULO       = CAJERO
    static Float PMA_DETALLESARTICULO    = CAPTURISTA
    static Float PMA_MODIFICARARTICULO   = CAPTURISTA
    static Float PMA_ELIMINARARTICULO    = GERENTE
    static Float PMA_LISTADEPRECIOSCRUD  = GERENTE
    static Float PMA_DEPARTAMENTOCRUD    = GERENTE

    static Float PMA_ABRIRCAJA       = SUPERVISOR
    static Float PMA_DETALLESCAJA    = SUPERVISOR
    static Float PMA_MODIFICARCAJA   = GERENTE
    static Float PMA_ELIMINARCAJA    = PROPIETARIO
    public static Float PMA_CANCELACION     = SUPERVISOR
    public static Float PMA_AUTO_CANCELACION= ADMINISTRADOR

    static Float PMA_ABRIRSUCURSAL   = SUPERVISOR

    static Float PMA_ABRIRUSUARIO       = GERENTE
    static Float PMA_DETALLESUSUARIO    = GERENTE
    static Float PMA_MODIFICARUSUARIO   = GERENTE
    static Float PMA_ELIMINARUSUARIO    = PROPIETARIO

    static Float PMA_ABRIRLINEA       = CAJERO
    static Float PMA_DETALLESLINEA    = CAJERO
    static Float PMA_MODIFICARLINEA   = SUPERVISOR
    static Float PMA_ELIMINARLINEA    = PROPIETARIO
    static Float PMA_DUAL             = PROPIETARIO

    static Float PMA_ABRIRGRUPO       = CAJERO
    static Float PMA_DETALLESGRUPO    = CAJERO
    static Float PMA_MODIFICARGRUPO   = SUPERVISOR
    static Float PMA_ELIMINARGRUPO    = PROPIETARIO

    static Float PMA_ABRIRCLIENTE       = CAJERO
    static Float PMA_DETALLESCLIENTE    = SUPERVISOR
    static Float PMA_MODIFICARCLIENTE   = SUPERVISOR
    static Float PMA_ELIMINARCLIENTE    = PROPIETARIO

    static Float PMA_ABRIRCORTES       = SUPERVISOR
    static Float PMA_DETALLESCORTES    = SUPERVISOR
    static Float PMA_MODIFICARCORTES   = ADMINISTRADOR
    static Float PMA_ELIMINARCORTES    = PROPIETARIO

    static Float PMA_ABRIRVENTAS       = SUPERVISOR
    static Float PMA_DETALLESVENTAS    = SUPERVISOR
    static Float PMA_MODIFICARVENTAS   = ADMINISTRADOR
    static Float PMA_ELIMINARVENTAS    = PROPIETARIO

    static Float PMA_ABRIRFACTURAS    = SUPERVISOR
    static Float PMA_CREARFACTURAS    = SUPERVISOR
    static Float PMA_CANCELARFACTURAS = SUPERVISOR

    static Float PMA_ABRIRMOVALMACEN       = SUPERVISOR
    static Float PMA_DETALLESMOVALMACEN    = SUPERVISOR
    static Float PMA_MODIFICARMOVALMACEN   = SUPERVISOR
    static Float PMA_ELIMINARMOVALMACEN    = PROPIETARIO
    static Float PMA_APLICARINVENTARIO     = ADMINISTRADOR
    static Float PMA_CREARINVENTARIO       = SUPERVISOR

    static Float PMA_REGISTRARTRASPASO     = SUPERVISOR
    static Float PMA_APLICARTRASPASO       = SUPERVISOR

    static Float PMA_REGISTRARCOMPRA       = SUPERVISOR
    static Float PMA_APLICARCOMPRA_A_STOCK = SUPERVISOR
    public static Float PMA_MARCAR_COMPRA_PAGADA  = ADMINISTRADOR

    static Float PMA_TOTALVENTA            = SUPERVISOR
    static Float PMA_TOTALVENTASUCURSAL    = SUPERVISOR
    static Float PMA_LANZARCAJA            = CAJERO
    static Float PMA_ABRIRCAJAS            = SUPERVISOR //INICIAR VENTAS EN CAJA
    static Float PMA_VENTAESPECIAL         = GERENTE
    static Float PMA_MOVIMIENTOSCAJA       = SUPERVISOR

    static Float PMA_IMPUESTOSCRUD         = GERENTE;

    public static Float PMA_CONFIGURACION         = SUPERVISOR;
    public static Float PMA_REPORTES              = SUPERVISOR;
    public static Float PMA_MEPRO                 = ADMINISTRADOR;
    public static Float PMA_ETIQUETAS             = CAPTURISTA;
    public static Float PMA_PROVEEDORES           = CAPTURISTA;

    public static Float PMA_ABRIRPACIENTES        = CAPTURISTA;
    public static Float PMA_CONSUMOPACIENTE       = CAJERO;
    public static Float PMA_CAJACLINICA           = CAPTURISTA;
    public static Float PMA_ARTEMISA_REPORTES     = CAPTURISTA;
    public static Float PMA_ARTEMISA_ARTICULOS    = CAPTURISTA;


}


