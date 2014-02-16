package omoikane.ha;

import omoikane.formularios.CatalogoArticulos;
import omoikane.principal.Articulos;
import omoikane.principal.Escritorio;
import omoikane.sistema.Dialogos;
import omoikane.sistema.Herramientas;

/**
 *
 * Al encontrarse con una desconexión de la BD o por llamarlo así, una caída del sistema, esta clase se encarga de manejar
 * la situación.
 *
 * Por el momento únicamente abre un catálogo de artículos.
 *
 * User: octavioruizcastillo
 * Date: 15/02/14
 * Time: 13:45
 */
public class DisconnectionHandler {

    public CatalogoArticulos handle() {
        CatalogoArticulos cat = (CatalogoArticulos) Articulos.lanzarCatalogo();

        return cat;
    }
}
