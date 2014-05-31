package phesus.configuratron.model;

import org.apache.log4j.spi.LoggerFactory;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 29/08/12
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class XMLConfigWriter {
    final Logger logger = org.slf4j.LoggerFactory.getLogger(XMLConfigWriter.class);
    public void write(Configuration config) throws ParserConfigurationException, TransformerException, SAXException, IOException {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( config.getConfigFile() );

            // ---- General

            Node ancho = doc.getElementsByTagName("resolucionPantalla").item(0).getAttributes().getNamedItem("ancho");
            ancho.setTextContent(config.getResolucionAncho().getValue().toString());
            Node alto  = doc.getElementsByTagName("resolucionPantalla").item(0).getAttributes().getNamedItem("alto");
            alto.setTextContent(config.getResolucionAlto().getValue().toString());
            Node idCaja = doc.getElementsByTagName("idCaja").item(0);
            idCaja.setTextContent(config.getIdCaja().getValue().toString());
            Node idAlmacen = doc.getElementsByTagName("idAlmacen").item(0);
            idAlmacen.setTextContent(config.getIdAlmacen().getValue().toString());
            Node urlServidor = doc.getElementsByTagName("URLServidor").item(0);
            urlServidor.setTextContent(config.getUrlNadesico().getValue());
            Node urlMySQL = doc.getElementsByTagName("URLMySQL").item(0);
            urlMySQL.setTextContent(config.getUrlMySQL().getValue());
            Node loginJasper = doc.getElementsByTagName("loginJasper").item(0);
            loginJasper.setTextContent(config.getUserBD().getValue());
            Node passJasper = doc.getElementsByTagName("passJasper").item(0);
            passJasper.setTextContent(config.getPassBD().getValue());
            Node tipoCorte = doc.getElementsByTagName("tipoCorte").item(0);
            tipoCorte.setTextContent(config.getTipoCorte().get().toNumericString());

            // ---- Periféricos

            Node impresoraActiva = doc.getElementsByTagName("impresoraActiva").item(0);
            impresoraActiva.setTextContent(config.getImpresoraActiva().getValue().toString());
            Node puertoImpresion = doc.getElementsByTagName("puertoImpresion").item(0);
            puertoImpresion.setTextContent(config.getPuertoImpresion().get());
            Node tipoImpresora = doc.getElementsByTagName("tipoImpresora").item(0);
            tipoImpresora.setTextContent(config.getTipoImpresora().toString());
            Node nombreImpresora = doc.getElementsByTagName("nombreImpresora").item(0);
            nombreImpresora.setTextContent( config.getNombreImpresora().get() );
            Node scannerActivo = doc.getElementsByTagName("scannerActivo").item(0);
            scannerActivo.setTextContent(config.getScannerActivo().getValue().toString());
            Node scannerPort = doc.getElementsByTagName("ScannerPort").item(0);
            scannerPort.setTextContent(config.getScannerPort().getValue());
            Node scannerBaudRate = doc.getElementsByTagName("ScannerBaudRate").item(0);
            scannerBaudRate.setTextContent(config.getScannerBaudRate().getValue().toString());

            // ---- Periféricos - Báscula

            NamedNodeMap xmlBascula    = doc.getElementsByTagName("bascula").item(0).getAttributes();
            Bascula      configBascula = config.getBascula();

            xmlBascula.getNamedItem("activa")       .setTextContent( configBascula.getActiva().getValue().toString() );
            xmlBascula.getNamedItem("baud")         .setTextContent( configBascula.getBaud().getValue().toString() );
            xmlBascula.getNamedItem("bits")         .setTextContent( configBascula.getBits().getValue().toString() );
            xmlBascula.getNamedItem("parity")       .setTextContent( configBascula.getParity().get() );
            xmlBascula.getNamedItem("port")         .setTextContent( configBascula.getPort().get() );
            xmlBascula.getNamedItem("stopBits")     .setTextContent( configBascula.getStopBits().getValue().toString());
            xmlBascula.getNamedItem("stopChar")     .setTextContent( configBascula.getStopChar().get() );
            xmlBascula.getNamedItem("weightCommand").setTextContent( configBascula.getWeightCommand().get() );

            // ---- Guardar archivo

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File( config.getConfigFile() ).getPath());
            transformer.transform(source, result);

        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            //Bypass upper
            throw e;
        }
    }
}
