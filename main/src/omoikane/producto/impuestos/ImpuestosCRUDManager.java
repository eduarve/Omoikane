package omoikane.producto.impuestos;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import omoikane.principal.Principal;
import omoikane.sistema.Herramientas;
import omoikane.sistema.Permisos;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 20/04/13
 * Time: 10:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImpuestosCRUDManager {

    ImpuestosCRUDController controller;
    private JInternalFrame frame;

    private Scene initImpuestosCRUD() throws IOException {
        Platform.setImplicitExit(false);
        ApplicationContext context = Principal.applicationContext;

        Scene scene = (Scene)context.getBean("impuestosCRUDView");

        return scene;
    }

    public JInternalFrame startJFXListaDePreciosCRUD() {
        JInternalFrame frame = null;
        if(omoikane.sistema.Usuarios.cerrojo(Permisos.getPMA_IMPUESTOSCRUD())) frame = _startJFXImpuestosCRUD();
        return frame;
    }

    private JInternalFrame _startJFXImpuestosCRUD() {
        frame = new JInternalFrame("Administrador de impuestos");
        final JFXPanel fxPanel = new JFXPanel();

        frame.setClosable(true);
        frame.add(fxPanel);
        frame.setVisible(true);

        Herramientas.panelCatalogo(frame);
        Principal.getEscritorio().getPanelEscritorio().add(frame);
        frame.setSize(706, 518);
        frame.setPreferredSize(new Dimension(706, 518));
        frame.setResizable(true);
        frame.setVisible(true);
        Herramientas.centrarVentana(frame);
        Herramientas.iconificable(frame);
        frame.toFront();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Scene scene = null;
                try {
                    scene = initImpuestosCRUD();
                    scene.setFill(null);
                    fxPanel.setScene(scene);
                } catch (IOException e) {

                }

            }
        });

        return frame;
    }

}
