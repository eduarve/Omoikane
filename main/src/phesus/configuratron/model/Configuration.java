package phesus.configuratron.model;

import javafx.beans.property.*;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 23/08/12
 * Time: 03:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class Configuration {
    private String          configFile = "config.xml";
    private IntegerProperty resolucionAncho;
    private IntegerProperty resolucionAlto;
    private IntegerProperty idSucursal;
    private IntegerProperty idCaja;
    private ObjectProperty<TipoCorte> tipoCorte;

    private StringProperty urlNadesico = new SimpleStringProperty("");
    private StringProperty urlMySQL    = new SimpleStringProperty("");
    private StringProperty userBD      = new SimpleStringProperty("");
    private StringProperty passBD      = new SimpleStringProperty("");

    private BooleanProperty impresoraActiva;
    private StringProperty  puertoImpresion = new SimpleStringProperty("");
    private ObjectProperty<TipoImpresora> tipoImpresora = new SimpleObjectProperty<>(TipoImpresora.PARALELO);
    private StringProperty nombreImpresora = new SimpleStringProperty("");

    private BooleanProperty scannerActivo;
    private StringProperty  scannerPort     = new SimpleStringProperty("");
    private IntegerProperty scannerBaudRate = new SimpleIntegerProperty(0);

    private Bascula bascula;

    public IntegerProperty getResolucionAncho() {
        return resolucionAncho;
    }

    public void setResolucionAncho(Integer resolucionAncho) {
        IntegerProperty res = new SimpleIntegerProperty();
        res.set(resolucionAncho);
        this.resolucionAncho = res;
    }

    public IntegerProperty getResolucionAlto() {
        return resolucionAlto;
    }

    public void setResolucionAlto(Integer resolucionAlto) {
        IntegerProperty res = new SimpleIntegerProperty();
        res.set(resolucionAlto);
        this.resolucionAlto = res;
    }

    public IntegerProperty getIdAlmacen() {
        return idSucursal;
    }

    public void setIdAlmacen(Integer idSucursal) {
        IntegerProperty id = new SimpleIntegerProperty();
        id.set(idSucursal);
        this.idSucursal = id;
    }

    public IntegerProperty getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(Integer idCaja) {
        IntegerProperty id = new SimpleIntegerProperty();
        id.set(idCaja);
        this.idCaja = id;
    }

    public ObjectProperty<TipoCorte> getTipoCorte() {
        return tipoCorte;
    }

    public void setTipoCorte(TipoCorte tipoCorte) {
        ObjectProperty<TipoCorte> obj = new SimpleObjectProperty<TipoCorte>(tipoCorte);
        this.tipoCorte = obj;
    }

    public StringProperty getUrlNadesico() {
        return urlNadesico;
    }

    public void setUrlNadesico(String urlNadesico) {
        StringProperty url = new SimpleStringProperty();
        url.setValue(urlNadesico);
        this.urlNadesico = url;
    }

    public StringProperty getUrlMySQL() {
        return urlMySQL;
    }

    public void setUrlMySQL(String urlMySQL) {
        StringProperty url = new SimpleStringProperty();
        url.setValue(urlMySQL);
        this.urlMySQL = url;
    }

    public StringProperty getUserBD() {
        return userBD;
    }

    public void setUserBD(String userBD) {
        StringProperty user = new SimpleStringProperty();
        user.setValue(userBD);
        this.userBD = user;
    }

    public StringProperty getPassBD() {
        return passBD;
    }

    public void setPassBD(String passBD) {
        StringProperty pass = new SimpleStringProperty();
        pass.setValue(passBD);
        this.passBD = pass;
    }

    public BooleanProperty getImpresoraActiva() {
        return impresoraActiva;
    }

    public void setImpresoraActiva(Boolean impresoraActiva) {
        BooleanProperty activa = new SimpleBooleanProperty();
        activa.set(impresoraActiva);
        this.impresoraActiva = activa;
    }

    public StringProperty getPuertoImpresion() {
        return puertoImpresion;
    }

    public void setPuertoImpresion(String puertoImpresion) {
        StringProperty puerto = new SimpleStringProperty();
        puerto.setValue(puertoImpresion);
        this.puertoImpresion = puerto;
    }

    public BooleanProperty getScannerActivo() {
        return scannerActivo;
    }

    public void setScannerActivo(Boolean scannerActivo) {
        BooleanProperty activo = new SimpleBooleanProperty();
        activo.set(scannerActivo);
        this.scannerActivo = activo;
    }

    public StringProperty getScannerPort() {
        return scannerPort;
    }

    public void setScannerPort(String scannerPort) {
        StringProperty port = new SimpleStringProperty();
        port.setValue(scannerPort);
        this.scannerPort = port;
    }

    public IntegerProperty getScannerBaudRate() {
        return scannerBaudRate;
    }

    public void setScannerBaudRate(Integer scannerBaudRate) {
        IntegerProperty baud = new SimpleIntegerProperty();
        baud.set(scannerBaudRate);
        this.scannerBaudRate = baud;
    }

    public Bascula getBascula() {
        return bascula;
    }

    public void setBascula(Bascula bascula) {
        this.bascula = bascula;
    }

    public TipoImpresora getTipoImpresora() {
        return tipoImpresora.get();
    }

    public void setTipoImpresora(TipoImpresora tipo) {
        tipoImpresora.set( tipo );
    }

    public StringProperty getNombreImpresora() {
        return nombreImpresora;
    }

    public void setNombreImpresora(String nombreImpresora) {
        this.nombreImpresora.set(nombreImpresora);
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }
}

