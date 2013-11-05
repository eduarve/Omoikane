package omoikane.inventarios.tomaInventario;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import omoikane.etiquetas.ImpresionEtiquetasController;
import omoikane.etiquetas.ImpresionEtiquetasModel;
import omoikane.principal.Principal;
import omoikane.sistema.Herramientas;
import omoikane.sistema.Permisos;
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
public class ConteoInventarioManager {

    ImpresionEtiquetasController controller;
    ImpresionEtiquetasModel model;
    private JInternalFrame frame;

    private Scene initTomaInventario() throws IOException {
        Platform.setImplicitExit(false);
        ApplicationContext context = Principal.applicationContext;

        Scene scene = (Scene)context.getBean("tomaInventarioView");

        return scene;
    }

    public JInternalFrame startJFXTomaInventario() {
        JInternalFrame frame = null;
        if(omoikane.sistema.Usuarios.cerrojo(Permisos.getPMA_CREARINVENTARIO())) frame = _startJFXTomaInventario();
        return frame;
    }

    public JInternalFrame _startJFXTomaInventario() {
        frame = new JInternalFrame("Toma de inventarios");
        final JFXPanel fxPanel = new JFXPanel();

        frame.setClosable(true);
        frame.add(fxPanel);
        frame.setVisible(true);

        Herramientas.panelCatalogo(frame);
        Principal.getEscritorio().getPanelEscritorio().add(frame);
        frame.setSize(806, 618);
        frame.setPreferredSize(new Dimension(806, 618));
        frame.setVisible(true);
        Herramientas.centrarVentana(frame);
        Herramientas.iconificable(frame);
        frame.setResizable(true);
        frame.toFront();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Scene scene = null;
                try {
                    scene = initTomaInventario();
                    //scene.setFill(null);
                    fxPanel.setScene(scene);
                } catch (IOException e) {

                }

            }
        });

        return frame;
    }

}
