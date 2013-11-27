package omoikane.compras.MVC;

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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import omoikane.compras.entities.Compra;
import omoikane.inventarios.tomaInventario.ConteoInventario;
import omoikane.inventarios.tomaInventario.TomaInventarioController;
import omoikane.repository.CompraRepo;
import omoikane.repository.ConteoInventarioRepo;
import omoikane.sistema.SceneOverloaded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.synyx.hades.domain.Order;
import org.synyx.hades.domain.Sort;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class ComprasCRUDController
        implements Initializable {

    @FXML //  fx:id="panelItem"
    private AnchorPane contenido; // Value injected by FXMLLoader

    @FXML
    ListView<Compra> lista;

    @FXML
    Button btnNuevo;

    @Autowired
    @Qualifier("compraView")
    Scene view;

    @Autowired
    CompraRepo compraRepo;

    CompraController compraController;

    @FXML
    public void onNuevoClic() {
        compraController.initModel();
    }

    @FXML
    public void onRecargar() {
        List<Compra> compraList = compraRepo.readAll( new Sort(Order.DESCENDING, "fecha") );
        lista.setItems(FXCollections.observableArrayList( compraList ));
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

        List<Compra> compraList = compraRepo.readAll( new Sort(Order.DESCENDING, "fecha") );
        lista.setItems(FXCollections.observableArrayList( compraList ));

        compraController = (CompraController) ((SceneOverloaded)view).getController();
        btnNuevo.disableProperty().bind( compraController.archivarButton.disabledProperty().not() );

        lista.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Compra>() {
            @Override
            public void changed(ObservableValue<? extends Compra> observableValue, Compra compra, Compra compra2) {
                compraController.initModel(compra2);
            }
        });
    }

}
