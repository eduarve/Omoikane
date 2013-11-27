package omoikane.compras.MVC;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.adapter.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import omoikane.compras.entities.Compra;
import omoikane.compras.entities.ItemCompra;
import omoikane.entities.Usuario;
import omoikane.proveedores.Proveedor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 05/04/13
 * Time: 22:15
 * To change this template use File | Settings | File Templates.
 */
public class CompraEntityWrapper {
    private JavaBeanObjectProperty<Date> fecha;
    private JavaBeanObjectProperty<Usuario> usuario;
    private ObservableList<ItemCompraEntityWrapper> items;
    private JavaBeanBooleanProperty completado;
    private JavaBeanLongProperty id;
    private JavaBeanStringProperty folioOrigen;
    private JavaBeanObjectProperty<Proveedor> proveedor;
    private SimpleStringProperty proveedorNombre;
    private SimpleDoubleProperty subtotal;

    public Compra _compra;
    static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CompraEntityWrapper.class);

    public CompraEntityWrapper(Compra compra) {
        _compra = compra;
        try {
            JavaBeanObjectPropertyBuilder<Date> builder = new JavaBeanObjectPropertyBuilder<>();
            builder.bean(compra);
            builder.name("fecha");
            fecha = builder.build();

            JavaBeanObjectPropertyBuilder<Usuario> builder2 = new JavaBeanObjectPropertyBuilder<>();
            builder2.bean(compra);
            builder2.name("usuario");
            usuario = builder2.build();

            JavaBeanBooleanPropertyBuilder builder3 = new JavaBeanBooleanPropertyBuilder();
            builder3.bean(compra);
            builder3.name("completado");
            completado = builder3.build();

            JavaBeanLongPropertyBuilder builder4 = new JavaBeanLongPropertyBuilder();
            builder4.bean(compra);
            builder4.name("id");
            id = builder4.build();

            JavaBeanStringPropertyBuilder builder5 = new JavaBeanStringPropertyBuilder();
            builder5.bean(compra);
            builder5.name("folioOrigen");
            folioOrigen = builder5.build();

            JavaBeanObjectPropertyBuilder<Proveedor> builder6 = new JavaBeanObjectPropertyBuilder<>();
            builder6.bean(compra);
            builder6.name("proveedor");
            proveedor = builder6.build();

            subtotal = new SimpleDoubleProperty();
            proveedorNombre = new SimpleStringProperty();
            proveedor.addListener(new ChangeListener<Proveedor>() {
                @Override
                public void changed(ObservableValue<? extends Proveedor> observableValue, Proveedor proveedor, Proveedor proveedor2) {
                    proveedorNombre.set(proveedor2.getNombre());
                }
            });
            if(proveedor != null && proveedor.get() != null) proveedorNombre.set( proveedor.get().getNombre() );

            items = getUpdatedItems();

        } catch (NoSuchMethodException e) {
            logger.error("Invalid method to wrap", e);
        }
    }

    private ObservableList<ItemCompraEntityWrapper> getUpdatedItems() {
        if(items == null) items = FXCollections.observableArrayList();
        items.clear();

        items.addListener(new ListChangeListener<ItemCompraEntityWrapper>() {
            @Override
            public void onChanged(Change<? extends ItemCompraEntityWrapper> change) {
                BigDecimal subtotal = new BigDecimal(0);
                for(ItemCompraEntityWrapper item : items) {
                    subtotal = subtotal.add( item.importeProperty().get() );
                }
                subtotalProperty().set( subtotal.doubleValue() );
            }
        });

        for(ItemCompra item : _compra.getItems()) {
            items.add(new ItemCompraEntityWrapper(item));
        }
        return items;
    }

    public ObservableList<ItemCompraEntityWrapper> getItems() {
        return items;
    }

    public JavaBeanObjectProperty<Date> getDate() {
        return fecha;
    }

    public JavaBeanLongProperty getId() {
        return id;
    }

    public JavaBeanBooleanProperty getCompletado() {
        return completado;
    }

    public void setCompletado(Boolean completado1) {
        getCompletado().set(completado1);
    }

    public JavaBeanObjectProperty<Usuario> getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario u) {
        getUsuario().set(u);
    }

    public JavaBeanObjectProperty<Proveedor> getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor p) {
        getProveedor().set(p);
    }

    public JavaBeanStringProperty getFolioOrigen() {
        return folioOrigen;
    }

    public void setFolioOrigen(String folioOrigen) {
        getFolioOrigen().set(folioOrigen);
    }

    public void addItem(ItemCompraEntityWrapper itemCompraEntityWrapper) {
        items.add(itemCompraEntityWrapper);
        _compra.getItems().add(itemCompraEntityWrapper.getBean());
    }

    public void deleteItem(int idx) {
        items.remove(idx);
        _compra.getItems().remove(idx);
    }

    public Compra getBean() {
        return _compra;
    }

    public SimpleDoubleProperty subtotalProperty() {
        return subtotal;
    }

    public SimpleStringProperty nombreProveedorProperty() {
        return proveedorNombre;
    }
}
