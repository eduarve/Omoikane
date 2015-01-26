package omoikane.caja.business.plugins;

import omoikane.caja.presentation.CajaModel;
import omoikane.entities.LegacyVenta;

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

    public void notifyPreSaveVenta(CajaModel model) throws PluginException {
        for (IPlugin plugin : plugins) {
            plugin.handleEvent(IPlugin.TIPO_EVENTO.PreSaveVenta);
            plugin.handlePreSaveVentaEvent(model);
        }
    }

    public void postSaveVentaEvent(LegacyVenta venta) {
        for (IPlugin plugin : plugins) {
            plugin.handleEvent(IPlugin.TIPO_EVENTO.PostSaveVenta);
            plugin.handlePostSaveVentaEvent(venta);
        }
    }

    public boolean exists(Class c) {
        for (IPlugin plugin : plugins) {
            if( c.isInstance(plugin) ) return true;
        }
        return false;
    }

    public IPlugin getPlugin(Class c) {
        for (IPlugin plugin : plugins) {
            if( c.isInstance(plugin) ) return plugin;
        }
        return null;

    }
}
