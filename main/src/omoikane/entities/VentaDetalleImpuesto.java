package omoikane.entities;

import omoikane.entities.LegacyVentaDetalle;
import omoikane.producto.VentaDetalleImpustoId;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 21/01/14
 * Time: 13:08
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "ventas_detalles_impuestos")
@IdClass(VentaDetalleImpustoId.class)
public class VentaDetalleImpuesto {

    @Column
    private BigDecimal base;
    @Column
    private String descripcion;
    @Column
    private BigDecimal porcentaje;
    @Column
    private BigDecimal total;
    @Id
    private Long impuestoId;
    /*@Id
    @Column(name = "id_renglon")
    private Long renglonId;*/

    @Id
    @ManyToOne
    @JoinColumn(name = "id_renglon")
    private LegacyVentaDetalle legacyVentaDetalle;

    public void setBase(BigDecimal base) {
        this.base = base;
    }

    public BigDecimal getBase() {
        return base;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Long getImpuestoId() {
        return impuestoId;
    }

    public void setImpuestoId(Long impuestoId) {
        this.impuestoId = impuestoId;
    }
    /*
    public Long getRenglonId() {
        return renglonId;
    }

    public void setRenglonId(Long renglonId) {
        this.renglonId = renglonId;
    }*/

    public LegacyVentaDetalle getLegacyVentaDetalle() {
        return legacyVentaDetalle;
    }

    public void setLegacyVentaDetalle(LegacyVentaDetalle legacyVentaDetalle) {
        this.legacyVentaDetalle = legacyVentaDetalle;
    }
}
