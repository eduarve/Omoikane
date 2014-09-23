package omoikane.caja.business.plugins;

import omoikane.caja.presentation.CajaController;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 18/09/14
 * Time: 20:44
 * To change this template use File | Settings | File Templates.
 */
public class DummyPlugin extends SimplePlugin {
    public DummyPlugin(CajaController controller) {
        super(controller);
    }

    @Override
    public void handleEvent(TIPO_EVENTO tipoEvento) {
        System.out.println("Evento: "+tipoEvento);
    }
}
