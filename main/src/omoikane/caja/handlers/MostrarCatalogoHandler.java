package omoikane.caja.handlers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import omoikane.caja.presentation.CajaController;
import omoikane.principal.Articulos;
import omoikane.principal.Caja;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 08/12/12
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
public class MostrarCatalogoHandler extends ICajaEventHandler {
    public MostrarCatalogoHandler(CajaController controller) {
        super(controller);
    }

    @Override
    public void handle(Event event) {
        /*Runnable r = new Runnable() {
            @Override
            public void run() {
                mostrarCatalogo();
            }
        };
        new Thread(r).start();*/

        Task t = new Task() {
            @Override
            protected Void call() throws Exception {
                mostrarCatalogo();
                return null;
            }
        };

        t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                Platform.runLater(() -> {
                    getController().getJInternalFrame().toFront();
                    getController().getFxPanel().requestFocus();
                    cajaController.getMainAnchorPane().requestFocus();
                    getController().getCapturaTextField().requestFocus();
                });
            }
        });

        new Thread(t).start();
    }

    private void mostrarCatalogo() {
        String retorno = Articulos.lanzarDialogoCatalogo();

        retorno = (retorno==null)?"":retorno;
        String captura = cajaController.getModel().getCaptura().get();
        captura = (captura==null)?"":captura;
        cajaController.getModel().getCaptura().set(captura + retorno);

        //cajaController.getMainAnchorPane().requestFocus();
        //cajaController.getCapturaTextField().requestFocus();
    }
}
