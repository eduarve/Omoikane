package omoikane.caja.services;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 04/09/14
 * Time: 19:30
 * To change this template use File | Settings | File Templates.
 */
public interface Plugin {

    /**
     * Conecta éste plugin con un modelo de venta
     */
    public void conectar();

    public void preVenta();

    public void postVenta();

    public void preAddProducto();

    public void postAddProducto();
}
