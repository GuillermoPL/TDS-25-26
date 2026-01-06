package umu.tds.vista;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import umu.tds.App;

public class UIUtils {

    // Icono de Éxito (Tick verde)
    public static void mostrarCheck(String titulo, String cabecera, String contenido) {
        crearAlertaPersonalizada(Alert.AlertType.INFORMATION, titulo, cabecera, contenido, "imagenes/check.png");
    }

    // Icono de Ayuda (Círculo azul)
    public static void mostrarAyuda(String titulo, String cabecera, String contenido) {
        crearAlertaPersonalizada(Alert.AlertType.INFORMATION, titulo, cabecera, contenido, "imagenes/informacion.png");
    }

    // Icono de Advertencia (Triángulo amarillo)
    public static void mostrarAlertaWarning(String titulo, String cabecera, String contenido) {
        crearAlertaPersonalizada(Alert.AlertType.WARNING, titulo, cabecera, contenido, "imagenes/warning.png");
    }

    // Icono de Error (Cruz roja)
    public static void mostrarAlertaError(String titulo, String cabecera, String contenido) {
        crearAlertaPersonalizada(Alert.AlertType.ERROR, titulo, cabecera, contenido, "imagenes/error.png");
    }

    public static boolean mostrarInterrogacion(String titulo, String cabecera, String contenido) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setContentText(contenido);

        // Configuramos botones de SÍ y NO explícitos para mayor claridad
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        try {
            // Usamos App.class para asegurar que la ruta es relativa al paquete raíz
            Image image = new Image(App.class.getResourceAsStream("imagenes/interrogacion.png"));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            alert.setGraphic(imageView);
        } catch (Exception e) {
            System.err.println("Error cargando: imagenes/interrogacion.png");
        }

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
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