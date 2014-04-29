package omoikane.clientes;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import javafx.application.Application;
import omoikane.principal.Principal;
import omoikane.sistema.Usuarios;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 19/04/14
 * Time: 12:27
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-test.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DatabaseSetup("../repository/sampleDataLight.xml")
public class ClienteViewTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void testEditarCliente() throws Exception {

        omoikane.principal.Principal.applicationContext = applicationContext;
        Principal.initJavaFx();
        omoikane.principal.Principal.setConfig( new omoikane.sistema.Config() );

        //Principal.IDCaja = 1;
        Principal.IDAlmacen = 1;
        Usuarios.setIDUsuarioActivo(1);
        ((HashMap)applicationContext.getBean("properties")).put("ClienteViewApp.clienteTest", 2);
        ((HashMap)applicationContext.getBean("properties")).put("ClienteViewApp.clienteTestEditable", true);

        Application.launch(ClienteViewApp.class);

    }

    @Test
    public void testNewCliente() throws Exception {
        omoikane.principal.Principal.applicationContext = applicationContext;
        Principal.initJavaFx();
        omoikane.principal.Principal.setConfig( new omoikane.sistema.Config() );

        //Principal.IDCaja = 1;
        Principal.IDAlmacen = 1;
        Usuarios.setIDUsuarioActivo(1);
        ((HashMap)applicationContext.getBean("properties")).put("ClienteViewApp.clienteTest", 0);
        ((HashMap)applicationContext.getBean("properties")).put("ClienteViewApp.clienteTestEditable", true);

        Application.launch(ClienteViewApp.class);
    }

    @Test
    public void testDetallesCliente() throws Exception {
        omoikane.principal.Principal.applicationContext = applicationContext;
        Principal.initJavaFx();
        omoikane.principal.Principal.setConfig( new omoikane.sistema.Config() );

        //Principal.IDCaja = 1;
        Principal.IDAlmacen = 1;
        Usuarios.setIDUsuarioActivo(1);
        ((HashMap)applicationContext.getBean("properties")).put("ClienteViewApp.clienteTest", 2);
        ((HashMap)applicationContext.getBean("properties")).put("ClienteViewApp.clienteTestEditable", false);

        Application.launch(ClienteViewApp.class);
    }

}
