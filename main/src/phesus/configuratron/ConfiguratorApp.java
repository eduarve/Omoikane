package phesus.configuratron;

 import java.io.IOException;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 import javafx.application.Application;
 import javafx.fxml.FXMLLoader;
 import javafx.scene.Scene;
 import javafx.scene.layout.AnchorPane;
 import javafx.scene.layout.StackPane;
 import javafx.stage.Stage;
 import phesus.configuratron.model.Configuration;
 import phesus.configuratron.model.ConfigurationDao;

public class ConfiguratorApp extends Application {

    final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConfiguratorApp.class);

    FXMLLoader fxmlLoader;

    /**
    * @param args the command line arguments
    */
    public static void main(String[] args) {
        Application.launch(ConfiguratorApp.class, (java.lang.String[])null);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Scene scene = initConfigurator();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Configuratron");
            primaryStage.show();
        } catch (Exception ex) {
            logger.error( ex.getMessage(), ex );
        }
    }

    public Scene initConfigurator() throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getResource("view/configurator.fxml"));
        AnchorPane page = (AnchorPane) fxmlLoader.load();
        Scene scene = new Scene(page);
        return scene;
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }
}