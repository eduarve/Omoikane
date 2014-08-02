package omoikane.reportes;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import javafx.application.Application;
import omoikane.principal.Principal;
import omoikane.producto.DummyJFXApp;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import static omoikane.reportes.JasperServerReportsController.*;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 21/07/14
 * Time: 18:32
 * To change this template use File | Settings | File Templates.
 */
/*
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-test.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })    */

public class JasperServerReportsViewTest {
    final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JasperServerReportsController.class);

    @Test
    public void viewTest() {

        Principal.initJavaFx();
        omoikane.principal.Principal.setConfig(new omoikane.sistema.Config());
        Principal.applicationContext = new ClassPathXmlApplicationContext("applicationContext-test.xml");
        HashMap testProperties = (HashMap) Principal.applicationContext.getBean( "properties" );
        testProperties.put("DummyJFXApp.viewBeanToTest", "jasperServerReportsView");
        Application.launch(DummyJFXApp.class);
    }

    @Test
    public void wsTest() {
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.universal("jasperadmin", "jasperadmin");

        Client client;
        client = ClientBuilder.newClient().register(feature);
        WebTarget myResource = client.target("http://172.16.0.3:8080/jasperserver/rest");

        ResourceDescriptors reports = myResource.path("/resources/reports")
                .request(MediaType.APPLICATION_XML)
                .get(ResourceDescriptors.class);

        for(ResourceDescriptor rd : reports.descriptors) {
            System.out.println(rd.getName());
        }
    }

    @XmlRootElement(name = "resourceDescriptors")
    public static class ResourceDescriptors {
        @XmlElement(name = "resourceDescriptor")
        public List<ResourceDescriptor> descriptors;
    }

    @XmlRootElement
    public static class ResourceDescriptor {
        private String name;

        @XmlAttribute
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
