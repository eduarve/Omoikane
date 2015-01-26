package omoikane.repository;

import omoikane.entities.LegacyVenta;
import omoikane.inventarios.Stock;
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
public interface StockRepo extends GenericDao<Stock, Long> {

}
