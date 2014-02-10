package omoikane.entities;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 14/07/11
 * Time: 04:04
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "cortes")
public class Corte {

    @Column(name = "id_corte")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "id_caja")
    private int idCaja;

    @Column(name = "id_almacen")
    private int sucursalId;

    @Column
    @Min(0) private Double subtotal;

    @Column
    @Min(0) private Double impuestos;

    @Column
    @Min(0) private Double descuentos;

    @Column
    @Min(0) private Double total;

    @Column(name = "n_ventas")
    @Min(0) private int nVentas;

    @Column
    @Min(0) private Double depositos;

    @Column
    @Min(0) private Double retiros;

    @Column(name = "fecha_hora")
    private Timestamp fechaHora;

    @Column
    private Timestamp desde;

    @Column
    private Timestamp hasta;

    @Column
    @NotNull private boolean abierto;

    @Column(name = "folio_inicial")
    private Long folioInicial;

    @Column(name = "folio_final")
    private Long folioFinal;

    @ElementCollection()
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name="corte_impuesto",
            joinColumns=@JoinColumn(name="id_corte")
    )
    private List<CorteImpuesto> corteImpuestoList;

    @PrePersist
    protected void onCreate() {
        fechaHora = new Timestamp( Calendar.getInstance().getTime().getTime() );
    }

    public int getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(int idCaja) {
        this.idCaja = idCaja;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(Double impuestos) {
        this.impuestos = impuestos;
    }

    public Double getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(Double descuentos) {
        this.descuentos = descuentos;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public int getnVentas() {
        return nVentas;
    }

    public void setnVentas(int nVentas) {
        this.nVentas = nVentas;
    }

    public Timestamp getDesde() {
        return desde;
    }

    public void setDesde(Timestamp desde) {
        this.desde = desde;
    }

    public Timestamp getHasta() {
        return hasta;
    }

    public void setHasta(Timestamp hasta) {
        this.hasta = hasta;
    }

    public Double getDepositos() {
        return depositos;
    }

    public void setDepositos(Double depositos) {
        this.depositos = depositos;
    }

    public Double getRetiros() {
        return retiros;
    }

    public void setRetiros(Double retiros) {
        this.retiros = retiros;
    }

    public boolean isAbierto() {
        return abierto;
    }

    public void setAbierto(boolean abierto) {
        this.abierto = abierto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Corte corte = (Corte) o;

        if (abierto != corte.abierto) return false;
        if (corte.depositos.compareTo(depositos) != 0) return false;
        if (corte.descuentos.compareTo(descuentos) != 0) return false;
        if (idCaja != corte.idCaja) return false;
        if (corte.impuestos.compareTo(impuestos) != 0) return false;
        if (nVentas != corte.nVentas) return false;
        if (corte.retiros.compareTo(retiros) != 0) return false;
        if (corte.subtotal.compareTo(subtotal) != 0) return false;
        if (corte.total.compareTo(total) != 0) return false;
        if (desde != null ? !desde.equals(corte.desde) : corte.desde != null) return false;
        if (hasta != null ? !hasta.equals(corte.hasta) : corte.hasta != null) return false;

        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(int sucursalId) {
        this.sucursalId = sucursalId;
    }

    public Long getFolioInicial() {
        return folioInicial;
    }

    public void setFolioInicial(Long folioInicial) {
        this.folioInicial = folioInicial;
    }

    public Long getFolioFinal() {
        return folioFinal;
    }

    public void setFolioFinal(Long folioFinal) {
        this.folioFinal = folioFinal;
    }

    public List<CorteImpuesto> getCorteImpuestoList() {
        return corteImpuestoList;
    }

    public void setCorteImpuestoList(List<CorteImpuesto> corteImpuestoList) {
        this.corteImpuestoList = corteImpuestoList;
    }

    public Timestamp getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Timestamp fechaHora) {
        this.fechaHora = fechaHora;
    }
    //El siguiente bloque queda comentado a la espera de implementar las entidades que se relacionan (CorteSucursal, MovimientoCorte, Venta)
    /*
    @ManyToOne
    @JoinColumn(name = "corte_sucursal_id", referencedColumnName = "id", insertable=false, updatable=false)
    private CorteSucursal corteSucursalByCorteSucursalId;

    CorteSucursal getCorteSucursalByCorteSucursalId() {
        return corteSucursalByCorteSucursalId;
    }

    public void setCorteSucursalByCorteSucursalId(CorteSucursal corteSucursalByCorteSucursalId) {
        this.corteSucursalByCorteSucursalId = corteSucursalByCorteSucursalId;
    }

    @OneToMany(mappedBy = "corte")
    private List<MovimientoCorte> movimientoCorte;

    public List<MovimientoCorte> getMovimientoCorte() {
        return movimientoCorte;
    }

    public void setMovimientoCorte(List<MovimientoCorte> movimientoCorte) {
        this.movimientoCorte = movimientoCorte;
    }

    @OneToMany(mappedBy = "corte")
    private Collection<Venta> ventas;

    public Collection<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(Collection<Venta> ventas) {
        this.ventas = ventas;
    }
    */
}
