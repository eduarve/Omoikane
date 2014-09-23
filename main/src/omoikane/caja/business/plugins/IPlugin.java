package omoikane.caja.business.plugins;

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
        PreModifyPartida,
        PostModifyPartida,
        PreRemovePartida,
        PostRemovePartida
        }
    public void handleEvent(TIPO_EVENTO tipoEvento);
}
