package omoikane.caja.business;

import omoikane.caja.presentation.CajaController;
import omoikane.caja.presentation.CajaModel;
import omoikane.entities.LegacyVenta;
import omoikane.entities.LegacyVentaDetalle;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 13/09/12
 * Time: 01:15 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ICajaLogic {

    public void        onCaptura(CajaModel model);
    public void        buscar(CajaModel model);
    public void        calcularCambio(CajaModel model);
    public LegacyVenta terminarVenta(CajaModel model);
    public void        imprimirVenta(LegacyVenta venta);
    public void onVentaListChanged(CajaModel model);

    void setController(CajaController cajaController);

    void nuevaVenta();

    LegacyVenta getVentaAbiertaBean();

    void deleteRowFromVenta(int row);

    void persistirVenta();

    LegacyVentaDetalle persistirItemVenta(LegacyVentaDetalle lvd);
}
