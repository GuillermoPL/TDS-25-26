package umu.tds;

import javafx.application.Application;
import javafx.stage.Stage;
import umu.tds.controlador.ControladorSesion;
import umu.tds.modelo.Usuario;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        // 1. Inicialización de la configuración (Fachada/Service Locator)
        Configuracion configuracion = new ConfiguracionImpl();
        Configuracion.setInstancia(configuracion);
        
        // Creamos un usuario de prueba (usando el constructor que recibe el ID/Login)
        Usuario usuarioPrueba = new Usuario("pepe"); 
        
        // Lo asignamos como usuario de la sesión actual
        ControladorSesion.getInstancia().setUsuarioActual(usuarioPrueba);
        // ------------------------------------------------
        
        // 2. Inicializar el SceneManager
        configuracion.getSceneManager().inicializar(stage);
        
        // 3. Lanzar la ventana principal
        configuracion.getSceneManager().showGastos();
    }

    public static void main(String[] args) {
        launch(args);
    }
}