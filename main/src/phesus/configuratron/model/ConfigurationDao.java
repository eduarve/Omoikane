package phesus.configuratron.model;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.*;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 23/08/12
 * Time: 05:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationDao {
    public Configuration read()
            throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException {

        Configuration config = new Configuration();
        XMLConfigReader reader = new XMLConfigReader(config);

        reader.read();
        return config;
    }

    public void write(Configuration config) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        XMLConfigWriter configWriter = new XMLConfigWriter();
        configWriter.write(config);
    }

}
