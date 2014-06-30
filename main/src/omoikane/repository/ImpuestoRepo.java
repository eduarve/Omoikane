package omoikane.repository;


import omoikane.producto.Impuesto;
import omoikane.proveedores.Proveedor;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.Query;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 25/02/13
 * Time: 09:16 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ImpuestoRepo extends GenericDao<Impuesto, Long> {
    @Query("FROM Impuesto p WHERE p.activo = 1")
    List<Impuesto> findAllActive();

    @Query("FROM Impuesto p WHERE (p.activo = true OR p.activo = ?1) AND descripcion like ?2")
    List<Impuesto> findByActivoAndDescripcionLike(Boolean activo, String like);
}
