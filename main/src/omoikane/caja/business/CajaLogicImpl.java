package omoikane.caja.business;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.Eval;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import name.antonsmirnov.javafx.dialog.Dialog;
import omoikane.caja.business.domain.VentaIncompleta;
import omoikane.caja.business.plugins.DummyPlugin;
import omoikane.caja.business.plugins.IPlugin;
import omoikane.caja.business.plugins.PluginManager;
import omoikane.caja.data.IProductosDAO;
import omoikane.caja.handlers.StockIssuesHandler;
import omoikane.caja.presentation.*;
import omoikane.clientes.Cliente;
import omoikane.entities.*;
import omoikane.principal.Principal;
import omoikane.producto.*;
import omoikane.repository.CajaRepo;
import omoikane.repository.VentaRepo;
import omoikane.sistema.Comprobantes;
import omoikane.sistema.Usuarios;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.synyx.hades.domain.PageRequest;
import org.synyx.hades.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 13/09/12
 * Time: 02:01 AM
 * To change this template use File | Settings | File Templates.
 */

@Component
public class CajaLogicImpl implements ICajaLogic {
    public static Logger logger = Logger.getLogger(CajaLogicImpl.class);
    Boolean capturaBloqueada = false;

    @Autowired
    IProductosDAO productosDAO;

    @Autowired
    Comprobantes comprobantes;

    @Autowired
    VentaRepo ventaRepo;

    @Autowired
    CajaRepo cajaRepo;

    @PersistenceContext
    EntityManager entityManager;
    private CajaController controller;

    @Autowired
    JpaTransactionManager transactionManager;

    PluginManager pluginManager;

    public PluginManager getPluginManager() { return pluginManager; }

    public CajaLogicImpl() {
        pluginManager = new PluginManager();
    }

