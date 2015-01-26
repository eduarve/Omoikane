package omoikane.sistema;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import omoikane.caja.nadroCDS.BeneficiosController;
import omoikane.caja.nadroCDS.ValidarTarjetaCDSController;
import omoikane.compras.MVC.CompraController;
import omoikane.compras.MVC.ComprasCRUDController;
import omoikane.etiquetas.ImpresionEtiquetasController;
import omoikane.inventarios.StockLevelsController;
import omoikane.inventarios.tomaInventario.ConteoInventarioCRUDController;
import omoikane.inventarios.tomaInventario.TomaInventarioController;
import omoikane.inventarios.traspasoEntrante.TraspasoEntranteCRUDController;
import omoikane.inventarios.traspasoEntrante.TraspasoEntranteController;
import omoikane.inventarios.traspasoSaliente.TraspasoSaliente;
import omoikane.inventarios.traspasoSaliente.TraspasoSalienteCRUDController;
import omoikane.inventarios.traspasoSaliente.TraspasoSalienteController;
import omoikane.producto.CodigosController;
import omoikane.producto.PaqueteController;
import omoikane.producto.compras.ComprasProductoController;
import omoikane.producto.departamento.DepartamentoCRUDController;
import omoikane.producto.impuestos.ImpuestosCRUDController;
import omoikane.producto.listadeprecios.ListaDePreciosCRUDController;
import omoikane.producto.listadeprecios.ListaDePreciosProductoController;
import omoikane.proveedores.ProveedoresController;
import omoikane.reportes.JasperServerReportsController;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 17/02/13
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
@Configuration
public class SpringAnnotatedConfig {
    public static final Logger logger = Logger.getLogger(SpringAnnotatedConfig.class);

    @Bean
    @Scope("prototype")
    CodigosController codigosController() {
        return new CodigosController();
    }

    @Bean
    @Scope("prototype")
    PaqueteController paqueteController() {
        return new PaqueteController();
    }

    @Bean
    @Scope("prototype")
    StockLevelsController stockLevelsController() {
        return new StockLevelsController();
    }

    @Bean
    @Scope("prototype")
    ProveedoresController proveedoresController() {
        return new ProveedoresController();
    }

    @Bean
    @Scope("prototype")
    TomaInventarioController tomaInventarioController() {
        return new TomaInventarioController();
    }

    @Bean
    @Scope("prototype")
    TraspasoSalienteController traspasoSalienteController() { return new TraspasoSalienteController(); }

    @Bean
    @Scope("prototype")
    TraspasoEntranteController traspasoEntranteController() { return new TraspasoEntranteController(); }

    @Bean
    @Scope("prototype")
    CompraController compraController() {
        return new CompraController();
    }

    @Bean
    @Scope("prototype")
    ConteoInventarioCRUDController conteoInventarioCRUDController() {
        return new ConteoInventarioCRUDController();
    }

    @Bean
    @Scope("prototype")
    TraspasoSalienteCRUDController traspasoSalienteCRUDController() {
        return new TraspasoSalienteCRUDController();
    }

    @Bean
    @Scope("prototype")
    TraspasoEntranteCRUDController traspasoEntranteCRUDController() {
        return new TraspasoEntranteCRUDController();
    }

    @Bean
    @Scope("prototype")
    ComprasCRUDController comprasCRUDController() { return new ComprasCRUDController(); }

    @Bean
    @Scope("prototype")
    ListaDePreciosCRUDController listaDePreciosCRUDController() {
        return new ListaDePreciosCRUDController();
    }

    @Bean
    @Scope("prototype")
    ImpresionEtiquetasController impresionEtiquetasController() {
        return new ImpresionEtiquetasController();
    }

    @Bean
    @Scope("prototype")
    ImpuestosCRUDController impuestosCRUDController() {
        return new ImpuestosCRUDController();
    }

    @Bean
    @Scope("prototype")
    ListaDePreciosProductoController listaDePreciosProductoController() {
        return new ListaDePreciosProductoController();
    }

    @Bean
    @Scope("prototype")
    ComprasProductoController comprasProductoController() {
        return new ComprasProductoController();
    }

    @Bean
    @Scope("prototype")
    DepartamentoCRUDController departamentoCRUDController() {
        return new DepartamentoCRUDController();
    }

    @Bean
    @Scope("prototype")
    JasperServerReportsController jasperServerReportsController() {
        return new JasperServerReportsController();
    }

    @Bean
    @Scope("prototype")
    ValidarTarjetaCDSController validarTarjetaCDSController() {
        return new ValidarTarjetaCDSController();
    }

    @Bean
    @Scope("prototype")
    BeneficiosController beneficiosController() { return new BeneficiosController(); }

