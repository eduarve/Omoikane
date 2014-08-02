package omoikane.producto.departamento;

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
import omoikane.producto.ListaDePrecios;
import omoikane.producto.departamento.Departamento;
import omoikane.proveedores.Proveedor;
import omoikane.repository.DepartamentoRepo;
import omoikane.repository.ListaDePreciosRepo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 25/02/13
 * Time: 08:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class DepartamentoCRUDController implements Initializable {
    @FXML TextField txtBuscar;
    @FXML TextField txtId;
    @FXML TextField txtNombre;

    @FXML TableView<Departamento> departamentosTableView;
    @FXML TextArea txtNotas;

    @FXML Label notaNombre;
    @FXML Label notaNota;

    @FXML TableColumn idCol;
    @FXML TableColumn nombreCol;

    @FXML CheckBox chkIncluirInactivos;

    @Autowired
    DepartamentoRepo departamentoRepo;

    @Autowired
    Validator validator;

    public static final Logger logger = Logger.getLogger(DepartamentoCRUDController.class);

    ObservableList<Departamento> departamentos;

    Departamento selectedDepartamento;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        idCol.setCellValueFactory(new PropertyValueFactory<Proveedor, Long>("id"));
        nombreCol.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("nombre"));

        departamentos = FXCollections.observableArrayList();
        departamentosTableView.setItems(departamentos);

        llenarTabla();
        departamentosTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Departamento>() {
            @Override
            public void changed(ObservableValue<? extends Departamento> observableValue, Departamento departamento, Departamento departamento2) {
                if(departamento2 != null) {
                    selectedDepartamento = departamento2;
                    notaNota.setText("");
                    notaNombre.setText("");
                    txtNombre.textProperty().set(departamento2.getNombre());
                    txtNotas.textProperty().set(departamento2.getNotas());
                    if(departamento2.getId() != null)
                        txtId.textProperty().set(departamento2.getId().toString());
                    else
                        txtId.textProperty().set("");
                }
            }
        });

    }

    public void llenarTabla() {
        Boolean soloActivos = !chkIncluirInactivos.isSelected();
        List<Departamento> dep = departamentoRepo.findByActivoAndNombreLike(soloActivos, "%"+txtBuscar.getText()+"%");
        departamentos.clear();
        departamentos.addAll(dep);
    }

    @FXML
    public void agregarAction(ActionEvent event) {
        Departamento dep = new Departamento();
        selectedDepartamento = dep;
        departamentos.add(dep);
        departamentosTableView.getSelectionModel().select(dep);
    }

    @FXML
    /**
     * Ésta acción en realidad desactiva el departamento
     */
    public void eliminarAction(ActionEvent event) {
        Departamento dep = selectedDepartamento;
        if(selectedDepartamento != null && selectedDepartamento.getId() != null && departamentoRepo.exists(selectedDepartamento.getId())) {
            dep.setActivo(false);
            departamentoRepo.saveAndFlush(dep);
            llenarTabla();
            borrarCampos();
            logger.info("Departamento inhabilitado!");
        }
    }

    private void borrarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtNotas.setText("");
        notaNota.setText("");
        notaNombre.setText("");
        selectedDepartamento = null;
    }

    @FXML
    public void guardarAction(ActionEvent event) {
        Departamento dep = selectedDepartamento != null ? selectedDepartamento : new Departamento();
        dep.setNombre(txtNombre.getText());
        dep.setNotas( txtNotas.getText() );

        if(validar(dep)) {
            departamentoRepo.saveAndFlush(dep);
            llenarTabla();
            departamentosTableView.getSelectionModel().select(dep);
            logger.info("Cambios almacenados");
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

    private boolean validar(Departamento dep) {
        DataBinder binder = new DataBinder(dep);
        binder.setValidator(validator);
        binder.validate();
        BindingResult bindingResult = binder.getBindingResult();

        if(bindingResult.hasErrors()) {
            for( ObjectError oe : bindingResult.getAllErrors() ) {
                if(oe.getClass() == FieldError.class) {
                    FieldError fe = (FieldError) oe;
                    if(fe.getField().equals("nombre"))  { notaNombre.setText(fe.getDefaultMessage());   }
                    if(fe.getField().equals("nota"))    { notaNota.setText(fe.getDefaultMessage()); }
                } else {
                    logger.info(oe.getDefaultMessage());
                }
            }
        } else {
            notaNombre.setText("");
            notaNota.setText("");
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
                    DepartamentoCRUDController.this.llenarTabla();
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
