package omoikane.clientes;

import omoikane.entities.LegacyVenta;
import omoikane.producto.ListaDePrecios;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 14/07/11
 * Time: 04:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Cliente {

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private int id = 0;

    @Column(length = 255)
    @NotEmpty
    private String nombre = "";

    @Column(name = "saldo")
    @Basic
    @NotNull
    private BigDecimal saldo = new BigDecimal("0.00");

    @Column(name = "RFC")
    @Basic
    @NotEmpty
    private String rfc;

    @Column(name = "actualizacion")
    @Basic
    @NotNull
    private Timestamp actualizacion;

    @Column(name = "creacion")
    @Basic
    @NotNull
    private Timestamp creacion;

    @Column(name = "listaDePrecios_id")
    private Integer listaDePreciosId = 0;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(insertable = false, updatable = false)
    @NotFound(action= NotFoundAction.IGNORE)
    private ListaDePrecios listaDePrecios;

    public Integer getListaDePreciosId() {
        return listaDePreciosId;
    }

    public void setListaDePreciosId(Integer listaDePreciosId) {
        this.listaDePreciosId = listaDePreciosId;
    }

    public ListaDePrecios getListaDePrecios() {
        return listaDePrecios;
    }

    public void setListaDePrecios(ListaDePrecios listaDePrecios) {
        this.listaDePrecios = listaDePrecios;
    }

    @PrePersist
    protected void onCreate() {
        creacion = new Timestamp(Calendar.getInstance().getTime().getTime());
        actualizacion = new Timestamp(Calendar.getInstance().getTime().getTime());
    }

    @PreUpdate
    protected void onUpdate() {
        actualizacion = new Timestamp(Calendar.getInstance().getTime().getTime());
    }

    public int getId() {
        return id;
    }

    public void setId(int clienteId) {
        this.id = clienteId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Timestamp getActualizacion() {
        return actualizacion;
    }

    public void setActualizacion(Timestamp umodificacion) {
        this.actualizacion = umodificacion;
    }

    public Timestamp getCreacion() {
        return creacion;
    }

    public void setCreacion(Timestamp creacion) {
        this.creacion = creacion;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cliente cliente = (Cliente) o;

        if (id != cliente.id) return false;
        if (cliente.saldo.compareTo(saldo) != 0) return false;
        if (nombre.equals(cliente.getNombre())) return false;
        if (actualizacion != null ? !actualizacion.equals(cliente.actualizacion) : cliente.actualizacion != null)
            return false;
        if (creacion != null ? !creacion.equals(cliente.creacion) : cliente.creacion != null)
                    return false;

        return true;
    }

    @OneToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Collection<LegacyVenta> ventas;

    public Collection<LegacyVenta> getVentas() {
        return ventas;
    }

    public void setVentas(Collection<LegacyVenta> ventas) {
        this.ventas = ventas;
    }
}
