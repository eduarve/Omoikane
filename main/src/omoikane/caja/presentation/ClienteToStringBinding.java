package omoikane.caja.presentation;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import omoikane.clientes.Cliente;


/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 16/04/14
 * Time: 20:09
 * To change this template use File | Settings | File Templates.
 */
public class ClienteToStringBinding extends StringBinding {
    ObjectProperty<Cliente> cp;

    public ClienteToStringBinding(ObjectProperty<Cliente> cp) {
        super.bind(cp);
        this.cp = cp;
    }

    @Override
    protected String computeValue() {

        return cp.get().getNombre();
    }
}
