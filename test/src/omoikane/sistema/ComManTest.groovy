package omoikane.sistema;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by octavioruizcastillo on 17/01/16.
 */
public class ComManTest {

    @Test
    public void testMaskWeight() throws Exception {
        ComMan comMan = new ComMan("");
        String lecturaBascula = "";
        def mask = /[A-Z]{2,2},[A-Z]{2,2},.{2,2},(?<peso>.{8,8}).{5,5}/;
        def weight = comMan.maskWeight("ST,NT,01,00140000 kgRQ", mask);
        assertEquals("00140000", weight);

    }

}
