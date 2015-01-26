package omoikane.caja.business.service;


import com.github.springtestdbunit.DbUnitTestExecutionListener;
import omoikane.caja.business.domain.PartidaModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Date: 10/11/14
 * Time: 21:06
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-test.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })

public class CajaServiceTest {

    @Autowired
    CajaService cajaService;

    @Test
    public void testStartVenta() {

        cajaService.init();
        cajaService.startVenta();
    }

    void testFinishVenta() {

    }

    void testCancelVenta() {

    }

    void testAddPartida() {
        cajaService.init();
        cajaService.startVenta();

    }

    void testRemovePartida() {

    }

    void testUpdatePartida() {

    }

    void testSetCliente() {

    }

    void testGetModel() {

    }
}

