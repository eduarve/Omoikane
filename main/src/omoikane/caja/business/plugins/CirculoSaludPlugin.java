package omoikane.caja.business.plugins;

import com.net.cds_oroDemo.ArrayOfBonusProductList;
import com.net.cds_oroDemo.ArrayOfResponseBonusList;
import com.net.cds_oroDemo.BonusProductList;
import com.net.cds_oroDemo.Tarjeta;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.stage.Modality;
import javafx.stage.Stage;
import omoikane.caja.nadroCDS.BeneficiosController;
import omoikane.caja.nadroCDS.CDSService;
import omoikane.caja.nadroCDS.NadroCDSException;
import omoikane.caja.presentation.CajaController;
import omoikane.caja.presentation.CajaModel;
import omoikane.entities.LegacyVenta;
import omoikane.nadesicoiLegacy.Ventas;
import omoikane.principal.Principal;
import omoikane.sistema.SceneOverloaded;
import omoikane.sistema.Usuarios;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 23/12/14
 * Time: 20:08
 */
public class CirculoSaludPlugin extends SimplePlugin {
    public static Logger logger = Logger.getLogger(CirculoSaludPlugin.class);
    private CDSService cdsService;
    private Tarjeta tarjeta;
    private ArrayOfResponseBonusList beneficios;

    public CirculoSaludPlugin(CajaController controller) {

        super(controller);
        cdsService = new CDSService();
    }

    @Override
    public void handleEvent(TIPO_EVENTO tipoEvento) {

    }

    /**
     * Muestra al usuario los beneficios obtenidos por la venta para que pueda acotarlos a la disponibilidad de stock antes de redimirlos del sistema
     *
     * @param model
     */
    @Override
    public void handlePreSaveVentaEvent(CajaModel model) throws PluginException {

        try {
            ArrayOfResponseBonusList beneficios = getBeneficios(model);

            Object monitor = new Object();

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {

                    Stage ventana = lanzarVentanaBeneficios(beneficios);
                    ventana.setOnCloseRequest((event) -> {
                        synchronized (monitor) {
                            monitor.notifyAll();
                        }
                    });

                    return null;
                }
            };
            task.setOnFailed((workerStateEvent) -> {
                monitor.notifyAll();
            });

            Platform.runLater(task);

            try {
                synchronized (monitor) {
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                logger.error("Excepción mostrando ventana de beneficios círculo de la salud", e);
            }
        } catch (NadroCDSException e) {
            throw new PluginException("Problema relacionado con el círculo de la salud", e);
        }
    }

    private Stage lanzarVentanaBeneficios(ArrayOfResponseBonusList beneficios) {
        ApplicationContext context = Principal.applicationContext;
        SceneOverloaded scene = (SceneOverloaded) context.getBean("beneficiosView");
        BeneficiosController controller = (BeneficiosController) scene.getController();
        controller.setResponseBonus(beneficios);

        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle("Beneficios círculo de la salud");
        ventana.setAlwaysOnTop(true);
        ventana.setScene(scene);
        ventana.show();

        return ventana;
    }

    @Override
    public void handlePostSaveVentaEvent(LegacyVenta venta) {
        if(tarjeta != null)
            logger.trace("Tarjeta: "+tarjeta.getId());

    }


    public CDSService getCdsService() {
        return cdsService;
    }

    public void setTarjeta(Tarjeta tarjeta) {
        this.tarjeta = tarjeta;
    }

    public Tarjeta getTarjeta() {
        return tarjeta;
    }

    //Analiza la venta y envía la lista de compras al servicio de CDS para obtener una lista de beneficios disponibles
    public ArrayOfResponseBonusList getBeneficios(CajaModel model) throws NadroCDSException {

        ArrayOfBonusProductList productos = new ArrayOfBonusProductList();
                productos.getBonusProductList()
                        .addAll(
                                Arrays.asList(

                                        new BonusProductList() {{
                                            setPiezas(5l);
                                            setSku("7506200700038");
                                        }},
                                        new BonusProductList() {{
                                            setPiezas(10l);
                                            setSku("7501094917012");
                                        }}
                                )
                        )
                        ;

        ArrayOfResponseBonusList beneficios = cdsService.getBeneficios(tarjeta.getFolioCodigo(), productos);
        return beneficios;
    }
}
