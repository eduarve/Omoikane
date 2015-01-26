package omoikane.repository;

import omoikane.inventarios.tomaInventario.ConteoInventario;
import omoikane.inventarios.traspasoSaliente.TraspasoSaliente;
import org.synyx.hades.dao.GenericDao;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 09/04/13
 * Time: 19:07
 * To change this template use File | Settings | File Templates.
 */
public interface TraspasoSalienteRepo extends GenericDao<TraspasoSaliente, Long> {
    TraspasoSaliente findByCompletado(Boolean completado);
}
