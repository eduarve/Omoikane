package omoikane.caja.business.domain;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.beans.property.adapter.JavaBeanObjectProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import omoikane.caja.presentation.ImpuestoModel;
import omoikane.entities.LegacyVentaDetalle;
import omoikane.producto.Producto;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

/**
 * Clase de modelado, con el único propósito de servir como muestra y experimento
 * No es una clase de producción
 */
public class PartidaModel {

    private LongProperty id;
    private StringProperty concepto;
    private StringProperty codigo;
    private ObjectProperty<BigDecimal> cantidad;
    private ObjectProperty<BigDecimal> costo;
    private ObjectProperty<BigDecimal> precio;
    private ObjectProperty<BigDecimal> precioBase;
    private ObservableList<ImpuestoModel> impuestos;
    private ObjectProperty<BigDecimal> descuentosBase;
    private ObjectProperty<BigDecimal> porcDescuentosBase;
    private StringProperty importeString;
    private ObjectProperty<Producto> productoData;
    private LegacyVentaDetalle ventaDetalleEntity;
    private ObjectBinding<BigDecimal> subtotalBinding;
    private ReadOnlyObjectWrapper<BigDecimal> subtotalProperty;

    public PartidaModel() {
        id         = new SimpleLongProperty();
        concepto   = new SimpleStringProperty();
        codigo     = new SimpleStringProperty();
        cantidad   = new SimpleObjectProperty<>(BigDecimal.ZERO);
        costo      = new SimpleObjectProperty<>();
        precio     = new SimpleObjectProperty<>(BigDecimal.ZERO);
        precioBase = new SimpleObjectProperty<>();
        impuestos  = FXCollections.emptyObservableList();
        descuentosBase     = new SimpleObjectProperty<>();
        porcDescuentosBase = new SimpleObjectProperty<>();
        importeString      = new SimpleStringProperty();
        productoData       = new SimpleObjectProperty<>();

        // ---- Lógica del enlace de subtotal ----
        // (cantidad * precio) --> subtotalBinding --> subtotalProperty
        subtotalBinding    = Bindings.createObjectBinding(
                () -> {
                    return cantidad.get().multiply(precio.get());
                }
                , cantidad
                , precio
        );
        subtotalProperty   = new ReadOnlyObjectWrapper<>();
        subtotalProperty.bind(subtotalBinding);
    }

    public ReadOnlyObjectProperty<BigDecimal> subtotalProperty() {
        return subtotalProperty.getReadOnlyProperty();
    }

    public LongProperty getId() {
        return id;
    }

    public StringProperty getConcepto() {
        return concepto;
    }

    public StringProperty getCodigo() {
        return codigo;
    }

    public ObjectProperty<BigDecimal> getCantidad() {
        return cantidad;
    }

    public ObjectProperty<BigDecimal> getCosto() {
        return costo;
    }

    public ObjectProperty<BigDecimal> getPrecio() {
        return precio;
    }

    public ObjectProperty<BigDecimal> getPrecioBase() {
        return precioBase;
    }

    public ObservableList<ImpuestoModel> getImpuestos() {
        return impuestos;
    }

    public ObjectProperty<BigDecimal> getDescuentosBase() {
        return descuentosBase;
    }

    public ObjectProperty<BigDecimal> getPorcDescuentosBase() {
        return porcDescuentosBase;
    }

    public StringProperty getImporteString() {
        return importeString;
    }

    public ObjectProperty<Producto> getProductoData() {
        return productoData;
    }

    public LegacyVentaDetalle getVentaDetalleEntity() {
        return ventaDetalleEntity;
    }
}
