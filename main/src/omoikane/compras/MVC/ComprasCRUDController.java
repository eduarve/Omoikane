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
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
import jfxtras.scene.control.CalendarTextField;
import omoikane.compras.entities.Compra;
import omoikane.inventarios.tomaInventario.ConteoInventario;
import omoikane.inventarios.tomaInventario.TomaInventarioController;
import omoikane.proveedores.Proveedor;
import omoikane.repository.CompraRepo;
import omoikane.repository.ConteoInventarioRepo;
import omoikane.repository.ProveedorRepo;
import omoikane.sistema.Permisos;
import omoikane.sistema.SceneOverloaded;
import org.apache.log4j.Logger;
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
import javax.swing.*;
import javax.transaction.TransactionManager;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


public class ComprasCRUDController
        implements Initializable {

    @FXML //  fx:id="panelItem"
    private AnchorPane contenido; // Value injected by FXMLLoader

    @FXML
    TableView<Compra> table;

    @FXML TableColumn<Compra, Date> fechaCol;
    @FXML TableColumn<Compra, String> folioCol;
    @FXML TableColumn<Compra, String> proveedorCol;
    TableColumn<Compra, Compra.EstadoPago> actionCol = new TableColumn<>("Estado Pago");

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

    public static Logger logger = Logger.getLogger(ComprasCRUDController.class);

    CompraController compraController;
    private javax.swing.JInternalFrame JInternalFrame;
    private JFXPanel fxPanel;

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
                if(txDesde.calendarProperty().getValue() != null && txHasta != null)
                    crit.add(Restrictions.between("fecha", txDesde.calendarProperty().getValue().getTime(), txHasta.calendarProperty().getValue().getTime()));

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

        // - Redimensionar las columnas para que la suma de sus anchos sea igual al ancho de la tabla - //
        fechaCol        .prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        folioCol        .prefWidthProperty().bind(table.widthProperty().multiply(0.14));
        proveedorCol    .prefWidthProperty().bind(table.widthProperty().multiply(0.40));
        actionCol       .prefWidthProperty().bind(table.widthProperty().multiply(0.255));

        compraController = (CompraController) ((SceneOverloaded)view).getController();
        compraController.setParent(this);
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

    public JInternalFrame getJInternalFrame() {
        return JInternalFrame;
    }

    public void setJInternalFrame(JInternalFrame JInternalFrame) {
        this.JInternalFrame = JInternalFrame;
    }

    public JFXPanel getFXPanel() {
        return fxPanel;
    }

    public void setFXPanel(JFXPanel fxPanel) {
        this.fxPanel = fxPanel;
    }

    private class ActionsCell extends TableCell<Compra, Compra.EstadoPago> {
        // a button for adding a new person.
        final Button pagarButton       = new Button("---");
        final Label label = new Label("-");
        // pads and centers the add button in the cell.
        final StackPane paddedButton = new StackPane();
        final HBox hBox = new HBox();

        /**
         * ActionsCell constructor
         * @param table the table to which a new person can be added.
         */
        ActionsCell(final TableView table) {
            // -- Formato del botón -- //
            paddedButton.setMaxHeight(18);
            pagarButton.setFont(new Font("Verdana", 10));
            paddedButton.setPadding(new javafx.geometry.Insets(1, 0, 0, 0));

            // -- Configuración del botón y su acción -- //
            label.setMinWidth(70);
            hBox.getChildren().add(label);
            hBox.getChildren().add(pagarButton);
            paddedButton.getChildren().add(hBox);
            final TableCell<Compra, Compra.EstadoPago> c = this;

            pagarButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    //Verifico el permiso para realizar ésta acción
                    if(!omoikane.sistema.Usuarios.cerrojo(Permisos.PMA_MARCAR_COMPRA_PAGADA)){ logger.info("Acceso Denegado"); return; }

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

                    //Si ha sido marcada PAGADA, entonces se solicitará la fecha de pago
                    if(nuevoEstadoPago == Compra.EstadoPago.PAGADA) {
                        Optional<Date> fechaOpt = pedirFecha();
                        //Si no se da una fecha, no se marcará como pagada ésta compra
                        try {
                            compra.setFechaPago(fechaOpt.get());
                        } catch(NoSuchElementException nsee) {
                            return ;
                        }
                    }

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

        /**
         * Solicita al usuario que especifique una fecha de pago
         * @return
         */
        private Optional<Date> pedirFecha() {
            //Crea el diálogo para pedir fecha de pago
            Dialog<Date> dialog = new Dialog<>();
            dialog.setTitle("Compra pagada");
            dialog.setHeaderText("Fecha de pago de compra");

            // Set the button types.
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

            // Crea el datepicker para poder seleccionar una fecha
            DatePicker dp = new DatePicker();

            // Crea el layout del dialogo
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Establece el valor de date picker como el valor de retorno del diálogo
            dialog.setResultConverter(button -> {
                LocalDate localdate = dp.getValue();
                if(localdate == null) return null;
                Date date = Date.from(localdate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                return date;
            });

            // Une todas las partes
            grid.add(new Label("¿En que fecha se realizó el pago de esta compra?"), 0, 0);
            grid.add(dp, 0, 1);
            dialog.getDialogPane().setContent(grid);

            return dialog.showAndWait();
        }

        /** places an add button in the row only if the row is not empty. */
        @Override protected void updateItem(Compra.EstadoPago item, boolean empty) {
            //Accede al objeto que es representado por la fila que posee a esta celda
            Compra compra = (Compra) this.getTableRow().getItem();
            //Null-safe, para celdas sin domino no hay contenido en esta celda
            if(compra == null) return;
            //Actualizo el valor interno del modelo de la tabla
            super.updateItem(item, empty);

            item = item == null ? Compra.EstadoPago.IMPAGA : item;
            switch(item) {
                case PAGADA:
                    renderPagada(compra);
                    break;
                case IMPAGA:
                    renderImpaga();
                    break;
            }

            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            }
        }

        //Pone el display de la celda en el formato PAGADA
        private void renderPagada(Compra parent) {
            //Inicializo un formateador de fecha del tipo día / mes / año
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            pagarButton.setText("Marcar impaga");
            if(parent.getFechaPago() == null)
                    label.setText("Pagada, falta fecha");
                else
                    label.setText("Pagada el "+sdf.format( parent.getFechaPago() ));
            label.setTextFill(Color.GREEN);
        }

        //Pone el display de la celda en el formato IMPAGA
        private void renderImpaga() {
            pagarButton.setText("Marcar pagada");
            label.setText("Impaga");
            label.setTextFill(Color.RED);
        }
    }
}
