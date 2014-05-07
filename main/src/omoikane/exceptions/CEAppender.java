/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package omoikane.exceptions;

import javafx.application.Platform;
import omoikane.principal.Principal;
import omoikane.sistema.Dialogos;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 * @author mora
 */
public class CEAppender extends AppenderSkeleton {

    @Override
    public void close() {
        //Nothing
    }

    @Override
    public boolean requiresLayout() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void append(final LoggingEvent event) {
        if ( event.getLevel().isGreaterOrEqual(Priority.WARN) ) {
            errorWindow(event);
        } else if(event.getLevel().isGreaterOrEqual(Priority.INFO)) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null,
                            event.getMessage(),
                            "Información",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });

        } else if(Principal.DEBUG) {
            JOptionPane.showMessageDialog(null,
                    event.getMessage(),
                    "Información de depuración",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private void errorWindow(final LoggingEvent event) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //System.out.println(Misc.getStackTraceString(event.getThrowableInformation().getThrowable()));
                JFrame mainJFrame = JFrame.getFrames().length > 0 ? (JFrame) JFrame.getFrames()[0] : null;
                String stackTrace = "";
                if(event.getThrowableInformation() != null && event.getThrowableInformation().getThrowable() != null)
                    stackTrace = Misc.getStackTraceString(event.getThrowableInformation().getThrowable());
                else
                    stackTrace = "Sin stacktrace";
                Dialogos.lanzarDialogoError(mainJFrame, event.getMessage(), stackTrace);
            }
        });
    }

}
