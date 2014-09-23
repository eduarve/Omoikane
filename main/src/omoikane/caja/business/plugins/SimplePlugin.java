package omoikane.caja.business.plugins;

import omoikane.caja.presentation.CajaController;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 18/09/14
 * Time: 19:56
 * To change this template use File | Settings | File Templates.
 */
public abstract class SimplePlugin implements IPlugin {
    CajaController cajaController;
    public SimplePlugin(CajaController controller) {
        setCajaController(controller);
    }

    public CajaController getController() {
        return cajaController;
    }

    public void setCajaController(CajaController controller) {
        this.cajaController = controller;
    }
}
