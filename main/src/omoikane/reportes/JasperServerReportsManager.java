package omoikane.reportes;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import omoikane.principal.Principal;
import omoikane.proveedores.ProveedoresController;
import omoikane.sistema.Herramientas;
import omoikane.sistema.Permisos;
import omoikane.sistema.SceneOverloaded;
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
public class JasperServerReportsManager {

    ProveedoresController controller;
    private JInternalFrame frame;

    private Scene initReports() throws IOException {
        Platform.setImplicitExit(false);
        ApplicationContext context = Principal.applicationContext;

        SceneOverloaded scene = (SceneOverloaded) context.getBean("jasperServerReportsView");

        JasperServerReportsController jsrp = (JasperServerReportsController)scene.getController();
        jsrp
                .getSalirButton()
                .setOnAction(new CerrarBtnHandler());

        return scene;
    }

    public JInternalFrame startJFXReports() {
        JInternalFrame frame = null;
        if(omoikane.sistema.Usuarios.cerrojo(Permisos.PMA_REPORTES)) frame = _startJFXReports();
        return frame;
    }

    private JInternalFrame _startJFXReports() {
        frame = new JInternalFrame("Reportes avanzados");
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
                    scene = initReports();
                    scene.setFill(null);
                    fxPanel.setScene(scene);
                } catch (IOException e) {

                }

            }
        });

        return frame;
    }

    private class CerrarBtnHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            JasperServerReportsManager.this.frame.setVisible(false);
            JasperServerReportsManager.this.frame.dispose();
        }
    }

}
