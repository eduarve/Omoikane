/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package omoikane.principal

import javafx.application.Application
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import omoikane.configuracion.ConfiguratorAppManager
import omoikane.formularios.CatalogoArticulos
import omoikane.ha.DisconnectionHandler
import omoikane.repository.CajaRepo
import omoikane.repository.UsuarioRepo
import omoikane.sistema.*
import omoikane.sistema.Usuarios as SisUsuarios

import omoikane.sistema.cortes.ContextoCorte
import org.apache.log4j.Logger
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.context.ApplicationContext
import omoikane.exceptions.UEHandler
import omoikane.sistema.huellas.ContextoFPSDK.SDK
import omoikane.sistema.huellas.HuellasCache
import omoikane.sistema.seguridad.AuthContext
import phesus.configuratron.ConfiguratorApp

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.swing.Action
import javax.swing.JFrame
import javax.swing.JInternalFrame
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.beans.PropertyChangeListener
import java.util.concurrent.CountDownLatch

/**
 * ////////////////////////////////////////////////////////////////////////////////////////////
 * ////////////////////////////////////////////////////////////////////////////////////////////
 * ////////////////////////////////////////////////////////////////////////////////////////////
 *
 * 
 *  * @author Octavio
 */
public class Principal {
        static Escritorio escritorio
        static MenuPrincipal        menuPrincipal;
        static def        config
        private static def splash;
        public static int                   IDAlmacen
        public static int                   IDCaja
        public static int                   sysAncho
        public static int                   sysAlto
        public static int                   CacheSTableAtras
        public static int                   CacheSTableAdelante
        public static boolean               fondoBlur
        public static String                puertoImpresion
        public static boolean               impresoraActiva
        public static boolean               scannerActivo
        public static boolean               basculaActiva
        public static HashMap               driverBascula
        public static String                URLMySQL
        public static String                loginJasper
        public static String                passJasper
        public static int                   scannerBaudRate
        public static String                scannerPort
        public static SDK                   sdkFingerprint = SDK.ONETOUCH;
        public static ShutdownHandler       shutdownHandler
        public static def                   toFinalizeTracker       = [:]
        public static def                   scanMan
        public static def                   tipoCorte               = ContextoCorte.TIPO_DUAL
        final  static def                   ASEGURADO               = true
        final  static def                   SHOW_UNHANDLED_EXCEPTIONS = false
        public static Logger                logger                  = Logger.getLogger(Principal.class);
        public static ApplicationContext    applicationContext;
        public static final Boolean         DEBUG                   = false;
        public static final String          VERSION                 = "4.1.1";
        public static  Boolean              HA                      = false; //Características de alta disponibilidad
        public static def                   authType                = AuthContext.AuthType.NIP;

    public static void main(args)
        {
            logger.trace( "Prueba de codificación: áéíóú" )
            iniciar()
        }
        public static ApplicationContext getContext() {
            return applicationContext;
        }
        static iniciar()
        {
            try {

                logger.trace("Iniciando sistema. Versión " + VERSION);
                configExceptions()

                //Inicializa el hilo que muestra el splash

                Thread.start {
                    splash = new Splash()
                    splash.iniciar()
                }

                Locale.setDefault(Locale.US);

                shutdownHandler = new ShutdownHandler()
                Runtime.getRuntime().addShutdownHook(shutdownHandler);

                logger.trace("Cargando configuración...")
                config = new omoikane.sistema.Config()

                logger.trace("Cargando ApplicationContext...")
                applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");

                /*
                    Verifico la conexión a la base de datos, si no hay se procede al comportamiento para casos sin conexión
                 */
                logger.trace("Verificando conexión con BD...")
                try {
                    checkDatabaseAvailability();
                } catch( Exception e ) {
                    disconnectedBehavior();
                    return;
                }

                logger.trace("Cargando huellas en caché...")
                applicationContext.getBean(HuellasCache.class).getHuellasBD();

                logger.trace("Inicializando JavaFX")
                initJavaFx()

                logger.trace("Inicializando escritorio...");
                //Herramientas.utilImpresionCortes()
                //System.exit(0)
                escritorio = new Escritorio()
                escritorio.iniciar()
                logger.trace("Inicializando menú principal...")
                menuPrincipal = new MenuPrincipal()
                splash.detener()

                iniciarSesion()
                menuPrincipal.iniciar()

                if(scannerActivo){
                    scanMan = new DefaultScanMan()
                    try {
                        println "comienza intento de conexi?n"
                        scanMan.connect(Principal.scannerPort, Principal.scannerBaudRate)
                        println "fin intento de conexi?n"
                    } catch(Exception ex2) { Dialogos.error(ex2.getMessage(), ex2) }

                    toFinalizeTracker.put("scanMan", "")

                }

            } catch(e) {
                //Dialogos.lanzarDialogoError(null, "Al iniciar aplicación: ${e.message}", Herramientas.getStackTraceString(e))
                logger.error("Al iniciar aplicación: ${e.message}".toString(), e)
                System.exit(1);
            }
            ///////////////////////

        }

