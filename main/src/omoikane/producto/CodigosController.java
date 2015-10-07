
package omoikane.producto;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import omoikane.caja.data.IProductosDAO;
import omoikane.caja.data.ProductosNadesicoAdapter;
import omoikane.entities.CodigoProducto;
import omoikane.repository.ProductoRepo;
import omoikane.sistema.Dialogos;
import omoikane.sistema.DialogosFX;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.synyx.hades.dao.Query;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static omoikane.sistema.Permisos.getPMA_MODIFICARARTICULO;
import static omoikane.sistema.Usuarios.cerrojo;


public class CodigosController
        implements Initializable {

    @Autowired
    ProductoRepo productoRepo;

    @FXML //  fx:id="btnAgregarCodigo"
    private Button btnAgregarCodigo; // Value injected by FXMLLoader

    @FXML //  fx:id="btnEliminar"
    private Button btnEliminar; // Value injected by FXMLLoader

    @FXML //  fx:id="txtCodigoNuevo"
    private TextField txtCodigoNuevo; // Value injected by FXMLLoader

    @FXML
    private ListView<CodigoProducto> listCodigos;

    Articulo art;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    JpaTransactionManager transactionManager;

    @Autowired
    IProductosDAO productosDao;

    public static final Logger logger = Logger.getLogger(CodigosController.class);


    // Handler for Button[fx:id="btnAgregarCodigo"] onAction
    public void onAgregar(ActionEvent event) {
        try {
            agregarCodigo();
        } catch (Exception e) {
            logger.info("Código repetido");
        }
    }

    private void agregarCodigo() {
        final CodigoProducto cp = new CodigoProducto();
        cp.setCodigo(txtCodigoNuevo.getText());
        cp.setProducto(art);

        if(!cerrojo(getPMA_MODIFICARARTICULO())){ DialogosFX.lanzarAlertaFX("Acceso Denegado"); return; }
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        Object result = transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {
                //Validar que el código no se encuentre en uso
                List<Producto> productos = productosDao.findByCodigo(cp.getCodigo());
                if(productos.size() == 0) {
                    entityManager.persist(cp);
                    listCodigos.getItems().add(cp);
                } else {

                    DialogosFX.lanzarAlertaFX("Código en uso en el producto: " + productos.get(0).getDescripcion());
                }
            }
        });
    }

    // Handler for Button[fx:id="btnEliminar"] onAction
    public void onEliminar(ActionEvent event) {

        if(!cerrojo(getPMA_MODIFICARARTICULO())){ Dialogos.lanzarAlerta("Acceso Denegado"); return; }
        if(listCodigos.getSelectionModel().getSelectedItem() == null) return;

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        Object result = transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {
                CodigoProducto codigoSeleccionado = entityManager.find(CodigoProducto.class, listCodigos.getSelectionModel().getSelectedItem().getId());
                entityManager.remove(codigoSeleccionado);
                listCodigos.getItems().remove( listCodigos.getSelectionModel().getSelectedItem() );
                listCodigos.getSelectionModel().clearSelection();
                listCodigos.getSelectionModel().selectFirst();
            }
        });
    }


    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert btnAgregarCodigo != null : "fx:id=\"btnAgregarCodigo\" was not injected: check your FXML file 'ListaDePreciosProductoView.fxml'.";
        assert btnEliminar != null : "fx:id=\"btnEliminar\" was not injected: check your FXML file 'ListaDePreciosProductoView.fxml'.";
        assert txtCodigoNuevo != null : "fx:id=\"txtCodigoNuevo\" was not injected: check your FXML file 'ListaDePreciosProductoView.fxml'.";

    }

    public void setProducto(Articulo art) {
        if(art != null) {
            listCodigos.getItems().addAll(art.getCodigosAlternos());
            this.art = art;

        }

    }

}
