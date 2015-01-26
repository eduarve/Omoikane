package omoikane.inventarios.traspasoSaliente;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.DefaultStringConverter;
import name.antonsmirnov.javafx.dialog.Dialog;
import net.sf.dynamicreports.report.builder.VariableBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.Calculation;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import omoikane.compras.MVC.ItemCompraEntityWrapper;
import omoikane.entities.Usuario;
import omoikane.inventarios.Stock;
import omoikane.inventarios.StockIssuesLogic;
import omoikane.principal.Articulos;
import omoikane.principal.Principal;
import omoikane.producto.Articulo;
import omoikane.repository.ProductoRepo;
import omoikane.repository.StockRepo;
import omoikane.repository.TraspasoSalienteRepo;
import omoikane.sistema.Permisos;
import omoikane.sistema.TextFieldTableCell;
import omoikane.sistema.Usuarios;
import org.apache.commons.lang.RandomStringUtils;
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
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 27/02/13
 * Time: 05:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class TraspasoSalienteController implements Initializable {

    public static final Logger logger = Logger.getLogger(TraspasoSalienteController.class);

    TraspasoSalientePropWrapper modelo;

    @Autowired
    EhCacheManagerFactoryBean cacheManager;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    TraspasoSalienteRepo repo;

    @Autowired
    ProductoRepo productoRepo;

    @FXML TableView<ItemTraspasoPropWrapper> itemsTable;
    @FXML TableColumn<ItemTraspasoPropWrapper, String> codigoCol;
    @FXML TableColumn<ItemTraspasoPropWrapper, String> nombreProductoCol;
    @FXML TableColumn<ItemTraspasoPropWrapper, BigDecimal> cantidadCol;
    @FXML TableColumn<ItemTraspasoPropWrapper, BigDecimal> stockDBCol;
    @FXML TableColumn<ItemTraspasoPropWrapper, BigDecimal> precioPublicoCol;
    @FXML TableColumn<ItemTraspasoPropWrapper, BigDecimal> ultimoCostoCol;
          TableColumn<ItemTraspasoPropWrapper, Boolean> actionCol = new TableColumn<>("Acciones");
    @FXML Label idLabel;
    @FXML Label fechaLabel;
    @FXML TextField codigoTextField;
    @FXML TextField conteoTextField;
    @FXML TextArea notasTextArea;
    @FXML TextField almacenOrigen;
    @FXML TextField almacenDestino;
    @FXML TextField uid;
    @FXML Label descripcionLabel;
    @FXML Button agregarButton;
    @FXML Button aplicarInventarioButton;
    @FXML Button descartarButton;
    @FXML Button importarButton;
    @FXML Button imprimirButton;
    @FXML AnchorPane mainPane;

    private Articulo capturaArticulo;
    private HashMap<Long, ItemTraspasoPropWrapper> indice;
    private boolean persistOnChange;
    private MyListChangeListener persistOnChangeListener;
    private boolean persistable = true; //Por default persistable

    // -------------------------------------------------
    /**
     * Desecha la captura actual
     */
    // -------------------------------------------------

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

    // -------------------------------------------------
    /**
     * Afecta las existencias del inventario (a través de otro método), persiste el traspaso (otro método)
     * y exporta el movimiento para su posterior (otro método) importación por otra tienda (traspaso entrante)
     */
    // -------------------------------------------------
    @FXML public void onAplicarInventarioAction() {

        Dialog.buildConfirmation("Confirmación", "Al aplicar este traspasos, los stocks de los productos en el almacén quedarán actualizados a los números introducidos en este formulario, la operación no es reversible, ¿Está seguro de continuar?")
                .addYesButton(new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        /* Actualizar existencias */
                        handleAplicarInventario();
                        modelo.setCompletado(true);

                        /* Persistir el traspaso por última vez por si hubo algún cambio */
                        Task<Void> persistTask = persistModelTask();
                        persistTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent workerStateEvent) {
                                logger.info("Captura de traspaso de mercancía archivada");
                            }
                        });
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

    // -------------------------------------------------
    /**
     * Genera Reporte del traspaso
     *
     * @param actionEvent
     */
    // -------------------------------------------------

    @FXML public void onImprimir(ActionEvent actionEvent) {
        List<Map<String, Object>> model = new ArrayList<>();
        try {
            for (ItemTraspasoSaliente itemTraspasoSaliente : modelo._traspasoSaliente.getItems()) {
                HashMap<String, Object> mapa = new HashMap<>();
                mapa.put("codigo", itemTraspasoSaliente.getCodigo());
                mapa.put("descripcion", itemTraspasoSaliente.getNombre());
                mapa.put("cantidad", itemTraspasoSaliente.getCantidad());
                mapa.put("existencia", itemTraspasoSaliente.getStockDB());
                mapa.put("precioUnitario", itemTraspasoSaliente.getPrecioPublico());
                mapa.put("costoUnitario", itemTraspasoSaliente.getCostoUnitario());
                mapa.put("importe", itemTraspasoSaliente.getImporte());
                model.add(mapa);
            }
            StyleBuilder boldStyle         = stl.style().bold();

            StyleBuilder rightStyle        = stl.style().setHorizontalAlignment(HorizontalAlignment.RIGHT);

            StyleBuilder boldCenteredStyle = stl.style(boldStyle)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);

            StyleBuilder boldCenteredStyle2 = stl.style(boldStyle)
                    .setHorizontalAlignment(HorizontalAlignment.LEFT).setFontSize(18);

            StyleBuilder columnTitleStyle  = stl.style(boldCenteredStyle)
                    .setBorder(stl.pen1Point())
                    .setBackgroundColor(Color.LIGHT_GRAY);

            TextColumnBuilder<BigDecimal> cantidadCol = col.column("Cantidad", "cantidad", type.bigDecimalType());
            TextColumnBuilder<BigDecimal> existenciaCol = col.column("Stock Sistema", "existencia", type.bigDecimalType());
            TextColumnBuilder<BigDecimal> costoUnitarioCol = col.column("Costo U.", "costoUnitario", type.bigDecimalType()).setPattern("#,###.##");
            TextColumnBuilder<BigDecimal> precioUnitarioCol = col.column("Precio U.", "precioUnitario", type.bigDecimalType()).setPattern("#,###.##");
            TextColumnBuilder<BigDecimal> importeCol = col.column("Importe", "importe", type.bigDecimalType()).setPattern("#,###.##");

            report()
                    .columns(
                            col.column("Código", "codigo", type.stringType()).setMinColumns(4),
                            col.column("Descripción", "descripcion", type.stringType()),
                            cantidadCol.setMinColumns(2),
                            existenciaCol.setMinColumns(2),
                            costoUnitarioCol.setMinColumns(2),
                            precioUnitarioCol.setMinColumns(2),
                            importeCol.setMinColumns(3).setPattern("#,###.##")

                    )
                    .setColumnTitleStyle(columnTitleStyle)
                    .highlightDetailEvenRows()
                    .setDataSource(model)
                    .title(
                            cmp.horizontalList().add(
                                cmp.verticalList().add(
                                    cmp.text("Traspaso saliente de mercancía").setStyle(boldCenteredStyle2).setWidth(300),
                                    cmp.text("Almacén origen: " + almacenOrigen.textProperty().get()),
                                    cmp.text("Almacén destino: " + almacenDestino.textProperty().get())
                                ),
                                cmp.verticalList().add(
                                    cmp.text(fechaLabel.textProperty().get()).setHorizontalAlignment(HorizontalAlignment.RIGHT),
                                    cmp.text("UID: " + uid.textProperty().get()).setHorizontalAlignment(HorizontalAlignment.RIGHT),
                                    bcode.code128(uid.textProperty().get()) .setHeight(30).setStyle(rightStyle)
                                )
                            )
                    )
                    .subtotalsAtSummary(sbt.sum(importeCol))
                    .addSummary(cmp.verticalList(
                            cmp.verticalGap(20),
                            cmp.line(),
                            cmp.text("Notas").setStyle(boldStyle),
                            cmp.text(notasTextArea.getText())
                    ))
                    .pageFooter(
                            cmp.pageXofY().setStyle(boldCenteredStyle)
                    )
                    .show(false);
        } catch (DRException e) {
            logger.error("Problema al generar reporte de traspaso de mercancía" ,e);
        }
    }

    // -------------------------------------------------
    /**
     * Wrapper de aplicarTraspasoToModel. Establece mecanismos de seguridad para aplicar el traspso
     */
    // -------------------------------------------------
    private void handleAplicarInventario() {
        mainPane.setDisable(true);
        Task aplicarInventarioTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                aplicarTraspasoToModel();
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
        aplicarInventarioTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                mainPane.setDisable(false);
                logger.info("Traspaso aplicado a las existencias correctamente.");
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

    @Autowired StockIssuesLogic stockIssuesLogic;
    @Autowired StockRepo stockRepo;
    // -------------------------------------------------
    /**
     * Hace los cambios de existencias en la BD. Requiere permiso
     */
    // -------------------------------------------------
    @Transactional
    private void aplicarTraspasoToModel() {
        if(!Usuarios.cerrojo(Permisos.getPMA_APLICARTRASPASO())){ logger.info("Acceso Denegado"); return; }

        for (ItemTraspasoPropWrapper itemTraspasoPropWrapper : modelo.getItems()) {
            Articulo a        = itemTraspasoPropWrapper.articuloProperty().get();

            Articulo articulo = stockIssuesLogic.reduceStock(a.getIdArticulo(), itemTraspasoPropWrapper.cantidadProperty().get() );
            stockRepo.save(articulo.getStock());

            productoRepo.saveAndFlush(articulo);
        }

        String uidString = RandomStringUtils.randomAlphanumeric(8);
        modelo.setAplicado(true);
        modelo.setUsuario(new Usuario(new Long(Usuarios.getIDUsuarioActivo())));
        modelo.setUid(uidString);
        repo.saveAndFlush(modelo._traspasoSaliente);

        // --------// Exportación automática de traspaso //--------
        exportar(modelo._traspasoSaliente);

    }

    // -------------------------------------------------
    /**
     * Genera un archivo json en el path de dropbox que contiene los metadatos de éste traspaso
     */
    // -------------------------------------------------
    private void exportar(TraspasoSaliente ts) {
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        try {
            File json = new File(Principal.dropboxPath+ts.getUid()+".json");
            mapper.writeValue(json, ts);
        } catch (IOException e) {
            logger.error("Error al escribir metadatos del traspaso saliente",e);
        }
    }

    // -------------------------------------------------
    /**
     * Evento del botón importar, envuelve el método importar en un nuevo hilo de swing
     */
    // -------------------------------------------------
    @FXML public void onImportarAction(ActionEvent actionEvent) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                importar();
            }
        });
    }

    // -------------------------------------------------
    /**
     * Permite importar productos desde un archivo de terminal de datos a la transferencia en edición
     */
    // -------------------------------------------------
    public void importar() {
        ITerminalHandler[] handlers = {
                new ScanPetTerminalHandler(this),
                new FreeInventarioTerminalHandler(this)  };
        ITerminalHandler handler =
                (ITerminalHandler) JOptionPane.showInputDialog(
                        null,
                        "¿Con que app se realizó el conteo?",
                        "Seleccione formato",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        handlers,
                        handlers[0]);
        if(handler == null) return;

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    ITerminalHandler terminal = handler;
                    terminal.importData();
                } catch (Exception e) {
                    logger.error("Error al importar", e);
                }
                return null;
            }
        };
        Platform.runLater(task);
    }

    @FXML public void onExportarAction(ActionEvent actionEvent) {
        ITerminalHandler terminal = new FreeInventarioTerminalHandler(this);
        terminal.exportData();
    }

    @FXML public void onAgregarAction(ActionEvent actionEvent) {
        if(capturaArticulo == null) return ;
        if(modelo.getCompletado().get()) return;

        BigDecimal conteo = new BigDecimal(conteoTextField.getText());
        try {
            addItemConteo(capturaArticulo, conteo);
        } catch (Exception e) {
            logger.error("Error en la captura", e);
        }

        conteoTextField.setText("");
        codigoTextField.setText("");
        descripcionLabel.setText("");
        codigoTextField.requestFocus();
        capturaArticulo = null;
    }

    /**
     * Intenta agregar un item a la lista, si ya exíste lo suma y lo persiste.
     * @param capturaArticulo
     * @param conteo
     */
    public void addItemConteo(Articulo capturaArticulo, BigDecimal conteo) throws Exception {
        if(modelo.getCompletado().get()) return;

        String codigo            = capturaArticulo.getCodigo();
        String descripcion       = capturaArticulo.getDescripcion();
        BigDecimal stockBD       = capturaArticulo.getStockInitializated().getEnTienda();
        BigDecimal ultimoCosto   = new BigDecimal( capturaArticulo.getBaseParaPrecio().getCosto() );
        BigDecimal precioPublico = capturaArticulo.getPrecio().getPrecio();

        if(indice.containsKey(capturaArticulo.getIdArticulo())) {
            //Acumular en una partida ya existente
            ItemTraspasoPropWrapper itemTraspasoPropWrapper = indice.get(capturaArticulo.getIdArticulo());
            conteo = conteo.add( itemTraspasoPropWrapper.getBean().getCantidad() );
            itemTraspasoPropWrapper.setCantidad(conteo);

            /*new Thread(
                    persistModel()
            ).start();*/
            persistModel();

        } else {
            //Agregar nueva partida
            ItemTraspasoSaliente newItemBean = new ItemTraspasoSaliente(codigo, descripcion, conteo, ultimoCosto);
            newItemBean.setArticulo  ( capturaArticulo );
            newItemBean.setStockDB   ( stockBD         );
            newItemBean.setPrecioPublico( precioPublico );

            ItemTraspasoPropWrapper newItem = new ItemTraspasoPropWrapper(newItemBean);
            modelo.addItem(newItem);
            indice.put(capturaArticulo.getIdArticulo(), newItem);

        }
    }

    Character decimalSeparator = getDecimalSeparator();

    private Character getDecimalSeparator() {
        DecimalFormatSymbols custom=new DecimalFormatSymbols();
        return custom.getDecimalSeparator();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assert agregarButton != null : "fx:id=\"agregarButton\" was not injected: check your FXML file 'TraspasoSalienteView.fxml'.";
        assert codigoCol != null : "fx:id=\"codigoCol\" was not injected: check your FXML file 'TraspasoSalienteView.fxml'.";
        assert codigoTextField != null : "fx:id=\"codigoTextField\" was not injected: check your FXML file 'TraspasoSalienteView.fxml'.";
        assert cantidadCol != null : "fx:id=\"cantidadCol\" was not injected: check your FXML file 'TraspasoSalienteView.fxml'.";
        assert conteoTextField != null : "fx:id=\"conteoTextField\" was not injected: check your FXML file 'TraspasoSalienteView.fxml'.";
        assert descripcionLabel != null : "fx:id=\"descripcionLabel\" was not injected: check your FXML file 'TraspasoSalienteView.fxml'.";
        assert fechaLabel != null : "fx:id=\"fechaLabel\" was not injected: check your FXML file 'TraspasoSalienteView.fxml'.";
        assert idLabel != null : "fx:id=\"idLabel\" was not injected: check your FXML file 'TraspasoSalienteView.fxml'.";
        assert itemsTable != null : "fx:id=\"itemsTable\" was not injected: check your FXML file 'TraspasoSalienteView.fxml'.";
        assert nombreProductoCol != null : "fx:id=\"nombreProductoCol\" was not injected: check your FXML file 'TraspasoSalienteView.fxml'.";

        codigoCol        .setCellValueFactory(new PropertyValueFactory<ItemTraspasoPropWrapper, String>("codigo"));
        codigoCol        .setCellFactory(new ImprovedCellFactory<ItemTraspasoPropWrapper>(ItemTraspasoPropWrapper.class));

        nombreProductoCol.setCellValueFactory(new PropertyValueFactory<ItemTraspasoPropWrapper, String>("nombre"));
        nombreProductoCol.setCellFactory(new ImprovedCellFactory<ItemTraspasoPropWrapper>(ItemTraspasoPropWrapper.class));

        cantidadCol      .setCellValueFactory(new PropertyValueFactory<ItemTraspasoPropWrapper, BigDecimal>("cantidad"));
        cantidadCol      .setCellFactory(new NumericCellFactory<ItemTraspasoPropWrapper>(ItemTraspasoPropWrapper.class));

        stockDBCol       .setCellValueFactory(new PropertyValueFactory<ItemTraspasoPropWrapper, BigDecimal>("stockDB"));
        stockDBCol       .setCellFactory(new NumericCellFactory<ItemTraspasoPropWrapper>(ItemTraspasoPropWrapper.class));

        ultimoCostoCol   .setCellValueFactory(new PropertyValueFactory<ItemTraspasoPropWrapper, BigDecimal>("costoUnitario"));
        ultimoCostoCol   .setCellFactory(new NumericCellFactory<ItemTraspasoPropWrapper>(ItemTraspasoPropWrapper.class));

        precioPublicoCol .setCellValueFactory(new PropertyValueFactory<ItemTraspasoPropWrapper, BigDecimal>("precioPublico"));
        precioPublicoCol .setCellFactory(new NumericCellFactory<ItemTraspasoPropWrapper>(ItemTraspasoPropWrapper.class));

        initModel();

        // - Redimensionar las columnas para que la suma de sus anchos sea igual al ancho de la tabla - //
        codigoCol        .prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.10));
        nombreProductoCol.prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.40));
        cantidadCol      .prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.10));
        stockDBCol       .prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.10));
        ultimoCostoCol   .prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.10));
        precioPublicoCol .prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.10));
        actionCol        .prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.095));

        codigoTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    conteoTextField.requestFocus();
                }
                if(event.getCode() == KeyCode.F1)
                    new MostrarCatalogoHandler(TraspasoSalienteController.this).handle();
            }
        });

        conteoTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
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
        actionCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ItemTraspasoPropWrapper, Boolean>, ObservableValue<Boolean>>() {
            @Override public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<ItemTraspasoPropWrapper, Boolean> features) {
                return new SimpleBooleanProperty(features.getValue() != null);
            }
        });

        // create a cell value factory with an add button for each row in the table.
        actionCol.setCellFactory(new Callback<TableColumn<ItemTraspasoPropWrapper, Boolean>, TableCell<ItemTraspasoPropWrapper, Boolean>>() {
            @Override public TableCell<ItemTraspasoPropWrapper, Boolean> call(TableColumn<ItemTraspasoPropWrapper, Boolean> personBooleanTableColumn) {
                return new ActionsCell(itemsTable);
            }
        });

        itemsTable.getColumns().add(actionCol);
        //*************************************************************
    }

    private class ActionsCell extends TableCell<ItemTraspasoPropWrapper, Boolean> {
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
            final TableCell<ItemTraspasoPropWrapper, Boolean> c = this;
            delButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    TableRow tableRow = c.getTableRow();
                    ItemTraspasoPropWrapper item= (ItemTraspasoPropWrapper) tableRow.getTableView().getItems().get(tableRow.getIndex());
                    for(int i = 0; i < modelo.getBean().getItems().size(); i++) {
                         if(modelo.getBean().getItems().get(i) == item.getBean() ) modelo.getBean().getItems().remove(i);
                    }
                    modelo.getItems().remove(tableRow.getIndex());
                    TraspasoSalienteController.this.indice.remove(item.getBean().getArticulo().getIdArticulo());
                    Platform.runLater(persistModelTask());
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
                        TraspasoSalienteController.this.capturaArticulo = finalResultado;
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
        if(resultados != null && !resultados.isEmpty()) resultado = productoRepo.findByIdComplete(resultados.get(0).getIdArticulo());

        return resultado;
    }

    public void initModel() {
        initModel(null);
    }

    public void initModel(TraspasoSaliente traspasoSaliente) {

        //------------// Deshacer bindings viejos //--------------
        imprimirButton.disableProperty().unbind();
        descartarButton.disableProperty().unbind();
        aplicarInventarioButton.disableProperty().unbind();
        uid.textProperty().unbind();
        almacenOrigen.textProperty().unbind();
        almacenDestino.textProperty().unbind();
        notasTextArea.textProperty().unbind();
        if(modelo != null) modelo.getAlmacenOrigen().unbind();
        if(modelo != null) modelo.getAlmacenDestino().unbind();
        if(modelo != null) modelo.getNotas().unbind();

        //------------// Reiniciar estado de los botones //--------------
        imprimirButton.setDisable(false);
        descartarButton.setDisable(false);
        aplicarInventarioButton.setDisable(false);

        //------------// Cargar modelo //--------------
        modelo = loadOrCreateModel(traspasoSaliente);
        itemsTable.setItems(modelo.getItems());

        indice = new HashMap<>();
        for(ItemTraspasoPropWrapper ci : modelo.getItems()) {
            indice.put(ci.getBean().getArticulo().getIdArticulo(), ci);
        }

        //------------// Binding de botones y misc. //--------------
        imprimirButton.disableProperty().bind(modelo.getCompletado().not());
        descartarButton.disableProperty().bind(modelo.getCompletado());
        aplicarInventarioButton.disableProperty().bind(modelo.getAplicado());
        fechaLabel.textProperty().bind(Bindings.convert(modelo.getDate()));
        idLabel.textProperty().bind(Bindings.convert(modelo.getId()));

        //------------// Binding almacenes, notas y UID //--------------
        almacenOrigen.setText( traspasoSaliente != null ? traspasoSaliente.getAlmacenOrigen() : "" );  //AlmacenOrigen
        almacenOrigen.textProperty().set( modelo.getAlmacenOrigen().get() );
        modelo.getAlmacenOrigen().bind(almacenOrigen.textProperty());
        almacenDestino.setText( traspasoSaliente != null ? traspasoSaliente.getAlmacenDestino() : "" );  //AlmacenDestino
        almacenDestino.textProperty().set( modelo.getAlmacenDestino().get() );
        modelo.getAlmacenDestino().bind(almacenDestino.textProperty());
        notasTextArea.setText( traspasoSaliente != null ? traspasoSaliente.getNotas() : "" );  //Notas
        notasTextArea.textProperty().set(modelo.getNotas().get());
        modelo.getNotas().bind(notasTextArea.textProperty());
        uid.textProperty().bind(modelo.getUid());                                              //UID
        //------------// FIN Binding almacenes y UID //--------------


        setPersistOnChage(true);
    }

    /**
     * Éste método inicializa el modelo del formulario, de una de las siguientes 3 maneras:
     * - Si le es dado un bean lo carga
     * - Si le es dado un bean nulo busca el último inventario incompleto y lo carga
     * - Si le es dado un bean nulo y no existe un inventario incompleto crea uno nuevo
     * */
    @Transactional
    private TraspasoSalientePropWrapper loadOrCreateModel(TraspasoSaliente traspasoSaliente) {

        if(traspasoSaliente == null)
            traspasoSaliente = repo.findByCompletado(false);
        if(traspasoSaliente == null) {
            traspasoSaliente = new TraspasoSaliente();
            traspasoSaliente.setFecha( new Date() );
            traspasoSaliente = repo.saveAndFlush(traspasoSaliente);
        }

        TraspasoSalientePropWrapper conteoInventarioPropWrapper = new TraspasoSalientePropWrapper(traspasoSaliente);

        return conteoInventarioPropWrapper;
    }

    private TraspasoSalientePropWrapper loadOrCreateModel() {
        TraspasoSalientePropWrapper conteoInventarioPropWrapper = loadOrCreateModel(null);

        return conteoInventarioPropWrapper;
    }

    public void setPersistOnChage(boolean persistOnChange) {
        //Remuevo el listener actual, si es que existe
        if( persistOnChangeListener != null )
            modelo.getItems().removeListener(persistOnChangeListener);

        //Si se activa persistOnChange
        if(persistOnChange)
        {
            persistOnChangeListener = new MyListChangeListener();
            modelo.getItems().addListener(persistOnChangeListener);
        }

        //Guardo el último valor deseado para persistOnChange
        this.persistOnChange = persistOnChange;
    }

    public boolean isPersistOnChage() {
        return persistOnChange;
    }

    public boolean isPersistable() {
        return persistable;
    }

    public void setPersistable(boolean persistable) {
        this.persistable = persistable;
    }

    private class MyListChangeListener implements ListChangeListener<ItemTraspasoPropWrapper> {

        @Override
        public void onChanged(Change<? extends ItemTraspasoPropWrapper> change) {
            change.next();
            new Thread(
                    persistModelTask()
            ).start();
        }
    }

    /**
     * Ésta función almacena todo el modelo de esta vista.
     * Nota: Éste método utiliza Spring e Hibernate directamente, TransactionTemplate para la transacción
     * y entityManager para persistir a diferencia del método deleteModel. Simplemente por experimentación.
     * @return
     */

    private Task<Void> persistModelTask() {
        Task persistTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                persistModel();
                return null;
            }
        };
        return persistTask;
    }

    public void persistModel() throws Exception {
        if (!isPersistable()) return ;

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult()
        {
            protected void doInTransactionWithoutResult(TransactionStatus status)
            {
                entityManager.merge(modelo._traspasoSaliente);
                entityManager.flush();
            }
        });

        itemsTable.edit(itemsTable.getSelectionModel().getSelectedIndex() + 1, itemsTable.getColumns().get(0));
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
                repo.delete(modelo._traspasoSaliente);
                return null;
            }
        };
        return deleteTask;
    }

    public class ImprovedCellFactory<S> implements Callback<TableColumn<S, String>, TableCell<S, String>> {

        Class<S> impl;
        public ImprovedCellFactory(Class<S> impl) { this.impl = impl;  }
        @Override
        public TableCell<S, String> call(TableColumn<S, String> stTableColumn) {
            return new ImprovedTableCell<S, String>(impl);
        }

    }

    public class NumericCellFactory<S> implements Callback<TableColumn<S, BigDecimal>, TableCell<S, BigDecimal>> {

        Class<S> impl;
        public NumericCellFactory(Class<S> impl) { this.impl = impl;  }
        @Override
        public TableCell<S, BigDecimal> call(TableColumn<S, BigDecimal> stTableColumn) {
            return new NumericTableCell<S, BigDecimal>(impl);
        }

    }

    public class NumericTableCell<S, T> extends ImprovedTableCell<S, BigDecimal> {

        public NumericTableCell(Class<S> impl) {
            super(impl, new BigDecimalStringConverter());
        }

        @Override
        public void postCreateTextField(TextField t) {
            super.postCreateTextField(t);
            textField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
                public void handle(KeyEvent t) {
                    Integer pos = textField.caretPositionProperty().get();
                    String newVal = textField.getText().substring(0, pos)
                            + t.getCharacter()
                            + textField.getText().substring(pos);


                    Pattern pattern = Pattern.compile("^\\d*(\\"+decimalSeparator+"?\\d{0,2})$");
                    Matcher matcher = pattern.matcher(newVal);
                    if (!matcher.find()) {
                        t.consume();
                    }

                }
            });

        }
    }

    public class ImprovedTableCell<S, T> extends TextFieldTableCell<S, T> {

        Class<S> impl;
        public ImprovedTableCell(Class<S> impl) {
            this(impl, (StringConverter<T>) new DefaultStringConverter());
        }
        public ImprovedTableCell(Class<S> impl, StringConverter<T> sc) {
            super(sc);
            this.impl = impl;
        }

        @Override
        public void postCreateTextField(TextField t) {

            t.setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(final KeyEvent t) {
                            toRunLater(t);
                }

                private void toRunLater(KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        t.consume();
                        if (getConverter() == null) {
                            throw new IllegalStateException(
                                    "Attempting to convert text input into Object, but provided "
                                            + "StringConverter is null. Be sure to set a StringConverter "
                                            + "in your cell factory.");
                        }
                        commitEdit(getConverter().fromString(textField.getText()));
                        //persistModelTask();

                        TablePosition position = getTableView().getFocusModel().getFocusedCell();
                        int nextCol = position.getColumn()+1;
                        int nextRow = position.getRow();

                        if(getTableView().getColumns().size()-1 < nextCol)
                        {
                            nextCol = 0;
                        }

                        final int finalNextRow = nextRow;
                        final int finalNextCol = nextCol;
                        getTableView().edit(finalNextRow, getTableView().getColumns().get(finalNextCol));
                        getTableView().getFocusModel().focus(finalNextRow, getTableView().getColumns().get(finalNextCol));


                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                }
            });
        }

        @Override
        public void startEdit() {

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ImprovedTableCell.super.startEdit();
                    textField.requestFocus();

                }
            });
        }
    }

    public class MostrarCatalogoHandler {
        TraspasoSalienteController controller;

        public MostrarCatalogoHandler(TraspasoSalienteController controller) {
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
