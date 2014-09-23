package omoikane.caja.business.domain;

import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import omoikane.caja.presentation.ImpuestoModel;
import omoikane.entities.LegacyVentaDetalle;
import omoikane.producto.Producto;

import java.math.BigDecimal;

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
    private ListProperty<ImpuestoModel> impuestos;
    private ObjectProperty<BigDecimal> descuentosBase;
    private ObjectProperty<BigDecimal> porcDescuentosBase;
    private StringProperty importeString;
    private Producto productoData;
    private LegacyVentaDetalle ventaDetalleEntity;
}
