package umu.tds.vista;

import javafx.application.Platform;
import javafx.event.ActionEvent;
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
	        javafx.application.Platform.runLater(() -> {
	        	UIUtils.mostrarAlertaWarning("Límite de Gastos", "¡Atención: Presupuesto Excedido!", "Has superado tu límite de " + alerta.getLimite() + "€.");
	        });
	    }
	}
    @FXML
    private void salir() {
        // Cerramos la aplicación de forma limpia
        System.exit(0);
    }
    
    @FXML
    private void abrirAcerca() {
        UIUtils.mostrarAyuda(
            "Ayuda del Sistema", 
            "ExpenseManager v1.0", 
            "Para cualquier duda, consulte el manual de usuario o contacte con soporte."
        );
    }
    // Métodos de navegación que ya teníamos...
    @FXML private void irAGastos() { Configuracion.getInstancia().getSceneManager().showGastos(); }
    @FXML private void irAAlertas() { Configuracion.getInstancia().getSceneManager().showAlertas(); }
    @FXML private void irACategorias() { Configuracion.getInstancia().getSceneManager().showCategorias(); }
    @FXML private void irAEstadisticas() { Configuracion.getInstancia().getSceneManager().showEstadisticas(); }
    @FXML
    private void irACuentas() {
        // Usamos el SceneManager que ya tiene el método showCuentas()
        Configuracion.getInstancia().getSceneManager().showCuentas();
    }
    @FXML
    private void irACalendario(ActionEvent event) {
        Configuracion.getInstancia().getSceneManager().showCalendario();
    }
}