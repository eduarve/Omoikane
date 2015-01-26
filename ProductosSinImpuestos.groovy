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

public Logger logger = Logger.getLogger(omoikane.principal.Principal.class);

EntityManagerFactory emf = (EntityManagerFactory) omoikane.principal.Principal.applicationContext.getBean("entityManagerFactory");
EntityManager em = emf.createEntityManager();

List articulos = (List) em.createNativeQuery("""
    SELECT 
        a.codigo, 
        a.descripcion as producto, 
        l.descripcion as linea  
    FROM 
        articulos a 
        JOIN lineas l ON a.id_linea = l.id_linea
        LEFT JOIN articulos_Impuesto ai ON ai.articulos_id_articulo = a.id_articulo
    WHERE            
        isnull(ai.impuestos_id)
    ORDER BY
        a.descripcion ASC 
        """).getResultList();

def arts = [];

articulos.each { a ->
    arts << ["codigo": a[0], "descripcion": a[1], "linea": a[2] as String];
}

TextColumnBuilder cargoColumn, abonoColumn;

StyleBuilder boldStyle         = stl.style().bold();
StyleBuilder boldCenteredStyle = stl.style(boldStyle)
        .setHorizontalAlignment(CENTER);
StyleBuilder columnTitleStyle  = stl.style(boldCenteredStyle)
        .setBorder(stl.pen1Point())
        .setBackgroundColor(Color.LIGHT_GRAY);
StyleBuilder titleStyle = stl.style(boldCenteredStyle)
        .setVerticalAlignment(MIDDLE)
        .setFontSize(15);
StyleBuilder subTitleStyle = stl.style()
        .setVerticalAlignment(MIDDLE)
        .setFontSize(15);
StyleBuilder colStyle = stl.style()
        .setFontSize(8);
StyleBuilder colStyleMini = stl.style().setFontSize(5);

try {
    report()
            .setPageFormat(PageType.LETTER, PageOrientation.PORTRAIT)
            .setPageMargin(DynamicReports.margin().setRight(25).setLeft(25).setTop(25).setBottom(10))
            .columns(
            col.column("Línea"      , "linea", type.stringType()).setWidth(2).setStyle(colStyle),
            col.column("Código"     , "codigo", type.stringType()).setWidth(1).setHorizontalAlignment(LEFT).setStyle(colStyle),
            col.column("Descripción", "descripcion", type.stringType()).setWidth(4).setStyle(colStyle)            
    )
            .setColumnTitleStyle(columnTitleStyle)
            .highlightDetailEvenRows()
            .title(
            cmp.horizontalList()
                    .add(
                    cmp.text("Super Farmacias Medina").setStyle(titleStyle).setHorizontalAlignment(LEFT),
                    cmp.text("Productos sin impuestos").setStyle(titleStyle).setHorizontalAlignment(RIGHT))
                    .newRow()
                    .add(
                    cmp.text("Todo el catálogo").setStyle(subTitleStyle).setHorizontalAlignment(LEFT),
                    cmp.text(Calendar.getInstance().getTime()).setStyle(subTitleStyle).setHorizontalAlignment(RIGHT))
                    .newRow()
                    .add(cmp.filler().setStyle(stl.style().setTopBorder(stl.pen2Point())).setFixedHeight(10)))
            .pageFooter(cmp.pageXofY().setStyle(boldCenteredStyle))

            .setDataSource(arts)
            .show(false);
} catch (DRException e) {
    logger.error("Error al imprimir", e);
}