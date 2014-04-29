package omoikane.producto;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 13/04/14
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */

@Entity
@IdClass(PrecioAlternoPK.class)
public class PrecioAlterno {

    @ManyToOne(fetch = FetchType.LAZY)
    @Id
    Articulo articulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @Id
    ListaDePrecios listaDePrecios;

    @Column
    BigDecimal utilidad;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrecioAlterno that = (PrecioAlterno) o;

        if (!articulo.equals(that.articulo)) return false;
        if (!listaDePrecios.equals(that.listaDePrecios)) return false;
        if (!utilidad.equals(that.utilidad)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = articulo.hashCode();
        result = 31 * result + listaDePrecios.hashCode();
        result = 31 * result + utilidad.hashCode();
        return result;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    public ListaDePrecios getListaDePrecios() {
        return listaDePrecios;
    }

    public void setListaDePrecios(ListaDePrecios listaDePrecios) {
        this.listaDePrecios = listaDePrecios;
    }

    public BigDecimal getUtilidad() {
        return utilidad;
    }

    public void setUtilidad(BigDecimal utilidad) {
        this.utilidad = utilidad;
    }
}
