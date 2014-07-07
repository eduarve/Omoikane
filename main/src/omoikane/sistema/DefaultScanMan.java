/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package omoikane.sistema;

import java.text.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Octavio
 */
public class DefaultScanMan extends ScanMan {
    public void handleScan(String scanned) {

        CharacterIterator it = new StringCharacterIterator(scanned);
        for (char ch=it.first(); ch != CharacterIterator.DONE; ch=it.next()) {
            if(((int)ch)==13) { break; }

            /*
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                Logger.getLogger(DefaultScanMan.class.getName()).log(Level.SEVERE, null, ex);

            }
            */
            super.robot.keyPress((int) ch);
            super.robot.keyRelease((int) ch);
            super.robot.delay(20);

        }
        


        try {

            super.robot.keyPress(KeyEvent.VK_ENTER);
            super.robot.keyRelease(KeyEvent.VK_ENTER);

        } catch(Exception exc) { Dialogos.error("Error al capturar desde escáner de códigos de barras", exc); }
    }
}
