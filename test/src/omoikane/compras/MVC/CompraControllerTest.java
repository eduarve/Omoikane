package omoikane.compras.MVC;

import omoikane.compras.ImportXML;
import omoikane.compras.ImportadorXML;
import omoikane.compras.entities.Compra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by octavioruizcastillo on 12/01/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-test.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
public class CompraControllerTest {

    @Autowired
    CompraController compraController;

    /**
     * Comprueba que se cargue la información de artículos desde un XML
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test
    public void importartTest() throws IOException, SAXException, ParserConfigurationException {
        ImportadorXML importadorXML = new ImportadorXML(new CompraController());
        List<HashMap<String, String>> list = importadorXML.importarRaw("facturaPruebas.xml");

        assertEquals("Importación incorrecta (noIdentificacion o todos)", "7501088505003", list.get(0).get("noIdentificacion"));
        assertEquals("Importación incorrecta (valorUnitario)", "32.44", list.get(0).get("valorUnitario"));
        assertEquals("Importación incorrecta (Cantidad)", "3", list.get(0).get("cantidad"));

        assertEquals("Importación incorrecta (noIdentificacion) 2da fila", "7501318645080", list.get(1).get("noIdentificacion"));
        assertEquals("Importación incorrecta (valorUnitario) 2da fila", "77.21", list.get(1).get("valorUnitario"));
        assertEquals("Importación incorrecta (Cantidad) 2da fila", "3", list.get(1).get("cantidad"));

        assertEquals("Importación incompleta (faltan filas)", 21, list.size());
    }

    /**
     * Comprueba que los códigos de artículo cargados desde un XML sean buscados en la BD
     */
    @Test
    public void importarArticulo() {
        File xml = new File("facturaPruebas.xml");
        CompraController controller = compraController;
        ImportadorXML importadorXML = new ImportadorXML(controller);

        ImportXML importXML = importadorXML.importFromFile(xml);

        assertEquals("Recuperando artículo desde la BD", 1, importXML.encontrados);
        assertEquals("Artículo distinto al que se esperaba", "NESTLE SVELTY FIGURA 0 FRESA 225 GR", importXML.items.get(0).articulo.getDescripcion());

    }

    /**
     * Comprueba que los artículos recuperados de la BD sean cargados en el modelo de compras
     */
    @Test
    public void importarArticuloEnCompra() {
        File xml = new File("facturaPruebas.xml");
        CompraController controller = compraController;
        controller.setModelo( new CompraEntityWrapper(new Compra()) );

        ImportadorXML importadorXML = new ImportadorXML(controller);

        ImportXML importXML = importadorXML.importFromFile(xml);
        importadorXML.addToCompra(importXML);

        assertEquals("Artículo agregado al modelo de compra es incorrecto",
                "NESTLE SVELTY FIGURA 0 FRESA 225 GR",
                controller.getModel().getItems().get(0).nombreProperty().getValue());

    }
}