 /* Author Phesus        //////////////////////////////
 *  ORC,ACR             /////////////
 *                     /////////////
 *                    /////////////
 *                   /////////////
 * //////////////////////////////                   */
 //iiiiiiiiiiiiiiiiiiiiiiiiiiiiii
 //iiiiiiiiiiiiiiiiiiiiiiiiiiiiii
 //iiiiiiiiiiiiiiiiiiiiiiiiiiiiii
 //iiiiiiiiiiiiiiiiiiiiiiiiiiiiii

package omoikane.sistema;


 import groovy.lang.GroovyObject;
 import omoikane.principal.*;

 import omoikane.repository.UsuarioRepo;
 import omoikane.sistema.seguridad.*;
 import org.apache.log4j.Logger;

 import omoikane.entities.Usuario;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Service;

 import omoikane.sistema.huellas.MiniLeerHuella;

 import java.util.LinkedHashMap;

 @Service
 public class Usuarios {


    @Autowired
    UsuarioRepo usuarioRepo;

    public Long getUserCount() {
        return usuarioRepo.countIt();
    }

    //public Usuario usrActivo = new Usuario();
    private static boolean autorizado = false;
    public static RespuestaLogin usuarioActivo;
    public static RespuestaLogin ultimoUsuarioIdentificado;
    public static Float CAJERO        = 0f;
    public static Float CAPTURISTA    = 0.5f;
    public static Float SUPERVISOR    = 1f;
    public static Float GERENTE       = 2f;
    public static Float ADMINISTRADOR = 3f;
    public static Float PROPIETARIO   = 4f;
    public static Logger logger        = Logger.getLogger(Usuarios.class);

    public static RespuestaLogin login() throws Exception {
        usuarioActivo = identificaPersona();
        return usuarioActivo;
    }

    public static void logout() {
        usuarioActivo = null;
    }

     public static Long getIDUsuarioActivo() {
        return usuarioActivo.getId();
    }

    public static Long getIDUltimoAutorizado() {
        return ultimoUsuarioIdentificado.getId();
    }

    public static void setIDUsuarioActivo(Long id) {
        if(usuarioActivo==null) {
            usuarioActivo = new RespuestaLogin(id, "", -1f);
        } else {
            usuarioActivo.setId( id );
        }
    }
    public static RespuestaLogin identificaPersona() {
            RespuestaLogin respuesta;

            Usuarios sysUsers = omoikane.principal.Principal.applicationContext.getBean(Usuarios.class);

            if(Principal.ASEGURADO && sysUsers.getUserCount() > 0) {
                Usuario usuario;

                try {
                    usuario = AuthContext.instanciar().authenticate();
                } catch (AuthException e) {
                    logger.error("No se puede verificar la identidad del usuario", e);
                    return new RespuestaLogin(0l, "Sin sesión", -1f);
                }

                if(usuario == null) {
                    respuesta = null;
                } else {

                    LinkedHashMap nadesicoUsuario = (LinkedHashMap) Nadesico.getUsuario(usuario.getId(), 1);
                    Float perfil;
                    try {
                        perfil = (Float) nadesicoUsuario.get("perfil");
                    } catch (Exception e) {
                        logger.info("Perfil del usuario es inválido");
                        perfil = -1f;
                    }
                    respuesta = new RespuestaLogin(usuario.getId(), usuario.getNombre(), perfil);
                }

            } else {
                respuesta = new RespuestaLogin(1l, "Instalador", 4f);
            }

            ultimoUsuarioIdentificado = respuesta;


            return respuesta;
    }
     //** Esta función sirve para dar un acceso especial a un usuario, por ejemplo para cancelaciones

    public static boolean autentifica(Float llave) {
        return identificaPersona().cerrojo(llave);
    }
    public static boolean cerrojo(Object llave) { return cerrojo((Float) llave); }

    public static boolean cerrojo(Float llave) {
        if(usuarioActivo == null || usuarioActivo.getId() == 0 || usuarioActivo.getNivelDeAcceso() < 0)
            return autentifica(llave);
        else
            return usuarioActivo.cerrojo(llave);
    }

    /* Borrar
    public static MiniLeerHuella leerHuella(){
        def escritorio   = omoikane.principal.Principal.escritorio.getFrameEscritorio()
        def fingerPrint  = new omoikane.formularios.WndLeerHuella(escritorio).getMiniLeerHuella()
        return fingerPrint
    }
    */

    /* Intento de pasar la DAL y parte de la BL a Omoikane
    public def checkFingerPrint(fingerP ) {
        try {
            Template        ref1, ref2;
            ref1            = new Template();
            ref2            = new Template();
            MatchingContext checador = null;
            def autorizado  = false, respuesta = null

            try {
                checador = new MatchingContext();
                ref1.setData(fingerP);

            } catch(Exception grje) {
                logger.error(Level.SEVERE, "Error al convertir hex a bytes", grje);
                grje.printStackTrace();
                throw grje
            }

            def data = [:], usr_suc, mC = null

            try {
                    def usuarios = usuarioRepo.findAll();
                    try {
                        for(Usuario usuario : usuarios)
                        {
                            data['ID']       = usuario.id;
                            data['nombre']   = usuario.nombre;

                            mC = new MatchingContext()
                            ref2.setData(usuario.huella1);
                            if(ref2!=null)  {autorizado = (mC.identify(ref1, ref2));
                            if(!autorizado) {ref2.setData(usuario.huella2);  autorizado = (mC.identify(ref1, ref2)); }
                            if(!autorizado) {ref2.setData(usuario.huella3);  autorizado = (mC.identify(ref1, ref2)); }
                            }
                            if(mC != null)
                            {
                                mC.destroy();
                            }
                            if(autorizado) {
                                autorizado = data;
                                throw new Exception("BREAK")
                            }
                        }
                    } catch(Exception ex)  {
                        if(ex.message != "BREAK") {
                            logger.error("Error al autenticar: ${ex.message}", ex);
                        }
                    }

                    respuesta = (autorizado)?data:0
                    return respuesta

            } catch(Exception exc) {
                logger.error("Error al autenticar: ${exc.message}", exc)
                throw exc
            }
        } catch(e) {
          logger.error("Error al identificar usuario", e);
          throw e
        }
    }
    */
}