package omoikane.producto;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 13/04/14
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class ListaDePrecios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column
    private Long id;

    @Column
    private String descripcion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
