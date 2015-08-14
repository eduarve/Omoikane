package omoikane.etiquetas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import omoikane.etiquetas.presentation.NumericEditableTableCell;
import omoikane.principal.Principal;
import omoikane.producto.*;
import omoikane.repository.ProductoRepo;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.URL;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Pedro
 * Date: 11/03/13
 * Time: 00:39
 * To change this template use File | Settings | File Templates.
 */
public class ImpresionEtiquetasController implements Initializable {

    public static Logger logger = Logger.getLogger(ImpresionEtiquetasController.class);

    private ProductoRepo productoRepo;

    ObservableList<ImpresionEtiquetasModel> articulos;

    @FXML private TableColumn<ImpresionEtiquetasModel, String> codigoCol;
    @FXML private TableColumn<ImpresionEtiquetasModel, Long> cantidadCol;
    @FXML private TableColumn<ImpresionEtiquetasModel, String> productoCol;

    @FXML //  fx:id="addButton"
    private Button addButton;

    @FXML //  fx:id="removeButton"
    private Button removeButton;

    @FXML // fx:id="seguienteButton"
    private Button seguienteButton;

    @FXML //  fx:id="tipoEtiqueta"
    private ComboBox tipoEtiqueta;

    @FXML //  fx:id="contenidoTable"
    private TableView<ImpresionEtiquetasModel> tablaContenido;

    private List<Articulo> articulosToReport;

    @FXML
    private void actionAdd() {
        addPartida();
    }

    private void addPartida() {
        ImpresionEtiquetasModel impresionEtiquetasModel = new ImpresionEtiquetasModel(productoRepo);
        tablaContenido.getItems().add(impresionEtiquetasModel);
        impresionEtiquetasModel.addCodigoListener(() ->
        {
            String productoDescripcion = findProducto(impresionEtiquetasModel.getCodigo());
            impresionEtiquetasModel.setProducto( productoDescripcion );
        });
    }

    private String findProducto(String codigo) {
        // Primero busca artículos por código principal
        List<Articulo> articulos = productoRepo.findByCodigo(codigo);

        // Si no encuentra artículos por código principal entonces busca por código alterno
        if(articulos.isEmpty())
            articulos = productoRepo.findByCodigoAlterno(codigo);

        if (articulos.isEmpty()) {
            return "Producto no encontrado";
        } else  {
            return articulos.get(0).getDescripcion();
        }
    }

    @FXML
    private void actionRemove() {
        ImpresionEtiquetasModel impresionEtiquetasModel = tablaContenido.selectionModelProperty().get().getSelectedItem();
        if(impresionEtiquetasModel != null) {
            tablaContenido.getItems().remove(impresionEtiquetasModel);
        }
    }

    @FXML
    private void actionSeguiente() {
        articulosToReport = new ArrayList<Articulo>();
        for (ImpresionEtiquetasModel articuloModel: tablaContenido.getItems()) {

            // Primero busca artículos por código principal
            List<Articulo> articulos = productoRepo.findByCodigo(articuloModel.getCodigo());

            // Si no encuentra artículos por código principal entonces busca por código alterno
            if(articulos.size() < 1)
                articulos = productoRepo.findByCodigoAlterno(articuloModel.getCodigo());

            //No encontró artículos
            if (articulos.isEmpty())
                continue;

            // Si encontró cualquier artículo por código principal o alterno toma el primero
            Articulo articulo = articulos.get(0);

            // Inicializa los impuestos
            articulo = productoRepo.findByIdComplete(articulo.getIdArticulo());

            Long cantidad = articuloModel.getCantidad();
            int i= 0;
            while ( cantidad.compareTo(new Long(i)) > 0) {
                articulosToReport.add(articulo);
                i++;
            }
        }
        if (!articulosToReport.isEmpty()) {
            EtiquetaGenerator eg = new EtiquetaGenerator();
            String te = (String)tipoEtiqueta.getValue();
//            eg.generate("");
            if (tipoEtiqueta.getValue() != null) {
                if (tipoEtiqueta.getValue().equals("Big label"))
                {
                    eg.generate("Plantillas/bigLabel.jrxml",articulosToReport);
                }
                else if (tipoEtiqueta.getValue().equals("Label Printer")) {
                    eg.generate("Plantillas/labelPrint.jrxml",articulosToReport);
                }else if (tipoEtiqueta.getValue().equals("Standard")) {
                    eg.generate("Plantillas/standardLabel.jrxml",articulosToReport);
                }
            }
        }

    }

