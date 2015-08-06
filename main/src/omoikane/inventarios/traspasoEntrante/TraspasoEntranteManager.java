package omoikane.inventarios.traspasoEntrante;

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
public class TraspasoEntranteManager {

    ImpresionEtiquetasController controller;
    ImpresionEtiquetasModel model;
    private JInternalFrame frame;

    private SceneOverloaded initTraspasoEntranteCRUD() throws IOException {
        Platform.setImplicitExit(false);
        ApplicationContext context = Principal.applicationContext;

        SceneOverloaded scene = (SceneOverloaded)context.getBean("traspasoEntranteCRUDView");

        return scene;
    }

    private Scene traspasoEntranteInventario() throws IOException {
        Platform.setImplicitExit(false);
        ApplicationContext context = Principal.applicationContext;

        Scene scene = (Scene)context.getBean("traspasoEntranteView");

        return scene;
    }

    public JInternalFrame startJFXTraspasoEntrante() {
        JInternalFrame frame = null;
        if(omoikane.sistema.Usuarios.cerrojo(Permisos.getPMA_CREARINVENTARIO())) frame = _startJFXTraspasoEntrante();
        return frame;
    }

    private JInternalFrame _startJFXTraspasoEntrante() {
        frame = new JInternalFrame("Traspaso entrante");
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
                    scene = initTraspasoEntranteCRUD();
                    ((TraspasoEntranteCRUDController)scene.getController()).setFxPanel(fxPanel);
                    ((TraspasoEntranteCRUDController)scene.getController()).setjInternalFrame(frame);
                    //scene.setFill(null);
                    fxPanel.setScene(scene);
                } catch (IOException e) {

                }

            }
        });

        return frame;
    }

}
