package omoikane.entities;

import omoikane.entities.LegacyVentaDetalle;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 21/01/14
 * Time: 13:08
 * To change this template use File | Settings | File Templates.
 */
@Embeddable
public class VentaDetalleImpuesto {
    @Column
    private BigDecimal base;
    @Column
    private String descripcion;
    @Column
    private BigDecimal porcentaje;
    @Column
    private BigDecimal total;
    @Column
    private Long impuestoId;

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
}
