package omoikane.producto;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import omoikane.repository.ProductoRepo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 01/11/12
 * Time: 20:30
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-test.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DatabaseSetup("../repository/sampleDataLight.xml")
public class PrecioTest {
    @Autowired
    ProductoRepo productoRepo;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void testPrecio() {
        Articulo a = productoRepo.findByCodigo("7501059238305").get(0);
        Assert.assertTrue( a.getBaseParaPrecio().getCosto() == 6.92d );
    }

    @Test
    public void testPrecioAlterno() {
        Articulo a = productoRepo.findByCodigo("12345").get(0);
        Assert.assertTrue( a.getPrecio().getPrecio().equals(new BigDecimal("25.1720")) );
        Assert.assertTrue( a.getPrecio(1).getPrecio().equals(new BigDecimal("21.7000")) );
        Assert.assertTrue( a.getPrecio(2).getPrecio().equals(new BigDecimal("18.2280")) );
    }
}
