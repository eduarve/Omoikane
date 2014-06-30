package omoikane.producto.impuestos;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import omoikane.producto.Impuesto;
import omoikane.producto.ListaDePrecios;
import omoikane.proveedores.Proveedor;
import omoikane.repository.ImpuestoRepo;
import omoikane.repository.ListaDePreciosRepo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.*;

import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 25/02/13
 * Time: 08:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImpuestosCRUDController implements Initializable {
    @FXML TextField txtBuscar;
    @FXML TextField txtId;
    @FXML TextField txtDescripcion;
    @FXML TextField txtPorcentaje;

    @FXML TableView<Impuesto> impuestosTableView;
    @FXML TextArea txtNotas;

    @FXML Label notaNombre;
    @FXML Label notaNota;
    @FXML Label notaPorcentaje;

    @FXML TableColumn idCol;
    @FXML TableColumn descripcionCol;
    @FXML TableColumn porcentajeCol;

    @FXML CheckBox chkIncluirInactivos;

    @Autowired
    ImpuestoRepo impuestoRepo;

    @Autowired
    Validator validator;

    public static final Logger logger = Logger.getLogger(ImpuestosCRUDController.class);

    ObservableList<Impuesto> impuestos;

    Impuesto selectedImpuesto;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        idCol.setCellValueFactory(new PropertyValueFactory<Impuesto, Long>("id"));
        descripcionCol.setCellValueFactory(new PropertyValueFactory<Impuesto, String>("descripcion"));
        porcentajeCol.setCellValueFactory(new PropertyValueFactory<Impuesto, String>("porcentaje"));

        impuestos = FXCollections.observableArrayList();
        impuestosTableView.setItems(impuestos);

        llenarTabla();
        impuestosTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Impuesto>() {
            @Override
            public void changed(ObservableValue<? extends Impuesto> observableValue, Impuesto impuesto, Impuesto impuesto2) {
                if(impuesto2 != null) {
                    selectedImpuesto = impuesto2;
                    notaNota.setText("");
                    notaNombre.setText("");
                    notaPorcentaje.setText("");
                    txtDescripcion.textProperty().set(impuesto2.getDescripcion());
                    txtNotas.textProperty().set(impuesto2.getNotas());
                    NumberFormat df = DecimalFormat.getNumberInstance();
                    txtPorcentaje.textProperty().set(df.format(impuesto2.getPorcentaje()));
                    if(impuesto2.getId() != null)
                        txtId.textProperty().set(impuesto2.getId().toString());
                    else
                        txtId.textProperty().set("");
                }
            }
        });

    }

    public void llenarTabla() {
        Boolean soloActivos = !chkIncluirInactivos.isSelected();
        List<Impuesto> imptos = impuestoRepo.findByActivoAndDescripcionLike(soloActivos, "%"+txtBuscar.getText()+"%");
        impuestos.clear();
        impuestos.addAll(imptos);
    }

    @FXML
    public void agregarAction(ActionEvent event) {
        Impuesto imp = new Impuesto();
        selectedImpuesto = imp;
        impuestos.add(imp);
        impuestosTableView.getSelectionModel().select(imp);
    }

    @FXML
    /**
     * Ésta acción en realidad desactiva al proveedor
     */
    public void eliminarAction(ActionEvent event) {
        Impuesto imp = selectedImpuesto;
        if(selectedImpuesto != null && selectedImpuesto.getId() != null && impuestoRepo.exists(selectedImpuesto.getId())) {

            imp.setActivo(false);
            impuestoRepo.saveAndFlush(imp);
            llenarTabla();
            borrarCampos();
            logger.info("Impuesto inhabilitado. Atención: Los productos que hagan referencia a este impuesto seguirán aplicándolo!");
        }
    }

    private void borrarCampos() {
        txtId.setText("");
        txtDescripcion.setText("");
        txtNotas.setText("");
        txtPorcentaje.setText("");
        notaNota.setText("");
        notaNombre.setText("");
        notaPorcentaje.setText("");
        selectedImpuesto = null;
    }

    @FXML
    public void guardarAction(ActionEvent event) {
        Impuesto imp = selectedImpuesto != null ? selectedImpuesto : new Impuesto();
        imp.setDescripcion(txtDescripcion.getText());
        imp.setNotas( txtNotas.getText() );
        imp.setPorcentaje( new BigDecimal( txtPorcentaje.getText() ));

        if(validar(imp)) {
            impuestoRepo.saveAndFlush(imp);
            llenarTabla();
            impuestosTableView.getSelectionModel().select(imp);
            logger.info("Impuesto guardado!");
        }
    }

    private TimerBusqueda timerBusqueda;
    @FXML
    private void onBusquedaKey(KeyEvent event) {
        String txtBusqueda = txtBuscar.getText();
        if ( txtBusqueda != null && !txtBusqueda.isEmpty() ) {
            if(timerBusqueda != null && timerBusqueda.isAlive()) { timerBusqueda.cancelar(); }
            this.timerBusqueda = new TimerBusqueda();
            timerBusqueda.start();
        }
    }

    @FXML
    private void mostrarInactivosAction(ActionEvent event) {
        llenarTabla();
    }

    private boolean validar(Impuesto ldp) {
        DataBinder binder = new DataBinder(ldp);
        binder.setValidator(validator);
        binder.validate();
        BindingResult bindingResult = binder.getBindingResult();

        if(bindingResult.hasErrors()) {
            for( ObjectError oe : bindingResult.getAllErrors() ) {
                if(oe.getClass() == FieldError.class) {
                    FieldError fe = (FieldError) oe;
                    if(fe.getField().equals("descripcion"))  { notaNombre.setText(fe.getDefaultMessage());   }
                    if(fe.getField().equals("nota"))    { notaNota.setText(fe.getDefaultMessage()); }
                    if(fe.getField().equals("porcentaje"))    { notaPorcentaje.setText(fe.getDefaultMessage()); }
                } else {
                    logger.info(oe.getDefaultMessage());
                }
            }
        } else {
            notaNombre.setText("");
            notaNota.setText("");
            notaPorcentaje.setText("");
            return true;
        }
        return false;
    }

    class TimerBusqueda extends Thread
    {
        boolean busquedaActiva = true;

        public void run()
        {
            synchronized(this)
            {
                busquedaActiva = true;
                try { this.wait(500); } catch(Exception e) { logger.error("Error en el timer de búsqueda automática", e); }
                if(busquedaActiva) {
                    ImpuestosCRUDController.this.llenarTabla();
                }
            }
        }
        void cancelar()
        {
            busquedaActiva = false;
            try { this.notify(); } catch(Exception e) {}
        }
    }
}
