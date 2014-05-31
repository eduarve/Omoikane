package phesus.configuratron.model;

import javafx.beans.property.*;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 23/08/12
 * Time: 04:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class Bascula {
    private BooleanProperty activa;
    private StringProperty port;
    private IntegerProperty baud;
    private IntegerProperty bits;
    private IntegerProperty stopBits;
    private StringProperty  parity;
    private StringProperty  stopChar;
    private StringProperty  weightCommand;

    public BooleanProperty getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        BooleanProperty a = new SimpleBooleanProperty();
        a.set(activa);
        this.activa = a;
    }

    public StringProperty getPort() {
        return port;
    }

    public void setPort(String puerto) {
        StringProperty port = new SimpleStringProperty();
        port.set(puerto);
        this.port = port;
    }

    public IntegerProperty getBaud() {
        return baud;
    }

    public void setBaud(Integer baud) {
        IntegerProperty b = new SimpleIntegerProperty();
        b.set(baud);
        this.baud = b;
    }

    public IntegerProperty getBits() {
        return bits;
    }

    public void setBits(Integer bits) {
        IntegerProperty b = new SimpleIntegerProperty();
        b.set(bits);
        this.bits = b;
    }

    public IntegerProperty getStopBits() {
        return stopBits;
    }

    public void setStopBits(Integer stopBits) {
        IntegerProperty stop = new SimpleIntegerProperty();
        stop.set(stopBits);
        this.stopBits = stop;
    }

    public StringProperty getParity() {
        return parity;
    }

    public void setParity(String parity) {
        StringProperty p = new SimpleStringProperty();
        p.set(parity);
        this.parity = p;
    }

    public StringProperty getStopChar() {
        return stopChar;
    }

    public void setStopChar(String stopChar) {
        StringProperty sc = new SimpleStringProperty();
        sc.set( stopChar );
        this.stopChar = sc;
    }

    public StringProperty getWeightCommand() {
        return weightCommand;
    }

    public void setWeightCommand(String weightCommand) {
        StringProperty cmd = new SimpleStringProperty();
        cmd.set(weightCommand);
        this.weightCommand = cmd;
    }
}
