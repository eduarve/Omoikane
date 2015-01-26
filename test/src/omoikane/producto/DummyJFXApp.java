package omoikane.producto;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;
import omoikane.principal.Principal;
import omoikane.sistema.SceneOverloaded;
import omoikane.sistema.Usuarios;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 16/02/13
 * Time: 12:08
 * To change this template use File | Settings | File Templates.
 */
public class DummyJFXApp extends Application {

    private static DummyJFXApp instance;

    public static DummyJFXApp getInstance() { return instance; }

    private Object controller;

    public Object getController() { return controller; }

    static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DummyJFXApp.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            omoikane.principal.Principal.setConfig( new omoikane.sistema.Config() );

            //Principal.IDCaja = 1;
            Principal.IDAlmacen = 1;
            Usuarios.setIDUsuarioActivo(1l);

            HashMap testProperties = (HashMap) Principal.applicationContext.getBean( "properties" );
            String beanToTest = (String) testProperties.get("DummyJFXApp.viewBeanToTest");
            SceneOverloaded scene = (SceneOverloaded) Principal.applicationContext.getBean(beanToTest);

            primaryStage.setScene(scene);
            primaryStage.setTitle("View Test");
            primaryStage.show();
            if(testProperties.containsKey("DummyJFXApp.task")) {
                Task<Void> task = (Task<Void>) testProperties.get("DummyJFXApp.task");
                Platform.runLater(task);
            }

            controller = scene.getController();
            instance = this;

        } catch (Exception ex) {
            logger.error( ex.getMessage(), ex );
        }
    }

}
