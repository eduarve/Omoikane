package omoikane.caja.handlers;

import omoikane.caja.presentation.CajaController;
import omoikane.caja.presentation.ProductoModel;
import omoikane.entities.Paquete;
import omoikane.inventarios.Stock;
import omoikane.inventarios.StockIssuesLogic;
import omoikane.principal.Principal;
import omoikane.producto.Articulo;
import omoikane.producto.Producto;
import omoikane.repository.CancelacionRepo;
import omoikane.repository.ProductoRepo;
import omoikane.repository.VentaRepo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 16/04/13
 * Time: 02:10 AM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class StockIssuesHandler {

    @Autowired
    StockIssuesLogic stockIssuesLogic;

    public static Logger logger = Logger.getLogger(StockIssuesHandler.class);

    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(CajaController controller) {
        for(ProductoModel pm : controller.getModel().getVenta()) {

            stockIssuesLogic.reduceStock(pm.getLongId(), pm.getCantidad() );

        }
    }
}
