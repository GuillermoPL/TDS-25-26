package umu.tds.vista;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import umu.tds.Configuracion;

public class BarraMenuControllerView {

    @FXML
    private void irAGastos() {
        Configuracion.getInstancia().getSceneManager().showGastos();
    }

    @FXML
    private void irACategorias() {
        Configuracion.getInstancia().getSceneManager().showCategorias();
    }

    @FXML
    private void irAEstadisticas() {
        Configuracion.getInstancia().getSceneManager().showEstadisticas();
    }

    @FXML
    public void salir(Event e) {
        Platform.exit();
    }
    
    @FXML
    void abrirAcerca(Event e) {
        // Asumiendo que implementaremos un diálogo de "Acerca de"
    	Configuracion.getInstancia().getSceneManager().showAcerca();
    }
}