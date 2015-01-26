package omoikane.repository;

import omoikane.inventarios.traspasoEntrante.TraspasoEntrante;
import omoikane.inventarios.traspasoSaliente.TraspasoSaliente;
import org.synyx.hades.dao.GenericDao;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 09/04/13
 * Time: 19:07
 * To change this template use File | Settings | File Templates.
 */
public interface TraspasoEntranteRepo extends GenericDao<TraspasoEntrante, Long> {
    TraspasoEntrante findByCompletado(Boolean completado);
}
