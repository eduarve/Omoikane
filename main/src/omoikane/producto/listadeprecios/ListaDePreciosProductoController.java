
package omoikane.producto.listadeprecios;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import omoikane.entities.CodigoProducto;
import omoikane.producto.Articulo;
import omoikane.producto.ListaDePrecios;
import omoikane.producto.PrecioOmoikaneLogic;
import omoikane.repository.ListaDePreciosRepo;
import omoikane.repository.ProductoRepo;
import omoikane.sistema.Dialogos;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static omoikane.sistema.Permisos.getPMA_MODIFICARARTICULO;
import static omoikane.sistema.Usuarios.cerrojo;


public class ListaDePreciosProductoController
        implements Initializable {

    @Autowired
    ListaDePreciosRepo ldpRepo;

    @Autowired
    ProductoRepo productoRepo;

    @FXML
    private GridPane listaGrid;

    Articulo art;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    JpaTransactionManager transactionManager;

    public static final Logger logger = Logger.getLogger(ListaDePreciosProductoController.class);

    @FXML
    private void onGuardar(ActionEvent event) {

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        Object result = transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {

                    productoRepo.setPreciosAlternosFor(art.getPreciosAlternosAsString(), art.getIdArticulo());

                    logger.info("Registrado");
                } catch(Exception e) {
                    logger.error("No registrado por causa de un error.", e);
                }
            }
        });

    }

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert listaGrid != null;
    }

    public void load() {

        //Por defecto: La siguiente instrucción soluciona un caso de uso para test únicamente
        //if(art == null) setProducto(productoRepo.readByPrimaryKey(22935l));

        List<ListaDePrecios> ldps = ldpRepo.findAllActive();
        int i = 1;

        for(ListaDePrecios ldp : ldps) {
            List<Node> nodes = buildRow(ldp);
            listaGrid.addRow(i, (Node[]) nodes.toArray(new Node[nodes.size()]));
            i++;
        }
    }

    private List<Node> buildRow(final ListaDePrecios ldp) {
        //Inicialización de variables
        ArrayList<Node> nodes = new ArrayList();
        final NumberFormat cf = NumberFormat.getCurrencyInstance();
        final NumberFormat nb = NumberFormat.getNumberInstance();
        nb.setMaximumFractionDigits(2);
        nb.setGroupingUsed(false);
        final PrecioOmoikaneLogic precioAlterno = art.getPrecio( ldp.getId().intValue() );

        //Declaración de variables y asignación de valores
        Label labelDescripcion = new Label(ldp.getDescripcion());
        Label labelCosto = new Label( cf.format( precioAlterno.getCosto() ));
        final TextField textFieldFactorUtilidad = new TextField( nb.format( precioAlterno.getFactorUtilidad() ));
        final Label labelUtilidad = new Label( cf.format(precioAlterno.getUtilidad()) );
        final Label labelDescuentos = new Label( cf.format(precioAlterno.getDescuento()) );
        final Label labelImpuestos = new Label( cf.format(precioAlterno.getImpuestos()) );
        final Label labelPrecio = new Label( cf.format(precioAlterno.getPrecio()) );

        //Bindings y eventos
        textFieldFactorUtilidad.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                Articulo a = art;
                BigDecimal nuevoFactorUtilidad;
                try {
                    nuevoFactorUtilidad = new BigDecimal(s2);
                } catch (NumberFormatException nfe) {
                    //Si el número es inválido falla silenciosamente y establece un factor de 0
                    nuevoFactorUtilidad = new BigDecimal(0);
                    textFieldFactorUtilidad.setText("0.00");
                }
                a.getBaseParaPrecio().setPrecioAlterno(ldp.getId().intValue(), nuevoFactorUtilidad);
                PrecioOmoikaneLogic pol = a.getPrecio( ldp.getId().intValue() );

                labelUtilidad.setText( cf.format(pol.getUtilidad()) );
                labelDescuentos.setText( cf.format( pol.getDescuento() ) );
                labelImpuestos.setText( cf.format( pol.getImpuestos() ) );
                labelPrecio.setText( cf.format( pol.getPrecio() ) );
            }
        });

        //Formación del row que es el resultado de éste método
        nodes.add(labelDescripcion);
        nodes.add(labelCosto);
        nodes.add(textFieldFactorUtilidad);
        nodes.add(labelUtilidad);
        nodes.add(labelDescuentos);
        nodes.add(labelImpuestos);
        nodes.add(labelPrecio);

        return nodes;
    }

    public void setProducto(Articulo a) {
        art = a;
        load();
    }

}
