package omoikane.compras.MVC;

import omoikane.caja.handlers.StockIssuesHandler;
import omoikane.inventarios.StockIssuesLogic;
import omoikane.principal.Principal;
import omoikane.producto.Articulo;
import omoikane.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Proyecto Omoikane
 * User: octavioruizcastillo
 * Date: 24/07/15
 * Time: 19:17
 */
@Component
public class CompraSaveLogic {

    @Autowired
    StockIssuesHandler stockIssuesHandler;

    @Autowired
    CompraRepo repo;

    @Autowired
    ProductoRepo productoRepo;

    @Autowired
    StockRepo stockRepo;

    /**
     * A partir de la versión 4.4 éste método sólo es llamado al concluir la captura de compra, por lo que
     * le asigna una fecha
     * @param model
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void concluir(CompraEntityWrapper model) {
            aplicarInventarioToModel(model);
            repo.saveAndFlush(model._compra);

    }


    private void aplicarInventarioToModel(CompraEntityWrapper model) {
        for (ItemCompraEntityWrapper itemCompraEntityWrapper : model.getItems()) {
            Articulo a = itemCompraEntityWrapper.articuloProperty().get();

            StockIssuesLogic stockIssuesLogic = Principal.applicationContext.getBean(StockIssuesLogic.class);
            Articulo articulo = stockIssuesLogic.increaseStock(a.getIdArticulo(), itemCompraEntityWrapper.cantidadProperty().get() );

            stockRepo.save(articulo.getStock());

        }
    }

    public Articulo getArticulo(String codigo) {
        Articulo resultado = null;
        List<Articulo> resultados = productoRepo.findByCodigo(codigo);
        if(resultados == null || resultados.isEmpty()) resultados = productoRepo.findByCodigoAlterno(codigo);
        if(resultados != null && !resultados.isEmpty()) resultado = productoRepo.findByIdIncludeStock(resultados.get(0).getIdArticulo());
        return resultado;
    }
}
