package omoikane.caja.business.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.math.BigDecimal;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 15/11/14
 * Time: 21:06
 */

public class PartidaModelTest {
    @Test
    public void testBinding() {
        PartidaModel pm = new PartidaModel();
        pm.getCantidad().set(BigDecimal.TEN);
        pm.getPrecio().set(new BigDecimal("45"));
        BigDecimal subtotal = pm.subtotalProperty().get();
        Assert.assertTrue(subtotal.equals(new BigDecimal("450")));
    }
}
