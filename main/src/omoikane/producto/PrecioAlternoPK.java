package omoikane.producto;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 13/04/14
 * Time: 23:35
 * To change this template use File | Settings | File Templates.
 */
public class PrecioAlternoPK implements Serializable {


    private Articulo articulo;

    private ListaDePrecios listaDePrecios;

    public PrecioAlternoPK() {

    }

    public PrecioAlternoPK(Articulo articulo, ListaDePrecios listaDePrecios) {
        this.articulo = articulo;
        this.listaDePrecios = listaDePrecios;
    }
}
