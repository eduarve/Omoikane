package omoikane.repository;


import omoikane.producto.ListaDePrecios;
import omoikane.producto.departamento.Departamento;
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
public interface DepartamentoRepo extends GenericDao<Departamento, Long> {
    @Query("FROM Departamento d WHERE d.activo = 1")
    List<Departamento> findAllActive();

    @Query("FROM Departamento d WHERE (d.activo = true OR d.activo = ?1) AND nombre like ?2")
    List<Departamento> findByActivoAndNombreLike(Boolean activo, String like);
}