    /**
     * Éste método desribe el comportamiento a seguir para el arranque del programa sin conexión a la BD.
     */
    static void disconnectedBehavior() {
        splash.detener();

        String[] options = [ "Abrir catálogo para emergencias", "Abrir configuración", "Cerrar aplicación" ];
        int decision = JOptionPane.showOptionDialog(null, "Servidor caído ¿Que hacer?", "Servidor caído",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

        switch(decision) {
            case 0:
                iniciarSistemaOffline()
                break;
            case 1:
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        omoikane.principal.Principal.setConfig(new omoikane.sistema.Config());
                        omoikane.principal.Principal.applicationContext = new ClassPathXmlApplicationContext("applicationContext-test.xml");

                        ConfiguratorAppManager manager = new ConfiguratorAppManager();
                        JInternalFrame frame = manager.startJFXConfigurator();
                        JFrame jFrame = new JFrame("Configurator");
                        jFrame.setContentPane(frame);
                        jFrame.setVisible(true);
                        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    }
                });

                break;
            case 2:
                System.exit(0);
                break;
        }

    }

    static void iniciarSistemaOffline() {

        escritorio = new Escritorio()
        escritorio.iniciar()

        CatalogoArticulos cat = new DisconnectionHandler().handle();
        cat.getBtnCerrar().removeAll();

        cat.getBtnCerrar().addActionListener(new ActionListener() {

            @Override
            void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        })
    }

    static void checkDatabaseAvailability() throws Exception {

        applicationContext.getBean(CajaRepo.class).count();

    }

    static def iniciarSesion() throws Exception {
        try{
        while(!SisUsuarios.login().cerrojo(SisUsuarios.CAJERO)) {}  // Aquí se detendrá el programa a esperar login
        escritorio.setNombreUsuario(SisUsuarios.usuarioActivo.nombre)
        } catch(e) { Dialogos.lanzarDialogoError(null, "Error al iniciar sesión en ciclo de huella: "+e.getMessage(), Herramientas.getStackTraceString(e)) }
    }

    static def cerrarSesion(){
        try{
                SisUsuarios.logout()
                escritorio.setNombreUsuario("Sin Sesión")
                Principal.menuPrincipal = new MenuPrincipal()
                Thread.start(){
                Principal.iniciarSesion()
                Principal.menuPrincipal.iniciar()
                }
        } catch(e) { Dialogos.lanzarDialogoError(null, "Error al cerrar secion ciclo de huella", Herramientas.getStackTraceString(e)) }
    }

    static def configExceptions() {
        if(SHOW_UNHANDLED_EXCEPTIONS) {
            Thread.setDefaultUncaughtExceptionHandler(new UEHandler());
        }
        //Logger.getRootLogger().addAppender(new CEAppender());
    }

    static def initJavaFx() {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JFXPanel(); // initializes JavaFX environment
                latch.countDown();
            }
        });
        latch.await();
        Platform.setImplicitExit(false);
    }
}
