package omoikane.producto;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Lógica concreta para determinar el precio al público de un producto.
 * User: octavioruizcastillo
 * Date: 28/09/12
 * Time: 12:34
 */
public class PrecioOmoikaneLogic implements IPrecio {

    BaseParaPrecio baseParaPrecio;
    Collection<Impuesto> impuestos;


    public PrecioOmoikaneLogic(BaseParaPrecio baseParaPrecio, Collection<Impuesto> impuestos) {
        this.baseParaPrecio = baseParaPrecio;
        this.impuestos = impuestos;
    }

    public PrecioOmoikaneLogic(Integer listaDePrecios_id, BaseParaPrecio baseParaPrecio, Collection<Impuesto> impuestos) {
        this.baseParaPrecio = baseParaPrecio;
        this.impuestos = impuestos;
        loadPrecioAlterno(listaDePrecios_id);
    }

    /**
     * Analiza los alternos y aplica el seleccionado a esta instancia.
     * Si no exíste dicha instancia de precio alterno (o ninguna) toma la utilidad base.
     * @param listaDePrecios_id
     */
    public void loadPrecioAlterno(Integer listaDePrecios_id) {
        if(!baseParaPrecio.getPreciosAlternos().isEmpty() && baseParaPrecio.getPreciosAlternos().containsKey(listaDePrecios_id)) {
            Double porcentajeUtilidad = baseParaPrecio.getPreciosAlternos().get(listaDePrecios_id).doubleValue();
            baseParaPrecio.setPorcentajeUtilidad( porcentajeUtilidad );
        } else {
            baseParaPrecio.setPorcentajeUtilidad( baseParaPrecio.getPorcentajeUtilidadBase() );
        }
    }

    @Override
    public BigDecimal getCosto() {
        return new BigDecimal( baseParaPrecio.getCosto() );
    }

    /** Obtiene el descuento en conjunto de todos los factores que generan descuento
     * Formula, factor valor final del producto: valor = - (x-1) (y-1) (z-1)
     * Formula, factor de descuento: descuento = 1 - valor
     * x = Porcentaje de descuento del producto
     * y = Porcentaje de descuento de la línea
     * z = Porcentaje de descuento del grupo
     * @return
     */
    @Override
    public BigDecimal getPorcentajeDescuentoTotal() {
        BigDecimal cien = new BigDecimal( 100 );
        BigDecimal x = new BigDecimal( baseParaPrecio.getPorcentajeDescuentoProducto() ).divide( cien );
        BigDecimal y = new BigDecimal( baseParaPrecio.getPorcentajeDescuentoLinea()    ).divide( cien );
        BigDecimal z = new BigDecimal( baseParaPrecio.getPorcentajeDescuentoGrupo()    ).divide( cien );
        BigDecimal a = new BigDecimal( -1 );

        BigDecimal valor     = a.multiply( x.add(a) ).multiply( y.add(a) ).multiply(z.add(a));
        BigDecimal descuento = new BigDecimal(1).subtract(valor);
        return descuento.multiply( cien );
    }

    /**
     * Calcula la utilidad utilizando un factor de utilidad
     * utilidad = costo * ( porcentajeUtilidad / 100 )
     * @return
     */
    @Override
    public BigDecimal getUtilidad() {
        BigDecimal x = new BigDecimal( baseParaPrecio.getCosto() );
        BigDecimal y = new BigDecimal( baseParaPrecio.getPorcentajeUtilidad() );
        BigDecimal a = new BigDecimal( 100 );

        return x.multiply( y.divide(a) );
    }

    /**
     * Calcula el precioBase
     * precioBase = costo + utilidad
     * @return
     */
    @Override
    public BigDecimal getPrecioBase() {
        BigDecimal x = new BigDecimal( baseParaPrecio.getCosto() );
        BigDecimal y = getUtilidad();

        return x.add(y);
    }

    /**
     * Obtiene el descuento
     * descuento = precioBase * ( porcentajeDescuentoTotal / 100 )
     * @return
     */
    @Override
    public BigDecimal getDescuento() {

        BigDecimal cien = new BigDecimal( 100 );

        BigDecimal x = getPrecioBase();
        BigDecimal y = getPorcentajeDescuentoTotal().divide( cien );


        return  x.multiply( y );

    }

    /** Obtiene la suma de los impuestos
     *  Para cada impuesto:
     *      impuestos += impuesto
     *  Retornar impuestos
     */
    @Override
    public BigDecimal getImpuestos() {
        BigDecimal sumaImpuestos = new BigDecimal(0);
        Collection<Impuesto> listaImpuestos = getListaImpuestos();

        for(Impuesto imp : listaImpuestos) {
            sumaImpuestos = sumaImpuestos.add( imp.getImpuesto() );
        }
        return sumaImpuestos;
    }

    /** Obtiene los impuestos
     * Para cada impuesto hacer:
     *      impuesto = ( precioBase - descuento ) * ( porcentajeDeImpuesto / 100 )
     *      add impuesto to impuestos
     * Retornar impuestos
     *
     * @return Lista de impuestos
     */
    @Override
    public Collection<Impuesto> getListaImpuestos() {
        Collection<Impuesto> listaImpuestos = this.impuestos;

        for(Impuesto imp : this.impuestos) {
            BigDecimal cien = new BigDecimal( 100 );
            BigDecimal a    = getPrecioBase();
            BigDecimal b    = getDescuento();
            BigDecimal c    = a.subtract(b);
            BigDecimal x    = imp.getPorcentaje().divide( cien );

            imp.setImpuesto( x.multiply( c ) );
        }

        return listaImpuestos;
    }


    /**
     * Retorna el precio con utilidad, descuento e impuestos aplicados
     * precio = precioBase - descuento + impuestos
     * @return
     */
    @Override
    public BigDecimal getPrecio() {
        BigDecimal x = getPrecioBase();
        BigDecimal y = getDescuento();
        BigDecimal z = getImpuestos();

        return x.subtract(y).add(z);
    }

    public double getFactorUtilidad() {
        return baseParaPrecio.getPorcentajeUtilidad();
    }
}
