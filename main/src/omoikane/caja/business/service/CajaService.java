package omoikane.caja.business.service;

import omoikane.caja.business.domain.PartidaModel;
import omoikane.caja.business.domain.VentaModel;
import omoikane.caja.business.plugins.IPlugin;
import omoikane.caja.business.plugins.PluginManager;
import omoikane.caja.presentation.ProductoModel;
import omoikane.clientes.Cliente;
import omoikane.entities.LegacyVentaDetalle;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;

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
    private VentaModel model;
    private PluginManager pluginManager;
    private Logger logger = Logger.getLogger(CajaService.class);

    @PersistenceContext
        EntityManager entityManager;

    public void init() {
        pluginManager = new PluginManager();

    }

    @Transactional
    public void startVenta() {
        pluginManager.notify(IPlugin.TIPO_EVENTO.PreStartVenta);

        logger.trace("Buscar venta truncada");
        model = new VentaModel();

        Cliente cliente = entityManager.find(Cliente.class, 1);
        model.getCliente().set(cliente);

        pluginManager.notify(IPlugin.TIPO_EVENTO.PostStartVenta);
    }

    @Transactional
    public void finishVenta() {
        pluginManager.notify(IPlugin.TIPO_EVENTO.PreFinishVenta);

        pluginManager.notify(IPlugin.TIPO_EVENTO.PostFinishVenta);
    }

    @Transactional
    public void cancelVenta() {
        pluginManager.notify(IPlugin.TIPO_EVENTO.PreCancelVenta);

        pluginManager.notify(IPlugin.TIPO_EVENTO.PostCancelVenta);
    }

    @Transactional
    public void addPartida(PartidaModel partida) {
        pluginManager.notify(IPlugin.TIPO_EVENTO.PreAddPartida);

        Boolean       agrupar     = false;
        PartidaModel  partidaAgrupadora = null;

        for ( PartidaModel p : model.getPartidas() ) {
            if(p.getId().get() == partida.getId().get()) {
                agrupar = true;
                partidaAgrupadora = p;
                break;
            }
        }
        if(agrupar) {
            BigDecimal cantidadBase  = partidaAgrupadora.getCantidad().get();
            BigDecimal nuevaCantidad = cantidadBase.add(partida.getCantidad().get());
            partida.getCantidad().set( nuevaCantidad );
            model.getPartidas().remove(partidaAgrupadora);
            model.getPartidas().add(0, partida);

        }   else {
            model.getPartidas().add(0, partida);

        }

        pluginManager.notify(IPlugin.TIPO_EVENTO.PostAddPartida);
    }

    @Transactional
    public void removePartida() {
        pluginManager.notify(IPlugin.TIPO_EVENTO.PreRemovePartida);

        pluginManager.notify(IPlugin.TIPO_EVENTO.PostRemovePartida);
    }

    @Transactional
    public void updatePartida() {
        pluginManager.notify(IPlugin.TIPO_EVENTO.PreUpdatePartida);

        pluginManager.notify(IPlugin.TIPO_EVENTO.PostUpdatePartida);
    }

    public void setCliente(Cliente c) {

    }

    public VentaModel getModel() {
        return model;
    }
}
