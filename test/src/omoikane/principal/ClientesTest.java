package omoikane.principal;

import javafx.application.Platform;
import omoikane.caja.CajaManager;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 20/04/14
 * Time: 12:41
 * To change this template use File | Settings | File Templates.
 */
public class ClientesTest {

    @Test
    public void testLanzarCatalogo() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                Platform.setImplicitExit(false);
                omoikane.principal.Principal.setConfig(new omoikane.sistema.Config());
                omoikane.principal.Principal.applicationContext = new ClassPathXmlApplicationContext("applicationContext-test.xml");


                JFrame jFrame = new JFrame("Caja");
                jFrame.setSize(960,600);
                JDesktopPane jDesktopPane = new JDesktopPane();

                //jFrame.getContentPane().add(jDesktopPane);
                jFrame.setContentPane(jDesktopPane);
                jFrame.setVisible(true);

                JInternalFrame frame = (JInternalFrame) Clientes.lanzarCatalogo();

                jDesktopPane.add(frame);

            }
        });

        while(true) {
            Thread.sleep(10000);
        }
    }
}
