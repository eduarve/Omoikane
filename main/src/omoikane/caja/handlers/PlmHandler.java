package omoikane.caja.handlers;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import omoikane.caja.presentation.CajaController;
import omoikane.sistema.Herramientas;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 23/11/13
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */
public class PlmHandler extends ICajaEventHandler {
    public PlmHandler(CajaController controller) {
        super(controller);
    }

    @Override
    public void handle(Event event) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                crearVentanaPLM();
            }
        });
    }

    public void crearVentanaPLM() {
        JFrame frame = new JFrame("PLM");
        final JFXPanel fxPanel = new JFXPanel();

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(fxPanel);

        frame.pack();
        frame.setVisible(true);
        frame.setBounds(0,0, 1000, 650);

        fxPanel.setVisible(true);

        Platform.runLater(new Runnable() { // this will run initFX as JavaFX-Thread
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }

    private void initFX(final JFXPanel fxPanel) {
        Browser browser = new Browser();
        Scene scene = new Scene(browser,750,500, Color.web("#666970"));
        fxPanel.setScene(scene);

    }

    class Browser extends Region {

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        private HBox toolBar;

        private String[] captions = new String[]{
                "· Laboratorios",
                "· Marcas",
                "· Sustancias activas",
                "· Indicaciones"
        };

        private String[] urls = new String[]{
                "http://www.medicamentosplm.com/labs/a.htm",
                "http://www.medicamentosplm.com/marcas/a.htm",
                "http://www.medicamentosplm.com/sustancias/a.htm",
                "http://www.medicamentosplm.com/indicaciones/a.htm"
        };

        final Hyperlink[] hpls = new Hyperlink[captions.length];

        public Browser() {
            for (int i = 0; i < captions.length; i++) {
                final Hyperlink hpl = hpls[i] = new Hyperlink(captions[i]);
                hpl.setStyle("-fx-font-size: 28; -fx-text-fill: white;");
                //Image image = images[i] =
                //        new Image(getClass().getResourceAsStream(imageFiles[i]));
                //hpl.setGraphic(new ImageView (image));
                final String url = urls[i];

                hpl.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        webEngine.load(url);
                    }
                });
            }

            // load the home page
            webEngine.load("http://www.medicamentosplm.com/labs/a.htm");
            browser.impl_setScale(1.5);

            // create the toolbar
            toolBar = new HBox();
            toolBar.getChildren().addAll(hpls);

            //add components
            getChildren().add(toolBar);
            getChildren().add(browser);
        }
        private Node createSpacer() {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            return spacer;
        }

        @Override protected void layoutChildren() {
            double w = getWidth();
            double h = getHeight();
            double tbHeight = toolBar.prefHeight(w);
            layoutInArea(browser,0,0,w,h-tbHeight,0, HPos.CENTER, VPos.CENTER);
            layoutInArea(toolBar,0,h-tbHeight,w,tbHeight,0,HPos.CENTER,VPos.CENTER);
        }

        @Override protected double computePrefWidth(double height) {
            return 750;
        }

        @Override protected double computePrefHeight(double width) {
            return 500;
        }
    }
}
