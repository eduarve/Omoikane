package omoikane.caja.business.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 21/12/14
 * Time: 2:36
 */
public class VentaIncompleta {
    Long idCliente;

    List<Partida> partidas;

    public VentaIncompleta() {
        partidas = new ArrayList<>();
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public List<Partida> getPartidas() {
        return partidas;
    }

    public void setPartidas(List<Partida> partidas) {
        this.partidas = partidas;
    }

    public static class Partida {

        BigDecimal cantidad;
        String codigo;

        public Partida() {}

        public Partida(BigDecimal cantidad, String codigo) {
            this.cantidad = cantidad;
            this.codigo = codigo;
        }

        public BigDecimal getCantidad() {
            return cantidad;
        }

        public void setCantidad(BigDecimal cantidad) {
            this.cantidad = cantidad;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }
    }
}
