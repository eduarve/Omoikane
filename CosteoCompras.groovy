import net.sf.dynamicreports.report.builder.DynamicReports
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder
import net.sf.dynamicreports.report.builder.style.StyleBuilder
import net.sf.dynamicreports.report.builder.subtotal.AggregationSubtotalBuilder
import net.sf.dynamicreports.report.constant.HorizontalAlignment
import net.sf.dynamicreports.report.constant.PageOrientation
import net.sf.dynamicreports.report.constant.PageType
import net.sf.dynamicreports.report.constant.VerticalAlignment
import net.sf.dynamicreports.report.exception.DRException
import omoikane.producto.Articulo

import java.awt.Color
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory
import org.apache.log4j.Logger

import static net.sf.dynamicreports.report.constant.HorizontalAlignment.*
import static net.sf.dynamicreports.report.constant.VerticalAlignment.*;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

imprimir();
 
 public List getData() {
     EntityManagerFactory emf = (EntityManagerFactory) omoikane.principal.Principal.applicationContext.getBean("entityManagerFactory");
     EntityManager em = emf.createEntityManager();

     List data = (List) em.createNativeQuery("""
        SELECT a.codigo, i.nombre, i.cantidad, i.costounitario 
        FROM compra_items i JOIN articulos a ON i.articulo_id_articulo = a.id_articulo 
        GROUP BY articulo_id_articulo;
        """).getResultList();
     return data;
 }
 
 public void imprimir() {
        
        try {
            def model = [];
            for (def item : getData()) {
                HashMap mapa = new HashMap();
                mapa.put("codigo"       , item[0]);
                mapa.put("descripcion"  , item[1]);
                mapa.put("existencia"   , item[2]);        
                mapa.put("costoUnitario", item[3]);
                model.add(mapa);
            }
            StyleBuilder boldStyle         = stl.style().bold();

            StyleBuilder boldCenteredStyle = stl.style(boldStyle)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);

            StyleBuilder boldCenteredStyle2 = stl.style(boldStyle)
                    .setHorizontalAlignment(HorizontalAlignment.LEFT).setFontSize(18);

            StyleBuilder columnTitleStyle  = stl.style(boldCenteredStyle)
                    .setBorder(stl.pen1Point())
                    .setBackgroundColor(Color.LIGHT_GRAY);

            
            TextColumnBuilder<BigDecimal> existenciaCol = col.column("Stock Sistema", "existencia", type.bigDecimalType());
            TextColumnBuilder<BigDecimal> costoUnitarioCol = col.column("Costo U.", "costoUnitario", type.bigDecimalType());
            TextColumnBuilder<BigDecimal> costoCol = costoUnitarioCol.multiply(existenciaCol);
            report()
                    .columns(
                            col.column("Código", "codigo", type.stringType()).setMinColumns(4),
                            col.column("Descripción", "descripcion", type.stringType()),
                           
                            existenciaCol.setMinColumns(2),
                           
                            costoUnitarioCol.setMinColumns(2),
                            costoCol.setTitle("Costeo de compras").setMinColumns(3)

                    )
                    .setColumnTitleStyle(columnTitleStyle)
                    .highlightDetailEvenRows()
                    .setDataSource(model)
                    .title(cmp.text("Conteo de inventario").setStyle(boldCenteredStyle2))
                    .subtotalsAtSummary(sbt.sum(costoCol))
                    .setSubtotalStyle(boldCenteredStyle2)
                    .pageFooter(cmp.pageXofY().setStyle(boldCenteredStyle))
                    .show(false);
        } catch (DRException e) {
            logger.error("Problema al generar reporte de conteo de inventario" ,e);
        }
    }