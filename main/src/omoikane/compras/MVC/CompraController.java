package omoikane.compras.MVC;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
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
import javafx.util.Callback;
import name.antonsmirnov.javafx.dialog.Dialog;
import omoikane.compras.entities.Compra;
import omoikane.compras.entities.ItemCompra;
import omoikane.principal.Articulos;
import omoikane.proveedores.Proveedor;
import omoikane.repository.CompraRepo;
import omoikane.entities.Usuario;
import omoikane.inventarios.Stock;
import omoikane.producto.Articulo;
import omoikane.repository.ProductoRepo;
import omoikane.repository.ProveedorRepo;
import omoikane.sistema.Permisos;
import omoikane.sistema.Usuarios;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 27/02/13
 * Time: 05:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompraController implements Initializable {

    public static final Logger logger = Logger.getLogger(CompraController.class);

    CompraEntityWrapper modelo;

    @Autowired
    EhCacheManagerFactoryBean cacheManager;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    CompraRepo repo;

    @Autowired
    ProductoRepo productoRepo;

    @Autowired
    ProveedorRepo proveedorRepo;

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
    @FXML AnchorPane mainPane;
    @FXML Label subtotalLabel;

    private Articulo capturaArticulo;
    private HashMap<Long, Articulo> indice;

    @FXML public void archivarAction(ActionEvent actionEvent) {
        //Mini validación
        if(modelo.getProveedor().get() == null) { logger.info("Falta introducir proveedor"); return; }
        if(modelo.getFolioOrigen().get() == null || modelo.getFolioOrigen().get().equals("")) { logger.info("Falta introducir folio de origen"); return; }

        //Ahora archivar
        Dialog.buildConfirmation("Confirmación", "Al aplicar esta compra, los stocks de los productos quedarán actualizados y no se podrá editar este documento, la operación no es reversible, ¿Está seguro de continuar?")
                .addYesButton(new EventHandler() {
                    @Override
                    public void handle(Event event) {

                        modelo.setCompletado(true);
                        Task<Void> persistTask = persistModel();
                        persistTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent workerStateEvent) {
                                logger.info("Captura de compra archivada");
                                            }
                        });
                        new Thread(persistTask).start();
                        handleAplicarInventario();
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

    private void handleAplicarInventario() {
        mainPane.setDisable(true);
        Task aplicarInventarioTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                aplicarInventarioToModel();
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
        aplicarInventarioTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                if(!modelo.getCompletado().get()) archivarButton.fire();
                mainPane.setDisable(false);
                logger.info("Compra aplicada a las existencias correctamente.");
            }
        });
        aplicarInventarioTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                mainPane.setDisable(false);
            }
        });
        new Thread(aplicarInventarioTask).start();
    }

    @Transactional
    private void aplicarInventarioToModel() {

        for (ItemCompraEntityWrapper itemCompraEntityWrapper : modelo.getItems()) {
            Articulo a        = itemCompraEntityWrapper.articuloProperty().get();
            Articulo articulo = productoRepo.findByIdIncludeStock(a.getIdArticulo());

            Stock s = articulo.getStock();
            s.setEnTienda(s.getEnTienda().add( itemCompraEntityWrapper.cantidadProperty().get() ));

            productoRepo.saveAndFlush(articulo);
;
            modelo.setUsuario( new Usuario( new Long(Usuarios.getIDUsuarioActivo() ) ) );
            repo.saveAndFlush(modelo._compra);

        }
    }

    @FXML public void onAgregarAction(ActionEvent actionEvent) {
        if(capturaArticulo == null) return ;
        if(modelo.getCompletado().get()) return;

        if(indice.containsKey(capturaArticulo.getIdArticulo())) {
            logger.info("Artículo ya agregado al conteo. No se puede volver a agregar.");
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

            paddedButton.setPadding(new javafx.geometry.Insets(3, 0, 0, 0));
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
            }
        }
    }

    private Task<Void> findArticulo() {
        Task findArticuloTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String   codigo = codigoTextField.getText();
                Articulo resultado = null;
                if(codigo.isEmpty()) return null;

                resultado = getArticulo(codigo);

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

    public Articulo getArticulo(String codigo) {
        Articulo resultado = null;
        List<Articulo> resultados = productoRepo.findByCodigo(codigo);
        if(resultados == null || resultados.isEmpty()) resultados = productoRepo.findByCodigoAlterno(codigo);
        if(resultados != null && !resultados.isEmpty()) resultado = productoRepo.findByIdIncludeStock(resultados.get(0).getIdArticulo());
        return resultado;
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
        idLabel.textProperty().bind(Bindings.convert(modelo.getId()));
        lblProveedor.textProperty().bind(modelo.nombreProveedorProperty());
        //txtFolioOrigen.textProperty().bind( modelo.getFolioOrigen() );
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
        for(ItemCompraEntityWrapper ci : modelo.getItems()) {
            indice.put(ci.getBean().getArticulo().getIdArticulo(), ci.getBean().getArticulo());
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
     * Éste método inicializa el modelo del formulario, de una de las siguientes 3 maneras:
     * - Si le es dado un bean lo carga
     * - Si le es dado un bean nulo busca el último inventario incompleto y lo carga
     * - Si le es dado un bean nulo y no existe un inventario incompleto crea uno nuevo
     * */
    @Transactional
    private CompraEntityWrapper loadOrCreateModel(Compra compra) {

        if(compra == null)
            compra = repo.findByCompletado(false);
        if(compra == null) {
            compra = new Compra();
            compra.setFecha( new Date() );
            compra = repo.saveAndFlush(compra);
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
     * Ésta función almacena todo el modelo de esta vista.
     * Nota: Éste método utiliza Spring e Hibernate directamente, TransactionTemplate para la transacción
     * y entityManager para persistir a diferencia del método deleteModel. Simplemente por experimentación.
     * @return
     */

    private Task<Void> persistModel() {
        Task persistTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {

                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        entityManager.merge(modelo._compra);
                        entityManager.flush();
                        itemsTable.edit(itemsTable.getSelectionModel().getSelectedIndex() + 1, itemsTable.getColumns().get(0));
                    }
                });


                return null;
            }
        };
        return persistTask;
    }

    /**
     * Elimina el modelo de la presentación.
     * Nota: Utiliza Hades/Spring DAO para la eliminación a diferencia del método persistModel, claramente
     * el código es más simple y legible.
     * @return
     */
    private Task<Void> deleteModel() {
        Task deleteTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                repo.delete(modelo._compra);
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
            String retorno = Articulos.lanzarDialogoCatalogo();

            retorno = (retorno==null)?"":retorno;
            String captura = controller.codigoTextField.getText();
            captura = (captura==null)?"":captura;
            controller.codigoTextField.setText( captura + retorno );

            controller.mainPane.requestFocus();
            controller.codigoTextField.requestFocus();
        }
    }

}


