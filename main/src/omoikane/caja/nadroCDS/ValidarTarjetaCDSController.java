package omoikane.caja.nadroCDS;

import com.net.cds_oroDemo.ResponseBonus;
import com.net.cds_oroDemo.Tarjeta;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class ValidarTarjetaCDSController implements Initializable {

    public static final Logger logger = Logger.getLogger(ValidarTarjetaCDSController.class);

    CDSService cdsService;

    @FXML
    private TextField noTarjeta;

    @FXML
    private TextField nombre;

    @FXML
    private TextField id;

    @FXML
    private TextField folio;

    @FXML
    private TextField activacion;

    @FXML
    private TextField vigencia;

    @FXML
    private Button cerrarBtn;

    private Tarjeta tarjeta;

    @FXML
    void onNoTarjetaTyped(KeyEvent event) {
        if(noTarjeta.textProperty().get().length() >= 13) {
            try {
                tarjeta = cdsService.getInfoTarjeta(noTarjeta.getText());
                nombre.textProperty().set(tarjeta.getCliente().getNombre() + " "
                        + tarjeta.getCliente().getApellidoPaterno() + " "
                        + tarjeta.getCliente().getApellidoMaterno());
                id.textProperty().set(((Long)tarjeta.getId()).toString());
                folio.textProperty().set(tarjeta.getFolio());
                activacion.textProperty().set(tarjeta.getFechaActivacion().toString());
                vigencia.textProperty().set(tarjeta.getFechaVigencia().toString());
            } catch (NadroCDSException e) {
                e.printStackTrace();
                logger.info("Tarjeta inválida");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public Tarjeta getTarjeta() {
        return tarjeta;
    }

    public void setCdsService(CDSService cdsService) {
        this.cdsService = cdsService;
    }

    public Button getCerrarBtn() {
        return cerrarBtn;
    }
}
