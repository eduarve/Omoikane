package omoikane.caja.business.plugins;

import javafx.beans.binding.ObjectBinding;
import omoikane.caja.presentation.CajaModel;
import omoikane.entities.LegacyVenta;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 18/09/14
 * Time: 19:50
 * To change this template use File | Settings | File Templates.
 */
public interface IPlugin {

    public enum TIPO_EVENTO { PreStartVenta,
        PostStartVenta,
        PreFinishVenta,
        PostFinishVenta,
        PreCancelVenta,
        PostCancelVenta,
        PreAddPartida,
        PostAddPartida,
        PreUpdatePartida,
        PostUpdatePartida,
        PreRemovePartida,
        PreSaveVenta, PostSaveVenta, PostRemovePartida
        }
    public void handleEvent(TIPO_EVENTO tipoEvento);

    public void handlePreSaveVentaEvent(CajaModel model) throws PluginException;

    public void handlePostSaveVentaEvent(LegacyVenta venta);
}
