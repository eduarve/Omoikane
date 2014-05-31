package phesus.configuratron;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 30/08/12
 * Time: 19:32
 */
import name.antonsmirnov.javafx.dialog.Dialog;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

import java.awt.*;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.JOptionPane;

/**
 *
 * @author mora
 */
public class JFXAppender extends AppenderSkeleton {

    @Override
    public void close() {
        //Nothing
    }

    @Override
    public boolean requiresLayout() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void append(LoggingEvent event) {
        if ( event.getLevel().isGreaterOrEqual(Priority.ERROR) ) {
            Toolkit.getDefaultToolkit().beep();
            Dialog.showThrowable( "Error", event.getRenderedMessage(), event.getThrowableInformation().getThrowable());
        } else {
            Toolkit.getDefaultToolkit().beep();
            Dialog.showInfo( "Aviso", event.getRenderedMessage() );
        }
        /*
        System.out.println(event.getMessage());
        ExceptionWindow ew = new ExceptionWindow();
        ew.getLblTituloError().setText((String) event.getMessage());
        if(event.getThrowableInformation() != null) {
            ew.getTxtExcepcion().setText(Misc.getStackTraceString(event.getThrowableInformation().getThrowable()));
        }
        ew.setVisible(true);
        */
    }
    /*
    private static class CEFilter implements Filter {
        @Override
        public boolean isLoggable(LogRecord record) {
            return record.getLevel().intValue() >= Level.INFO.intValue();
        }
    }

    public JFXAppender() {
        setFilter(new CEFilter());
    }

    @Override
    public void publish(LogRecord record) {
        if(record.getLevel() == Level.INFO)
            aviso(record);
        else
            excepcion(record);

    }

    private void excepcion(LogRecord record) {

        //ExceptionWindow ew = new ExceptionWindow();
        //ew.getLblTituloError().setText(record.getMessage());
        //ew.getTxtExcepcion().setText(Misc.getStackTraceString(record.getThrown()));
        //ew.setVisible(true);
        //record.getThrown().printStackTrace();

        System.out.println("Appender... exception");
    }

    private void aviso(LogRecord record) {

        //JOptionPane.showMessageDialog(null, record.getMessage());

        System.out.println("Appender... aviso");
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() throws SecurityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    */

}
