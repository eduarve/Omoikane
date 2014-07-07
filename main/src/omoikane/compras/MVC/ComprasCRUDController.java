package omoikane.compras.MVC;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 10/11/13
 * Time: 01:00
 * To change this template use File | Settings | File Templates.
 */

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import jfxtras.labs.scene.control.CalendarTextField;
import omoikane.compras.entities.Compra;
import omoikane.inventarios.tomaInventario.ConteoInventario;
import omoikane.inventarios.tomaInventario.TomaInventarioController;
import omoikane.proveedores.Proveedor;
import omoikane.repository.CompraRepo;
import omoikane.repository.ConteoInventarioRepo;
import omoikane.repository.ProveedorRepo;
import omoikane.sistema.SceneOverloaded;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.TransactionManager;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


public class ComprasCRUDController
        implements Initializable {

    @FXML //  fx:id="panelItem"
    private AnchorPane contenido; // Value injected by FXMLLoader

    @FXML
    TableView<Compra> table;

    @FXML TableColumn<Compra, Date> fechaCol;
    @FXML TableColumn<Compra, String> folioCol;
    @FXML TableColumn<Compra, String> proveedorCol;

    @FXML ChoiceBox<Proveedor> proveedorChoiceBox;
    @FXML CheckBox chkEstadoPago;
    @FXML CalendarTextField txDesde;
    @FXML CalendarTextField txHasta;

    @FXML
    Button btnNuevo;

    @Autowired
    @Qualifier("compraView")
    Scene view;

    @Autowired
    CompraRepo compraRepo;

    @Autowired
    ProveedorRepo proveedorRepo;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    JpaTransactionManager transactionManager;

    CompraController compraController;

    @FXML
    public void onNuevoClic() {
        compraController.initModel();
    }

    @FXML
    public void onRecargar() {
        TransactionTemplate transactionTemplate = new TransactionTemplate((PlatformTransactionManager) transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            public void doInTransactionWithoutResult(TransactionStatus status) {
                Session sess = entityManager.unwrap(Session.class);
                Criteria crit = sess.createCriteria(Compra.class);

                // ** Filtros **
                // Por proveedor. Si el ID  es nulo (Proveedor dummy) no se filtra por proveedor
                if(proveedorChoiceBox.getValue().getId() != null)
                    crit.add(Restrictions.eq("proveedor", proveedorChoiceBox.getValue()));
                // Por estado del pago. Si el estado del checkbox es intermedio, omitir.
                if(!chkEstadoPago.isIndeterminate())
                    crit.add(Restrictions.eq("estadoPago", chkEstadoPago.isSelected()?Compra.EstadoPago.PAGADA:Compra.EstadoPago.IMPAGA));
                //Si se han establecido fechas entonces se filtra
                if(txDesde.getValue() != null && txHasta != null)
                    crit.add(Restrictions.between("fecha", txDesde.getValue().getTime(), txHasta.getValue().getTime()));

                // ** Ordenación **
                crit.addOrder(Order.desc("fecha"));

                // ** Paginación **
                crit.setMaxResults(100);

                List<Compra> compraList = crit.list();
                table.setItems(FXCollections.observableArrayList( compraList ));
            }
        });

        /*
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Compra> criteria = cb.createQuery(Compra.class);
        criteria.from(Compra.class);
        criteria.select()

        TypedQuery <Compra> tq = entityManager.createQuery(criteria);
        List<Compra> compraList = tq.getResultList();*/
        //List<Compra> compraList = compraRepo.findAll(new PageRequest(0, 25, new Sort(Order.DESCENDING, "fecha")));
        //table.setItems(FXCollections.observableArrayList( compraList ));
    }

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert contenido != null : "fx:id=\"panelItem\" was not injected: check your FXML file 'ConteoInventarioCRUDController.fxml'.";

        // initialize your logic here: all @FXML variables will have been injected

        fechaCol        .setCellValueFactory(new PropertyValueFactory<Compra, Date>("fecha"));
        folioCol        .setCellValueFactory(new PropertyValueFactory<Compra, String>("folioOrigen"));
        proveedorCol    .setCellValueFactory(new PropertyValueFactory<Compra, String>("proveedor"));


        contenido.getChildren().clear();
        AnchorPane.setBottomAnchor(view.getRoot(), 0d);
        AnchorPane.setTopAnchor(view.getRoot(), 0d);
        AnchorPane.setLeftAnchor(view.getRoot(), 0d);
        AnchorPane.setRightAnchor(view.getRoot(), 0d);
        contenido.getChildren().setAll(view.getRoot());


        compraController = (CompraController) ((SceneOverloaded)view).getController();
        btnNuevo.disableProperty().bind( compraController.archivarButton.disabledProperty().not() );

        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Compra>() {
            @Override
            public void changed(ObservableValue<? extends Compra> observableValue, Compra compra, Compra compra2) {
                compraController.initModel(compra2);
            }
        });

        //*************************************************************
        //Configurar celda con boton eliminar transacción. Cortesía de: https://gist.github.com/jewelsea/3081826
        //*************************************************************
        TableColumn<Compra, Compra.EstadoPago> actionCol = new TableColumn<>("Estado Pago");
        actionCol.setSortable(false);

        actionCol.setCellValueFactory(new PropertyValueFactory<Compra, Compra.EstadoPago>("estadoPago"));

        actionCol.setCellFactory(new Callback<TableColumn<Compra, Compra.EstadoPago>, TableCell<Compra, Compra.EstadoPago>>() {
            @Override public TableCell<Compra, Compra.EstadoPago> call(TableColumn<Compra, Compra.EstadoPago> tableColumn) {
                ActionsCell ac = new ActionsCell(table);
                return ac;
            }
        });

        table.getColumns().add(actionCol);
        //*************************************************************

        //*********************************************
        // Inicializar filtro de proveedores
        //*********************************************

        //Cargo todos los proveedores activos
        List<Proveedor> proveedors = proveedorRepo.findAllActive();
        //Se crea una proveedor dummy para representar un filtro nulo que abarque todos
        Proveedor proveedorNulo = new Proveedor();
        proveedorNulo.setNombre("Todos los proveedores");
        ObservableList<Proveedor> proveedorObservableList = FXCollections.observableList(proveedors);
        proveedorObservableList.add(0, proveedorNulo);

        proveedorChoiceBox.setItems(proveedorObservableList);
        proveedorChoiceBox.setValue(proveedorNulo);

        //*********************************************
        // Inicializar filtro de estado de pago
        //*********************************************



        // ** Cargar modelo **
        onRecargar();
    }

    private class ActionsCell extends TableCell<Compra, Compra.EstadoPago> {
        // a button for adding a new person.
        final Button delButton       = new Button("---");
        final Label label = new Label("-");
        // pads and centers the add button in the cell.
        final StackPane paddedButton = new StackPane();
        final HBox hBox = new HBox();

        /**
         * ActionsCell constructor
         * @param table the table to which a new person can be added.
         */
        ActionsCell(final TableView table) {

            paddedButton.setPadding(new javafx.geometry.Insets(3, 0, 0, 0));
            label.setMinWidth(70);
            hBox.getChildren().add(label);
            hBox.getChildren().add(delButton);
            paddedButton.getChildren().add(hBox);
            final TableCell<Compra, Compra.EstadoPago> c = this;

            delButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    //Inicialización de variables auxiliares
                    final TableRow tableRow = c.getTableRow();
                    final Integer idx = tableRow.getIndex();

                    //Persistencia del estado de pago en  la BD
                    final Compra compra= (Compra) tableRow.getTableView().getItems().get(idx);
                    Compra.EstadoPago nuevoEstadoPago =
                            compra.getEstadoPago() == Compra.EstadoPago.IMPAGA
                            ? Compra.EstadoPago.PAGADA
                            : Compra.EstadoPago.IMPAGA;
                    compra.setEstadoPago(nuevoEstadoPago);

                    compraRepo.saveAndFlush(compra);

                    //Cambios en la GUI
                    //Remuevo la fila que actualicé
                    tableRow.getTableView().getItems().removeAll(compra);

                    //Reañado la fila que actualicé para mostrar los cambios (ya que Compra no es observable)
                    //  Es necesario poner en cola de ejecución el segundo cambio a la GUI ya que no es
                    //  instantáneo el primero
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            tableRow.getTableView().getItems().add(idx, compra);
                        }
                    });

                }
            });
        }

        /** places an add button in the row only if the row is not empty. */
        @Override protected void updateItem(Compra.EstadoPago item, boolean empty) {
            super.updateItem(item, empty);
            item = item == null ? Compra.EstadoPago.IMPAGA : item;
            switch(item) {
                case PAGADA:
                    delButton.setText("Marcar impaga");
                    label.setTextFill(Color.GREEN);
                    break;
                case IMPAGA:
                    delButton.setText("Marcar pagada");
                    label.setTextFill(Color.RED);
                    break;
            }
            label.setText(String.valueOf(item));
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            }
        }
    }
}
