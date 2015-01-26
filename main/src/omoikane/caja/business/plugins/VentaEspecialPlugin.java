package omoikane.caja.business.plugins;

import omoikane.caja.handlers.CancelarVenta;
import omoikane.caja.presentation.CajaController;
import omoikane.caja.presentation.CajaModel;
import omoikane.entities.LegacyVenta;
import omoikane.nadesicoiLegacy.Ventas;
import omoikane.sistema.Usuarios;
import org.apache.log4j.Logger;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 23/12/14
 * Time: 20:08
 */
public class VentaEspecialPlugin extends SimplePlugin {
    public static Logger logger = Logger.getLogger(VentaEspecialPlugin.class);

    public VentaEspecialPlugin(CajaController controller) {
        super(controller);
    }

    @Override
    public void handleEvent(TIPO_EVENTO tipoEvento) {

    }

    @Override
    public void handlePreSaveVentaEvent(CajaModel model) {

    }

    @Override
    public void handlePostSaveVentaEvent(LegacyVenta venta) {
        registrarVentaEspecial(venta);
    }

    private void registrarVentaEspecial(LegacyVenta venta) {

        logger.debug("Aquí registrar la venta especial, con id: "+venta.getId());

        Integer idVenta       = venta.getId().intValue();
        Long idAutorizador    = Usuarios.getIDUltimoAutorizado();

        Ventas.addVentaEspecialLegacy(idVenta, idAutorizador);
    }
}
