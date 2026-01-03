package umu.tds;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        // 1. Inicialización según el profesor
        Configuracion configuracion = new ConfiguracionImpl();
        Configuracion.setInstancia(configuracion);
        
        // 2. Inicializar el SceneManager con el stage principal
        configuracion.getSceneManager().inicializar(stage);
        
        // 3. Lanzar la ventana principal
        configuracion.getSceneManager().showGastos(); // O la que prefieras como inicio
    }

    public static void main(String[] args) {
        launch(args);
    }
}