    /**
     * Pseudo evento gatillado cuando se intenta capturar un producto en la "línea de captura".
     * Ignora cualquier intento de captura si ya existe una en curso
     */
    public void onCaptura(CajaModel model) {
        if(!capturaBloqueada) {
            capturaBloqueada = true;
            try {
                LineaDeCapturaFilter capturaFilter = new LineaDeCapturaFilter(model.getCaptura().get());
                model.getCaptura().set("");

                addProducto(model, capturaFilter, true);

            } catch(IndexOutOfBoundsException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Producto no encontrado");
                alert.setHeaderText("Producto no encontrado");
                alert.setContentText("¡Producto no encontrado!");

                alert.showAndWait();

                logger.trace("Producto no encontrado");
            } catch (Exception e) {
                logger.error("Error durante captura ('evento onCaptura')", e);
            } finally {
                capturaBloqueada = false;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void buscar(CajaModel model) {

        Pageable pagina = model.getPaginacionBusqueda();
        ObservableList<ProductoModel> obsProductos = model.getProductos();

        LineaDeCapturaFilter capturaFilter = new LineaDeCapturaFilter(model.getCaptura().get());
        String descripcion = capturaFilter.getCodigo();
        if(descripcion.isEmpty()) { obsProductos.clear(); return; }

        ArrayList<Producto> productos = (ArrayList<Producto>) productosDAO.findByDescripcionLike( "%"+descripcion+"%", pagina);

        if (pagina.getPageNumber()==0 )
            obsProductos.clear();
        else
            obsProductos.remove( obsProductos.size() - 1 ); //Remueve el renglón "Buscar más productos"

        for( Producto p : productos ) {
            ProductoModel productoModel = new ProductoModel();
            productoToProductoModel(getController().getModel(), p, productoModel);
            productoModel.cantidadProperty().set( new BigDecimal(1) );

            obsProductos.add(productoModel);
        }
        if(obsProductos.size() > 0) obsProductos.add( new BuscarMasDummyProducto() );

        model.setPaginacionBusqueda(new PageRequest(pagina.getPageNumber()+1, pagina.getPageSize()));
    }

    /**
     * Añade un producto a la venta de acuerdo a la captura realizada por el usuario
     * @param model
     * @param capturaFilter
     * @throws Exception
     */
    private void addProducto(CajaModel model, LineaDeCapturaFilter capturaFilter, Boolean mustPersist) throws Exception {
        pluginManager.notify(IPlugin.TIPO_EVENTO.PreAddPartida);
        model.getProductos().clear(); // Borra resultados de la búsqueda integrada

        Producto producto = productosDAO.findCompleteByCodigo(capturaFilter.getCodigo()).get(0);
        /*Articulo producto = productoRepo.findByCodigo(captura.getCodigo()).get(0);*/

        ProductoModel productoModel = new ProductoModel();
        productoToProductoModel(model, producto, productoModel);
        productoModel.cantidadProperty().set( capturaFilter.getCantidad() );
        reglasDeCantidad(productoModel);


        //Agrupar o agregar
        Boolean       agrupar = false;
        ProductoModel productoBase = null;
        for ( ProductoModel p : model.getVenta() ) {
            if(p.getId().get() == productoModel.getId().get()) {
                agrupar = true;
                productoBase = p;
                break;
            }
        }
        if(agrupar) {
            BigDecimal cantidadBase  = productoBase.cantidadProperty().get();
            BigDecimal nuevaCantidad = cantidadBase.add(productoModel.cantidadProperty().get());
            productoModel.cantidadProperty().set( nuevaCantidad );
            model.getVenta().remove(productoBase);
            model.getVenta().add(0, productoModel);

        }   else {
            model.getVenta().add(0, productoModel);

        }

        if(mustPersist) persistirVenta();
        pluginManager.notify(IPlugin.TIPO_EVENTO.PostAddPartida);

    }

    @Override
    public void deleteRowFromVenta(int row) {

        getController().getModel().getVenta().remove(row);
        persistirVenta();

    }

    /**
     * Método especializado en persistir ventas INCOMPLETAS
     */
    @Override
    public void persistirVenta() {

        VentaIncompleta vi = new VentaIncompleta();

        CajaModel model = getController().getModel();
        vi.setIdCliente((long) model.getCliente().get().getId());
        for (ProductoModel pm : model.getVenta()) {
            boolean add = vi.getPartidas().add(new VentaIncompleta.Partida(pm.getCantidad(), pm.codigoProperty().get()));
        }

        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        try {
            mapper.writeValue(new File("venta.json"), vi);
        } catch (IOException e) {
            logger.error("Error al escribir venta temporal",e);
        }
    }

    @Override
    public LegacyVentaDetalle persistirItemVenta(LegacyVentaDetalle lvd) {
        logger.error("Método descontinuado persistirItemVenta ha sido llamado.");
        return null;
    }

    @Override
    public void cambiarCliente(final Integer idCliente) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                _cambiarCliente(idCliente);
                return null;
            }
        };
        Platform.runLater(task);

    }

