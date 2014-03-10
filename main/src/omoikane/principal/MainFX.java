package omoikane.principal;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 08/03/14
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */
public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Principal.main(new String[0]);
    }
}
