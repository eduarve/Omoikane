package omoikane.compras.MVC;

import javafx.beans.binding.NumberBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanObjectProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import omoikane.compras.entities.ItemCompra;
import omoikane.producto.Articulo;

import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 05/04/13
 * Time: 03:29
 * To change this template use File | Settings | File Templates.
 */
public class ItemCompraEntityWrapper {

    private JavaBeanStringProperty codigo;
    private JavaBeanStringProperty nombre;
    private JavaBeanObjectProperty<BigDecimal> cantidad;
    private JavaBeanObjectProperty<Articulo> articulo;;
    private JavaBeanObjectProperty<BigDecimal> costoUnitario;
    private SimpleObjectProperty<BigDecimal> importe;

    private ItemCompra _itemCompra;
    static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ItemCompraEntityWrapper.class);

    public ItemCompraEntityWrapper(ItemCompra itemCompra) {
        _itemCompra = itemCompra;

        try {
            JavaBeanStringPropertyBuilder builder = JavaBeanStringPropertyBuilder.create();
            builder.bean(itemCompra);
            builder.name("codigo");
            codigo = builder.build();

            builder = JavaBeanStringPropertyBuilder.create();
            builder.bean(itemCompra);
            builder.name("nombre");
            nombre = builder.build();

            JavaBeanObjectPropertyBuilder<BigDecimal> builder1 = JavaBeanObjectPropertyBuilder.create();
            builder1.bean(itemCompra);
            builder1.name("cantidad");
            cantidad = builder1.build();

            builder1 = JavaBeanObjectPropertyBuilder.create();
            builder1.bean(itemCompra);
            builder1.name("costoUnitario");
            costoUnitario = builder1.build();

            importe = new SimpleObjectProperty<BigDecimal>();
            importe.set( new BigDecimal(0) );

            JavaBeanObjectPropertyBuilder<Articulo> builder2 = JavaBeanObjectPropertyBuilder.create();
            builder2.bean(itemCompra);
            builder2.name("articulo");
            articulo = builder2.build();

        } catch (NoSuchMethodException e) {
            logger.error("Invalid method to wrap", e);
        }
    }

    public ItemCompra getBean() {
        return _itemCompra;
    }

    public StringProperty codigoProperty() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        codigoProperty().set(codigo);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public void setNombre(String nombre) {
        nombreProperty().set(nombre);
    }

    public ObjectProperty<BigDecimal> cantidadProperty() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        cantidadProperty().set(cantidad);
    }

    public ObjectProperty<Articulo> articuloProperty() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        articuloProperty().set(articulo);
    }

    public ObjectProperty<BigDecimal> costoUnitarioProperty() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costoUnitario) {
        costoUnitarioProperty().set(costoUnitario);
    }

    public ObjectProperty<BigDecimal> importeProperty() {
        BigDecimal costoUnitario = costoUnitarioProperty().get();
        BigDecimal cantidad = cantidadProperty().get();

        importe.set(costoUnitario.multiply( cantidad ));

        return importe;
    }


}
