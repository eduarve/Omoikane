package omoikane.inventarios.tomaInventario;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import omoikane.producto.Articulo;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Octavio
 * Date: 27/02/13
 * Time: 05:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Embeddable
public class ItemConteoInventario {

    private String codigo;
    private String nombre;
    private BigDecimal conteo;
    private BigDecimal stockDB;
    private BigDecimal diferencia;
    private BigDecimal costoUnitario;

    private Articulo articulo;

    public ItemConteoInventario() {
        this("", "", new BigDecimal("0.00"), new BigDecimal("0.00"));
    }

    public ItemConteoInventario(String codigo, String nombre, BigDecimal conteo, BigDecimal costoUnitario) {
            setCodigo( codigo );
            setNombre( nombre );
            setConteo( conteo );
            setCostoUnitario( costoUnitario );
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
    public BigDecimal getConteo() {
        return conteo;
    }

    public void setConteo(BigDecimal conteo) {
        this.conteo = conteo;
    }

    @Column
    public BigDecimal getStockDB() {
        return stockDB;
    }

    public void setStockDB(BigDecimal stockDB) {
        this.stockDB = stockDB;
    }

    @Column
    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(BigDecimal diferencia) {
        this.diferencia = diferencia;
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
        this.costoUnitario = costoUnitario;
    }
}
