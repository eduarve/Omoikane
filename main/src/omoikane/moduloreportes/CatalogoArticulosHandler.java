package omoikane.moduloreportes;

import net.sf.dynamicreports.report.builder.datatype.BigDecimalType;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import omoikane.producto.Articulo;
import omoikane.repository.ProductoRepo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 03/11/13
 * Time: 00:11
 * To change this template use File | Settings | File Templates.
 */
public class CatalogoArticulosHandler {

    public Logger logger = Logger.getLogger(getClass());

    @Autowired
    ProductoRepo productoRepo;

    public void handle() {
        List<Articulo> productos = productoRepo.findAllIncludingStock();

        List<Map<String, Object>> model = new ArrayList<>();
        try {
            for (Articulo producto : productos) {
                HashMap<String, Object> mapa = new HashMap<>();
                mapa.put("codigo", producto.getCodigo());
                mapa.put("descripcion", producto.getDescripcion());
                mapa.put("linea", producto.getIdLinea());
                mapa.put("grupo", producto.getIdGrupo());
                mapa.put("precio", producto.getPrecio().getPrecio());
                mapa.put("existencia", producto.getStock().getEnTienda());
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

            report()
                    .columns(
                            col.column("Código", "codigo", type.stringType()).setMinColumns(4),
                            col.column("Descripción", "descripcion", type.stringType()),
                            col.column("Línea", "linea", type.integerType()).setMinColumns(1),
                            col.column("Grupo", "grupo", type.integerType()).setMinColumns(1),
                            col.column("Precio", "precio", new CurrencyType()).setMinColumns(3),
                            col.column("Existencia", "existencia", type.bigDecimalType()).setMinColumns(3)
                    )
                    .setColumnTitleStyle(columnTitleStyle)
                    .highlightDetailEvenRows()
                    .setDataSource(model)
                    .title(cmp.text("Catálogo de artículos").setStyle(boldCenteredStyle2))
                    .pageFooter(cmp.pageXofY().setStyle(boldCenteredStyle))
                    .show(false);
        } catch (DRException e) {
            logger.error("Problema al generar reporte de catálogo de artículos" ,e);
        }


    }

    private class CurrencyType extends BigDecimalType {

        private static final long serialVersionUID = 1L;
        @Override
        public String getPattern() {
            return "$ #,###.00";
        }
    }

}
