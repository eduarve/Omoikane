
 /* Author Phesus        //////////////////////////////
 *  ORC,ACR             /////////////
 *                     /////////////
 *                    /////////////
 *                   /////////////
 * //////////////////////////////                   */

package omoikane.sistema

 import omoikane.nadesicoiLegacy.NadesicoLegacy

 @Deprecated
 class Nadesico {
    static def nadesicoLegacy = null;

    static def conectar() {
        if(nadesicoLegacy == null) { nadesicoLegacy = new NadesicoLegacy(); }
        return new Nadesico();
    }

    @Deprecated
    static def getUsuario(id, almacen) {
        return omoikane.nadesicoiLegacy.Usuarios.getUsuario(id, almacen);
    }

    Object methodMissing(String name, Object args) {
            return nadesicoLegacy.invokeMethod("$name", args)
    }
    def desconectar() {

    }
    protected void finalize() throws Throwable {
        try {
            //desconectar()
        } finally {
            super.finalize()
        }
    }

}

