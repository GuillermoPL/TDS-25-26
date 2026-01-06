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

        try {
            double limite = Double.parseDouble(strLimite);
            // Si elige "Todas", pasamos null como categoría
            String catParaControlador = categoriaSel.equals("Todas") ? null : categoriaSel;
            
            Configuracion.getInstancia().getControladorAppGastos()
                         .crearAlerta(limite, periodo, catParaControlador);
            
            txtLimite.clear();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "El límite debe ser un número válido.").showAndWait();
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