package phesus.configuratron;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 23/08/12
 * Time: 02:01 PM
 * To change this template use File | Settings | File Templates.
 */
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import omoikane.principal.Principal;
import omoikane.repository.CajaRepo;
import omoikane.sistema.SceneOverloaded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import phesus.configuratron.model.*;

import javax.print.PrintService;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import com.fxexperience.javafx.scene.control.*;
import phesus.configuratron.view.PersistentButtonToggleGroup;


public class ConfiguratorController implements Initializable {

    private ConfigurationDao daoConfig;
    private Configuration config;
    private TemplatesDao daoTemplates;
    private Templates templates;

    final Logger logger = LoggerFactory.getLogger( ConfiguratorController.class );

    @FXML
    private TitledPane x3;

    @FXML private IntegerField resolucionAncho;
    @FXML private IntegerField resolucionAlto;
    @FXML private IntegerField idAlmacen;
    @FXML private IntegerField idCaja;

    @FXML private TextField urlServidor;
    @FXML private TextField urlBD;
    @FXML private TextField userBD;
    @FXML private TextField passBD;
    @FXML private ComboBox<TipoCorte> tipoCorte;

    @FXML private ToggleButton impresoraActiva;

    @FXML private ToggleButton impresoraInactiva;

    @FXML private TextField puertoImpresora;

    @FXML private ToggleButton scannerActivo;

    @FXML private ToggleButton scannerInactivo;

    @FXML private TextField puertoScanner;

    @FXML private IntegerField scannerBaud;

    @FXML private ToggleButton basculaActiva;

    @FXML private ToggleButton basculaInactiva;

    @FXML private TextField portScale;

    @FXML private IntegerField baudScale;

    @FXML private IntegerField bitsScale;

    @FXML private IntegerField stopBitScale;

    @FXML private TextField parityScale;

    @FXML private TextField stopCharScale;

    @FXML private TextField weightCommandScale;

    @FXML private TextArea plantillaTicket;

    @FXML private TextArea plantillaCorte;

    @FXML private Button cerrarBtn;

    @FXML private ChoiceBox impresoraList;

    @FXML private RadioButton puertoParaleloRadioButton;

    @FXML private RadioButton puertoUsbRadioButton;

    @FXML private ToggleGroup impresoraGroup;

    @FXML private Accordion accordion;

    @FXML public void aplicarCambios(ActionEvent event) {

        try {
            daoConfig.write(config);
            daoTemplates.save(templates);
            logger.info("Cambios aplicados");
            //Recargar configuración en caliente
            omoikane.principal.Principal.setConfig(new omoikane.sistema.Config());
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            logger.error(e.getMessage(), e);
        }

    }

    @FXML public void cerrar(ActionEvent event) {
        Platform.exit();
    }

    @FXML public void probarMySQL(ActionEvent event) {
            Boolean testResult = mySQLTester(config.getUrlMySQL().get(),
                     config.getUserBD().get(),
                     config.getPassBD().get()) ;

            String testMsg= "";

            if ( testResult ) {
                testMsg = "Conexión exitosa" ;
            } else {
                testMsg = "Conexión fallida, los datos son erróneos o la BD no se está ejecutando." ;
            }

            logger.info( testMsg );
    }

    public ConfiguratorController() {

        daoConfig = new ConfigurationDao();
        try {
            config = daoConfig.read();

        } catch (ParserConfigurationException e) {
            logger.error( e.getMessage(), e );
        } catch (SAXException e) {
            logger.error( e.getMessage(), e );
        } catch (IOException e) {
            logger.error( e.getMessage(), e );
        } catch (XPathExpressionException e) {
            logger.error( e.getMessage(), e );
        }

        daoTemplates = new TemplatesDao();
        try {
            templates = daoTemplates.getTemplates();
        } catch (IOException e) {
            templates = new Templates();
            logger.error( e.getMessage(), e );
        }

    }

    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert x3 != null : "fx:id=\"x3\" was not injected: check your FXML file 'configurator.fxml'.";
        assert impresoraActiva != null : "fx:id=\"impresoraActiva\" was not injected: check your FXML file 'configurator.fxml'.";


        // ---- Configuración general

        resolucionAlto.valueProperty().bindBidirectional(config.getResolucionAlto());
        resolucionAncho.valueProperty().bindBidirectional(config.getResolucionAncho());
        idAlmacen.valueProperty().bindBidirectional(config.getIdAlmacen());
        idCaja.valueProperty().bindBidirectional(config.getIdCaja());
        tipoCorte.getItems().add(TipoCorte.SENCILLO);
        tipoCorte.getItems().add(TipoCorte.DUAL);
        tipoCorte.getSelectionModel().select(config.getTipoCorte().get());
        config.getTipoCorte().bind( tipoCorte.getSelectionModel().selectedItemProperty() );

