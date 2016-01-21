
 /* Author Phesus        //////////////////////////////
 *  ORC,ACR             /////////////
 *                     /////////////
 *                    /////////////
 *                   /////////////
 * //////////////////////////////                   */

package omoikane.sistema


import groovy.inspect.swingui.*
 import omoikane.principal.Principal
 import omoikane.sistema.huellas.ContextoFPSDK
 import omoikane.sistema.seguridad.AuthContext
 import phesus.configuratron.model.TipoImpresora

public class Config {
    def    prefs
    Config config
    
    Config(String file = Principal.configFilePath) {
       cargar(file)
       if(config == null ) {
           config = this
           defineAtributos()
       }
       config = this
    }

    def cargar (String file) {
        //def xmlTxt = getClass().getResourceAsStream("/omoikane/principal/config.xml")
        def xmlTxt = new File(file)
        def xml    = new groovy.util.XmlParser().parseText(xmlTxt.text)
        prefs = xml
    }

    Object propertyMissing(String name, Object args) {
        if(prefs."$name".size() > 0) {
            return prefs."$name"
        } else {
            throw new Exception("Falta parámetro $name en la configuración!") }
    }

    def defineAtributos() {
            Principal.sysAncho                = Integer.valueOf(config.resolucionPantalla.@ancho[0])
            Principal.sysAlto                 = Integer.valueOf(config.resolucionPantalla.@alto[0])
            Principal.CacheSTableAtras        = Integer.valueOf(config.cacheSTable.@atras[0])
            Principal.CacheSTableAdelante     = Integer.valueOf(config.cacheSTable.@adelante[0])
            Principal.fondoBlur               = Boolean.valueOf(config.fondoBlur[0].text())
            Principal.IDAlmacen               = Integer.valueOf(config.idAlmacen[0].text())
            Principal.IDCaja                  = Integer.valueOf(config.idCaja[0].text())
            Principal.puertoImpresion         = String .valueOf(config.puertoImpresion[0].text())
            Principal.impresoraActiva         = Boolean.valueOf(config.impresoraActiva[0].text())
            Principal.nombreImpresora         = String .valueOf(config.nombreImpresora[0].text())
            Principal.tipoImpresora           = TipoImpresora.valueOf( config.tipoImpresora[0].text() )
            Principal.URLMySQL                = String .valueOf(config.URLMySQL[0].text())
            Principal.loginJasper             = String .valueOf(config.loginJasper[0].text())
            Principal.passJasper              = String .valueOf(config.passJasper[0].text())
            Principal.scannerBaudRate         = Integer.valueOf(config.ScannerBaudRate[0].text())
            Principal.scannerPort             = String .valueOf(config.ScannerPort[0].text())
            Principal.scannerActivo           = Boolean.valueOf(config.scannerActivo[0].text())
            Principal.sdkFingerprint          = ContextoFPSDK.sdkValueOf(String.valueOf(config.fingerPrintSDK[0].text()))
            Principal.basculaActiva           = Boolean.valueOf(config.bascula.@activa[0])
            Principal.tipoCorte               = Integer.valueOf(config.tipoCorte[0].text())
            Principal.authType                = AuthContext.valueOf(String.valueOf(config.authType[0].text()))
            Principal.HA                      = Boolean.valueOf(config.HA[0].text())

            //Definición de propiedades opcionales
            assignProp(Principal.urlJasperserver)   { Principal.urlJasperserver = String.valueOf(config.URLJasperserver[0].text()); }
            assignProp(Principal.loginJasperserver) { Principal.loginJasperserver = String.valueOf(config.userJasperserver[0].text()); }
            assignProp(Principal.passJasperserver)  { Principal.passJasperserver = String.valueOf(config.passJasperserver[0].text()); }
            assignProp(Principal.multiSucursal)     { Principal.multiSucursal = Boolean.valueOf(config.multiSucursal[0].text()); }
            assignProp(Principal.isFlywayActive)    { Principal.isFlywayActive = Boolean.valueOf(config.isFlywayActive[0].text()); }
            assignProp(Principal.modoKiosko)        { Principal.modoKiosko = Boolean.valueOf(config.modoKiosko[0].text()); }
            assignProp(Principal.dropboxPath)       { Principal.dropboxPath = String.valueOf(config.dropboxPath[0].text()); }

            if(Principal.basculaActiva) {
                String cmd = ""
                String.valueOf(config.bascula.@weightCommand[0]).split(",").each { cmd += (it as Integer) as char }
                String mask = config.bascula?.@mask[0];
                Principal.driverBascula       = [
                        port: String.valueOf(config.bascula.@port[0]),
                        baud: Integer.valueOf(config.bascula.@baud[0]),
                        bits: String.valueOf(config.bascula.@bits[0]),
                        stopBits: String.valueOf(config.bascula.@stopBits[0]),
                        parity:   String.valueOf(config.bascula.@parity[0]),
                        stopChar: String.valueOf(config.bascula.@stopChar[0]),
                        mask: mask,

                        weightCommand: cmd
                ];
            }
        }
     def assignProp(def var, Closure expr) {
         try {
             //Intento cargar el valor del archivo de configuración, si es que existe
             expr();
         } catch(Exception e) {
             //No existe la propiedad en el archivos de configuración
             //Verifico si no existe una propiedad default
             if(var == null)
             //No existe una propiedad default así que lanzo la excepción
                 throw e;
         }
     }
}