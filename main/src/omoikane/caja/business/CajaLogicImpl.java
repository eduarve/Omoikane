package omoikane.caja.business;

import groovy.util.Eval;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import name.antonsmirnov.javafx.dialog.Dialog;
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.synyx.hades.domain.PageRequest;
import org.synyx.hades.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    /**
     * Pseudo evento gatillado cuando se intenta capturar un producto en la "línea de captura".
     * Ignora cualquier intento de captura si ya existe una en curso
     */
    public synchronized void onCaptura(CajaModel model) {
        if(!capturaBloqueada) {
            capturaBloqueada = true;
            try {
                LineaDeCapturaFilter capturaFilter = new LineaDeCapturaFilter(model.getCaptura().get());
                model.getCaptura().set("");

                addProducto(model, capturaFilter);

            } catch(IndexOutOfBoundsException e) {
                Dialog.showInfo("Producto no encontrado", "Producto no encontrado");
                logger.trace("Producto no encontrado");
            } catch (Exception e) {
                logger.error("Error durante captura ('evento onCaptura')", e);
            }
            capturaBloqueada = false;
        }
    }

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
            productoToProductoModel(p, productoModel);
            productoModel.cantidadProperty().set( new BigDecimal(1) );

            obsProductos.add(productoModel);
        }
        if(obsProductos.size() > 0) obsProductos.add( new BuscarMasDummyProducto() );

        model.setPaginacionBusqueda(new PageRequest(pagina.getPageNumber()+1, pagina.getPageSize()));
    }


    private void addProducto(CajaModel model, LineaDeCapturaFilter capturaFilter) throws Exception {
        model.getProductos().clear(); // Borra resultados de la búsqueda integrada

        Producto producto = productosDAO.findByCodigo(capturaFilter.getCodigo()).get(0);
        /*Articulo producto = productoRepo.findByCodigo(captura.getCodigo()).get(0);*/

        ProductoModel productoModel = new ProductoModel();
        productoToProductoModel(producto, productoModel);
        productoModel.cantidadProperty().set( capturaFilter.getCantidad() );
        reglasDeCantidad(productoModel);

        LegacyVentaDetalle lvd = null;

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

            lvd = productoBase.getVentaDetalleEntity();

        }   else {
            model.getVenta().add(0, productoModel);

            lvd = new LegacyVentaDetalle();
        }

        syncLegacyVentaDetalleWithModel(Principal.IDCaja, Principal.IDAlmacen, productoModel, lvd);

        lvd.setVenta(model.getVentaEntity());

        LegacyVentaDetalle l = persistirItemVenta(lvd);
        productoModel.setVentaDetalleEntity( l );

    }

    @Override
    public void deleteRowFromVenta(int row) {

        getController().getModel().getVenta().remove(row);
        persistirVenta();

    }

    @Override
    public void persistirVenta() {

        CajaModel model = getController().getModel();
        model.setVentaEntity(  guardarVenta(model) );
    }

    @Override
    public LegacyVentaDetalle persistirItemVenta(LegacyVentaDetalle lvd) {
        return transactionalPersistItemVenta(lvd);
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
        System.out.println("prueba");
        try {
            Cliente cliente = entityManager.find(Cliente.class, idCliente);

            getController().getModel().setCliente(cliente);
            System.out.println("prueba"+cliente.getNombre());

            persistirVenta();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public LegacyVentaDetalle transactionalPersistItemVenta(final LegacyVentaDetalle lvd) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        LegacyVentaDetalle result = transactionTemplate.execute(new TransactionCallback<LegacyVentaDetalle>() {

            @Override
            public LegacyVentaDetalle doInTransaction(TransactionStatus status) {
                return entityManager.merge(lvd);
            }
        });
        return result;
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
    private void productoToProductoModel(Producto producto, ProductoModel productoModel) {

        //nvp: nivel de precio, puede ser el ID de la lista de precios asignada al cliente o 0 para usar el precio primario
        Integer nvp   = getController().getModel().getCliente().get().getListaDePreciosId();
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

    @Transactional
    public LegacyVenta terminarVenta(CajaModel model) throws RuntimeException {

        if(!isCajaOpen()) { Dialog.showInfo("Caja cerrada", "Caja cerrada, no se puede vender."); return null; }

        BigDecimal ventaTotal = model.getTotal().get();
        if( ventaTotal.compareTo( new BigDecimal("0.10") ) > 0 ) {
            try {
                model.getVentaEntity().setCompletada(true);
                LegacyVenta venta = guardarVenta(model);

                //Hace las salidas de inventario / Make inventory issues
                new StockIssuesHandler(getController()).handle();
                return venta;

            } catch (Exception e) {
                throw new RuntimeException("Excepción persistiendo venta o aplicando cambios al stock", e);
            }
        }
        return null;
    }


    @Override
    public void nuevaVenta() {
        instanciarModeloVenta();

        getController().getCapturaTextField().requestFocus();
    }

    @Override
    public LegacyVenta getVentaAbiertaBean() {
        return getController().getModel().getVentaEntity();
    }


    private void instanciarModeloVenta() {
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
        }
    }

    private LegacyVenta buscarVentaIncompleta() {
        Integer idCaja    = Principal.IDCaja;
        LegacyVenta venta = ventaRepo.findByIdCajaAndCompletada(idCaja, false);
        return venta;
    }

    private void cargarVentaIncompleta(LegacyVenta venta) {
        cargarCliente(venta);
        for( LegacyVentaDetalle lvd : venta.getItems()) {
            ProductoModel productoModel = new ProductoModel();
            productoModel.cantidadProperty().set(BigDecimal.valueOf(lvd.getCantidad()));
            productoToProductoModel(productosDAO.findById(new Long(lvd.getIdArticulo())), productoModel);
            productoModel.setVentaDetalleEntity( lvd );
            getController().getModel().getVenta().add(productoModel);
        }
    }

    private void cargarCliente(LegacyVenta venta) {
        if(venta.getIdCliente()==null) venta.setIdCliente(1);
        Cliente cliente = entityManager.find(Cliente.class, venta.getIdCliente());
        getController().getModel().setCliente(cliente);
    }

    public void imprimirVenta(LegacyVenta venta) {
        comprobantes.ticketVenta(venta, venta.getId()); //imprimir ticket
        comprobantes.imprimir();
    }

    @Transactional(rollbackFor = Exception.class)
    private LegacyVenta guardarVenta(CajaModel model) {
        Integer idCaja    = Principal.IDCaja;
        Integer idAlmacen = Principal.IDAlmacen;
        Integer idUsuario = Usuarios.getIDUsuarioActivo();
        Integer idCliente = model.getCliente().get().getId();
        Double  efectivo  = model.getEfectivo().get().doubleValue();
        Double  cambio    = model.getCambio().get().doubleValue();
        Date    fechaHora = (Date) entityManager.createNativeQuery("SELECT current_timestamp").getSingleResult();

        LegacyVenta venta = model.getVentaEntity();

        if(model.getVentaEntity().getFolio() == null) {
            Integer folio     = asignarFolio(idCaja);
            venta.setCompletada(false);
            venta.setFolio(Long.valueOf(folio));
        }

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
        List<LegacyVentaDetalle> itemsTmp = venta.getItems();
        venta.setItems(null);

        for (ProductoModel producto : model.getVenta()) {
            LegacyVentaDetalle lvd;
            if(itemsTmp == null || itemsTmp.size() <= i)
                    lvd = new LegacyVentaDetalle();
                else
                    lvd = itemsTmp.get(i);

            syncLegacyVentaDetalleWithModel(idCaja, idAlmacen, producto, lvd);

            venta.addItem(lvd);
            i++;
        }

        //Si no se agrego ningún renglón a "venta" entonces retomamos la colección original "itemsTmp".
        //  Nota: Agregué ésta línea para corregir un bug causado por eliminar mediante cancelación el último renglón de la venta
        if(venta.getItems() == null) venta.setItems(itemsTmp);

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
