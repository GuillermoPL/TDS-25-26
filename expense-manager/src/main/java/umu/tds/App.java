package umu.tds;

import javafx.application.Application;
import javafx.stage.Stage;
import umu.tds.controlador.ControladorSesion;
import umu.tds.modelo.Usuario;

public class App extends Application {
	@Override
    public void start(Stage stage) {
        // 1. Crear y establecer la configuración
        Configuracion configuracion = new ConfiguracionImpl();
        Configuracion.setInstancia(configuracion);
        
        // 2. INICIALIZAR DATOS: Ahora que 'configuracion' ya no es null para el Repo
        configuracion.getControladorAppGastos().inicializarDatos(); 
        
        // 3. Sesión de prueba
        Usuario usuarioPrueba = new Usuario("pepe"); 
        ControladorSesion.getInstancia().setUsuarioActual(usuarioPrueba);
        
        // 4. Mostrar interfaz
        configuracion.getSceneManager().inicializar(stage);
        configuracion.getSceneManager().showGastos();
    }

    public static void main(String[] args) {
        launch(args);
    }
}