        // ---- Datos de conexión

        urlServidor.textProperty().bindBidirectional(config.getUrlNadesico());
        urlBD.textProperty().bindBidirectional(config.getUrlMySQL());
        userBD.textProperty().bindBidirectional(config.getUserBD());
        passBD.textProperty().bindBidirectional(config.getPassBD());

        // ---- Impresora
        PersistentButtonToggleGroup tgImpresora = new PersistentButtonToggleGroup();
        impresoraInactiva.setToggleGroup(tgImpresora);
        impresoraActiva.setToggleGroup(tgImpresora);
        impresoraActiva.selectedProperty().bindBidirectional(config.getImpresoraActiva());

        puertoImpresora.textProperty().bindBidirectional(config.getPuertoImpresion());
        impresoraList.valueProperty().bindBidirectional(config.getNombreImpresora());
        if(config.getTipoImpresora() == TipoImpresora.PARALELO) puertoParaleloRadioButton.fire();
        if(config.getTipoImpresora() == TipoImpresora.USB) puertoUsbRadioButton.fire();

        PrintService[] ps = PrinterJob.lookupPrintServices();
        for(PrintService psx : ps)
            impresoraList.getItems().add(psx.getName());

        puertoImpresora.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                config.setTipoImpresora(TipoImpresora.PARALELO);
                puertoParaleloRadioButton.setSelected(true);
            }
        });

        impresoraList.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object o2) {
                config.setTipoImpresora(TipoImpresora.USB);
                puertoUsbRadioButton.setSelected(true);
            }
        });

        // ---- Scanner

        PersistentButtonToggleGroup tgScanner = new PersistentButtonToggleGroup();
        scannerInactivo.setToggleGroup(tgScanner);
        scannerActivo.setToggleGroup(tgScanner);
        scannerActivo.selectedProperty().bindBidirectional(config.getScannerActivo());

        puertoScanner.textProperty().bindBidirectional(config.getScannerPort());
        scannerBaud.valueProperty().bindBidirectional(config.getScannerBaudRate());

        // ---- Báscula

        PersistentButtonToggleGroup tgBascula = new PersistentButtonToggleGroup();
        basculaInactiva.setToggleGroup(tgBascula);
        basculaActiva.setToggleGroup(tgBascula);
        basculaActiva.selectedProperty().bindBidirectional(config.getBascula().getActiva());

        portScale.textProperty().bindBidirectional(config.getBascula().getPort());
        baudScale.valueProperty().bindBidirectional(config.getBascula().getBaud());
        bitsScale.valueProperty().bindBidirectional(config.getBascula().getBits());
        stopBitScale.valueProperty().bindBidirectional(config.getBascula().getStopBits());
        parityScale.textProperty().bindBidirectional(config.getBascula().getParity());
        stopCharScale.textProperty().bindBidirectional(config.getBascula().getStopChar());
        weightCommandScale.textProperty().bindBidirectional(config.getBascula().getWeightCommand());

        // ---- Plantillas

        plantillaCorte .textProperty().bindBidirectional( templates.getPlantillaCorte() );
        plantillaTicket.textProperty().bindBidirectional(templates.getPlantillaTicket());


        // Verificar disponibilidad de Spring, Hibernate y BD
        Boolean entornoExtendido = false;
        try {
            checkSpringHibernate();
            entornoExtendido = true;
        } catch(Exception e) { }

        // ---- Impuestos

        if(entornoExtendido) addImpuestosPanel();


        // ---- Listas de precios

        if(entornoExtendido) addListasDePrecios();

    }

    public Boolean mySQLTester( String url, String user, String pass ) {
        Boolean result = false;
        try {
            Statement stmt;

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            stmt = con.createStatement();
            result = stmt.execute("show tables;");

            con.close();
            stmt.close();
        } catch (Exception e) {
            result = false;
        } finally {
            return result;
        }
    }

    public Button getCerrarBtn() {
        return cerrarBtn;
    }

    private void addImpuestosPanel() {
        final TitledPane ip = new TitledPane();
        ip.setText("Tipos de impuestos");
        accordion.getPanes().add(ip);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                SceneOverloaded scene = (SceneOverloaded) Principal.applicationContext.getBean("impuestosCRUDView");
                ip.setContent(scene.getRoot());
            }
        });
    }

    private void addListasDePrecios() {
        final TitledPane ip = new TitledPane();
        ip.setText("Niveles de precios");
        accordion.getPanes().add(ip);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                SceneOverloaded scene = (SceneOverloaded) Principal.applicationContext.getBean("listaDePreciosCRUDView");
                ip.setContent(scene.getRoot());
            }
        });
    }

    private void checkSpringHibernate() throws Exception {

        omoikane.principal.Principal.applicationContext.getBean(CajaRepo.class).count();

    }

}

