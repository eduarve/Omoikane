package omoikane.compras.entities;

import omoikane.entities.Usuario;
import omoikane.inventarios.tomaInventario.ItemConteoInventario;
import omoikane.proveedores.Proveedor;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 23/11/13
 * Time: 20:08
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Compra {
    private Long id;

    private Date fecha;
    private Usuario usuario;

    private List<ItemCompra> items;

    private String folioOrigen;

    private Proveedor proveedor;

    @PrePersist
    public void prePersist() {
        setFecha(new Date());
        setCompletado(false);
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    public List<ItemCompra> getItems() { return items; }

    public void setItems(List<ItemCompra> items) { this.items = items; }

    private Boolean completado;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Compra() {
        items = new ArrayList<>();
    }

    @Column
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @ManyToOne
    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    @Column
    @Index(name = "completadoIndex")
    public Boolean getCompletado() {
        return completado;
    }

    public void setCompletado(Boolean completado) {
        this.completado = completado;
    }

    @Column
    public String getFolioOrigen() {
        return folioOrigen;
    }

    public void setFolioOrigen(String folioOrigen) {
        this.folioOrigen = folioOrigen;
    }

    public String toString() {
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        return  dateFormat.format(getFecha());
    }
}
