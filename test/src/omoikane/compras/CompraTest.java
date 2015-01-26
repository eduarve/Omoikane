package omoikane.compras;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import javafx.application.Application;
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
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 27/02/13
 * Time: 06:12 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-test.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })

public class CompraTest {
    final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CompraTest.class);

    @Test
    public void compraTest() {
        Principal.initJavaFx();
        Principal.applicationContext = new ClassPathXmlApplicationContext("applicationContext-test.xml");
        HashMap testProperties = (HashMap) Principal.applicationContext.getBean( "properties" );
        testProperties.put("DummyJFXApp.viewBeanToTest", "compraView");
        Application.launch(DummyJFXApp.class);
    }

    @Test
    public void comprasCRUDTest() {
        Principal.initJavaFx();
        Principal.applicationContext = new ClassPathXmlApplicationContext("applicationContext-test.xml");
        HashMap testProperties = (HashMap) Principal.applicationContext.getBean( "properties" );
        testProperties.put("DummyJFXApp.viewBeanToTest", "comprasCRUDView");
        Application.launch(DummyJFXApp.class);
    }

}
