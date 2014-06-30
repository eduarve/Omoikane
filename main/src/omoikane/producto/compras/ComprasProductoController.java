
package omoikane.producto.compras;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import omoikane.producto.Articulo;
import omoikane.producto.ListaDePrecios;
import omoikane.producto.PrecioOmoikaneLogic;
import omoikane.repository.ProductoRepo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


public class ComprasProductoController
        implements Initializable {

    @Autowired
    ProductoRepo productoRepo;

    @FXML
    private GridPane listaGrid;

    @FXML
    private TextField textCostoPromedio;

    Articulo art;

    BigDecimal costoPromedio;
    BigDecimal itemsComprados;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    JpaTransactionManager transactionManager;

    public static final Logger logger = Logger.getLogger(ComprasProductoController.class);


    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert listaGrid != null;
        //Por defecto: La siguiente instrucción soluciona un caso de uso para test únicamente
        //load();
    }

    public void load() {

        //Por defecto: La siguiente instrucción soluciona un caso de uso para test únicamente
        //if(art == null) setProducto(productoRepo.readByPrimaryKey(22935l));
        costoPromedio = new BigDecimal(0);
        itemsComprados = new BigDecimal(0);

        Query query = entityManager.createNativeQuery(
                "SELECT c.fecha, c.folioOrigen, p.nombre, ci.cantidad, ci.costoUnitario, ci.cantidad*ci.costoUnitario " +
                "FROM Proveedor p JOIN Compra c ON p.id = c.proveedor_id JOIN Compra_items ci ON ci.Compra_id = c.id " +
                "WHERE ci.articulo_id_articulo = ? LIMIT 5;");
        query.setParameter(1, art.getIdArticulo());
        List<Object[]> comprasPorItem = query.getResultList();
        int i = 1;
        for(Object[] row : comprasPorItem) {
            List<Node> nodes = buildRow(Arrays.asList(row));
            listaGrid.addRow(i, (Node[]) nodes.toArray(new Node[nodes.size()]));
            i++;
        }

        final NumberFormat cf = NumberFormat.getCurrencyInstance();

        costoPromedio = costoPromedio.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : costoPromedio.divide( itemsComprados , RoundingMode.HALF_EVEN );
        textCostoPromedio.setText(cf.format( costoPromedio ));
    }

    private List<Node> buildRow(List row) {
        //Inicialización de variables
        ArrayList<Node> nodes = new ArrayList();
        final NumberFormat cf = NumberFormat.getCurrencyInstance();
        final NumberFormat nb = NumberFormat.getNumberInstance();
        nb.setMaximumFractionDigits(2);
        nb.setMinimumFractionDigits(2);
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();

        //Declaración de variables y asignación de valores
        Label labelFecha       = new Label(sdf.format( row.get(0) ));
        Label labelFolioOrigen = new Label((String) row.get(1));
        Label labelProveedor   = new Label((String) row.get(2));
        Label labelCantidad      = new Label( nb.format( row.get(3) ) );
        Label labelCostoUnitario = new Label( cf.format( row.get(4) ) );
        Label labelImporte       = new Label( cf.format( row.get(5) ) );
        BigDecimal cantidad      = (BigDecimal) row.get(3) ;
        BigDecimal importe       = (BigDecimal) row.get(5) ;

        //Formato
        labelCantidad.setTextAlignment(TextAlignment.RIGHT);
        labelCostoUnitario.setTextAlignment(TextAlignment.RIGHT);
        labelImporte.setTextAlignment(TextAlignment.RIGHT);

        //Formación del row que es el resultado de éste método
        nodes.add(labelFecha);
        nodes.add(labelFolioOrigen);
        nodes.add(labelProveedor);
        nodes.add(labelCantidad);
        nodes.add(labelCostoUnitario);
        nodes.add(labelImporte);

        //Operaciones
        costoPromedio  = costoPromedio.add( importe );
        itemsComprados = itemsComprados.add( cantidad );

        return nodes;
    }

    public void setProducto(Articulo a) {
        art = a;
        load();
    }

}
