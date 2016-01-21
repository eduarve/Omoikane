package omoikane.compras;

import omoikane.producto.Articulo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores the import of the importing process of a XML invoice in CFDI's standard
 * Created by octavioruizcastillo on 15/01/16.
 */
public class ImportXML {
    public int encontrados = 0;
    public int noEncontrados = 0;
    public int erroneos = 0;

    public StringBuilder summary;

    public List<ItemImportXML> items;

    public class ItemImportXML {
        public BigDecimal cantidad;
        public BigDecimal valorUnitario;
        public Articulo articulo;
        public String codigo;
    }

    public ImportXML() {
        items = new ArrayList<>();
        summary = new StringBuilder();
    }
}
