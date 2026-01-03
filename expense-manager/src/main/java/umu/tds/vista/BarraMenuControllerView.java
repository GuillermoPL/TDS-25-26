package umu.tds.vista;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import umu.tds.Configuracion;
import umu.tds.controlador.ControladorAppGastos;
import umu.tds.modelo.Alerta;
import umu.tds.modelo.EstrategiaAlertaSemanal;
import umu.tds.modelo.EventoSistema;

public class BarraMenuControllerView implements IObservador {

	@FXML
	public void initialize() {
	    ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
	    ctrl.eliminarObservador(this); 
	    ctrl.registrarObservador(this);
	}

    @Override
    public void actualizar(EventoSistema evento, Object datos) {
        if (evento == EventoSistema.ALERTA_DISPARADA) {
            Alerta alerta = (Alerta) datos;
            
            // Importante: Ejecutar en el hilo de la UI
            javafx.application.Platform.runLater(() -> {
                Alert dialog = new Alert(Alert.AlertType.WARNING);
                dialog.setTitle("Límite de Gastos");
                dialog.setHeaderText("¡Atención: Presupuesto Excedido!");
                String periodo = (alerta.getEstrategia() instanceof EstrategiaAlertaSemanal) ? "semanal" : "mensual";
                dialog.setContentText("Has superado tu límite " + periodo + " de " + alerta.getLimite() + "€.");
                dialog.showAndWait();
            });
        }
    }

    // Métodos de navegación que ya teníamos...
    @FXML private void irAGastos() { Configuracion.getInstancia().getSceneManager().showGastos(); }
    @FXML private void irAAlertas() { Configuracion.getInstancia().getSceneManager().showAlertas(); }
    @FXML private void irACategorias() { Configuracion.getInstancia().getSceneManager().showCategorias(); }
    @FXML private void irAEstadisticas() { Configuracion.getInstancia().getSceneManager().showEstadisticas(); }
}