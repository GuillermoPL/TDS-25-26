package umu.tds;

import javafx.application.Application;
import javafx.stage.Stage;
import umu.tds.vista.SceneManager;
import umu.tds.modelo.Usuario;
import umu.tds.controlador.ControladorSesion; 

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
    	Usuario userTest = new Usuario("PedroF");
    	ControladorSesion.getInstancia().setUsuarioActual(userTest);
        //1. Vinculamos el Stage principal con nuestro SceneManager (Singleton)
        SceneManager.getInstancia().setStage(primaryStage);

        //2. Cargamos la vista inicial usando el archivo FXML de resources
        SceneManager.getInstancia().cambiarEscena("/umu/tds/App.fxml", "Gestión de Gastos - TDS");
    }

    public static void main(String[] args) {
        launch(args);
    }
}