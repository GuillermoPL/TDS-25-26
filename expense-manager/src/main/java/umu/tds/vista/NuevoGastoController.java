package umu.tds.vista;

import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import umu.tds.controlador.ControladorAppGastos;

public class NuevoGastoController {

    @FXML private TextField txtImporte;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbCategoria;

    @FXML
    public void initialize() {
        // 1. Ponemos la fecha de hoy por defecto
        dpFecha.setValue(LocalDate.now());

        // 2. Rellenamos el combo con las categorías existentes en el sistema
        cbCategoria.getItems().addAll(ControladorAppGastos.getInstancia().getNombreCategorias());
    }

    @FXML
    private void handleGuardar() {
        // 1. Recoger datos y validar
        String strImporte = txtImporte.getText();
        LocalDate fecha = dpFecha.getValue();
        String categoria = cbCategoria.getValue();

        if (strImporte.isEmpty() || fecha == null || categoria == null) {
            mostrarAlerta("Error de validación", "Por favor, rellena todos los campos.");
            return;
        }

        try {
            double importe = Double.parseDouble(strImporte);

            // 2. Llamar al controlador de negocio
            // Esto guardará en el JSON y NOTIFICARÁ a la tabla automáticamente
            ControladorAppGastos.getInstancia().registrarGasto(importe, fecha, categoria);

            // 3. Cerrar la ventana actual
            cerrarVentana();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "El importe debe ser un número válido.");
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtImporte.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}