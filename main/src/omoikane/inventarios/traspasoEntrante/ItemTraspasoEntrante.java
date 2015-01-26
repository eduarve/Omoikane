package omoikane.inventarios.traspasoEntrante;

import com.fasterxml.jackson.annotation.JsonIgnore;
import omoikane.producto.Articulo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 27/02/13
 * Time: 05:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Embeddable
public class ItemTraspasoEntrante {

    private String codigo;
    private String nombre;
    private BigDecimal cantidad;
    private BigDecimal stockDB;
    private BigDecimal costoUnitario;
    private BigDecimal precioPublico;

    @JsonIgnore
    private Articulo articulo;

    public ItemTraspasoEntrante() {
        this("", "", new BigDecimal("0.00"), new BigDecimal("0.00"));
    }

    public ItemTraspasoEntrante(String codigo, String nombre, BigDecimal conteo, BigDecimal costoUnitario) {
        setCodigo( codigo );
        setNombre( nombre );
        setCantidad(conteo);
        setCostoUnitario( costoUnitario );
        stockDB = new BigDecimal("0.00");
        precioPublico = new BigDecimal("0.00");

        cantidad.setScale(2, RoundingMode.HALF_EVEN);
        stockDB.setScale(2, RoundingMode.HALF_EVEN);
        costoUnitario.setScale(2, RoundingMode.HALF_EVEN);
        precioPublico.setScale(2, RoundingMode.HALF_EVEN);
    }

    @Column
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Column
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Column
    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        cantidad.setScale(2, RoundingMode.HALF_EVEN);
        this.cantidad = cantidad;
    }

    @Column
    public BigDecimal getStockDB() {
        return stockDB;
    }

    public void setStockDB(BigDecimal stockDB) {
        stockDB.setScale(2, RoundingMode.HALF_EVEN);
        this.stockDB = stockDB;
    }

    @Column
    public BigDecimal getPrecioPublico() {
        return precioPublico;
    }

    public void setPrecioPublico(BigDecimal precioPublico) {
        precioPublico.setScale(2, RoundingMode.HALF_EVEN);
        this.precioPublico = precioPublico;
    }

    @ManyToOne
    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    @Column
    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costoUnitario) {
        costoUnitario.setScale(2, RoundingMode.HALF_EVEN);
        this.costoUnitario = costoUnitario;
    }

    @Transient
    @JsonIgnore
    public BigDecimal getImporte() {
        BigDecimal importe = getCantidad().multiply(getPrecioPublico(), MathContext.DECIMAL128);
        importe.setScale(2, RoundingMode.HALF_EVEN);

        return importe;
    }
}
