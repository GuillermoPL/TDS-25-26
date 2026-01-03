package umu.tds.vista;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import umu.tds.App;

public class SceneManager {
    private Stage stage;
    private Scene scenaActual;

    public void inicializar(Stage stage) {
        this.stage = stage;
    }

    
    public void showGastos() {
        cargarYMostrar("/umu/tds/GastosView");
    }

    public void showCategorias() {
        cargarYMostrar("/umu/tds/CategoriasView");
    }

    private void cargarYMostrar(String fxml) {
        try {
            Parent root = loadFXML(fxml);
            if (scenaActual == null) {
                // La primera vez se crea la escena
                scenaActual = new Scene(root);
                stage.setScene(scenaActual);
                stage.show();
            } else {
                // Las siguientes veces SOLO se cambia el contenido de la escena
                scenaActual.setRoot(root); 
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
}