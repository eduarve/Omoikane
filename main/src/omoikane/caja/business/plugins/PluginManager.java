package omoikane.caja.business.plugins;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 18/09/14
 * Time: 13:19
 *
 * Clase de modelado, con el único propósito de servir como muestra y experimento
 * No es una clase de producción
 */
public class PluginManager {
    private ArrayList<IPlugin> plugins;

    public PluginManager() {
        plugins = new ArrayList<IPlugin>();
    }

    public void registerPlugin(IPlugin ip) {
        plugins.add(ip);
    }

    public void clearPlugins() {
        plugins.clear();
    }

    public void notify(IPlugin.TIPO_EVENTO tipo_evento) {
        for (IPlugin plugin : plugins) {
            plugin.handleEvent(tipo_evento);
        }
    }
}
