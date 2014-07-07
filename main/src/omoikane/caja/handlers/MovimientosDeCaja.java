package omoikane.caja.handlers;

import javafx.concurrent.Task;
import javafx.event.Event;
import omoikane.caja.presentation.CajaController;
import omoikane.principal.Caja;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 08/12/12
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
public class MovimientosDeCaja extends ICajaEventHandler {
    public MovimientosDeCaja(CajaController controller) {
        super(controller);
    }

    @Override
    public void handle(Event event) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ventanaMovimientos();
                return null;
            }
        };
        new Thread(task).start();
    }

    private void ventanaMovimientos() {
        Caja.btnMovimientosAction();

        cajaController.getVentaTableView().requestFocus();
        cajaController.getCapturaTextField().requestFocus();
    }
}
