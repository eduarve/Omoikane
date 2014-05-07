package omoikane.inventarios;

import omoikane.entities.Paquete;
import omoikane.producto.Articulo;
import omoikane.repository.ProductoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 29/04/14
 * Time: 21:14
 *
 * When selling or capturing invoices, there are 2 cases:
 * - Simple product sold or purchased: Just reduces or increase the quantity sold or purchased
 * - Package product: Infers this type and load the child products and quantity per package from database,
 * then, multiplies quantity per package by quantity sold or purchased to increase or decrease stock
 *
 */
@Service
public class StockIssuesLogic {
    Articulo p;

    @Autowired
    ProductoRepo productoRepo;

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRED)
    public void setArticulo(Long id) {

        Articulo articulo = productoRepo.findByIdIncludeStock(id);
        articulo.getRenglonesPaquete();
        p = articulo;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Articulo reduceStock(BigDecimal howMany) {

        if(p.getEsPaquete()) {
            for(Paquete paquete : p.getRenglonesPaquete()) {
                Articulo productoContenido = paquete.getProductoContenido();
                Stock s = productoContenido.getStockInitializated();
                BigDecimal quantitySold = paquete.getCantidad().multiply( howMany );
                s.setEnTienda( s.getEnTienda().subtract(quantitySold) );
            }
        } else {
            BigDecimal quantitySold = howMany;
            Stock s = p.getStockInitializated();
            s.setEnTienda( s.getEnTienda().subtract( quantitySold ) );
        }

        return p;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Articulo increaseStock(BigDecimal howMany) {
        if(p.getEsPaquete()) {
            for(Paquete paquete : p.getRenglonesPaquete()) {
                Articulo productoContenido = paquete.getProductoContenido();
                Stock s = productoContenido.getStockInitializated();
                BigDecimal quantitySold = paquete.getCantidad().multiply( howMany );
                s.setEnTienda( s.getEnTienda().add(quantitySold) );
            }
        } else {
            BigDecimal quantitySold = howMany;
            Stock s = p.getStockInitializated();
            s.setEnTienda( s.getEnTienda().add( quantitySold ) );
        }

        return p;
    }
}
