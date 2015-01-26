package omoikane.caja.handlers;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import omoikane.caja.business.plugins.CirculoSaludPlugin;
import omoikane.caja.business.plugins.PluginManager;
import omoikane.caja.business.plugins.VentaEspecialPlugin;
import omoikane.caja.nadroCDS.NadroCDSException;
import omoikane.caja.nadroCDS.ValidarTarjetaCDSController;
import omoikane.caja.presentation.CajaController;
import omoikane.principal.Principal;
import omoikane.sistema.SceneOverloaded;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 23/11/13
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */
public class ValidarTarjetaCDSHandler extends ICajaEventHandler {

    public static final Logger logger = Logger.getLogger(ValidarTarjetaCDSHandler.class);

    public ValidarTarjetaCDSHandler(CajaController controller) {
        super(controller);
    }

    @Override
    public void handle(Event event) {
        lanzarVentana();
    }

    private void lanzarVentana() {
        try {
            ApplicationContext context = Principal.applicationContext;

            Stage ventana = new Stage();

            ventana.initModality(Modality.APPLICATION_MODAL);
            ventana.setAlwaysOnTop(true);
            ventana.setTitle("Tarjeta Círculo de la Salud");

            CirculoSaludPlugin plugin = registrarPlugin();

            SceneOverloaded scene = (SceneOverloaded) context.getBean("validarTarjetaCDSView");
            ventana.setScene(scene);
            ValidarTarjetaCDSController controller = ((ValidarTarjetaCDSController) scene.getController());
            controller.setCdsService(plugin.getCdsService());

            controller.getCerrarBtn().setOnAction(
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            plugin.setTarjeta(controller.getTarjeta());
                            ventana.close();
                        }
                    }
            );
            ventana.show();
        } catch(NadroCDSException nde) {
            logger.error("No es posible iniciar sesión o conexión con Nadro", nde);
        }

    }

    private CirculoSaludPlugin registrarPlugin() throws NadroCDSException {
        PluginManager pm = getController().getCajaLogic().getPluginManager();

        // Revisa si el plugin ya fue agregado a ésta venta, si no, lo agrega
        if(!pm.exists(CirculoSaludPlugin.class))
        {
            CirculoSaludPlugin plugin = new CirculoSaludPlugin(getController());

            //Inicio sesión en el servicio del círculo de la salud
            plugin.getCdsService().login();

            pm.registerPlugin(plugin);

        }

        CirculoSaludPlugin plugin = (CirculoSaludPlugin) pm.getPlugin(CirculoSaludPlugin.class);

        return plugin;
    }

}
