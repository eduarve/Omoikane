package omoikane.moduloreportes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.builder.subtotal.AggregationSubtotalBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import org.apache.log4j.Logger;

import java.awt.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.sbt;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 09/03/14
 * Time: 13:46
 * To change this template use File | Settings | File Templates.
 */

public class FaltantesXLineaController implements Initializable {
    @FXML
    Button btnReporte;

    @FXML
    AnchorPane pnlReporte;

    public final Logger logger = Logger.getLogger(FaltantesXLineaController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void onGenerarReporte(ActionEvent event) {
        previewReporte();
    }

    public void previewReporte() {

        TextColumnBuilder cargoColumn, abonoColumn;

        StyleBuilder boldStyle         = stl.style().bold();
        StyleBuilder boldCenteredStyle = stl.style(boldStyle)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);
        StyleBuilder columnTitleStyle  = stl.style(boldCenteredStyle)
                .setBorder(stl.pen1Point())
                .setBackgroundColor(Color.LIGHT_GRAY);
        StyleBuilder titleStyle = stl.style(boldCenteredStyle)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(15);
        StyleBuilder subTitleStyle = stl.style()
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(15);
        AggregationSubtotalBuilder<BigDecimal> sumaA,sumaB;



        try {
            report()
                    .setPageFormat(PageType.LETTER, PageOrientation.PORTRAIT)
                    .setPageMargin(DynamicReports.margin().setRight(25).setLeft(25).setTop(25).setBottom(10))
                    .columns(
                            col.column("Código", "codigo", type.dateType()).setWidth(1).setHorizontalAlignment(HorizontalAlignment.LEFT),
                            col.column("Línea", "linea", type.stringType()).setWidth(4),
                            col.column("Existencia", "cargo", type.bigDecimalType()).setWidth(1),
                            col.column("Mínimo", "abono", type.bigDecimalType()).setWidth(1),
                            col.column("Máximo", "abono", type.bigDecimalType()).setWidth(1),
                            col.column("Faltante", "abono", type.bigDecimalType()).setWidth(1)
                    )
                    .setColumnTitleStyle(columnTitleStyle)
                    .highlightDetailEvenRows()
                    .title(
                            cmp.horizontalList()
                                    .add(
                                            cmp.image(getClass().getResourceAsStream("/omoikane/artemisa/images/LogoHA.png")).setFixedDimension(150, 70),
                                            //cmp.text("Estado de cuenta").setStyle(titleStyle).setHorizontalAlignment(HorizontalAlignment.LEFT),
                                            cmp.text("Estado de cuenta").setStyle(titleStyle).setHorizontalAlignment(HorizontalAlignment.RIGHT))
                                    .newRow()
                                    .add(
                                            cmp.text("Título").setStyle(subTitleStyle).setHorizontalAlignment(HorizontalAlignment.LEFT),
                                            cmp.text(Calendar.getInstance().getTime()).setStyle(subTitleStyle).setHorizontalAlignment(HorizontalAlignment.RIGHT))
                                    .newRow()
                                    .add(cmp.filler().setStyle(stl.style().setTopBorder(stl.pen2Point())).setFixedHeight(10)))
                    .pageFooter(cmp.pageXofY().setStyle(boldCenteredStyle))

                    //.setDataSource(tabEdoCuenta.getItems())
                    .show(false);
        } catch (DRException e) {
            logger.error("Error al imprimir", e);
        }
    }
}
