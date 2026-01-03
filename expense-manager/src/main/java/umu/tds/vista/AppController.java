package umu.tds.vista;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import umu.tds.controlador.ControladorAppGastos;
import umu.tds.modelo.EventoSistema;

public class AppController implements IObservador {

    @FXML
    private StackPane contenedorPrincipal;

    @FXML
    public void initialize() {
        // Registro en el controlador (Singleton) para recibir notificaciones 
     
        // ControladorAppGastos.getInstancia().registrar(this);
        
        // Cargamos la vista de gastos por defecto al iniciar
        mostrarGastos();
    }

    // Métodos vinculados a los onAction del FXML
    @FXML
    private void mostrarGastos() {
        cargarSubVista("/umu/tds/GastosView.fxml");
    }

    @FXML
    private void mostrarEstadisticas() {
        cargarSubVista("/umu/tds/EstadisticasView.fxml");
    }

    @FXML
    private void mostrarCuentas() {
        cargarSubVista("/umu/tds/CuentasView.fxml");
    }

    @FXML
    private void mostrarAlertas() {
        cargarSubVista("/umu/tds/AlertasView.fxml");
    }

    // Método auxiliar para cambiar el contenido del StackPane central
    private void cargarSubVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node nodo = loader.load();
            contenedorPrincipal.getChildren().setAll(nodo);
        } catch (IOException e) {
            System.err.println("Error: No se pudo cargar " + fxmlPath);
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(EventoSistema evento, Object datos) {
        System.out.println("Evento recibido: " + evento);
        // Lógica según el evento
    }
}