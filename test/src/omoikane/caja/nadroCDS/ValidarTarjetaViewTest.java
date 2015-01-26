package omoikane.caja.nadroCDS;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import javafx.application.Application;
import javafx.concurrent.Task;
import omoikane.principal.Principal;
import omoikane.producto.DummyJFXApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.HashMap;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 26/12/14
 * Time: 19:24
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-test.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })

public class ValidarTarjetaViewTest {
    final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ValidarTarjetaViewTest.class);

    @Test
    public void compraTest() throws NadroCDSException {
        Principal.initJavaFx();
        Principal.applicationContext = new ClassPathXmlApplicationContext("applicationContext-test.xml");
        HashMap testProperties = (HashMap) Principal.applicationContext.getBean( "properties" );
        testProperties.put("DummyJFXApp.viewBeanToTest", "validarTarjetaCDSView");
        testProperties.put("DummyJFXApp.task", new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ValidarTarjetaCDSController cc = (ValidarTarjetaCDSController) DummyJFXApp.getInstance().getController();
                CDSService cdsService = new CDSService();
                try {
                    cdsService.login();
                } catch (NadroCDSException e) {
                    logger.error("Error", e);
                }
                cc.setCdsService(cdsService);
                return null;
            }
        });

        Application.launch(DummyJFXApp.class);
    }

}

