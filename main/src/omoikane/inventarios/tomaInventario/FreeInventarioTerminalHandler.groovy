package omoikane.inventarios.tomaInventario

import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import omoikane.producto.Articulo
import omoikane.repository.ProductoRepo
import org.apache.log4j.Logger

import java.text.NumberFormat

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 01/11/13
 * Time: 10:10
 * To change this template use File | Settings | File Templates.
 */
class FreeInventarioTerminalHandler implements ITerminalHandler {
    private TomaInventarioController controller;
    public static final Logger logger = Logger.getLogger(FreeInventarioTerminalHandler.class);

    public FreeInventarioTerminalHandler(TomaInventarioController c) {
        setController(c);
    }

    @Override
    public String toString() {
        return "FreeInventario by iGes";
    }

    @Override
    void setController(TomaInventarioController controller) {
        this.controller = controller
    }

    @Override
    TomaInventarioController getController() {
        return controller;
    }

    @Override
    void exportData() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccione la carpeta Aplicaciones/FreeInventario en dropbox");
        Stage stage = this.getController().mainPane.getScene().getWindow();

        final File selectedDirectory =
            directoryChooser.showDialog(stage);
        if (selectedDirectory == null) {
            return ;
        }

        String destino = selectedDirectory.absolutePath + '/productos.csv';

        FileWriter fw= new FileWriter(destino);
        NumberFormat nb = NumberFormat.getNumberInstance();
        nb.setMaximumFractionDigits(2);

        ProductoRepo repo = getController().productoRepo;
        List<Articulo> articulos = repo.findAllIncludingStock();
        for (Articulo articulo : articulos) {
            def renglonData = [
                    articulo.idArticulo,
                    articulo.codigo,
                    articulo.descripcion,
                    articulo.unidad,
                    nb.format( articulo.getPrecio().precio.doubleValue() ),
                    articulo.stockInitializated.getUbicacion()
            ]
            String renglon = renglonData.join("\t")+"\r\n";
            logger.debug(renglon);
            fw.write(renglon);
            fw.flush();
        }
        fw.close();
    }

    @Override
    void importData() {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccione la carpeta Aplicaciones/FreeInventario en dropbox");
        com.sun.javafx.stage.EmbeddedWindow stage = this.getController().mainPane.getScene().getWindow();

        final File selectedDirectory =
            directoryChooser.showDialog(stage);
        if (selectedDirectory == null) {
            return ;
        }

        String origen = selectedDirectory.absolutePath + '/inventario.csv';
        ConteoInventarioPropWrapper modelo = getController().modelo;
        def encontrados = 0, noEncontrados = 0;

        new File(origen).eachLine{
            def(String idString, String codigo, String descripcion, String cantidadString, String precioString) = it.split("\t");
            //BigDecimal precio   = new BigDecimal(precioString);
            //Long       id       = new Long(idString);
            BigDecimal cantidad = new BigDecimal(cantidadString);

            Articulo a = getController().getArticulo(codigo);
            if(a == null)
                noEncontrados++;
            else {
                encontrados++;
                getController().addItemConteo(a, cantidad);
            }
        }
        logger.info("Importación finalizada, renglones importados: ${encontrados}, productos no encontrado: ${noEncontrados}");
    }
}
