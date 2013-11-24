package omoikane.inventarios.tomaInventario;

import com.sun.javafx.stage.EmbeddedWindow;
import javafx.concurrent.Task;
import javafx.stage.DirectoryChooser;
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
    private TomaInventarioController controller;
    public static final Logger logger = Logger.getLogger(ScanPetTerminalHandler.class);

    public ScanPetTerminalHandler(TomaInventarioController c) {
        setController(c);
    }

    @Override
    public String toString() {
        return "ScanPet by Domusnatura SL";
    }

    @Override
    public void setController(TomaInventarioController controller) {
        this.controller = controller;
    }

    @Override
    public TomaInventarioController getController() {
        return controller;
    }

    @Override
    public void exportData() {
        throw new NotImplementedException("Exportar a scanpet no está implementado.");
    }

    @Override
    public void importData() {
        Task<Void> importTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                importa();
                return null;
            }
        };
        new Thread(importTask).start();
    }

    public void importa() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccione el inventario tomado usando la app ScanPet y (opcionalmente) almacenado en dropbox");
        EmbeddedWindow stage = (EmbeddedWindow) this.getController().mainPane.getScene().getWindow();

        final File selectedFile =
                fileChooser.showOpenDialog(stage);
        if (selectedFile == null) {
            return ;
        }

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

        ConteoInventarioPropWrapper modelo = getController().modelo;
        Integer encontrados = 0, noEncontrados = 0;
        Sheet sheet = wb.getSheetAt(0);
        Boolean first = true;
        for(Row row : sheet) {
            if(first) { first = false; continue; }
            BigDecimal cantidad = new BigDecimal(row.getCell(2).getNumericCellValue());
            String     codigo   = row.getCell(0).getStringCellValue();

            Articulo a = getController().getArticulo(codigo);
            if(a == null)
                noEncontrados++;
            else {
                try {
                    getController().addItemConteo(a, cantidad);
                    Thread.sleep(100);
                    encontrados++;
                } catch(Exception e) {
                    e.printStackTrace();
                    logger.info("No se pudo importar correctamente o por completo el código: "+codigo);
                    noEncontrados++;
                }
            }
        }

        logger.info(String.format("Importación finalizada, renglones importados: %s, productos no encontrado: %s ", encontrados, noEncontrados));

    }
}
