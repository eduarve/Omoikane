package omoikane.clientes;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.builder.FXFormBuilder;
import com.dooapp.fxform.view.FXFormSkinFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import omoikane.repository.ClienteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.swing.*;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 18/04/14
 * Time: 15:13
 * To change this template use File | Settings | File Templates.
 */

@Service
public class ClienteView {

    private Cliente cliente;
    private Scene scene;

    private Boolean editable = true;

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    EntityManager entityManager;

    @Autowired
    ClienteRepo clienteRepo;

    @Autowired
    Validator validator;

    FXForm fxForm;

    static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ClienteView.class);

    private Node title;

    private JInternalFrame clienteInternalFrame;

    public ClienteView() {
    }

    public void init(Integer id) {
        if(id < 1)
            cliente = new Cliente();
        else
            cliente = entityManager.find(Cliente.class, id);
        createScene();
    }

    private void createScene() {

        ClienteBeanWrapper clienteBeanWrapper = new ClienteBeanWrapper(cliente);
        FXForm fxForm = new FXFormBuilder()
                .readOnly(!editable)
                .resourceBundle(ResourceBundle.getBundle("omoikane.clientes.ClienteView"))
                .source(clienteBeanWrapper)
                .build();
        fxForm.setSkin(FXFormSkinFactory.INLINE_FACTORY.createSkin(fxForm));

        this.fxForm = fxForm;

        BorderPane borderPane = new BorderPane();
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER);
        stackPane.getChildren().add(fxForm);
        BorderPane.setMargin(stackPane, new Insets(12, 12, 12, 12));
        borderPane.setCenter(stackPane);

        borderPane.setTop(getTitle());

        borderPane.setBottom(getControles());
        scene = new Scene(borderPane);

    }

    public Scene getScene() {

        return scene;
    }

    @Transactional
    public void guardar() {
        try {
            List violaciones = fxForm.getConstraintViolations();

            if( violaciones.size() > 0  )
                logger.info("Verifique los datos capturas");
            else
                {
                    clienteRepo.saveAndFlush(cliente);
                    logger.info("Cliente guardado");
                }
        } catch (DataIntegrityViolationException e) {
            logger.error("Error al guardar: "+e.getMessage(), e);
        } catch(ConstraintViolationException e) {
            logger.info("Verifique los datos capturas");
        }
    }

    private Node getControles() {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(15, 12, 15, 12));
        hBox.setStyle("-fx-background-color: black;");
        Button guardar = new Button("Guardar");
        guardar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                ClienteView.this.guardar();
            }
        });
        guardar.setDisable(!editable);

        Button cerrar = new Button("Cerrar");
        cerrar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ClienteView.this.clienteInternalFrame.dispose();
            }
        });

        hBox.getChildren().addAll(guardar, cerrar);

        return hBox;
    }

    private Node getTitle() {
        HBox layout = new HBox();
        layout.setPadding(new Insets(15,12,15,12));
        layout.setSpacing(12);

        Label etiquetaTitulo = new Label(cliente.getId()==0 ? "Nuevo cliente" : "Detalles de cliente");
        etiquetaTitulo.setStyle("-fx-font-size: 24;");
        etiquetaTitulo.setAlignment(Pos.CENTER_RIGHT);
        layout.getChildren().add(etiquetaTitulo);
        HBox.setHgrow(etiquetaTitulo, Priority.ALWAYS);

        VBox idBox = new VBox();
        Label idLabelLabel = new Label("ID");
        Label idLabel = new Label(String.valueOf( cliente.getId() ));
        idLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        idBox.setAlignment(Pos.CENTER_RIGHT);
        idBox.getChildren().addAll(idLabelLabel, idLabel);
        HBox.setHgrow(idBox, Priority.ALWAYS);

        layout.getChildren().add(idBox);

        return layout;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public JInternalFrame getClienteInternalFrame() {
        return clienteInternalFrame;
    }

    public void setClienteInternalFrame(JInternalFrame clienteInternalFrame) {
        this.clienteInternalFrame = clienteInternalFrame;
    }
}
