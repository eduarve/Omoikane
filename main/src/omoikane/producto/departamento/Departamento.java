package omoikane.producto.departamento;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 17/07/14
 * Time: 19:26
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column
    private Long id;

    @Column
    private
    String nombre;

    @Column
    private
    String notas;

    @NotNull
    @Column
    private
    Boolean activo = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String toString() {
        String stringId = getId() == null ? "0" : getId().toString();
        return "("+stringId+") "+getNombre();
    }
}
