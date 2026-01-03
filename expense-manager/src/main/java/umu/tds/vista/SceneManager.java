package umu.tds.vista;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {
    private static SceneManager instancia;
    private Stage stage;

    private SceneManager() {} // Constructor privado para Singleton 

    public static SceneManager getInstancia() {
        if (instancia == null) {
            instancia = new SceneManager();
        }
        return instancia;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void cambiarEscena(String fxmlPath, String titulo) {
        try {
            // Carga el recurso FXML desde la carpeta resources 
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}