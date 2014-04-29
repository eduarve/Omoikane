package omoikane.clientes;

import com.dooapp.fxform.annotation.NonVisual;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.*;
import omoikane.producto.ListaDePrecios;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 18/04/14
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
public class ClienteBeanWrapper {
    @NonVisual private JavaBeanIntegerProperty id;
    private JavaBeanStringProperty nombre;
    private JavaBeanStringProperty rfc;
    private JavaBeanIntegerProperty listaDePreciosId;
    private JavaBeanObjectProperty<ListaDePrecios> listaDePrecios;

    @NonVisual
    private Cliente _cliente;

    @NonVisual
    static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ClienteBeanWrapper.class);

    public ClienteBeanWrapper(Cliente cliente) {
        _cliente = cliente;

        try {
            JavaBeanStringPropertyBuilder builder = JavaBeanStringPropertyBuilder.create();
            builder.bean(cliente);
            builder.name("nombre");
            nombre = builder.build();

            JavaBeanStringPropertyBuilder b2 = JavaBeanStringPropertyBuilder.create();
            b2.bean(cliente);
            b2.name("Rfc");
            rfc = b2.build();

            JavaBeanIntegerPropertyBuilder b3 = JavaBeanIntegerPropertyBuilder.create();
            b3.bean(cliente);
            b3.name("id");
            id = b3.build();

            JavaBeanIntegerPropertyBuilder b4 = JavaBeanIntegerPropertyBuilder.create();
            b4.bean(cliente);
            b4.name("listaDePreciosId");
            listaDePreciosId = b4.build();
            try { listaDePreciosId.get(); } catch (NullPointerException n) { listaDePreciosId.set(0); }

            JavaBeanObjectPropertyBuilder b5 = JavaBeanObjectPropertyBuilder.create();
            b5.bean(cliente);
            b5.name("listaDePrecios");
            listaDePrecios = b5.build();

        } catch (NoSuchMethodException e) {
            logger.error("Invalid method to wrap", e);
        }
    }

    public Cliente getBean() {
        return _cliente;
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(Integer id) {
        this.id.set(id);
    }

    @NotEmpty
    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    @NotEmpty
    public String getRfc() {
        return rfc.get();
    }

    public StringProperty rfcProperty() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc.set(rfc);
    }

    public IntegerProperty listaDePreciosIdProperty() {
        try { listaDePreciosId.get(); } catch (NullPointerException n) { listaDePreciosId.set(0); }

        return listaDePreciosId;
    }

    public void setListaDePreciosId(Integer id) {
        listaDePreciosId.set(id);
    }

    public ObjectProperty<ListaDePrecios> listaDePreciosProperty() {
        return listaDePrecios;
    }

    public void setListaDePrecios(ListaDePrecios ldp) {
        listaDePrecios.set(ldp);
    }
}
