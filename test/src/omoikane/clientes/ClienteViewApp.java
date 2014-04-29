package omoikane.clientes;

import javafx.application.Application;
import javafx.stage.Stage;
import omoikane.principal.Principal;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;

/**
* Created with IntelliJ IDEA.
* User: octavioruizcastillo
* Date: 19/04/14
* Time: 13:07
* To change this template use File | Settings | File Templates.
*/
public class ClienteViewApp extends Application {

    public ClienteViewApp() {

    }

    @Override
    public void start(Stage stage) throws Exception {
        Integer clienteId = (Integer) ((HashMap) Principal.applicationContext.getBean("properties")).get("ClienteViewApp.clienteTest");
        Boolean editable  = (Boolean) ((HashMap) Principal.applicationContext.getBean("properties")).get("ClienteViewApp.clienteTestEditable");

        ClienteView cv = Principal.applicationContext.getBean(ClienteView.class);
        cv.setEditable(editable);
        cv.init(clienteId);
        stage.setScene(cv.getScene());
        stage.show();
    }
}
