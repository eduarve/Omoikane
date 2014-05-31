package phesus.configuratron.model;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.xpath.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 23/08/12
 * Time: 05:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class XMLConfigReader {

    private Configuration config;
    private Document doc;

    public XMLConfigReader(Configuration config) {
        this.config = config;
    }

    public void read()
            throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException {

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();

        doc = builder.parse( config.getConfigFile() );

        config.setResolucionAncho( Integer.valueOf((String) readProperty("//resolucionPantalla/@ancho", XPathConstants.STRING)) );
        config.setResolucionAlto ( Integer.valueOf((String) readProperty("//resolucionPantalla/@alto", XPathConstants.STRING)) );

        config.setIdAlmacen(Integer.valueOf((String) readProperty("//idAlmacen", XPathConstants.STRING)));
        config.setIdCaja   (Integer.valueOf((String) readProperty("//idCaja"   , XPathConstants.STRING)));
        TipoCorte tipoCorte;
        Integer tipoCorteInt = Integer.valueOf((String) readProperty("//tipoCorte", XPathConstants.STRING));
        switch (tipoCorteInt) {
            case 1:   tipoCorte = TipoCorte.SENCILLO; break;
            case 2:   tipoCorte = TipoCorte.DUAL; break;
            default:  tipoCorte = TipoCorte.SENCILLO; break;
        }
        config.setTipoCorte(tipoCorte);

        config.setUrlNadesico((String) readProperty("//URLServidor/text()", XPathConstants.STRING));
        config.setUrlMySQL   ((String) readProperty("//URLMySQL/text()"   , XPathConstants.STRING));
        config.setUserBD     ((String) readProperty("//loginJasper/text()", XPathConstants.STRING));
        config.setPassBD     ((String) readProperty("//passJasper/text()" , XPathConstants.STRING));

        //--------------------- Impresora
        try {
            config.setImpresoraActiva( Boolean.valueOf( (String) readProperty("//impresoraActiva", XPathConstants.STRING) ) );
        } catch (Exception e) {
            config.setImpresoraActiva( false );
        }
        try {
            config.setTipoImpresora( TipoImpresora.valueOf( (String) readProperty("//tipoImpresora", XPathConstants.STRING) ) );
        } catch (Exception e) {
            config.setTipoImpresora( TipoImpresora.PARALELO );
        }
        try {
            config.setNombreImpresora( (String) readProperty("//nombreImpresora", XPathConstants.STRING) );
        } catch (Exception e) {
            config.setNombreImpresora( "" );
        }
        config.setPuertoImpresion( (String) readProperty("//puertoImpresion", XPathConstants.STRING) );

        //--------------------- Scanner
        try {
            config.setScannerActivo  ( Boolean.valueOf( (String) readProperty("//scannerActivo", XPathConstants.STRING) ) );
        } catch (Exception e) {
            config.setScannerActivo( false );
        }
        try {
            config.setScannerPort    ( (String) readProperty("//ScannerPort"    , XPathConstants.STRING) );
        } catch (Exception e) {
            config.setScannerPort ( "" );
        }
        try {
            config.setScannerBaudRate( Integer.valueOf((String) readProperty("//ScannerBaudRate", XPathConstants.STRING)) );
        } catch (Exception e) {
            config.setScannerBaudRate( 0 );
        }

        //--------------------- Báscula
        Bascula bascula = new Bascula();

        try {
            bascula.setActiva( Boolean.valueOf( (String) readProperty("//bascula/@activa", XPathConstants.STRING) ) );
        } catch (Exception e) {
            bascula.setActiva( false );
        }
        bascula.setPort( (String) readProperty("//bascula/@port", XPathConstants.STRING) );
        try {
            bascula.setBaud( Integer.valueOf((String) readProperty("//bascula/@baud", XPathConstants.STRING)) );
        } catch (Exception e) {
            bascula.setBaud( 0 );
        }
        try {
            bascula.setBits( Integer.valueOf((String) readProperty("//bascula/@bits", XPathConstants.STRING)) );
        } catch (Exception e) {
            bascula.setBits( 0 );
        }
        try {
            bascula.setStopBits( Integer.valueOf((String) readProperty("//bascula/@stopBits", XPathConstants.STRING)) );
        } catch (Exception e) {
            bascula.setStopBits( 0 );
        }
        bascula.setParity( (String) readProperty("//bascula/@parity", XPathConstants.STRING) );
        bascula.setStopChar( (String) readProperty("//bascula/@stopChar", XPathConstants.STRING) );
        bascula.setWeightCommand( (String) readProperty("//bascula/@weightCommand", XPathConstants.STRING) );

        config.setBascula(bascula);

    }

    private Object readProperty(String xPathString, QName tipo) throws XPathExpressionException {

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile(xPathString);

        Object result = expr.evaluate(doc, XPathConstants.STRING);
        return result;
    }

}
