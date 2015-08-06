package omoikane.artemisa.presentation;

import javafx.application.Platform;
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
import omoikane.artemisa.entity.Paciente;
import omoikane.artemisa.reports.PacientePrint;
import omoikane.proveedores.Proveedor;
import omoikane.sistema.Permisos;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.*;

import java.net.URL;
import java.text.DateFormat;
import java.util.List;
import java.util.ResourceBundle;
import omoikane.artemisa.PacienteRepo;
import org.synyx.hades.domain.PageRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 25/02/13
 * Time: 08:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class PacientesController implements Initializable {
    @FXML TextField txtBuscar;
    @FXML TextField txtId;
    @FXML TextField txtNombre;

    @FXML private Button imprimirButton; // Value injected by FXMLLoader

    @FXML TableView<Paciente> pacientesTable;
    @FXML Label idLabel;
    @FXML TextField txtHabitacion;
    @FXML TextField responsableTxt;
    @FXML TextField edadTxt;
    @FXML TextArea anotacionTxt;

    @FXML Label notaNombre;
    @FXML Label notaHabitacion;
    @FXML Label ingresoDateLabel;

    @FXML TableColumn habitacionCol;
    @FXML TableColumn idCol;
    @FXML TableColumn nombreCol;

    @FXML CheckBox chkIncluirInactivos;

    @Autowired
    PacienteRepo pacienteRepo;

    @Autowired
    Validator validator;

    public static final Logger logger = Logger.getLogger(PacientesController.class);

    ObservableList<Paciente> pacientes;

    Paciente selectedPaciente;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        habitacionCol.setCellValueFactory(new PropertyValueFactory<Paciente, String>("habitacion"));
        idCol.setCellValueFactory(new PropertyValueFactory<Paciente, String>("id"));
        nombreCol.setCellValueFactory(new PropertyValueFactory<Paciente, String>("nombre"));

        pacientes = FXCollections.observableArrayList();
        pacientesTable.setItems(pacientes);

        llenarTabla();
        pacientesTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Paciente>() {
            @Override
            public void changed(ObservableValue<? extends Paciente> observableValue, Paciente paciente, Paciente paciente2) {
                if(paciente2 != null) {
                    borrarCampos();
                    selectedPaciente = paciente2;
                    llenarCampos(paciente2);
                }
            }
        });


        imprimirButton.disableProperty().bind(txtId.textProperty().isEqualTo(""));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtBuscar.requestFocus();
            }
        });
    }

    public void onImprimir(ActionEvent event) {
        PacientePrint pacientePrint = new PacientePrint(selectedPaciente);
        pacientePrint.show();
    }

    public void llenarTabla() {
        Boolean soloActivos = chkIncluirInactivos.isSelected();
        List<Paciente> pacs = pacienteRepo.findByLiquidadoAndNombreLikeOrdered(soloActivos, "%" + txtBuscar.getText() + "%", new PageRequest(0, 10000));
        pacientes.clear();
        pacientes.addAll(pacs);
    }

    @FXML
    public void agregarAction(ActionEvent event) {
        /*
        Paciente paciente = new Paciente();
        selectedPaciente = paciente;
        pacientes.add(paciente);
        pacientesTable.getSelectionModel().select(paciente);
        */
        selectedPaciente = null;
        pacientesTable.getSelectionModel().clearSelection();
        borrarCampos();
        txtNombre.requestFocus();
    }

    /**
     * Ésta acción en realidad desactiva al paciente. Nota: Ésta función ya no está en uso, sigue aquí solo para referencia
     */
    public void eliminarAction(ActionEvent event) {
        Paciente paciente = selectedPaciente;
        if(selectedPaciente != null && selectedPaciente.getId() != null && pacienteRepo.exists(selectedPaciente.getId())) {
            paciente.setLiquidado(true);
            pacienteRepo.saveAndFlush(paciente);
            llenarTabla();
            borrarCampos();
            logger.info("Paciente inhabilitado!");
        }
    }

    private void borrarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtHabitacion.setText("");
        notaHabitacion.setText("");
        notaNombre.setText("");
        anotacionTxt.setText("");
        responsableTxt.setText("");
        edadTxt.setText("");
        ingresoDateLabel.setText("");
        selectedPaciente = null;
    }

    private void llenarCampos(Paciente p) {
        idLabel.textProperty().set(p.getId().toString());
        txtNombre.textProperty().set(p.getNombre());
        txtHabitacion.textProperty().set(p.getHabitacion());
        edadTxt.textProperty().set(( p.getEdad() ));
        responsableTxt.textProperty().set( p.getResponsable() );
        anotacionTxt.textProperty().set( p.getAnotacion() );
        ingresoDateLabel.setText("Ingreso: " + DateFormat.getDateTimeInstance().format( p.getEntrada() ));
        if(p.getId() != null)
            txtId.textProperty().set(p.getId().toString());
        else
            txtId.textProperty().set("");
    }

    @FXML
    public void guardarAction(ActionEvent event) {
        Paciente paciente = selectedPaciente != null ? selectedPaciente : new Paciente();
        paciente.setNombre( txtNombre.getText() );
        paciente.setHabitacion(txtHabitacion.getText());
        paciente.setEdad( edadTxt.getText() );
        paciente.setResponsable( responsableTxt.getText() );
        paciente.setAnotacion( anotacionTxt.getText() );

        if(validar(paciente)) {
            pacienteRepo.saveAndFlush(paciente);
            llenarTabla();
            pacientesTable.getSelectionModel().select(paciente);
            logger.info("Paciente guardado");
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

    private boolean validar(Paciente paciente) {
        DataBinder binder = new DataBinder(paciente);
        binder.setValidator(validator);
        binder.validate();
        BindingResult bindingResult = binder.getBindingResult();

        if(bindingResult.hasErrors()) {
            for( ObjectError oe : bindingResult.getAllErrors() ) {
                if(oe.getClass() == FieldError.class) {
                    FieldError fe = (FieldError) oe;
                    if(fe.getField().equals("nombre"))        { notaNombre.setText(fe.getDefaultMessage());   }
                    if(fe.getField().equals("habitacion"))    { notaHabitacion.setText(fe.getDefaultMessage()); }
                } else {
                    logger.info(oe.getDefaultMessage());
                }
            }
        } else {
            notaNombre.setText("");
            notaHabitacion.setText("");
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
                    PacientesController.this.llenarTabla();
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