    public static void main(String args[]) { }

    public ImpresionEtiquetasController() {
        ApplicationContext applicationContext = Principal.applicationContext;
        productoRepo = (ProductoRepo)applicationContext.getBean("productoRepo");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //To change body of implemented methods use File | Settings | File Templates.
        assert addButton != null : "fx:id=\"addButton\" was not injected: check your FXML file 'ImpressionEtiquetasView.fxml'.";
        assert removeButton != null : "fx:id=\"removeButton\" was not injected: check your FXML file 'ImpressionEtiquetasView.fxml'.";
        assert tablaContenido != null : "fx:id=\"contenidoTable\" was not injected: check your FXML file 'ImpressionEtiquetasView.fxml'.";
        assert tipoEtiqueta != null : "fx:id=\"tipoEtiqueta\" was not injected: check your FXML file 'ImpressionEtiquetasView.fxml'.";
        configColumns();
        addPartida();

        Callback<TableColumn<ImpresionEtiquetasModel,String>, TableCell<ImpresionEtiquetasModel,String>> editableFactory = new Callback<TableColumn<ImpresionEtiquetasModel,String>, TableCell<ImpresionEtiquetasModel,String>>() {
            @Override
            public TableCell call(TableColumn p) {
                //return new EditableTableCell(iem, productoRepo);
                return new TextFieldTableCell<>(new DefaultStringConverter());
            }
        };

        Callback<TableColumn<ImpresionEtiquetasModel,Long>, TableCell<ImpresionEtiquetasModel,Long>> numericFactory = new Callback<TableColumn<ImpresionEtiquetasModel,Long>, TableCell<ImpresionEtiquetasModel,Long>>() {
            @Override
            public TableCell call(TableColumn p) {
                return new NumericEditableTableCell();
            }
        };

        cantidadCol.setCellFactory(numericFactory);
        cantidadCol.setCellValueFactory(new PropertyValueFactory<ImpresionEtiquetasModel, Long>("cantidad"));
        cantidadCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ImpresionEtiquetasModel, Long>> () {
            public void handle(TableColumn.CellEditEvent<ImpresionEtiquetasModel, Long> t) {
                ((ImpresionEtiquetasModel) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setCantidad(new Long(t.getNewValue()));
            }
        }
        );

        productoCol.setCellValueFactory(new PropertyValueFactory<ImpresionEtiquetasModel, String>("producto"));
        codigoCol.setCellValueFactory(new PropertyValueFactory<ImpresionEtiquetasModel, String>("codigo"));
        codigoCol.setCellFactory(editableFactory);
        codigoCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ImpresionEtiquetasModel, String>> () {
            @Override
            public void handle(TableColumn.CellEditEvent<ImpresionEtiquetasModel, String> t) {
                ((ImpresionEtiquetasModel) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setCodigo(t.getNewValue());

//                fillProducto(t);
                //for table view refresh
                tablaContenido.getColumns().get(0).setVisible(false);
                tablaContenido.getColumns().get(0).setVisible(true);

            }
        }
        );

        articulos = FXCollections.observableArrayList();
        tablaContenido.setItems(articulos);


//        Articulo producto = productoRepo.readByPrimaryKey(productoId);

    }

    public void configColumns() {

        codigoCol.setCellValueFactory(
                new PropertyValueFactory<ImpresionEtiquetasModel, String>("codigo")
        );
        productoCol.setCellValueFactory(
                new PropertyValueFactory<ImpresionEtiquetasModel, String>("producto")
        );

        cantidadCol.setCellValueFactory(
                new PropertyValueFactory<ImpresionEtiquetasModel, Long>("cantidad")
        );
    }

}
