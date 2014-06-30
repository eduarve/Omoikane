package omoikane.producto.listadeprecios;

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
import omoikane.proveedores.Proveedor;
import omoikane.repository.ListaDePreciosRepo;
import omoikane.repository.ProveedorRepo;
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
public class ListaDePreciosCRUDController implements Initializable {
    @FXML TextField txtBuscar;
    @FXML TextField txtId;
    @FXML TextField txtDescripcion;

    @FXML TableView<ListaDePrecios> listaDePreciosTableView;
    @FXML TextArea txtNotas;

    @FXML Label notaNombre;
    @FXML Label notaNota;

    @FXML TableColumn idCol;
    @FXML TableColumn descripcionCol;

    @FXML CheckBox chkIncluirInactivos;

    @Autowired
    ListaDePreciosRepo listaDePreciosRepo;

    @Autowired
    Validator validator;

    public static final Logger logger = Logger.getLogger(ListaDePreciosCRUDController.class);

    ObservableList<ListaDePrecios> listasDePrecios;

    ListaDePrecios selectedListaDePrecios;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        idCol.setCellValueFactory(new PropertyValueFactory<Proveedor, Long>("id"));
        descripcionCol.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("descripcion"));

        listasDePrecios = FXCollections.observableArrayList();
        listaDePreciosTableView.setItems(listasDePrecios);

        llenarTabla();
        listaDePreciosTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ListaDePrecios>() {
            @Override
            public void changed(ObservableValue<? extends ListaDePrecios> observableValue, ListaDePrecios listaDePrecios, ListaDePrecios listaDePrecios2) {
                if(listaDePrecios2 != null) {
                    selectedListaDePrecios = listaDePrecios2;
                    notaNota.setText("");
                    notaNombre.setText("");
                    txtDescripcion.textProperty().set(listaDePrecios2.getDescripcion());
                    txtNotas.textProperty().set(listaDePrecios2.getNotas());
                    if(listaDePrecios2.getId() != null)
                        txtId.textProperty().set(listaDePrecios2.getId().toString());
                    else
                        txtId.textProperty().set("");
                }
            }
        });

    }

    public void llenarTabla() {
        Boolean soloActivos = !chkIncluirInactivos.isSelected();
        List<ListaDePrecios> ldp = listaDePreciosRepo.findByActivoAndDescripcionLike(soloActivos, "%"+txtBuscar.getText()+"%");
        listasDePrecios.clear();
        listasDePrecios.addAll(ldp);
    }

    @FXML
    public void agregarAction(ActionEvent event) {
        ListaDePrecios ldp = new ListaDePrecios();
        selectedListaDePrecios = ldp;
        listasDePrecios.add(ldp);
        listaDePreciosTableView.getSelectionModel().select(ldp);
    }

    @FXML
    /**
     * Ésta acción en realidad desactiva al proveedor
     */
    public void eliminarAction(ActionEvent event) {
        ListaDePrecios ldp = selectedListaDePrecios;
        if(selectedListaDePrecios != null && selectedListaDePrecios.getId() != null && listaDePreciosRepo.exists(selectedListaDePrecios.getId())) {
            ldp.setActivo(false);
            listaDePreciosRepo.saveAndFlush(ldp);
            llenarTabla();
            borrarCampos();
            logger.info("Lista de precios inhabilitada!");
        }
    }

    private void borrarCampos() {
        txtId.setText("");
        txtDescripcion.setText("");
        txtNotas.setText("");
        notaNota.setText("");
        notaNombre.setText("");
        selectedListaDePrecios = null;
    }

    @FXML
    public void guardarAction(ActionEvent event) {
        ListaDePrecios ldp = selectedListaDePrecios != null ? selectedListaDePrecios : new ListaDePrecios();
        ldp.setDescripcion(txtDescripcion.getText());
        ldp.setNotas( txtNotas.getText() );

        if(validar(ldp)) {
            listaDePreciosRepo.saveAndFlush(ldp);
            llenarTabla();
            listaDePreciosTableView.getSelectionModel().select(ldp);
            logger.info("Lista de precios guardada!");
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

    private boolean validar(ListaDePrecios ldp) {
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
                    ListaDePreciosCRUDController.this.llenarTabla();
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
