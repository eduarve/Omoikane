import org.flywaydb.core.Flyway
import omoikane.principal.Principal
import omoikane.sistema.Dialogos

try {
    Flyway flyway = new Flyway();
    flyway.setDataSource(Principal.URLMySQL, Principal.loginJasper, Principal.passJasper);
    flyway.repair();
    Dialogos.lanzarAlerta("Operación exitosa");
    
} catch(Exception e) {
    Dialogos.error(e.getMessage(), e);
}