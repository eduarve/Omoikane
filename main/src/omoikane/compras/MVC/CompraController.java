package omoikane.compras.MVC;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.Callback;
import name.antonsmirnov.javafx.dialog.Dialog;
import omoikane.compras.ImportadorXML;
import omoikane.compras.entities.Compra;
import omoikane.compras.entities.ItemCompra;
import omoikane.principal.Articulos;
import omoikane.proveedores.Proveedor;
import omoikane.entities.Usuario;
import omoikane.producto.Articulo;
import omoikane.repository.CompraRepo;
import omoikane.repository.ProveedorRepo;
import omoikane.sistema.Usuarios;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormatSymbols;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 27/02/13
 * Time: 05:50 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class CompraController implements Initializable {

    public static final Logger logger = Logger.getLogger(CompraController.class);

    CompraEntityWrapper modelo;

    @Autowired
    EhCacheManagerFactoryBean cacheManager;

    @Autowired
    CompraSaveLogic logic;

    @Autowired
    ProveedorRepo proveedorRepo;

    @Autowired
    CompraRepo repo;

    @FXML TableView<ItemCompraEntityWrapper> itemsTable;
    @FXML TableColumn<ItemCompraEntityWrapper, String> codigoCol;
    @FXML TableColumn<ItemCompraEntityWrapper, String> nombreProductoCol;
    @FXML TableColumn<ItemCompraEntityWrapper, BigDecimal> cantidadCol;
    @FXML TableColumn<ItemCompraEntityWrapper, BigDecimal> costoCol;
    @FXML TableColumn<ItemCompraEntityWrapper, BigDecimal> importeCol;
          TableColumn<ItemCompraEntityWrapper, Boolean> actionCol = new TableColumn<>("Acciones");
    @FXML Label idLabel;
    @FXML Label fechaLabel;
    @FXML Label lblProveedor;
    @FXML TextField txtIdProveedor;
    @FXML TextField txtFolioOrigen;
    @FXML TextField codigoTextField;
    @FXML TextField costoTextField;
    @FXML TextField cantidadTextField;
    @FXML Label descripcionLabel;
    @FXML Button agregarButton;
    @FXML Button descartarButton;
    @FXML Button archivarButton;
    @FXML Button imprimirButton;
    @FXML Button importarButton;
    @FXML AnchorPane mainPane;
    @FXML Label subtotalLabel;

    private Articulo capturaArticulo;
    private HashMap<Long, Articulo> indice;
    private ComprasCRUDController parent;

    public AnchorPane getMainPane() { return mainPane; }

    public CompraEntityWrapper getModel() { return modelo; }

    public CompraSaveLogic getLogic() { return logic; }

    public void setModelo(CompraEntityWrapper modelo) {
        this.modelo = modelo;
    }

    public CompraController() {
        indice = new HashMap<>();
    }

    @FXML public void archivarAction(ActionEvent actionEvent) {
        //Mini validación
        if(modelo.getProveedor().get() == null) { logger.info("Falta introducir proveedor"); return; }
        if(modelo.getFolioOrigen().get() == null || modelo.getFolioOrigen().get().equals("")) { logger.info("Falta introducir folio de origen"); return; }

        //Ahora archivar
        Dialog.buildConfirmation("Confirmación", "Al aplicar esta compra, los stocks de los productos quedarán actualizados y no se podrá editar este documento, la operación no es reversible, ¿Está seguro de continuar?")
                .addYesButton(new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        modelo.setCompletado(true); //Compra tentativamente completada, se revierte el estátus si ocurre una excepción
                        mainPane.setDisable(true);
                        Task<Void> persistTask = new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                logic.concluir(modelo);
                                return null;
                            }
                        };
                        persistTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent workerStateEvent) {
                                itemsTable.edit(itemsTable.getSelectionModel().getSelectedIndex() + 1, itemsTable.getColumns().get(0));
                                logger.info("Captura de compra archivada y aplicada");
                                mainPane.setDisable(false);
                            }
                        });
                        persistTask.setOnFailed((workerStateEvent) -> {
                                Throwable t = workerStateEvent.getSource().getException();
                                logger.error(t.getMessage(), t);
                                modelo.setCompletado(false);
                                mainPane.setDisable(false);
                            }
                        );
                        new Thread(persistTask).start();
                    }
                })
                .addNoButton(new EventHandler() {
                    @Override
                    public void handle(Event event) {
                    }
                })
                .build().show();

    }

    @FXML public void onEliminarAction(ActionEvent actionEvent) {
        Task<Void> deleteTask = deleteModel();
        deleteTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                initModel();
                logger.info("Captura eliminada");
            }
        });
        new Thread(deleteTask).start();
    }

    @FXML public void onImprimir(ActionEvent actionEvent) {

    }

    @FXML public void onImportar(ActionEvent actionEvent) {
        importar();
    }

    private void importar() {
        ImportadorXML importadorXML = new ImportadorXML(this);
        importadorXML.importDialog();
    }

    @FXML public void onAgregarAction(ActionEvent actionEvent) {
        if(capturaArticulo == null) return ;
        if(modelo.getCompletado().get()) return;

        if(indice.containsKey(capturaArticulo.getIdArticulo())) {
            logger.info("Artículo ya agregado. No se puede volver a agregar.");
            codigoTextField.requestFocus();
            return;
        }
        addItem(capturaArticulo, new BigDecimal(cantidadTextField.getText()), new BigDecimal(costoTextField.getText()));

        costoTextField.setText("");
        codigoTextField.setText("");
        descripcionLabel.setText("");
        cantidadTextField.setText("");
        codigoTextField.requestFocus();
        capturaArticulo = null;
    }

    public void addItem(Articulo capturaArticulo, BigDecimal cantidad, BigDecimal costoUnitario) {
        if(modelo.getCompletado().get()) return;

        if(indice.containsKey(capturaArticulo.getIdArticulo())) return;

        String codigo            = capturaArticulo.getCodigo();
        String descripcion       = capturaArticulo.getDescripcion();

        ItemCompra newItemBean = new ItemCompra(codigo, descripcion, cantidad, costoUnitario);
        newItemBean.setArticulo  ( capturaArticulo );

        ItemCompraEntityWrapper newItem = new ItemCompraEntityWrapper(newItemBean);
        modelo.addItem(newItem);
        indice.put(capturaArticulo.getIdArticulo(), capturaArticulo);
    }

    Character decimalSeparator = getDecimalSeparator();

    private Character getDecimalSeparator() {
        DecimalFormatSymbols custom=new DecimalFormatSymbols();
        return custom.getDecimalSeparator();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assert agregarButton != null : "fx:id=\"agregarButton\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert codigoCol != null : "fx:id=\"codigoCol\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert codigoTextField != null : "fx:id=\"codigoTextField\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert descripcionLabel != null : "fx:id=\"descripcionLabel\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert fechaLabel != null : "fx:id=\"fechaLabel\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert idLabel != null : "fx:id=\"idLabel\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert itemsTable != null : "fx:id=\"itemsTable\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert nombreProductoCol != null : "fx:id=\"nombreProductoCol\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";

        codigoCol        .setCellValueFactory(new PropertyValueFactory<ItemCompraEntityWrapper, String>("codigo"));
        nombreProductoCol.setCellValueFactory(new PropertyValueFactory<ItemCompraEntityWrapper, String>("nombre"));
        cantidadCol      .setCellValueFactory(new PropertyValueFactory<ItemCompraEntityWrapper, BigDecimal>("cantidad"));
        costoCol         .setCellValueFactory(new PropertyValueFactory<ItemCompraEntityWrapper, BigDecimal>("costoUnitario"));
        importeCol       .setCellValueFactory(new PropertyValueFactory<ItemCompraEntityWrapper, BigDecimal>("importe"));

        initModel();

        // - Redimensionar las columnas para que la suma de sus anchos sea igual al ancho de la tabla - //
        codigoCol        .prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.12));
        nombreProductoCol.prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.40));
        cantidadCol      .prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.12));
        costoCol         .prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.12));
        importeCol       .prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.12));
        actionCol        .prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.115));

        codigoTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    cantidadTextField.requestFocus();
                }
                if(event.getCode() == KeyCode.F1)
                    new MostrarCatalogoHandler(CompraController.this).handle();
            }
        });

        cantidadTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    costoTextField.requestFocus();
                }
            }
        });

        costoTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode() == KeyCode.ENTER) {
                    agregarButton.fire();
                }
            }
        });

        codigoTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                //Solo continuar si se está perdiendo el enfoque
                if(aBoolean2) return;
                Task<Void> findArticuloTask = findArticulo();
                new Thread(findArticuloTask).start();
            }
        });

        //*************************************************************
        //Configurar celda con boton eliminar transacción. Cortesía de: https://gist.github.com/jewelsea/3081826
        //*************************************************************
        actionCol.setMinWidth(80d);
        actionCol.setSortable(false);

        // define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
        actionCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ItemCompraEntityWrapper, Boolean>, ObservableValue<Boolean>>() {
            @Override public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<ItemCompraEntityWrapper, Boolean> features) {
                return new SimpleBooleanProperty(features.getValue() != null);
            }
        });

        // create a cell value factory with an add button for each row in the table.
        actionCol.setCellFactory(new Callback<TableColumn<ItemCompraEntityWrapper, Boolean>, TableCell<ItemCompraEntityWrapper, Boolean>>() {
            @Override public TableCell<ItemCompraEntityWrapper, Boolean> call(TableColumn<ItemCompraEntityWrapper, Boolean> personBooleanTableColumn) {
                return new ActionsCell(itemsTable);
            }
        });

        itemsTable.getColumns().add(actionCol);
        //*************************************************************

    }

    public JFXPanel getFXPanel() {
        return getParent().getFXPanel();
    }

    public ComprasCRUDController getParent() {
        return parent;
    }

    public JInternalFrame getJInternalFrame() {
        return getParent().getJInternalFrame();
    }

    public void setParent(ComprasCRUDController parent) {
        this.parent = parent;
    }

    private class ActionsCell extends TableCell<ItemCompraEntityWrapper, Boolean> {
        // a button for adding a new person.
        final Button delButton       = new Button("Borrar");
        // pads and centers the add button in the cell.
        final StackPane paddedButton = new StackPane();

        /**
         * ActionsCell constructor
         * @param table the table to which a new person can be added.
         */
        ActionsCell(final TableView table) {
            // -- Formato del botón -- //
            paddedButton.setMaxHeight(17);
            delButton.setFont(new Font("Verdana", 8));
            paddedButton.setPadding(new javafx.geometry.Insets(1, 0, 0, 0));

            // -- Configuración del botón y su acción -- //
            paddedButton.getChildren().add(delButton);
            final TableCell<ItemCompraEntityWrapper, Boolean> c = this;
            delButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    TableRow tableRow = c.getTableRow();
                    ItemCompraEntityWrapper item= (ItemCompraEntityWrapper) tableRow.getTableView().getItems().get(tableRow.getIndex());
                    for(int i = 0; i < modelo.getBean().getItems().size(); i++) {
                         if(modelo.getBean().getItems().get(i) == item.getBean() ) modelo.getBean().getItems().remove(i);
                    }
                    modelo.getItems().remove(tableRow.getIndex());
                    CompraController.this.indice.remove(item.getBean().getArticulo().getIdArticulo());
                    Platform.runLater(persistModel());
                }
            });
        }

        /** places an add button in the row only if the row is not empty. */
        @Override protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            } else {
                setGraphic(null);
            }
        }
    }

    /**
     * Método encargado de manejar la GUI al buscar un artículo.
     * El artículo es buscado mediante CompraSaveLogic.getArticulo
     * @return
     */
    private Task<Void> findArticulo() {
        Task findArticuloTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String   codigo = codigoTextField.getText();
                Articulo resultado = null;
                if(codigo.isEmpty()) return null;

                resultado = logic.getArticulo(codigo);

                final Articulo finalResultado = resultado;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if(finalResultado == null) {
                            descripcionLabel.setText("");
                            logger.info("No se encontró ningún producto con el código proporcionado");
                            return;
                        }
                        descripcionLabel.setText( finalResultado.getDescripcion() );
                        CompraController.this.capturaArticulo = finalResultado;
                    }
                });

                return null;
            }
        };
        return findArticuloTask;
    }

    public void initModel() {
        initModel(null);
    }

    /**
     * @see ComprasCRUDController
     * @param compra
     */
    public void initModel(Compra compra) {
        subtotalLabel.textProperty().unbind();
        imprimirButton.disableProperty().unbind();
        archivarButton.disableProperty().unbind();
        descartarButton.disableProperty().unbind();
        txtFolioOrigen.textProperty().unbind();
        lblProveedor.textProperty().unbind();
        txtIdProveedor.disableProperty().unbind();
        txtFolioOrigen.textProperty().unbind();
        actionCol.visibleProperty().unbind();
        if(modelo != null) modelo.getFolioOrigen().unbind();

        imprimirButton.setDisable(false);
        archivarButton.setDisable(false);
        descartarButton.setDisable(false);

        txtIdProveedor.setText("");

        modelo = loadOrCreateModel(compra);
        itemsTable.setItems(modelo.getItems());

        subtotalLabel.textProperty().bind( Bindings.format("$ %,.2f", modelo.subtotalProperty()) );
        imprimirButton.disableProperty().bind(modelo.getCompletado().not());
        archivarButton.disableProperty().bind(modelo.getCompletado());
        descartarButton.disableProperty().bind(modelo.getCompletado());
        fechaLabel.textProperty().bind(Bindings.convert(modelo.getDate()));
        //idLabel.textProperty().bind(Bindings.convert(modelo.getId()));
        lblProveedor.textProperty().bind(modelo.nombreProveedorProperty());
        txtFolioOrigen.setText( compra != null ? compra.getFolioOrigen() : "" );
        txtFolioOrigen.textProperty().set( modelo.getFolioOrigen().get() );
        modelo.getFolioOrigen().bind(txtFolioOrigen.textProperty());
        actionCol.visibleProperty().bind(modelo.getCompletado().not());

        modelo.getItems().addListener(new MyListChangeListener());
        txtIdProveedor.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                if(!aBoolean2) {
                    Platform.runLater(new SetProveedorTask());
                }
            }
        });

        /**
         * Generación del hashmap llamado índice. Sirve para rechazar artículos repetidos
         */
        indice = new HashMap<>();
        try {
            for (ItemCompraEntityWrapper ci : modelo.getItems()) {
                indice.put(ci.getBean().getArticulo().getIdArticulo(), ci.getBean().getArticulo());
            }
        } catch(NullPointerException npe) {
            logger.error("Artículo mal registrado. Si el problema persiste contacta a soporte técnico.", npe);
        }
    }

    class SetProveedorTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            try {
                if( modelo.getCompletado().get() ) return null;
                Long id = Long.parseLong( txtIdProveedor.getText() );
                Proveedor p = proveedorRepo.readByPrimaryKey( id );
                if(p == null) { logger.info("No exíste tal ID de proveedor"); return null; }
                modelo.setProveedor(p);
                new Thread(
                        persistModel()
                ).start();
            } catch (NumberFormatException nfe) {
                txtIdProveedor.setText("");
            } catch (Exception e) {
                logger.error("Error buscando proveedor", e);
            }
            return null;
        }
    }

    /**
     * Versión 4.4
     *      No se ofrece el mecanismo para persistir compras mientras se capturan (por bugs),
     *      por lo tanto cada vez que se inicia una compra se crea el modelo o se rescata una compra ya completada
     *      si el objeto Compra es provisto
     * Anterior a 4.4
     * Éste método inicializa el modelo del formulario, de una de las siguientes 3 maneras:
     * - Si le es dado un bean lo carga
     * - Si le es dado un bean nulo busca el último inventario incompleto y lo carga
     * - Si le es dado un bean nulo y no existe un inventario incompleto crea uno nuevo
     * */
    // Versión 4.4 @Transactional
    private CompraEntityWrapper loadOrCreateModel(Compra compra) {

        //Código anterior a 4.4 para persistir compra mientras se edita
        /*
        if(compra != null)
            compra = repo.find(compra.getId());
        if(compra == null)
            compra = repo.findByCompletado(false);
        if(compra == null) {
            compra = new Compra();
            compra.setFecha( new Date() );
            compra = repo.saveAndFlush(compra);
        }
        // Inicializar items
        compra.getItems().size();
        for (ItemCompra itemCompra : compra.getItems()) {
            itemCompra.getArticulo();
        }
        */
        if(compra != null) {
            compra = repo.find(compra.getId());
        } else {
            compra = new Compra();
            //El usuario se establece al momento de comenzar la captura
            compra.setUsuario(new Usuario(new Long(Usuarios.getIDUsuarioActivo())));
            //La fecha se establece al momento de guardarse
        }

        CompraEntityWrapper compraEntityWrapper = new CompraEntityWrapper(compra);

        return compraEntityWrapper;
    }

    private CompraEntityWrapper loadOrCreateModel() {
        CompraEntityWrapper compraEntityWrapper = loadOrCreateModel(null);

        return compraEntityWrapper;
    }

    private class MyListChangeListener implements ListChangeListener<ItemCompraEntityWrapper> {

        @Override
        public void onChanged(Change<? extends ItemCompraEntityWrapper> change) {
            change.next();
            new Thread(
                    persistModel()
            ).start();
        }
    }

    /**
     * A partir de la versión 4.4 se experimenta guardando únicamente una vez la compra con una única sesión al terminar
     *     por lo que este método queda vacío al no contar con un mecanismo para persistir la compra mientras se captura
     * @return
     */

    private Task<Void> persistModel() {

        Task persistTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                //Código anterior a 4.4
                /*
                TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {

                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        entityManager.merge(modelo._compra);
                        entityManager.flush();
                        itemsTable.edit(itemsTable.getSelectionModel().getSelectedIndex() + 1, itemsTable.getColumns().get(0));
                    }
                });
                */

                return null;
            }
        };
        return persistTask;
    }

    /**
     * Elimina el modelo de la presentación.
     * A partir de la versión 4.4 queda vacío ya que no se ofrece un mecanismo que persista la venta mientra se edita,
     * tampoco hay un mecanismo para eliminar una compra ya capturada
     */
    private Task<Void> deleteModel() {
        Task deleteTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                /* Código anterior a 4.4 */
                /*
                repo.delete(modelo._compra);
                repo.flush();
                */
                return null;
            }
        };
        return deleteTask;
    }

    public class MostrarCatalogoHandler {
        CompraController controller;

        public MostrarCatalogoHandler(CompraController controller) {
            this.controller = controller;
        }

        public void handle() {
            Task<String> t = new Task() {
                @Override
                protected String call() throws Exception {
                    return mostrarCatalogo();
                }
            };

            t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    Platform.runLater(() -> {

                        String retorno = t.getValue();
                        retorno = (retorno == null) ? "" : retorno;
                        String captura = controller.codigoTextField.getText();
                        captura = (captura == null) ? "" : captura;
                        controller.codigoTextField.setText(captura + retorno);

                        controller.getJInternalFrame().toFront();
                        controller.getFXPanel().requestFocus();
                        controller.mainPane.requestFocus();
                        controller.codigoTextField.requestFocus();
                    });
                }
            });

            new Thread(t).start();
        }

        private String mostrarCatalogo() {
            String retorno = Articulos.lanzarDialogoCatalogo();
            return retorno;
        }
    }


}


