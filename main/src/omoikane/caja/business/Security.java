package omoikane.caja.business;

import omoikane.sistema.Permisos;
import omoikane.sistema.Usuarios;

/**
 * Proyecto Omoikane
 * User: octavioruizcastillo
 * Date: 13/08/15
 * Time: 16:07
 */
public class Security {
    public static Boolean cancelacion() {
        //Autentica en memoria a un usuario (en una sesión interna y no en la sesión general)
        Boolean auth = Usuarios.autentifica(Permisos.PMA_CANCELACION);
        //Verifica si está efectuando auto-cancelación
        if(auth && Usuarios.getIDUltimoAutorizado() == Usuarios.getIDUsuarioActivo())
        {
            //Se está efectuando un autocancelación
            //Valida si tiene el privilegio para auto-cancelar
            if(Usuarios.ultimoUsuarioIdentificado.cerrojo(Permisos.PMA_AUTO_CANCELACION))
                auth = true; //Se autoriza auto-cancelar
            else
                auth = false; //Se deniega toda la cancelación
        }

        return auth;
    }
}
