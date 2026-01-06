package umu.tds;

import javafx.application.Application;
import javafx.stage.Stage;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;
import umu.tds.controlador.ControladorSesion;
import umu.tds.modelo.Usuario;

public class App extends Application {
	@Override
    public void start(Stage stage) {
        // 1. Crear y establecer la configuración
        Configuracion configuracion = new ConfiguracionImpl();
        Configuracion.setInstancia(configuracion);
        
        // 2. Inicializar datos
        try {
			configuracion.getControladorAppGastos().inicializarDatos();
		} catch (ErrorPersistenciaException e) {
			// Si salta esta excepción es porque no está bien configurado el json de los gastos
			// o el de las categorías.
			throw new IllegalStateException(e.getMessage());
		} 
        
        // 3. Sesión de prueba
        Usuario usuarioPrueba = new Usuario("yo"); 
        ControladorSesion.getInstancia().setUsuarioActual(usuarioPrueba);
        
        // 4. Mostrar interfaz
        configuracion.getSceneManager().inicializar(stage);
        configuracion.getSceneManager().showGastos();
    }

    public static void main(String[] args) {
        launch(args);
    }
}