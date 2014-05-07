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

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 16/04/13
 * Time: 02:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class StockIssuesHandler {
    CajaController controller;

    public StockIssuesHandler(CajaController c) {
        controller = c;
    }

    public void handle() {
        for(ProductoModel pm : controller.getModel().getVenta()) {
            StockIssuesLogic logic = Principal.applicationContext.getBean(StockIssuesLogic.class);
            logic.setArticulo(pm.getLongId());
            logic.reduceStock( pm.getCantidad() );
        }
    }
}
