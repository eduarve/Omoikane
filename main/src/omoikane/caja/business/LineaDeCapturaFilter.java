package omoikane.caja.business;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 15/09/12
 * Time: 01:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class LineaDeCapturaFilter {

    private BigDecimal cantidad;
    private String codigo;

    public LineaDeCapturaFilter(String lineaDeCapturaString) {
        fromString(lineaDeCapturaString);
    }

    public void fromString(String lineaDeCapturaString) {
        codigo   = lineaDeCapturaString;
        cantidad = new BigDecimal(1);

        //Los filtros siguientes llevan un orden secuencial
        filtroEAN13();
        filtroMultiplicidad();
    }

    /**
     * Filtra la línea de captura para separar cantidades y códigos de producto.
     * Si no se especifica ningún multiplicador en la línea de captura se considerará automáticamente una venta
     * de una unidad.
     */
    private void filtroMultiplicidad() {
        String captura = getCodigo();

        String[]          partesDeLaCaptura = captura.split("\\*");
        ArrayList<String> partes       = new ArrayList( Arrays.asList(partesDeLaCaptura) );

        if(partes.size() > 1) {
            codigo = partes.remove( partes.size() -1);
            for(String parte : partes) {
                final BigDecimal cant = new BigDecimal(parte);
                cant.setScale(2, BigDecimal.ROUND_HALF_UP);
                cantidad = cantidad.multiply(cant);
            }
        } else {
            codigo   = captura;
        }

    }

    /**
     * Código de barras de báscula electrónica 26[xxxxx=codigo][yyyyy=cantidad][codigo seguridad] (13 dígitos)
     * @return
     */
    private void filtroEAN13() {
        String captura = getCodigo();
        if(captura != null && captura.length() == 13 && captura.substring(0,2).equals("26")) {
            BigDecimal cant = new BigDecimal(captura.substring(7, 12) ).divide(new BigDecimal(1000));
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setGroupingUsed(false);
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(2);
            setCantidad(cant);
            setCodigo(captura.substring(2,7));
        }
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