    @Bean
    @Scope("prototype")
    Scene codigosView() {
        return initView("/omoikane/producto/CodigosView.fxml", codigosController());
    }

    @Bean
    @Scope("prototype")
    Scene validarTarjetaCDSView() {
        return initView("/omoikane/caja/nadroCDS/ValidarTarjetaCDSView.fxml", validarTarjetaCDSController());
    }

    @Bean
    @Scope("prototype")
    Scene beneficiosView() {
        return initView("/omoikane/caja/NadroCDS/BeneficiosView.fxml", beneficiosController());
    }

    @Bean
    @Scope("prototype")
    Scene stockLevelsView() {
        return initView("/omoikane/inventarios/StockLevelsView.fxml", stockLevelsController());
    }

    @Bean
    @Scope("prototype")
    Scene paqueteView() {
        return initView("/omoikane/producto/PaqueteView.fxml", paqueteController());
    }

    @Bean
    @Scope("prototype")
    Scene proveedoresView() {
        return initView("/omoikane/proveedores/ProveedoresView.fxml", proveedoresController());
    }

    @Bean
    @Scope("prototype")
    Scene tomaInventarioView() {
        return initView("/omoikane/inventarios/tomaInventario/TomaInventarioView.fxml", tomaInventarioController());
    }

    @Bean
    @Scope("prototype")
    Scene traspasoSalienteView() {
        return initView("/omoikane/inventarios/traspasoSaliente/TraspasoSalienteView.fxml", traspasoSalienteController());
    }

    @Bean
    @Scope("prototype")
    Scene traspasoEntranteView() {
        return initView("/omoikane/inventarios/traspasoEntrante/TraspasoEntranteView.fxml", traspasoEntranteController());
    }

    @Bean
    @Scope("prototype")
    Scene compraView() {
        return initView("/omoikane/compras/MVC/CompraView.fxml", compraController());
    }

    @Bean
    @Scope("prototype")
    Scene conteoInventarioCRUDView() {
        return initView("/omoikane/inventarios/tomaInventario/ConteoInventarioCRUDView.fxml", conteoInventarioCRUDController());
    }

    @Bean
    @Scope("prototype")
    Scene traspasoSalienteCRUDView() {
        return initView("/omoikane/inventarios/traspasoSaliente/TraspasoSalienteCRUDView.fxml", traspasoSalienteCRUDController());
    }

    @Bean
    @Scope("prototype")
    Scene traspasoEntranteCRUDView() {
        return initView("/omoikane/inventarios/traspasoEntrante/TraspasoEntranteCRUDView.fxml", traspasoEntranteCRUDController());
    }

    @Bean
    @Scope("prototype")
    Scene comprasCRUDView() {
        return initView("/omoikane/compras/MVC/ComprasCRUDView.fxml", comprasCRUDController());
    }

    @Bean
    @Scope("prototype")
    Scene impresionEtiquetasView() {
        return initView("/omoikane/etiquetas/presentation/ImpresionEtiquetasView.fxml", impresionEtiquetasController());
    }

    @Bean
    @Scope("prototype")
    Scene listaDePreciosCRUDView() {
        return initView("/omoikane/producto/listadeprecios/ListaDePreciosCRUDView.fxml", listaDePreciosCRUDController());
    }

    @Bean
    @Scope("prototype")
    Scene impuestosCRUDView() {
        return initView("/omoikane/producto/impuestos/ImpuestosCRUDView.fxml", impuestosCRUDController());
    }

    @Bean
    @Scope("prototype")
    Scene listaDePreciosProductoView() {
        return initView("/omoikane/producto/listadeprecios/ListaDePreciosProductoView.fxml", listaDePreciosProductoController());
    }

    @Bean
    @Scope("prototype")
    Scene comprasProductoView() {
        return initView("/omoikane/producto/compras/ComprasProductoView.fxml", comprasProductoController());
    }

    @Bean
    @Scope("prototype")
    Scene departamentoCRUDView() {
        return initView("/omoikane/producto/departamento/DepartamentoCRUDView.fxml", departamentoCRUDController());
    }

    @Bean
    @Scope("prototype")
    Scene jasperServerReportsView() {
        return initView("/omoikane/reportes/JasperServerReportsView.fxml", jasperServerReportsController());
    }

    private SceneOverloaded initView(String fxml, final Initializable controller) {
        FXMLLoader fxmlLoader;
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
            fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> aClass) {
                    return controller;
                }
            });
            AnchorPane page = (AnchorPane) fxmlLoader.load();
            SceneOverloaded scene = new SceneOverloaded(page, controller);
            return scene;
        } catch(IOException exception) {
            logger.error(exception.getMessage(), exception);
            return null;
        }
    }
}
