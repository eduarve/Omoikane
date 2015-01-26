package omoikane.sistema.seguridad;

/**
 * Proyecto Omoikane: SmartPOS 2.0
 * User: octavioruizcastillo
 * Date: 08/11/14
 * Time: 9:17
 */
public class RespuestaLogin {

    private Float nivelDeAcceso;  //También llamado perfil
    private Long id;
    private String nombre;
    private boolean logged;

    public RespuestaLogin(Long id, String nombre, Float nivelDeAcceso) {
        this.id = id;
        this.nombre = nombre;
        this.nivelDeAcceso = nivelDeAcceso;
    }

    public Boolean cerrojo(float llave) {
        return llave<= getNivelDeAcceso();
    }

    public Float getNivelDeAcceso() {
        return nivelDeAcceso;
    }

    protected void setNivelDeAcceso(Float nivelDeAcceso) {
        this.nivelDeAcceso = nivelDeAcceso;
    }

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

    public boolean isLogged() {
        return nivelDeAcceso >= 0;
    }
}
