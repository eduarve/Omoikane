package omoikane.moduloreportes

import omoikane.principal.Principal
import omoikane.producto.Articulo
import omoikane.repository.ProductoRepo
import omoikane.repository.VentaRepo

import javax.swing.DefaultListModel
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 29/10/13
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */
public class VentasXProductoController {
    private Integer i;
    private VentasXArticulos view;
    private DefaultListModel<Articulo> listModel;
    final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(VentasXProductoController.class);

    public VentasXProductoController() {
        view = new VentasXArticulos();
        initializeView();
    }

    def initializeView() {
        listModel = new DefaultListModel<Articulo>();
        view.listaArticulos.setModel( listModel );
        view.btnAgregar.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                Thread.start() { btnAddProducto(); }
            }
        })
        view.codigoTxt.addKeyListener( new KeyAdapter() {
            @Override
            void keyReleased(KeyEvent e) {
                if(e.keyCode == KeyEvent.VK_ENTER) view.btnAgregar.doClick();
            }
        })
        view.btnGenerar.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                Thread.start() { btnGenerarReporte() }
            }
        })
    }

    private void btnAddProducto() {
        String codigo = view.codigoTxt.getText();

        ProductoRepo productoRepo = Principal.applicationContext.getBean(ProductoRepo.class);
        List<Articulo> resultados = productoRepo.findByCodigo(codigo);
        if(resultados == null || resultados.isEmpty()) resultados = productoRepo.findByCodigoAlterno(codigo)
        if(resultados == null || resultados.isEmpty()) { logger.info("No se encontró ningún producto con el código proporcionado"); return; }

        listModel.addElement( resultados.first() );
    }

    private void btnGenerarReporte() {
        if(view.listaArticulos.getModel().getSize() == 0) { logger.info("Por favor agregue un producto al reporte"); return; }
        if(view.listaArticulos.selectionEmpty) view.listaArticulos.setSelectedIndex(0);

        view.barra.setIndeterminate(true);
        def sampleData = [
                [
                        dia: Calendar.getInstance().time,
                        cantidad: 333,
                        importe: 4444
                ]
        ]
        Articulo articuloReporte = (Articulo)view.listaArticulos.getSelectedValue();
        Date hasta = view.dateHasta.getDate();
        hasta.setHours(24);
        def data = getVentas(view.dateDesde.getDate(), hasta, articuloReporte);

        Map params = [
                "FDesde":               view.fechaDesde,
                "FHasta":               view.fechaHasta,
                "IDProducto":           articuloReporte?.idArticulo,
                "DescripcionProducto":  articuloReporte?.descripcion,
                "CodigoProducto":       articuloReporte?.codigo
        ]

        Reporte reporte = new Reporte("Plantillas/VentaPorArticulo.jrxml", (List) data, params)
        view.panel.add( reporte.getPreviewPanel() )
        reporte.getPreviewPanel().setVisible(true)
        view.barra.setIndeterminate(false);
    }

    def getVentas(Date desde, Date hasta, Articulo articulo) {
        List<Map> a = new ArrayList<Map>();
        try {
            VentaRepo ventaRepo = Principal.applicationContext.getBean(VentaRepo.class);
            a = ventaRepo.sumVentasOfArticuloByDay(desde, hasta, articulo.idArticulo.intValue());
        } catch(Exception e) {
            logger.error("Error al obtener ventas", e);
        }
        return a;
    }

    public VentasXArticulos getView() {
        return view;
    }

}
