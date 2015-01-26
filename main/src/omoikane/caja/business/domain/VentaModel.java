package omoikane.caja.business.domain;

import com.sun.javafx.binding.BindingHelperObserver;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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

    private ObjectBinding<BigDecimal> subtotal;
    private ObjectProperty<BigDecimal> montoDescuentos;
    private ObjectProperty<BigDecimal> montoImpuestos;
    private ObjectProperty<BigDecimal> total;

    private SimpleObjectProperty<Cliente> cliente;

    private ObservableList<PartidaModel> partidas;

    public VentaModel() {
        subtotal        = new ObjectBinding<BigDecimal>() {
            @Override
            protected BigDecimal computeValue() {
                BigDecimal result = BigDecimal.ZERO;
                for(PartidaModel pm : partidas)
                    result.add(pm.subtotalProperty().get());

                return result;
            }
        };

        montoDescuentos = new SimpleObjectProperty<>(BigDecimal.ZERO);
        montoImpuestos  = new SimpleObjectProperty<>(BigDecimal.ZERO);
        total           = new SimpleObjectProperty<>(BigDecimal.ZERO);

        cliente         = new SimpleObjectProperty<>();

        partidas        = FXCollections.observableArrayList();
        partidas.addListener(new MyListListener());
    }

    public Long getFolio() {
        return folio;
    }

    public void setFolio(Long folio) {
        this.folio = folio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCajaId() {
        return cajaId;
    }

    public void setCajaId(Integer cajaId) {
        this.cajaId = cajaId;
    }

    public Integer getCajeroId() {
        return cajeroId;
    }

    public void setCajeroId(Integer cajeroId) {
        this.cajeroId = cajeroId;
    }

    public ObjectBinding<? extends BigDecimal> getSubtotal() {
        return subtotal;
    }

    public ObjectProperty<BigDecimal> getMontoDescuentos() {
        return montoDescuentos;
    }

    public ObjectProperty<BigDecimal> getMontoImpuestos() {
        return montoImpuestos;
    }

    public ObjectProperty<BigDecimal> getTotal() {
        return total;
    }

    public SimpleObjectProperty<Cliente> getCliente() {
        return cliente;
    }

    public ObservableList<PartidaModel> getPartidas() {
        return partidas;
    }

    class MyListListener implements ListChangeListener<PartidaModel> {

        @Override
        public void onChanged(Change<? extends PartidaModel> change) {
            if(change.wasAdded()) {
                            for(PartidaModel pm : change.getList()) {
                                //A cada partida añade un listener, ese listener es el subtotal general
                                pm.subtotalProperty().addListener(new BindingHelperObserver(subtotal));
                            }
                        }
        }
    }
}
