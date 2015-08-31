package omoikane.sistema;

import javafx.scene.control.Alert;

/**
 * Proyecto Omoikane
 * User: octavioruizcastillo
 * Date: 22/08/15
 * Time: 14:29
 */
public class DialogosFX {

    public static void lanzarAlertaFX(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alerta");
        alert.setHeaderText("Información");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
