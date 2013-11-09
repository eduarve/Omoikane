package omoikane.repository;

import omoikane.entities.LegacyVenta;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.Query;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 05/11/12
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
public interface VentaRepo extends GenericDao<LegacyVenta, Integer> {
    LegacyVenta findByIdCajaAndCompletada(int idCaja, Boolean completada);

    @Query("SELECT new map(V.fechaHora as dia, SUM(VD.cantidad) as cantidad, SUM(VD.total) as importe) FROM LegacyVenta as V, LegacyVentaDetalle as VD WHERE V = VD.venta AND V.fechaHora >= ?1 AND V.fechaHora <= ?2 AND VD.idArticulo = ?3 GROUP BY day(V.fechaHora)")
    List sumVentasOfArticuloByDay(Date desde, Date hasta, Integer idArticulo);
}
