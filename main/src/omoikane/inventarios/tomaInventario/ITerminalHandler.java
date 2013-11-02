package omoikane.inventarios.tomaInventario;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 01/11/13
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
public interface ITerminalHandler {
    public void setController(TomaInventarioController controller);
    public TomaInventarioController getController();

    public void exportData();
    public void importData();
}
