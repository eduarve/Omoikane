package omoikane.caja.presentation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import omoikane.producto.Impuesto;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 16/01/14
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public class ImpuestoModel {
    private ObjectProperty<BigDecimal> impuestoBase;
    private ObjectProperty<BigDecimal> impuesto;
    private String descripcion;
    private BigDecimal porcentaje;
    private Impuesto impuestoEntity;

    public ImpuestoModel() {
        impuestoBase = new SimpleObjectProperty<>(new BigDecimal(0));
        impuesto     = new SimpleObjectProperty<>(new BigDecimal(0));
    }

    public void setImpuestoBase(BigDecimal val) {
        impuestoBase.set(val);
    }

    public void setImpuesto(BigDecimal val) {
        impuesto.set(val);
    }

    public ObjectProperty<BigDecimal> impuestoBaseProperty() {
        return impuestoBase;
    }

    public ObjectProperty<BigDecimal> impuestoProperty() {
        return impuesto;
    }

    public BigDecimal getImpuestoBase() {
        return impuestoBase.get();
    }

    public BigDecimal getImpuesto() {
        return impuesto.get();
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

    public void setImpuestoEntity(Impuesto impuestoEntity) {
        this.impuestoEntity = impuestoEntity;
    }

    public Impuesto getImpuestoEntity() {
        return impuestoEntity;
    }
}

