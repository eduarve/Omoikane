package omoikane.caja.nadroCDS;

import com.net.cds_oroDemo.ArrayOfResponseBonusList;
import com.net.cds_oroDemo.ResponseBonus;
import com.net.cds_oroDemo.ResponseBonusList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 29/12/14
 * Time: 17:37
 */
public class BeneficiosController implements Initializable {

    @FXML
    private Button cerrarBtn;

    @FXML
    private TableView<ResponseBonusList> mainTable;

    @FXML
    private TableColumn<ResponseBonusList, String> sku;

    @FXML
    private TableColumn<ResponseBonusList, String> colDescripcion;

    @FXML
    private TableColumn<ResponseBonusList, Long> colCantidad;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setResponseBonus(ArrayOfResponseBonusList array) {
        ObservableList<ResponseBonusList> items = FXCollections.observableArrayList(array.getResponseBonusList());
        mainTable.setItems(items);
    }
}
