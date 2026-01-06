package umu.tds.vista;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import umu.tds.App;

public class UIUtils {

    public static void mostrarCheck(String titulo, String cabecera, String contenido) {
        crearAlertaPersonalizada(Alert.AlertType.INFORMATION, titulo, cabecera, contenido, "imagenes/check.png");
    }

    public static void mostrarAyuda(String titulo, String cabecera, String contenido) {
        crearAlertaPersonalizada(Alert.AlertType.INFORMATION, titulo, cabecera, contenido, "imagenes/informacion.png");
    }

    public static void mostrarAlertaWarning(String titulo, String cabecera, String contenido) {
        crearAlertaPersonalizada(Alert.AlertType.WARNING, titulo, cabecera, contenido, "imagenes/warning.png");
    }

    public static void mostrarAlertaError(String titulo, String cabecera, String contenido) {
        crearAlertaPersonalizada(Alert.AlertType.ERROR, titulo, cabecera, contenido, "imagenes/error.png");
    }

    private static void crearAlertaPersonalizada(Alert.AlertType tipo, String t, String c, String cont, String rutaImg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(t);
        alert.setHeaderText(c);
        alert.setContentText(cont);

        try {
            Image image = new Image(App.class.getResourceAsStream(rutaImg));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            alert.setGraphic(imageView);
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + rutaImg);
        }
        alert.showAndWait();
    }
}