package omoikane.caja.business.domain;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import omoikane.clientes.Cliente;

import java.math.BigDecimal;

/**
 * Clase de modelado, con el único propósito de servir como muestra y experimento
 * No es una clase de producción
 */
public class VentaModel {
    private Long folio;
    private Long id;
    private Integer cajaId;
    private Integer cajeroId;

    private ObjectProperty<BigDecimal> subtotal;
    private ObjectProperty<BigDecimal> montoDescuentos;
    private ObjectProperty<BigDecimal> montoImpuestos;
    private ObjectProperty<BigDecimal> total;

    private SimpleObjectProperty<Cliente> cliente;

    private ObservableList<PartidaModel> productos;
}
