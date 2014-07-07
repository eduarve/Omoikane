package omoikane.caja.handlers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import omoikane.caja.presentation.CajaController;
import omoikane.principal.Clientes;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 08/12/12
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
public class MostrarCatalogoClientesHandler extends ICajaEventHandler {
    public MostrarCatalogoClientesHandler(CajaController controller) {
        super(controller);
    }

    @Override
    public void handle(Event event) {

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                mostrarCatalogo();
                return null;
            }
        };
        new Thread(task).start();
        //Platform.runLater(task);

        //SwingUtilities.invokeLater(task);

    }

    public void mostrarCatalogo() {
        final String[] retorno = {(String) Clientes.lanzarDialogoCatalogo()};

        retorno[0] = (retorno[0] ==null)?"": retorno[0];
        cajaController.getCajaLogic().cambiarCliente(Integer.valueOf(retorno[0]));

        cajaController.getVentaTableView().requestFocus();
        cajaController.getCapturaTextField().requestFocus();

    }
}
