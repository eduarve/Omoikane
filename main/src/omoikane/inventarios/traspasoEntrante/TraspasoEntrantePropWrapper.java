package omoikane.inventarios.traspasoEntrante;

import javafx.beans.property.adapter.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import omoikane.entities.Usuario;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 05/04/13
 * Time: 22:15
 * To change this template use File | Settings | File Templates.
 */
public class TraspasoEntrantePropWrapper {
    private JavaBeanObjectProperty<Date> fecha;
    private JavaBeanObjectProperty<Usuario> usuario;
    private ObservableList<ItemTraspasoPropWrapper> items;
    private JavaBeanBooleanProperty completado;
    private JavaBeanBooleanProperty aplicado;
    private JavaBeanLongProperty id;
    private JavaBeanStringProperty uid;
    private JavaBeanStringProperty almacenOrigen;
    private JavaBeanStringProperty almacenDestino;
    private JavaBeanStringProperty notas;

    public TraspasoEntrante _traspasoEntrante;
    static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TraspasoEntrantePropWrapper.class);

    public TraspasoEntrantePropWrapper(TraspasoEntrante traspasoEntrante) {
        _traspasoEntrante = traspasoEntrante;
        try {
            JavaBeanObjectPropertyBuilder<Date> builder = new JavaBeanObjectPropertyBuilder<>();
            builder.bean(traspasoEntrante);
            builder.name("fecha");
            fecha = builder.build();

            JavaBeanObjectPropertyBuilder<Usuario> builder2 = new JavaBeanObjectPropertyBuilder<>();
            builder2.bean(traspasoEntrante);
            builder2.name("usuario");
            usuario = builder2.build();

            items = getUpdatedItems();

            JavaBeanBooleanPropertyBuilder builder3 = new JavaBeanBooleanPropertyBuilder();
            builder3.bean(traspasoEntrante);
            builder3.name("completado");
            completado = builder3.build();

            JavaBeanBooleanPropertyBuilder builder5 = new JavaBeanBooleanPropertyBuilder();
            builder5.bean(traspasoEntrante);
            builder5.name("aplicado");
            aplicado = builder5.build();

            JavaBeanLongPropertyBuilder builder4 = new JavaBeanLongPropertyBuilder();
            builder4.bean(traspasoEntrante);
            builder4.name("id");
            id = builder4.build();

            JavaBeanStringPropertyBuilder builder6 = new JavaBeanStringPropertyBuilder();
            builder6.bean(traspasoEntrante);
            builder6.name("almacenOrigen");
            almacenOrigen = builder6.build();

            JavaBeanStringPropertyBuilder builder7 = new JavaBeanStringPropertyBuilder();
            builder7.bean(traspasoEntrante);
            builder7.name("almacenDestino");
            almacenDestino = builder7.build();

            JavaBeanStringPropertyBuilder builder8 = new JavaBeanStringPropertyBuilder();
            builder8.bean(traspasoEntrante);
            builder8.name("uid");
            uid = builder8.build();

            JavaBeanStringPropertyBuilder builder9 = new JavaBeanStringPropertyBuilder();
            builder9.bean(traspasoEntrante);
            builder9.name("notas");
            notas = builder9.build();

        } catch (NoSuchMethodException e) {
            logger.error("Invalid method to wrap", e);
        }
    }

    private ObservableList<ItemTraspasoPropWrapper> getUpdatedItems() {
        if(items == null) items = FXCollections.observableArrayList();
        items.clear();
        for(ItemTraspasoEntrante item : _traspasoEntrante.getItems()) {
            items.add(new ItemTraspasoPropWrapper(item));
        }
        return items;
    }

    public ObservableList<ItemTraspasoPropWrapper> getItems() {
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

    public JavaBeanBooleanProperty getAplicado() {
        return aplicado;
    }

    public void setAplicado(Boolean aplicado) {
        getAplicado().set(aplicado);
    }

    public JavaBeanObjectProperty<Usuario> getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario u) {
        getUsuario().set(u);
    }

    public void addItem(ItemTraspasoPropWrapper itemTraspasoPropWrapper) {
        items.add(itemTraspasoPropWrapper);
        _traspasoEntrante.getItems().add(itemTraspasoPropWrapper.getBean());
    }

    public void deleteItem(int idx) {
        items.remove(idx);
        _traspasoEntrante.getItems().remove(idx);
    }

    public TraspasoEntrante getBean() {
        return _traspasoEntrante;
    }


    public JavaBeanStringProperty getAlmacenOrigen() {
        return almacenOrigen;
    }

    public void setAlmacenOrigen(JavaBeanStringProperty almacenOrigen) {
        this.almacenOrigen = almacenOrigen;
    }

    public JavaBeanStringProperty getAlmacenDestino() {
        return almacenDestino;
    }

    public void setAlmacenDestino(JavaBeanStringProperty almacenDestino) {
        this.almacenDestino = almacenDestino;
    }

    public JavaBeanStringProperty getUid() {
        return uid;
    }

    public void setUid(String uid) {
        getUid().set(uid);
    }

    public JavaBeanStringProperty getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas.set(notas);
    }
}
