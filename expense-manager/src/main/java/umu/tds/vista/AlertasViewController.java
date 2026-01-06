package umu.tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import umu.tds.Configuracion;
import umu.tds.controlador.ControladorAppGastos;
import umu.tds.modelo.Alerta;
import umu.tds.modelo.EventoSistema;
import umu.tds.modelo.Notificacion;

import java.util.List;
import java.util.Optional;

public class AlertasViewController implements IObservador {
    @FXML private TextField txtLimite;
    @FXML private ComboBox<String> cbPeriodo;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private ListView<Alerta> listaAlertas;

    @FXML
    public void initialize() {
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        ctrl.registrarObservador(this);
        
        cbPeriodo.getItems().addAll("Semanal", "Mensual");
        cbPeriodo.setValue("Mensual");

        cbCategoria.getItems().add("Todas");
        cbCategoria.getItems().addAll(ctrl.getNombreCategorias());
        cbCategoria.setValue("Todas");
        
        refrescarLista();
    }

    @FXML
    private void handleCrearAlerta() {
        String strLimite = txtLimite.getText().trim();
        String periodo = cbPeriodo.getValue();
        String categoriaSel = cbCategoria.getValue();

        // 1. Validación de campos obligatorios
        if (strLimite.isEmpty() || periodo == null || categoriaSel == null) {
            UIUtils.mostrarAlertaWarning("Campos incompletos", null, "Por favor, rellena todos los campos.");
            return;
        }

        try {
            double limite = Double.parseDouble(strLimite);

            // 2. Evitar valores negativos o cero
            if (limite <= 0) {
                UIUtils.mostrarAlertaError("Valor no válido", null, "El límite debe ser un número positivo (mayor que cero).");
                return;
            }

            // Si elige "Todas", pasamos null como categoría al controlador
            String catParaControlador = categoriaSel.equals("Todas") ? null : categoriaSel;
            
            Configuracion.getInstancia().getControladorAppGastos()
                         .crearAlerta(limite, periodo, catParaControlador);
            
            txtLimite.clear(); // Limpiamos la caja de texto tras el éxito
            
        } catch (NumberFormatException e) {
            // Se dispara si el usuario introduce letras en el campo del límite
            UIUtils.mostrarAlertaError("Error de Formato", null, "El límite introducido debe ser un número válido.");
        }
    }

    @FXML
    private void handleEliminarAlerta() {
        Alerta seleccionada = listaAlertas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;
        
        Configuracion.getInstancia().getControladorAppGastos().eliminarAlerta(seleccionada);
        refrescarLista();
    }
    
    @FXML
    private void handleVerHistorial() {
        Configuracion.getInstancia().getSceneManager().showHistorialNotificaciones();
    }

    private void refrescarLista() {
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        listaAlertas.getItems().setAll(ctrl.getAlertas());
    }

    @Override
    public void actualizar(EventoSistema evento, Object datos) {
        // Si hay una nueva alerta o se dispara una, refrescamos
        if (evento == EventoSistema.NUEVA_ALERTA || evento == EventoSistema.ALERTA_DISPARADA) {
            javafx.application.Platform.runLater(() -> refrescarLista());
        }
    }
}