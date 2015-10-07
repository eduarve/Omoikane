package omoikane.caja.handlers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import omoikane.caja.business.Security;
import omoikane.caja.presentation.CajaController;
import omoikane.caja.presentation.ProductoModel;
import omoikane.entities.Cancelacion;
import omoikane.entities.Usuario;
import omoikane.principal.Principal;
import omoikane.producto.Articulo;
import omoikane.repository.CancelacionRepo;
import omoikane.repository.VentaRepo;
import omoikane.sistema.Usuarios;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 6/12/12
 * Time: 01:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class CancelarVenta extends ICajaEventHandler {
    public static Logger logger = Logger.getLogger(CancelarVenta.class);
    CancelacionRepo repo;

    VentaRepo ventaRepo;

    public CancelarVenta(CajaController controller) {
        super(controller);
        repo = (CancelacionRepo) Principal.applicationContext.getBean("cancelacionRepo");
        ventaRepo = (VentaRepo) Principal.applicationContext.getBean("ventaRepo");
    }

    @Override
    public void handle(Event event) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                cancelarVentaConAutorizacion();
            }
        });
    }

    private void cancelarVentaConAutorizacion() {
        Boolean auth = Security.cancelacion();
        Platform.runLater(cancelarVenta(auth));
    }

    private Task<Void> cancelarVenta(Boolean auth) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    if(auth) {
                        registrar(getController().getModel().getVenta());
                        //Cancelar una venta es eliminar su caché en el sistema de archivos local y crear un nuevo modelo usando
                        //  nuevaVenta
                        File f = new File("venta.json");
                        if(f.exists()) f.delete();

                        getController().getCajaLogic().nuevaVenta();
                    }

                    //Agenda la recuperación el enfoque en el campo de captura
                    Platform.runLater(() -> {
                        getController().getJInternalFrame().toFront();
                        getController().getFxPanel().requestFocus();
                        getController().getMainAnchorPane().requestFocus();
                        getController().getCapturaTextField().requestFocus();
                    });
                } catch (Exception e) {
                    logger.error("Error al cancelar venta", e);
                }
                return null;
            }
        };

        return task;
    }

    @Transactional
    private void registrar(ObservableList<ProductoModel> venta) {

        for(ProductoModel pm : venta) {
            Cancelacion c = new Cancelacion();
            c.setArticulo   ( new Articulo( pm.getLongId() ) );
            c.setCajero     ( new Usuario( new Long(Usuarios.getIDUsuarioActivo()   ) ) );
            c.setAutorizador( new Usuario( new Long(Usuarios.getIDUltimoAutorizado()) ) );
            repo.save(c);
        }
    }
}
