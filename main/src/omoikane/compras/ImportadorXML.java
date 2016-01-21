package omoikane.compras;

import com.sun.javafx.stage.EmbeddedWindow;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import omoikane.compras.MVC.CompraController;
import omoikane.compras.MVC.CompraEntityWrapper;
import omoikane.compras.MVC.ItemCompraEntityWrapper;
import omoikane.inventarios.tomaInventario.ConteoInventarioPropWrapper;
import omoikane.inventarios.tomaInventario.TomaInventarioController;
import omoikane.producto.Articulo;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by octavioruizcastillo on 13/01/16.
 */
public class ImportadorXML {

    private CompraController controller;

    public static final Logger logger = Logger.getLogger(ImportadorXML.class);

    public ImportadorXML(CompraController c) {
        setController(c);
    }

    public void setController(CompraController controller) {
        this.controller = controller;
    }

    public CompraController getController() {
        return controller;
    }


    public List<HashMap<String, String>> importarRaw(String urlXML) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        HashMap<String, String> mapa;
        List<HashMap<String, String>> list = new ArrayList<>();

        builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(urlXML));

        NodeList nodes = doc.getElementsByTagName("cfdi:Concepto");

        for(int i = 0; i < nodes.getLength() ; i++) {
            Node node = nodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                mapa = new HashMap<>();
                mapa.put("noIdentificacion", element.getAttribute("noIdentificacion"));
                mapa.put("valorUnitario", element.getAttribute("valorUnitario"));
                mapa.put("cantidad", element.getAttribute("cantidad"));
                list.add(mapa);
            }
        }

        return list;
    }

    public void importDialog() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccione el archivo XML de la factura");
        EmbeddedWindow stage = (EmbeddedWindow) this.getController().getMainPane().getScene().getWindow();

        File selectedFile = null;
        try {
            selectedFile = fileChooser.showOpenDialog(stage);
            final File finalSelectedFile = selectedFile;
            ImportXML importXML = importFromFile(finalSelectedFile);
            addToCompra(importXML);

            logger.info(String.format("Importación finalizada, productos encontrados: %s, no encontrado: %s, erróneos: %s ", importXML.encontrados, importXML.noEncontrados, importXML.erroneos));
            if(importXML.summary.length() > 0) summaryDialog(importXML.summary);

        } catch(Exception e) { logger.error("Problema con el dialogo de archivos", e); }
        if (selectedFile == null) {
            return ;
        }

    }

    /**
     * Dado un archivo xml lo parsea e intenta agregar cada elemento al modelo (a través del controller)
     * @param selectedFile
     * @return El resumen de la importación, con información de los productos no encontrados
     */
    public ImportXML importFromFile(File selectedFile) {

        ImportXML importXML = new ImportXML();

        InputStream inp = null;
        try {
            inp = new FileInputStream(selectedFile);
        } catch (FileNotFoundException e) {
            logger.error("Archivo xml de factura no encontrado", e);
            return importXML;
        }

        List<HashMap<String, String>> rawData = null;
        try {
            rawData = importarRaw(selectedFile.getAbsolutePath());
        } catch (ParserConfigurationException e) {
            logger.error("Formato de archivo incorrecto", e);
        } catch (IOException e) {
            logger.error("Error al abrir el archivo xml", e);
        } catch (SAXException e) {
            logger.error("Formato de archivo xml incorrecto", e);
        }

        for(HashMap<String, String> row : rawData) {

            BigDecimal cantidad = new BigDecimal(row.get("cantidad"));
            String codigo = row.get("noIdentificacion");
            BigDecimal valorUnitario = new BigDecimal(row.get("valorUnitario"));

            Articulo a = getController().getLogic().getArticulo(codigo);
            if (a == null)
            {
                importXML.summary.append("(No encontrado) Código: " + codigo + ", cantidad: " + cantidad +" "+"\n");
                importXML.noEncontrados++;
            } else {
                ImportXML.ItemImportXML item = importXML.new ItemImportXML();
                item.articulo = a;
                item.codigo = codigo;
                item.cantidad = cantidad;
                item.valorUnitario = valorUnitario;
                importXML.items.add(item);
                importXML.encontrados++;
            }
        }

        return importXML;
    }

    public void addToCompra(ImportXML importXML) {
        for(ImportXML.ItemImportXML item : importXML.items) {
            try {
                getController().addItem(item.articulo, item.cantidad, item.valorUnitario);

            } catch(Exception e) {
                e.printStackTrace();
                importXML.summary.append("(Error) Código: " + item.codigo + ", cantidad: " + item.cantidad + " "+"\n");
                importXML.erroneos++;
            }
        }
    }

    public void summaryDialog(StringBuilder errores) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Dialogo de excepciones");
        alert.setHeaderText("Dialogo de excepciones");
        alert.setContentText("Algunos códigos no pudieron ser importados");

        Label label = new Label("Los códigos fueron");

        TextArea textArea = new TextArea(errores.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

}
