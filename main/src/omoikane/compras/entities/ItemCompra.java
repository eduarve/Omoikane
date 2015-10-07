package omoikane.compras.entities;

import omoikane.producto.Articulo;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 23/11/13
 * Time: 20:09
 * To change this template use File | Settings | File Templates.
 */
@Embeddable
public class ItemCompra {

    private String codigo;
    private String nombre;
    private BigDecimal cantidad;
    private BigDecimal costoUnitario;

    private Articulo articulo;

    public ItemCompra() {
        this("", "", new BigDecimal("0.00"), new BigDecimal("0.00"));
    }

    public ItemCompra(String codigo, String nombre, BigDecimal cantidad, BigDecimal costoUnitario) {
        setCodigo   ( codigo   );
        setNombre   ( nombre   );
        setCantidad ( cantidad );
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
    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    @JoinColumn(name = "articulo_id_articulo", insertable = false, updatable = false)
    @NotNull
    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    @Column(name = "costo_unitario")
    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

}
