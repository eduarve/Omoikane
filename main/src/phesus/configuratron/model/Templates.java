package phesus.configuratron.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 30/08/12
 * Time: 23:30
 */
public class Templates {
    private StringProperty plantillaTicket;
    private StringProperty plantillaCorte;

    public Templates() {
        plantillaTicket = new SimpleStringProperty("");
        plantillaCorte  = new SimpleStringProperty("");
    }

    public StringProperty getPlantillaTicket() {
        return plantillaTicket;
    }

    public void setPlantillaTicket(String plantillaTicket) {
        StringProperty sp = new SimpleStringProperty( plantillaTicket );
        this.plantillaTicket = sp;
    }

    public StringProperty getPlantillaCorte() {
        return plantillaCorte;
    }

    public void setPlantillaCorte(String plantillaCorte) {
        StringProperty sp = new SimpleStringProperty( plantillaCorte );
        this.plantillaCorte = sp;
    }
}