    public void _cambiarCliente(Integer idCliente) {
        try {
            Cliente cliente = entityManager.find(Cliente.class, idCliente);

            getController().getModel().setCliente(cliente);

            persistirVenta();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void reglasDeCantidad(ProductoModel productoModel) throws Exception {
        try {
            String unidad = productoModel.getProductoData().getUnidad();
            BigDecimal cantidad = productoModel.cantidadProperty().get();

            if(!unidad.equalsIgnoreCase("KG") && !unidad.equalsIgnoreCase("LT"))
            { productoModel.cantidadProperty().set( cantidad.setScale(0, RoundingMode.CEILING) ); }
            else
            {
                if(cantidad.compareTo(new BigDecimal("0.025")) < 0)
                    productoModel.cantidadProperty().set(new BigDecimal("0.025"));
            }
        } catch(Exception e) {
            throw new Exception("Error en caja comprobando unidad de producto", e);
        }
    }

    /**
     * Éste método convierte un "modelo general de producto" en un "modelo de caja de producto" utilizado únicamente
     * en la caja. Además por defecto establece el nivel de precios correcto.
     * @param producto
     * @param productoModel
     */
    private void productoToProductoModel(CajaModel model, Producto producto, ProductoModel productoModel) {

        //nvp: nivel de precio, puede ser el ID de la lista de precios asignada al cliente o 0 para usar el precio primario
        Integer nvp   = model.getCliente().get().getListaDePreciosId();
        PrecioOmoikaneLogic precio= producto.getPrecio(nvp);

        productoModel.getId()                 .set( producto.getId() );
        productoModel.codigoProperty()        .set(producto.getCodigo());
        productoModel.precioBaseProperty()    .set(precio.getPrecioBase());
        productoModel.conceptoProperty()      .set(producto.getDescripcion());
        productoModel.descuentosBaseProperty().set(precio.getDescuento());
        Collection<ImpuestoModel> impuestos = impuestosToImpuestosModel(precio.getListaImpuestos());
        productoModel.setImpuestos( impuestos );
        productoModel.precioProperty()        .set( precio.getPrecio() );
        productoModel.setProductoData(producto);
    }

    private Collection<ImpuestoModel> impuestosToImpuestosModel(Collection<Impuesto> impuestos ) {

        Collection<ImpuestoModel> impuestoModels = new ArrayList<>();
        for(Impuesto impuesto : impuestos) {
            ImpuestoModel impuestoModel = new ImpuestoModel();
            impuestoModel.setDescripcion( impuesto.getDescripcion() );
            impuestoModel.setPorcentaje(impuesto.getPorcentaje());
            impuestoModel.setImpuestoBase(impuesto.getImpuesto());
            impuestoModel.setImpuestoEntity(impuesto);
            impuestoModels.add(impuestoModel);
        }

        return impuestoModels;
    }


    public void calcularCambio(CajaModel model )
    {
        BigDecimal efectivo = model.getEfectivo().get();
        BigDecimal total    = model.getTotal().get();
        BigDecimal cambio   = efectivo.subtract(total);
        model.getCambio().setValue( cambio );
    }

    public Boolean isCajaOpen() {
        Boolean cajaAbierta = null;
        cajaAbierta = (Boolean) Eval.me("cajaAbierta", cajaAbierta,
                "def serv = omoikane.sistema.Nadesico.conectar();" +
                        "cajaAbierta = serv.cajaAbierta(omoikane.principal.Principal.IDCaja);" +
                        "serv.desconectar();" +
                        "return cajaAbierta;");

        return cajaAbierta;
    }

    @Autowired StockIssuesHandler stockIssuesHandler;
    @Transactional
    public LegacyVenta terminarVenta(CajaModel model) throws RuntimeException {

        if(!isCajaOpen()) { Dialog.showInfo("Caja cerrada", "Caja cerrada, no se puede vender."); return null; }
        pluginManager.notify(IPlugin.TIPO_EVENTO.PreFinishVenta);

        BigDecimal ventaTotal = model.getTotal().get();
        if( ventaTotal.compareTo( new BigDecimal("0.10") ) > 0 ) {
            try {
                //Ejecutar plugins
                pluginManager.notifyPreSaveVenta(model);

                //** Persistir venta **
                LegacyVenta venta = guardarVenta(model);

                //Hace las salidas de inventario / Make inventory issues
                stockIssuesHandler.handle(getController());

                //Imprimir venta
                imprimirVenta(venta);

                //Borrar venta retenida / incompleta
                File f = new File("venta.json");
                if(f.exists()) f.delete();

                //Ejecutar plugins
                pluginManager.postSaveVentaEvent(venta);

                //Ejecutar plugins
                pluginManager.notify(IPlugin.TIPO_EVENTO.PostFinishVenta);
                //Eliminar instancias de plugins
                pluginManager.clearPlugins();

                return venta;

            } catch (Exception e) {
                throw new RuntimeException("Excepción persistiendo venta o aplicando cambios al stock", e);
            }
        }

        return null;
    }


    @Override
    public void nuevaVenta() {
        pluginManager.clearPlugins(); //Depura los plugins cargados
        pluginManager.registerPlugin(new DummyPlugin(getController()));
        pluginManager.notify(IPlugin.TIPO_EVENTO.PreStartVenta);
        instanciarModeloVenta();

        Platform.runLater(() -> { getController().getCapturaTextField().requestFocus(); });
        pluginManager.notify(IPlugin.TIPO_EVENTO.PostStartVenta);
    }

    private void instanciarModeloVenta() {
        /*
        LegacyVenta ventaIncompleta = buscarVentaIncompleta();

        CajaModel model = new CajaModel();
        Cliente cliente = entityManager.find(Cliente.class, 1);
        model.setCliente(cliente);

        getController().setModel( model );

        if(ventaIncompleta == null) {
            persistirVenta();
        } else {
            cargarVentaIncompleta(ventaIncompleta);
            model.setVentaEntity( ventaIncompleta );
        }*/
        CajaModel model;
        //Inicializa el modelo de la caja
        model = instanciarNuevoModeloVenta();

        //Asigna la nueva instancia del modelo al controlador que es singleton
        getController().setModel( model );

        if(buscarVentaIncompleta()) {
            try {
                model = cargarVentaIncompleta(model);
            } catch (Exception e) {
                logger.error("Problema al cargar venta incompleta, se comenzará una nueva", e);
            }
        }


    }

    private CajaModel instanciarNuevoModeloVenta() {
        //Inicializa el modelo de la caja
        CajaModel model = new CajaModel();
        //Inicializa con el cliente 1: público en general
        Cliente cliente = entityManager.find(Cliente.class, 1);
        model.setCliente(cliente);

        return model;
    }

    private Boolean buscarVentaIncompleta() {
        File ventaPersist = new File("venta.json");
        //Si exíste un archivo venta.json considera que hay una venta incompleta
        return ventaPersist.exists();
    }

    private CajaModel cargarVentaIncompleta(CajaModel model) throws Exception {

        File ventaPersist = new File("venta.json");

        ObjectMapper mapper = new ObjectMapper();
        VentaIncompleta vi = mapper.readValue(ventaPersist, VentaIncompleta.class);

        // ** Cargar cliente **
        Cliente cliente = entityManager.find(Cliente.class, vi.getIdCliente().intValue());
        model.setCliente(cliente);

        // ** Cargar partidas **
        for (VentaIncompleta.Partida partida : vi.getPartidas()) {
            logger.trace("Partida: " + partida.getCantidad() + ": " + partida.getCodigo());
            //Introduce una captura con el formato cantidad*código del producto
            LineaDeCapturaFilter capturaFilter = new LineaDeCapturaFilter(partida.getCantidad()+"*"+partida.getCodigo().toString());
            //Añade la captura al modelo
            addProducto(model, capturaFilter, false);
        }

        return model;
        /*
        cargarCliente(venta);
        for( LegacyVentaDetalle lvd : venta.getItems()) {
            ProductoModel productoModel = new ProductoModel();
            productoModel.cantidadProperty().set(BigDecimal.valueOf(lvd.getCantidad()));
            productoToProductoModel(productosDAO.findById(new Long(lvd.getIdArticulo())), productoModel);
            productoModel.setVentaDetalleEntity( lvd );
            getController().getModel().getVenta().add(productoModel);
        }   */
    }

    private void cargarCliente(Long id) {
        Cliente cliente = entityManager.find(Cliente.class, id.intValue());
        getController().getModel().setCliente(cliente);
    }

    public void imprimirVenta(LegacyVenta venta) {
        comprobantes.ticketVenta(venta, venta.getId()); //imprimir ticket
        comprobantes.imprimir();
    }

    /**
     * A partir de la versión 4.4 éste método sólo es llamado al concluir una venta, por lo que
     * marca la venta completada = true y le asigna un folio
     * @param model
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    private LegacyVenta guardarVenta(CajaModel model) {
        Integer idCaja    = Principal.IDCaja;
        Integer idAlmacen = Principal.IDAlmacen;
        Integer idUsuario = Usuarios.getIDUsuarioActivo().intValue();
        Integer idCliente = model.getCliente().get().getId();
        Double  efectivo  = model.getEfectivo().get().doubleValue();
        Double  cambio    = model.getCambio().get().doubleValue();
        Date    fechaHora = (Date) entityManager.createNativeQuery("SELECT current_timestamp").getSingleResult();

        Integer folio     = asignarFolio(idCaja);
        LegacyVenta venta = new LegacyVenta();
        venta.setCompletada(true);
        venta.setFolio(Long.valueOf(folio));


        venta.setIdUsuario(idUsuario);
        venta.setIdAlmacen(idAlmacen);
        venta.setIdCaja(idCaja);
        venta.setIdCliente(idCliente);
        venta.setEfectivo(efectivo);
        venta.setCambio(cambio);
        venta.setCentecimosredondeados(0d);
        venta.setFacturada(0);
        venta.setFechaHora ( fechaHora );
        venta.setDescuento (model.getDescuento().get().doubleValue());
        venta.setSubtotal  (model.getSubtotal().get().doubleValue());
        venta.setImpuestos (model.getImpuestos().getValue().doubleValue());
        venta.setTotal     (model.getTotal().get().doubleValue());

        int i = 0;
        venta.setItems(null);

        for (ProductoModel producto : model.getVenta()) {
            LegacyVentaDetalle lvd;
            lvd = new LegacyVentaDetalle();

            //Rellena el legacyVentaDetalles con la información del modelo ProductoModel
            syncLegacyVentaDetalleWithModel(idCaja, idAlmacen, producto, lvd);

            venta.addItem(lvd);
            i++;
        }

        venta = ventaRepo.saveAndFlush(venta);
        return venta;
    }

    private void syncLegacyVentaDetalleWithModel(Integer idCaja, Integer idAlmacen, ProductoModel producto, LegacyVentaDetalle lvd) {

        lvd.setIdAlmacen ( idAlmacen );
        lvd.setIdArticulo( producto.getLongId().intValue() );
        lvd.setIdCaja    ( idCaja );
        lvd.setIdLinea   ( producto.getProductoData().getLineaByLineaId().getId() );
        lvd.setCantidad  ( producto.cantidadProperty().get().doubleValue() );
        lvd.setPrecio    ( producto.precioProperty().get().doubleValue() );
        lvd.setDescuento ( producto.getDescuentos().doubleValue() );
        lvd.setImpuestos(producto.getSumaImpuestos().doubleValue());
        //Convertir ImpuestoModel a VentaDetalleImpuesto
        List<VentaDetalleImpuesto> vdis = new ArrayList<>();
        for(ImpuestoModel im : producto.getImpuestos().get()) {
            VentaDetalleImpuesto vdi = new VentaDetalleImpuesto();
            vdi.setBase( im.getImpuestoBase() );
            vdi.setDescripcion(im.getDescripcion());
            vdi.setPorcentaje(im.getPorcentaje());
            vdi.setTotal(im.getImpuesto());
            vdi.setImpuestoId(im.getImpuestoEntity().getId());
            vdi.setLegacyVentaDetalle(lvd);
            vdis.add(vdi);
        }
        lvd.setVentaDetalleImpuestos( vdis );
        lvd.setSubtotal  ( producto.getSubtotal().doubleValue() );
        lvd.setTotal     ( producto.getImporte().doubleValue() );
        lvd.setTipoSalida( "" );
    }

    public Integer asignarFolio(Integer idCaja) {
        //Query q = entityManager.createQuery("SELECT Caja.uFolio where id_caja = ?" );
        Caja caja = entityManager.find(Caja.class, idCaja);

        Integer folioActual = caja.getUFolio();
        folioActual++;
        caja.setUFolio( folioActual );
        cajaRepo.save(caja);

        return folioActual;
    }

    public void onVentaListChanged(CajaModel model) {
        BigDecimal subtotal = new BigDecimal(0);
        subtotal.setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal descuentos = new BigDecimal(0);
        descuentos.setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal impuestos = new BigDecimal(0);
        impuestos.setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal total = new BigDecimal(0);
        total.setScale(2, BigDecimal.ROUND_HALF_UP);

        for ( ProductoModel producto : model.getVenta() ) {
            subtotal   = subtotal  .add( producto.getSubtotal() );
            descuentos = descuentos.add( producto.getDescuentos() );
            impuestos  = impuestos .add( producto.getSumaImpuestos() );
        }

        total = total.add( subtotal );
        total = total.subtract( descuentos );
        total = total.add( impuestos );

        model.getSubtotal().set( subtotal );
        model.getDescuento().set( descuentos );
        model.getImpuestos().set( impuestos );
        model.getTotal().set( total );
    }

    @Override
    public void setController(CajaController cajaController) {
        this.controller = cajaController;
    }

    public CajaController getController() {
        return controller;
    }
}
