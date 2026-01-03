package umu.tds.vista;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import umu.tds.App;
import umu.tds.modelo.Gasto;

public class SceneManager {
    private Stage stage;
    private Scene scenaActual;

    public void inicializar(Stage stage) {
        this.stage = stage;
    }

    // --- MÉTODOS PÚBLICOS DE NAVEGACIÓN ---

    public void showGastos() {
        cargarYMostrar("GastosView");
    }

    public void showCategorias() {
        cargarYMostrar("CategoriasView");
    }
    
    public void showEstadisticas() {
        cargarYMostrar("EstadisticasView");
    }
    
    public void showCuentas() {
        cargarYMostrar("CuentasView");
    }
    
    public void showAlertas() {
        cargarYMostrar("AlertasView");
    }
    
    // Para "Nuevo Gasto" o "Nueva Categoría" si quieres que sean diálogos simples
    public void showNuevoGastoDialogo() {
        cargarYMostrarDialogo("NuevoGastoView", "Registrar Gasto");
    }
    
    public void showAcerca() {
        // Llamamos al método privado pasando el nombre del FXML de ayuda
        cargarYMostrarDialogo("DialogoAyuda", "Acerca de Expense Manager");
    }

    // --- MÉTODOS PRIVADOS DE INFRAESTRUCTURA (Iguales a los del profesor) ---

    private void cargarYMostrarDialogo(String fxml, String titulo) {
        try {
            DialogPane pane = (DialogPane) loadFXML(fxml);
            Dialog<Void> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle(titulo);
            dialog.initStyle(StageStyle.UTILITY);
            dialog.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el diálogo: " + fxml, e);
        }
    }

    private void cargarYMostrar(String fxml) {
        try {
            Parent root = loadFXML(fxml);
            if (scenaActual == null) {
                // Primera vez: creamos la escena y la vinculamos al stage
                scenaActual = new Scene(root);
                stage.setScene(scenaActual);
                stage.show();
            } else {
                // Resto de veces: solo cambiamos el contenido raíz
                scenaActual.setRoot(root);              
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar la vista: " + fxml, e);
        }
    }

    private Parent loadFXML(String fxml) throws IOException {
        // El profesor asume que están en el mismo paquete que la clase App
        return new FXMLLoader(App.class.getResource(fxml + ".fxml")).load();
    }
    
    //metodos epicas
 // Método para la HU 1.1
    public void showNuevoGasto() {
        cargarYMostrarDialogo("NuevoGastoView", "Registrar Nuevo Gasto");
    }

    // Método especial para la HU 1.3 (Editar)
    // Este es necesario porque hay que pasarle el objeto al controlador
    public void showEditarGasto(Gasto gasto) {
        try {
            FXMLLoader loader = new FXMLLoader(umu.tds.App.class.getResource("NuevoGastoView.fxml"));
            DialogPane pane = loader.load();

            // Aquí está la magia: obtenemos el controlador del FXML y le pasamos el gasto
            NuevoGastoController controller = loader.getController();
            controller.setGasto(gasto); 

            Dialog<Void> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle("Editar Gasto");
            dialog.initStyle(StageStyle.UTILITY);
            dialog.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    
}