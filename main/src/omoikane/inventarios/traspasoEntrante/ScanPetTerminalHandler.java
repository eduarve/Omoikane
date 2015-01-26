package omoikane.inventarios.traspasoEntrante;

import com.sun.javafx.stage.EmbeddedWindow;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import omoikane.producto.Articulo;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 23/11/13
 * Time: 14:12
 * To change this template use File | Settings | File Templates.
 */
public class ScanPetTerminalHandler implements ITerminalHandler {
    private TraspasoEntranteController controller;
    public static final Logger logger = Logger.getLogger(ScanPetTerminalHandler.class);

    public ScanPetTerminalHandler(TraspasoEntranteController c) {
        setController(c);
    }

    @Override
    public String toString() {
        return "ScanPet by Domusnatura SL";
    }

    @Override
    public void setController(TraspasoEntranteController controller) {
        this.controller = controller;
    }

    @Override
    public TraspasoEntranteController getController() {
        return controller;
    }

    @Override
    public void exportData() {
        throw new NotImplementedException("Exportar a scanpet no está implementado.");
    }

    @Override
    public void importData() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccione el inventario tomado usando la app ScanPet y (opcionalmente) almacenado en dropbox");
        EmbeddedWindow stage = (EmbeddedWindow) this.getController().mainPane.getScene().getWindow();

        File selectedFile = null;
        try {
            selectedFile = fileChooser.showOpenDialog(stage);
            final File finalSelectedFile = selectedFile;
            importa(finalSelectedFile);
        } catch(Exception e) { logger.error("Problema con el dialogo de archivos", e); }
        if (selectedFile == null) {
            return ;
        }

        /*
        Task<Void> importTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                importa(finalSelectedFile);
                return null;
            }
        };
        new Thread(importTask).start();
        */

    }

    public void importa(File selectedFile) {


        InputStream inp = null;
        try {
            inp = new FileInputStream(selectedFile);
        } catch (FileNotFoundException e) {
            logger.error("Archivo de inventario no encontrado", e);
            return;
        }

        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(inp);
        } catch (IOException e) {
            logger.error("Error de lectura del inventario", e);
            return;
        } catch (InvalidFormatException e) {
            logger.error("Formato inválido del archivo de inventario", e);
        }

        controller.setPersistOnChage(false); //No quiero que se haga merge cada vez que se inserta un renglon
        controller.setPersistable(false); //En general no quiero que se persista
        TraspasoEntrantePropWrapper modelo = getController().modelo;
        Integer encontrados = 0, noEncontrados = 0;
        Sheet sheet = wb.getSheetAt(0);
        Boolean first = true;
        StringBuilder errores = new StringBuilder();

        for(Row row : sheet) {
            if (first) {
                first = false;
                continue;
            }
            BigDecimal cantidad = new BigDecimal(row.getCell(2).getNumericCellValue());
            String codigo = row.getCell(0).getStringCellValue();

            Articulo a = getController().getArticulo(codigo);
            if (a == null)
            {
                errores.append("(No encontrado) Código: " + codigo + ", cantidad: " + cantidad +" ");
                noEncontrados++;
            } else {
                try {
                    getController().addItemConteo(a, cantidad);

                    encontrados++;
                } catch(Exception e) {
                    e.printStackTrace();
                    errores.append("(Error) Código: " + codigo + ", cantidad: " + cantidad + " ");
                    noEncontrados++;
                }
            }
        }

        controller.setPersistable(true);
        try {
            controller.persistModel();
        } catch(Exception e) {
            logger.error("Error al guardar datos", e);
        }
        if(errores.length() > 0) importDialog(errores);
        logger.info(String.format("Importación finalizada, renglones importados: %s, productos no encontrado: %s ", encontrados, noEncontrados));
        controller.setPersistOnChage(true);

    }

    public void importDialog(StringBuilder errores) {
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
