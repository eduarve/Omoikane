package omoikane.repository;

import omoikane.compras.entities.Compra;
import omoikane.compras.entities.ItemCompra;
import omoikane.producto.Articulo;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.Param;
import org.synyx.hades.dao.Query;
import org.synyx.hades.domain.Pageable;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 23/11/13
 * Time: 20:40
 * To change this template use File | Settings | File Templates.
 */
public interface CompraRepo extends GenericDao<Compra, Long> {
    @Query("FROM Compra c LEFT JOIN FETCH c.items ic LEFT JOIN FETCH c.proveedor p LEFT JOIN FETCH ic.articulo a LEFT JOIN FETCH a.baseParaPrecio bp WHERE completado = ?")
    Compra findByCompletado(Boolean completado);

    @Query("FROM Compra c JOIN FETCH c.proveedor p")
    List<Compra> findAll(Pageable pageable);

    @Query("FROM Compra c LEFT JOIN FETCH c.items ic LEFT JOIN FETCH c.proveedor p LEFT JOIN FETCH ic.articulo a LEFT JOIN FETCH a.baseParaPrecio bp WHERE c.id = ?")
    Compra find(Long id);

}
