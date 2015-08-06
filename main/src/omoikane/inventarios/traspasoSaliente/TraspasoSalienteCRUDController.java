package omoikane.inventarios.traspasoSaliente;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 10/11/13
 * Time: 01:00
 * To change this template use File | Settings | File Templates.
 */

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import omoikane.repository.TraspasoSalienteRepo;
import omoikane.sistema.SceneOverloaded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.synyx.hades.domain.Order;
import org.synyx.hades.domain.Sort;

import javax.swing.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class TraspasoSalienteCRUDController
        implements Initializable {

    @FXML //  fx:id="panelItem"
    private AnchorPane contenido; // Value injected by FXMLLoader

    @FXML
    ListView<TraspasoSaliente> lista;

    @FXML
    Button btnNuevo;

    @Autowired
    @Qualifier("traspasoSalienteView")
    Scene view;

    @Autowired
    TraspasoSalienteRepo traspasoSalienteRepo;

    TraspasoSalienteController traspasoSalienteController;
    private javax.swing.JInternalFrame jInternalFrame;
    private JFXPanel fxPanel;

    @FXML
    public void onNuevoClic() {
        traspasoSalienteController.initModel();
    }

    @FXML
    public void onRecargar() {
        List<TraspasoSaliente> traspasoSalienteList = traspasoSalienteRepo.readAll( new Sort(Order.DESCENDING, "fecha") );
        lista.setItems(FXCollections.observableArrayList(traspasoSalienteList));
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

        List<TraspasoSaliente> traspasoSalienteList = traspasoSalienteRepo.readAll( new Sort(Order.DESCENDING, "fecha") );
        lista.setItems(FXCollections.observableArrayList(traspasoSalienteList));

        traspasoSalienteController = (TraspasoSalienteController) ((SceneOverloaded)view).getController();
        traspasoSalienteController.setParent(this);
        btnNuevo.disableProperty().bind( traspasoSalienteController.aplicarInventarioButton.disabledProperty().not() );

        lista.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TraspasoSaliente>() {
            @Override
            public void changed(ObservableValue<? extends TraspasoSaliente> observableValue, TraspasoSaliente traspasoSaliente, TraspasoSaliente traspasoSaliente2) {
                traspasoSalienteController.initModel(traspasoSaliente2);
            }
        });
    }

    public JInternalFrame getJInternalFrame() {
        return jInternalFrame;
    }

    public void setJInternalFrame(JInternalFrame jInternalFrame) {
        this.jInternalFrame = jInternalFrame;
    }

    public JFXPanel getFXPanel() {
        return fxPanel;
    }

    public JFXPanel getFxPanel() {
        return fxPanel;
    }

    public void setFxPanel(JFXPanel fxPanel) {
        this.fxPanel = fxPanel;
    }
}
