package omoikane.caja.business.service;

import omoikane.caja.business.domain.VentaModel;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 18/09/14
 * Time: 13:09
 *
 * Clase de modelado, con el único propósito de servir como muestra y experimento
 * No es una clase de producción
 *
 */
public class CajaService {
    VentaModel ventaModel;

    @Transactional
    public void startVenta() {
        //plugins preStartVenta (modelo)

        //plugins postStartVenta (modelo)
    }

    @Transactional
    public void finishVenta() {
        //plugins preFinishVenta (modelo)

        //plugins postFinishVenta (modelo)
    }

    @Transactional
    public void cancelVenta() {
        //plugins preCancelVenta (modelo)

        //plugins postCancelVenta (modelo)
    }

    @Transactional
    public void addPartida() {
        //plugins preAddPartida(modelo, modeloPartida)

        //plugins preRemovePartida(modelo, modeloPartida)
    }

    @Transactional
    public void removePartida() {

    }

    @Transactional
    public void modifyPartida() {

    }
}
