package omoikane.caja.business.plugins;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 29/12/14
 * Time: 20:10
 */
public class PluginException extends Exception {

    private static final long serialVersionUID = 1L;
    private Throwable cause = null;

    /**
     * @param cause underlying exception that caused this to be thrown
     */
    public PluginException(Throwable cause) {
        this(cause.getMessage());
        this.cause = cause;
    }

    /**
     * @param msg describes the reason this exception is being thrown.
     */
    public PluginException(String msg) {
        super(msg);
    }

    /**
     * @param msg describes the reason this exception is being thrown.
     * @param cause underlying exception that caused this to be thrown
     */
    public PluginException(String msg, Throwable cause) {
        this(msg);
        this.cause = cause;
    }

    /**
     * @return the underlying exception that caused this to be thrown
     */
    @Override
    public Throwable getCause() {
        return cause;
    }
}