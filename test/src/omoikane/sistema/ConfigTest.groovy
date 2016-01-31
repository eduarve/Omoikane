package omoikane.sistema;

import org.junit.Before;
import org.junit.Test
import omoikane.principal.Principal
import omoikane.sistema.huellas.ContextoFPSDK.SDK

import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Octavio Ruiz Castillo
 * Date: 26/04/12
 * Time: 05:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigTest extends GroovyTestCase {
    Config config;

    @Before
    public void setUp() throws Exception {
        config = new Config();
    }

    @Test
    public void testDefineAtributos() throws Exception {
        config.defineAtributos();

        if(config.fingerPrintSDK[0].text() == "ONETOUCH") {
            assert Principal.sdkFingerprint == SDK.ONETOUCH
        } else {
            assert Principal.sdkFingerprint == SDK.GRIAULE
        }
    }

    @Test
    public void testMaskBascula() {
        config.defineAtributos();
        if(Principal.basculaActiva) {
            //Configuración de prueba basada en básculas CAS PDS en modo ECR-2
            String patternStringTest = /[\+\-]{1,1}[ ]*(?<peso>\d+[.]{1,1}\d+)/;
            assert Principal.driverBascula.mask.equals(patternStringTest);
        } else {
            assertTrue("Báscula desactivada en config, no se puede testear en estas condiciones!.", Principal.basculaActiva);
        }
    }
}
