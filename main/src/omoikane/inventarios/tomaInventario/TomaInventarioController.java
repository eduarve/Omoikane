package omoikane.inventarios.tomaInventario;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.DefaultStringConverter;
import name.antonsmirnov.javafx.dialog.Dialog;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import omoikane.entities.Usuario;
import omoikane.inventarios.Stock;
import omoikane.producto.Articulo;
import omoikane.repository.ConteoInventarioRepo;
import omoikane.repository.ProductoRepo;
import omoikane.sistema.Permisos;
import omoikane.sistema.TextFieldTableCell;
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
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 27/02/13
 * Time: 05:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class TomaInventarioController implements Initializable {

    public static final Logger logger = Logger.getLogger(TomaInventarioController.class);

    ConteoInventarioPropWrapper modelo;

    @Autowired
    EhCacheManagerFactoryBean cacheManager;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    ConteoInventarioRepo repo;

    @Autowired
    ProductoRepo productoRepo;

    @FXML TableView<ItemConteoPropWrapper> itemsTable;
    @FXML TableColumn<ItemConteoPropWrapper, String> codigoCol;
    @FXML TableColumn<ItemConteoPropWrapper, String> nombreProductoCol;
    @FXML TableColumn<ItemConteoPropWrapper, BigDecimal> conteoCol;
    @FXML TableColumn<ItemConteoPropWrapper, BigDecimal> stockDBCol;
    @FXML TableColumn<ItemConteoPropWrapper, BigDecimal> diferenciaCol;
    @FXML Label idLabel;
    @FXML Label fechaLabel;
    @FXML TextField codigoTextField;
    @FXML TextField conteoTextField;
    @FXML Label descripcionLabel;
    @FXML Button agregarButton;
    @FXML Button aplicarInventarioButton;
    @FXML Button descartarButton;
    @FXML Button archivarButton;
    @FXML Button importarButton;
    @FXML Button imprimirButton;
    @FXML AnchorPane mainPane;

    private Articulo capturaArticulo;
    private HashMap<Long, ItemConteoPropWrapper> indice;

    @FXML public void archivarAction(ActionEvent actionEvent) {
        modelo.setCompletado(true);
        Task<Void> persistTask = persistModel();
        persistTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                logger.info("Captura de conteo de inventario físico archivada");
            }
        });
        new Thread(persistTask).start();
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

    @FXML public void onAplicarInventarioAction() {

        Dialog.buildConfirmation("Confirmación", "Al aplicar este inventario, los stocks de los productos en el almacén quedarán actualizados a los números introducidos en este conteo, la operación no es reversible, ¿Está seguro de continuar?")
                .addYesButton(new EventHandler() {
                    @Override
                    public void handle(Event event) {
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

    @FXML public void onImprimir(ActionEvent actionEvent) {
        List<Map<String, Object>> model = new ArrayList<>();
        try {
            for (ItemConteoInventario itemConteoInventario : modelo._conteoInventario.getItems()) {
                HashMap<String, Object> mapa = new HashMap<>();
                mapa.put("codigo", itemConteoInventario.getCodigo());
                mapa.put("descripcion", itemConteoInventario.getNombre());
                mapa.put("conteo", itemConteoInventario.getConteo());
                mapa.put("existencia", itemConteoInventario.getStockDB());
                mapa.put("diferencia", itemConteoInventario.getDiferencia());
                mapa.put("costoUnitario", itemConteoInventario.getCostoUnitario());
                model.add(mapa);
            }
            StyleBuilder boldStyle         = stl.style().bold();

            StyleBuilder boldCenteredStyle = stl.style(boldStyle)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);

            StyleBuilder boldCenteredStyle2 = stl.style(boldStyle)
                    .setHorizontalAlignment(HorizontalAlignment.LEFT).setFontSize(18);

            StyleBuilder columnTitleStyle  = stl.style(boldCenteredStyle)
                    .setBorder(stl.pen1Point())
                    .setBackgroundColor(Color.LIGHT_GRAY);

            TextColumnBuilder<BigDecimal> conteoCol = col.column("Conteo", "conteo", type.bigDecimalType());
            TextColumnBuilder<BigDecimal> existenciaCol = col.column("Stock Sistema", "existencia", type.bigDecimalType());
            TextColumnBuilder<BigDecimal> costoUnitarioCol = col.column("Costo U.", "costoUnitario", type.bigDecimalType());
            TextColumnBuilder<BigDecimal> costoCol = costoUnitarioCol.multiply(conteoCol);
            report()
                    .columns(
                            col.column("Código", "codigo", type.stringType()).setMinColumns(4),
                            col.column("Descripción", "descripcion", type.stringType()),
                            conteoCol.setMinColumns(2),
                            existenciaCol.setMinColumns(2),
                            col.column("Diferencia", "diferencia", type.bigDecimalType()).setMinColumns(2),
                            costoUnitarioCol.setMinColumns(2),
                            costoCol.setTitle("Costo de inventario").setMinColumns(3)

                    )
                    .setColumnTitleStyle(columnTitleStyle)
                    .highlightDetailEvenRows()
                    .setDataSource(model)
                    .title(cmp.text("Conteo de inventario").setStyle(boldCenteredStyle2))
                    .subtotalsAtSummary(sbt.sum(costoCol))
                    .setSubtotalStyle(boldCenteredStyle2)
                    .pageFooter(cmp.pageXofY().setStyle(boldCenteredStyle))
                    .show(false);
        } catch (DRException e) {
            logger.error("Problema al generar reporte de conteo de inventario" ,e);
        }
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
                logger.info("Inventario aplicado a las existencias correctamente.");
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
        if(!omoikane.sistema.Usuarios.cerrojo(Permisos.getPMA_APLICARINVENTARIO())){ logger.info("Acceso Denegado"); return; }

        for (ItemConteoPropWrapper itemConteoPropWrapper : modelo.getItems()) {
            Articulo a        = itemConteoPropWrapper.articuloProperty().get();
            Articulo articulo = productoRepo.findByIdIncludeStock(a.getIdArticulo());

            Stock s = articulo.getStockInitializated();
            s.setEnTienda(itemConteoPropWrapper.conteoProperty().get());

            productoRepo.saveAndFlush(articulo);
        }

        modelo.setAplicado(true);
        modelo.setUsuario( new Usuario( new Long(Usuarios.getIDUsuarioActivo() ) ) );
        repo.saveAndFlush(modelo._conteoInventario);
    }
    @FXML public void onImportarAction(ActionEvent actionEvent) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                importar();
            }
        });
    }

    public void importar() {
        ITerminalHandler[] handlers = {
                new ScanPetTerminalHandler(this),
                new FreeInventarioTerminalHandler(this)  };
        ITerminalHandler handler =
                (ITerminalHandler) JOptionPane.showInputDialog(
                        null,
                        "¿Con que app se realizó el inventario?",
                        "Seleccione formato",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        handlers,
                        handlers[0]);
        if(handler == null) return;
        try {
            ITerminalHandler terminal = handler;
            terminal.importData();
        } catch (Exception e) {
            logger.error("Error al importar", e);
        }
    }

    @FXML public void onExportarAction(ActionEvent actionEvent) {
        ITerminalHandler terminal = new FreeInventarioTerminalHandler(this);
        terminal.exportData();
    }

    @FXML public void onAgregarAction(ActionEvent actionEvent) {
        if(capturaArticulo == null) return ;
        if(modelo.getCompletado().get()) return;

        BigDecimal conteo = new BigDecimal(conteoTextField.getText());
        addItemConteo(capturaArticulo, conteo);

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
    public void addItemConteo(Articulo capturaArticulo, BigDecimal conteo) {
        if(modelo.getCompletado().get()) return;

        String codigo            = capturaArticulo.getCodigo();
        String descripcion       = capturaArticulo.getDescripcion();
        BigDecimal stockBD       = capturaArticulo.getStockInitializated().getEnTienda();
        BigDecimal diferencia    = conteo.subtract(stockBD);
        BigDecimal costoUnitario = new BigDecimal( capturaArticulo.getBaseParaPrecio().getCosto() );

        if(indice.containsKey(capturaArticulo.getIdArticulo())) {

            ItemConteoPropWrapper itemConteoPropWrapper = indice.get(capturaArticulo.getIdArticulo());
            conteo = conteo.add( itemConteoPropWrapper.getBean().getConteo() );
            itemConteoPropWrapper.setConteo    ( conteo     );
            itemConteoPropWrapper.setDiferencia( diferencia );
            new Thread(
                    persistModel()
            ).start();

        } else {

            ItemConteoInventario newItemBean = new ItemConteoInventario(codigo, descripcion, conteo, costoUnitario);
            newItemBean.setArticulo  ( capturaArticulo );
            newItemBean.setStockDB   ( stockBD         );
            newItemBean.setDiferencia( diferencia      );

            ItemConteoPropWrapper newItem = new ItemConteoPropWrapper(newItemBean);
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
        assert agregarButton != null : "fx:id=\"agregarButton\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert codigoCol != null : "fx:id=\"codigoCol\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert codigoTextField != null : "fx:id=\"codigoTextField\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert conteoCol != null : "fx:id=\"conteoCol\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert conteoTextField != null : "fx:id=\"conteoTextField\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert descripcionLabel != null : "fx:id=\"descripcionLabel\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert fechaLabel != null : "fx:id=\"fechaLabel\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert idLabel != null : "fx:id=\"idLabel\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert itemsTable != null : "fx:id=\"itemsTable\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";
        assert nombreProductoCol != null : "fx:id=\"nombreProductoCol\" was not injected: check your FXML file 'TomaInventarioView.fxml'.";

        codigoCol        .setCellValueFactory(new PropertyValueFactory<ItemConteoPropWrapper, String>("codigo"));
        codigoCol        .setCellFactory(new ImprovedCellFactory<ItemConteoPropWrapper>(ItemConteoPropWrapper.class));

        nombreProductoCol.setCellValueFactory(new PropertyValueFactory<ItemConteoPropWrapper, String>("nombre"));
        nombreProductoCol.setCellFactory(new ImprovedCellFactory<ItemConteoPropWrapper>(ItemConteoPropWrapper.class));

        conteoCol        .setCellValueFactory(new PropertyValueFactory<ItemConteoPropWrapper, BigDecimal>("conteo"));
        conteoCol        .setCellFactory(new NumericCellFactory<ItemConteoPropWrapper>(ItemConteoPropWrapper.class));

        stockDBCol       .setCellValueFactory(new PropertyValueFactory<ItemConteoPropWrapper, BigDecimal>("stockDB"));
        stockDBCol       .setCellFactory(new NumericCellFactory<ItemConteoPropWrapper>(ItemConteoPropWrapper.class));

        diferenciaCol    .setCellValueFactory(new PropertyValueFactory<ItemConteoPropWrapper, BigDecimal>("diferencia"));
        diferenciaCol    .setCellFactory(new NumericCellFactory<ItemConteoPropWrapper>(ItemConteoPropWrapper.class));

        initModel();

        codigoTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    conteoTextField.requestFocus();
                }
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
                        TomaInventarioController.this.capturaArticulo = finalResultado;
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

    public void initModel(ConteoInventario conteoInventario) {
        imprimirButton.disableProperty().unbind();
        archivarButton.disableProperty().unbind();
        descartarButton.disableProperty().unbind();
        aplicarInventarioButton.disableProperty().unbind();

        imprimirButton.setDisable(false);
        archivarButton.setDisable(false);
        descartarButton.setDisable(false);
        aplicarInventarioButton.setDisable(false);

        modelo = loadOrCreateModel(conteoInventario);
        itemsTable.setItems(modelo.getItems());

        indice = new HashMap<>();
        for(ItemConteoPropWrapper ci : modelo.getItems()) {
            indice.put(ci.getBean().getArticulo().getIdArticulo(), ci);
        }

        imprimirButton.disableProperty().bind(modelo.getCompletado().not());
        archivarButton.disableProperty().bind(modelo.getCompletado());
        descartarButton.disableProperty().bind(modelo.getCompletado());
        aplicarInventarioButton.disableProperty().bind(modelo.getAplicado());
        fechaLabel.textProperty().bind(Bindings.convert(modelo.getDate()));
        idLabel.textProperty().bind(Bindings.convert(modelo.getId()));

        modelo.getItems().addListener(new MyListChangeListener());
    }

    /**
     * Éste método inicializa el modelo del formulario, de una de las siguientes 3 maneras:
     * - Si le es dado un bean lo carga
     * - Si le es dado un bean nulo busca el último inventario incompleto y lo carga
     * - Si le es dado un bean nulo y no existe un inventario incompleto crea uno nuevo
     * */
    @Transactional
    private ConteoInventarioPropWrapper loadOrCreateModel(ConteoInventario conteoInventario) {

        if(conteoInventario == null)
            conteoInventario = repo.findByCompletado(false);
        if(conteoInventario == null) {
            conteoInventario = new ConteoInventario();
            conteoInventario.setFecha( new Date() );
            conteoInventario = repo.saveAndFlush(conteoInventario);
        }

        ConteoInventarioPropWrapper conteoInventarioPropWrapper = new ConteoInventarioPropWrapper(conteoInventario);

        return conteoInventarioPropWrapper;
    }

    private ConteoInventarioPropWrapper loadOrCreateModel() {
        ConteoInventarioPropWrapper conteoInventarioPropWrapper = loadOrCreateModel(null);

        return conteoInventarioPropWrapper;
    }

    private class MyListChangeListener implements ListChangeListener<ItemConteoPropWrapper> {

        @Override
        public void onChanged(Change<? extends ItemConteoPropWrapper> change) {
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
                        entityManager.merge(modelo._conteoInventario);
                        entityManager.flush();
                    }
                });

                itemsTable.edit(itemsTable.getSelectionModel().getSelectedIndex() + 1, itemsTable.getColumns().get(0));
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
                repo.delete(modelo._conteoInventario);
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
                        persistModel();

                        TablePosition position = getTableView().getFocusModel().getFocusedCell();
                        int nextCol = position.getColumn()+1;
                        int nextRow = position.getRow();

                        if(getTableView().getColumns().size()-1 < nextCol)
                        {
                            //nextRow = position.getRow() + 1;
                            /*
                            if(getTableView().getItems().size()-1 < nextRow) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            getTableView().getItems().add(impl.newInstance());
                                        } catch (InstantiationException e) {
                                            logger.error(e.getMessage(), e);
                                        } catch (IllegalAccessException e) {
                                            logger.error(e.getMessage(), e);
                                        }
                                    }
                                });

                            }*/
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


}
