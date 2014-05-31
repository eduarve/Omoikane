package phesus.configuratron.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 28/08/12
 * Time: 04:34 PM
 * To change this template use File | Settings | File Templates.
 */
public enum TipoCorte {
    SENCILLO (1),
    DUAL (2);

    private final IntegerProperty integer;
    TipoCorte(Integer i) {
        integer = new SimpleIntegerProperty();
        integer.set(i);
    }

    public String toString() {
        if(this.integer.get() == 1) {
            return "Sencillo";
        }
        else
        {
            return "Dual";
        }
    }
    public String toNumericString() {
        return integer.getValue().toString();
    }
    public IntegerProperty getIntegerProperty() {
        return integer;
    }

}
