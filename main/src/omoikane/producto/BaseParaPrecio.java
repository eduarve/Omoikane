package omoikane.producto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Created with IntelliJ IDEA.
 * User: octavioruizcastillo
 * Date: 01/10/12
 * Time: 17:56
 * @author octavioruizcastillo
 * Mapea la vista base_para_precios, se utiliza para poner a disposición de la entidad articulo/producto la
 * información necesaria para calcular precio, utilidad y descuento total de un producto.
 */

@Entity
@Table(name = "base_para_precios")

public class BaseParaPrecio implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id_articulo", columnDefinition = "int(11)")
    private Long idArticulo;
    @Basic(optional = false)
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @Column(name = "costo")
    private double costo;
    @Basic(optional = false)
    @Column(name = "porcentajeDescuentoLinea")
    private double porcentajeDescuentoLinea;
    @Basic(optional = false)
    @Column(name = "porcentajeDescuentoGrupo")
    private double porcentajeDescuentoGrupo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "porcentajeDescuentoProducto")
    private Double porcentajeDescuentoProducto;
    @Basic(optional = false)
    @Column(name = "porcentajeUtilidad")
    private double porcentajeUtilidad;

    // Ésta columna contiene una cadena con el formato (ID Lista de precio):(factor de utilidad)[, ...]
    @Column
    private String preciosAlternos;

    /**
     * Analiza la cadena "preciosAlternos" para generar un mapa.
     * @return Map<"ID Lista de Precios":Integer, "Factor de utilidad":BigDecimal>
     */
    public HashMap<Integer, BigDecimal> getPreciosAlternos() {
        HashMap mapa = new HashMap<Integer, BigDecimal>();
        if(preciosAlternos != null && !preciosAlternos.isEmpty()) {
            for ( String precioAlterno : preciosAlternos.split(",") ) {
                String[] kv = precioAlterno.split(":");
                mapa.put(
                        Integer.valueOf( kv[0] ),
                        new BigDecimal ( kv[1] )
                );
            }
        }
        return mapa;
    }

    public BaseParaPrecio() {
    }

    public Long getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Long idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public double getPorcentajeDescuentoLinea() {
        return porcentajeDescuentoLinea;
    }

    public void setPorcentajeDescuentoLinea(double porcentajeDescuentoLinea) {
        this.porcentajeDescuentoLinea = porcentajeDescuentoLinea;
    }

    public double getPorcentajeDescuentoGrupo() {
        return porcentajeDescuentoGrupo;
    }

    public void setPorcentajeDescuentoGrupo(double porcentajeDescuentoGrupo) {
        this.porcentajeDescuentoGrupo = porcentajeDescuentoGrupo;
    }

    public Double getPorcentajeDescuentoProducto() {
        return porcentajeDescuentoProducto;
    }

    public void setPorcentajeDescuentoProducto(Double porcentajeDescuentoProducto) {
        this.porcentajeDescuentoProducto = porcentajeDescuentoProducto;
    }

    public double getPorcentajeUtilidad() {
        return porcentajeUtilidad;
    }

    public void setPorcentajeUtilidad(double porcentajeUtilidad) {
        this.porcentajeUtilidad = porcentajeUtilidad;
    }


}

