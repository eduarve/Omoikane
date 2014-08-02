package omoikane.reportes;

/**
 * Sample Skeleton for "JasperServerReportsView.fxml" Controller Class
 * You can copy and paste this code into your favorite IDE
 **/

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import omoikane.principal.Principal;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.*;


public class JasperServerReportsController
        implements Initializable {

    @FXML //  fx:id="menuVBox"
    private VBox menuVBox; // Value injected by FXMLLoader

    @FXML //  fx:id="reportePanel"
    private AnchorPane reportePanel; // Value injected by FXMLLoader

    @FXML //  fx:id="reporteWebView"
    private WebView reporteWebView; // Value injected by FXMLLoader

    @FXML //  fx:id="salirButton"
    private Button salirButton; // Value injected by FXMLLoader

    public static final Logger logger = Logger.getLogger(JasperServerReportsController.class);

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert menuVBox != null : "fx:id=\"menuVBox\" was not injected: check your FXML file 'JasperServerReportsView.fxml'.";
        assert reportePanel != null : "fx:id=\"reportePanel\" was not injected: check your FXML file 'JasperServerReportsView.fxml'.";
        assert reporteWebView != null : "fx:id=\"reporteWebView\" was not injected: check your FXML file 'JasperServerReportsView.fxml'.";
        assert getSalirButton() != null : "fx:id=\"salirButton\" was not injected: check your FXML file 'JasperServerReportsView.fxml'.";

        // initialize your logic here: all @FXML variables will have been injected
        try {
            List<ResourceDescriptor> reportes = findReports();

            for(ResourceDescriptor rd : reportes) {
                Button btn = createButton(rd);
                menuVBox.getChildren().add(btn);
            }
        } catch(final javax.ws.rs.ProcessingException pe) {
            Task log = new Task() {
                @Override
                protected Object call() throws Exception {
                    logger.error("No se puede contactar con el servidor de reportes", pe);
                    return null;
                }
            };
            new Thread(log).start();
        }

        getSalirButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ((Stage) getSalirButton().getScene().getWindow()).close();
            }
        });
    }

    List<ResourceDescriptor> findReports() {

        HttpAuthenticationFeature feature = HttpAuthenticationFeature
                .universal(
                        Principal.loginJasperserver,
                        Principal.passJasperserver
                );

        Client client;
        client = ClientBuilder.newClient().register(feature);
        WebTarget myResource = client.target(Principal.urlJasperserver + "/rest");

        ResourceDescriptors reports = myResource.path("/resources/reports")
                .queryParam("recursive", "1")
                .queryParam("type"     , "reportUnit")
                .request(MediaType.APPLICATION_XML)
                .get(ResourceDescriptors.class);

        List<ResourceDescriptor> reportList = reports.descriptors;

        return reportList;
    }

    Button createButton(final ResourceDescriptor rd) {
        Button btn = new Button(rd.getName());
        btn.setMaxWidth(1.7976931348623157E308);
        btn.setPrefWidth(1.7976931348623157E308);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                reporteWebView.getEngine().load(
                        Principal.urlJasperserver +
                                "/flow.html?_flowId=viewReportFlow" +
                                "&standAlone=true" +
                                "&j_username=" + Principal.loginJasperserver +
                                "&j_password=" + Principal.passJasperserver +
                                "&decorate=no" +
                                "&_flowId=viewReportFlow" +
                                "&ParentFolderUri=%2Freports" +
                                "&reportUnit=%2Freports%2F"+rd.getName());
            }
        });

        return btn;
    }

    public Button getSalirButton() {
        return salirButton;
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
