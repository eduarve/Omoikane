package omoikane.inventarios.traspasoSaliente;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import omoikane.etiquetas.ImpresionEtiquetasController;
import omoikane.etiquetas.ImpresionEtiquetasModel;
import omoikane.principal.Principal;
import omoikane.sistema.Herramientas;
import omoikane.sistema.Permisos;
import omoikane.sistema.SceneOverloaded;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio Ruiz
 * Date: 12/03/13
 * Time: 01:41
 * To change this template use File | Settings | File Templates.
 */
public class TraspasoSalienteManager {

    ImpresionEtiquetasController controller;
    ImpresionEtiquetasModel model;
    private JInternalFrame frame;

    private SceneOverloaded initTraspasoSalienteCRUD() throws IOException {
        Platform.setImplicitExit(false);
        ApplicationContext context = Principal.applicationContext;

        SceneOverloaded scene = (SceneOverloaded)context.getBean("traspasoSalienteCRUDView");

        return scene;
    }

    private Scene initTomaInventario() throws IOException {
        Platform.setImplicitExit(false);
        ApplicationContext context = Principal.applicationContext;

        Scene scene = (Scene)context.getBean("traspasoSalienteView");

        return scene;
    }

    public JInternalFrame startJFXTraspasoSaliente() {
        JInternalFrame frame = null;
        if(omoikane.sistema.Usuarios.cerrojo(Permisos.getPMA_CREARINVENTARIO())) frame = _startJFXTraspasoSaliente();
        return frame;
    }

    private JInternalFrame _startJFXTraspasoSaliente() {
        frame = new JInternalFrame("Traspasos salientes");
        final JFXPanel fxPanel = new JFXPanel();

        frame.setClosable(true);
        frame.add(fxPanel);
        frame.setVisible(true);

        Herramientas.panelCatalogo(frame);
        Principal.getEscritorio().getPanelEscritorio().add(frame);
        frame.setSize(1024, 640);
        frame.setPreferredSize(new Dimension(1024, 640));
        frame.setVisible(true);
        Herramientas.centrarVentana(frame);
        Herramientas.iconificable(frame);
        frame.setResizable(true);
        frame.toFront();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                SceneOverloaded scene = null;
                try {
                    scene = initTraspasoSalienteCRUD();
                    ((TraspasoSalienteCRUDController) scene.getController()).setJInternalFrame(frame);
                    ((TraspasoSalienteCRUDController) scene.getController()).setFxPanel(fxPanel);
                    //scene.setFill(null);
                    fxPanel.setScene(scene);
                } catch (IOException e) {

                }

            }
        });

        return frame;
    }

}
