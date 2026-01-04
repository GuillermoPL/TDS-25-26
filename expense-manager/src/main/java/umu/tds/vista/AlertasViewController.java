package umu.tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import umu.tds.Configuracion;
import umu.tds.controlador.ControladorAppGastos;
import umu.tds.modelo.Alerta;
import umu.tds.modelo.EventoSistema;
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

        // Cargamos categorías y añadimos opción global
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

    private void refrescarLista() {
        listaAlertas.getItems().setAll(
            Configuracion.getInstancia().getControladorAppGastos().getAlertas()
        );
    }

    @Override
    public void actualizar(EventoSistema evento, Object datos) {
        // Si el sistema notifica una nueva alerta o una eliminación, refrescamos la lista
        if (evento == EventoSistema.NUEVA_ALERTA) {
            // Platform.runLater asegura que la actualización ocurra en el hilo de la interfaz
            javafx.application.Platform.runLater(() -> refrescarLista());
        }
        if (evento == EventoSistema.ALERTA_DISPARADA) {
            Alerta alerta = (Alerta) datos;
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Límite de Gastos");
                alert.setHeaderText("¡Atención: Presupuesto Excedido!");
                alert.setContentText("Has superado tu límite de " + alerta.getLimite() + "€.");
                
                alert.show(); 
            });
        }
    }
}