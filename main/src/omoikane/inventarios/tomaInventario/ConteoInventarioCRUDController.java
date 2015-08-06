package omoikane.inventarios.tomaInventario;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 10/11/13
 * Time: 01:00
 * To change this template use File | Settings | File Templates.
 */

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import omoikane.repository.ConteoInventarioRepo;
import omoikane.sistema.SceneOverloaded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.synyx.hades.domain.Order;
import org.synyx.hades.domain.Sort;

import javax.swing.*;


public class ConteoInventarioCRUDController
        implements Initializable {

    @FXML //  fx:id="panelItem"
    private AnchorPane contenido; // Value injected by FXMLLoader

    @FXML
    ListView<ConteoInventario> lista;

    @FXML
    Button btnNuevo;

    @Autowired
    @Qualifier("tomaInventarioView")
    Scene view;

    @Autowired
    ConteoInventarioRepo conteoInventarioRepo;

    TomaInventarioController tomaInventarioController;
    private javax.swing.JInternalFrame JInternalFrame;
    private JFXPanel FXPanel;

    @FXML
    public void onNuevoClic() {
        tomaInventarioController.initModel();
    }

    @FXML
    public void onRecargar() {
        List<ConteoInventario> conteoInventarioList = conteoInventarioRepo.readAll( new Sort(Order.DESCENDING, "fecha") );
        lista.setItems(FXCollections.observableArrayList( conteoInventarioList ));
    }

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert contenido != null : "fx:id=\"panelItem\" was not injected: check your FXML file 'ConteoInventarioCRUDController.fxml'.";

        // initialize your logic here: all @FXML variables will have been injected

        contenido.getChildren().clear();
        AnchorPane.setBottomAnchor(view.getRoot(), 0d);
        AnchorPane.setTopAnchor(view.getRoot(), 0d);
        AnchorPane.setLeftAnchor(view.getRoot(), 0d);
        AnchorPane.setRightAnchor(view.getRoot(), 0d);
        contenido.getChildren().setAll(view.getRoot());

        List<ConteoInventario> conteoInventarioList = conteoInventarioRepo.readAll( new Sort(Order.DESCENDING, "fecha") );
        lista.setItems(FXCollections.observableArrayList( conteoInventarioList ));

        tomaInventarioController = (TomaInventarioController) ((SceneOverloaded)view).getController();
        tomaInventarioController.setParent(this);
        btnNuevo.disableProperty().bind( tomaInventarioController.archivarButton.disabledProperty().not() );

        lista.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ConteoInventario>() {
            @Override
            public void changed(ObservableValue<? extends ConteoInventario> observableValue, ConteoInventario conteoInventario, ConteoInventario conteoInventario2) {
                tomaInventarioController.initModel(conteoInventario2);
            }
        });
    }

    public void setJInternalFrame(JInternalFrame JInternalFrame) {
        this.JInternalFrame = JInternalFrame;
    }

    public JInternalFrame getJInternalFrame() {
        return JInternalFrame;
    }

    public void setFXPanel(JFXPanel FXPanel) {
        this.FXPanel = FXPanel;
    }

    public JFXPanel getFXPanel() {
        return FXPanel;
    }
}
