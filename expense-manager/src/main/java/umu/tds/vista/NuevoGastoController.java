package umu.tds.vista;

import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Window; // Cambiamos Stage por Window
import umu.tds.Configuracion;
import umu.tds.controlador.ControladorAppGastos;
import umu.tds.modelo.Categoria;
import umu.tds.modelo.Gasto;

public class NuevoGastoController {

    @FXML private TextField txtImporte;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbCategoria;

    private Gasto gastoAEditar = null;

    @FXML
    public void initialize() {
        dpFecha.setValue(LocalDate.now());

        // Acceso correcto según el patrón del profesor
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        cbCategoria.getItems().addAll(ctrl.getNombreCategorias());
    }

    public void setGasto(Gasto gasto) {
        this.gastoAEditar = gasto;
        
        txtImporte.setText(String.valueOf(gasto.getImporte()));
        dpFecha.setValue(gasto.getFecha());
        cbCategoria.setValue(gasto.getCategoria().toString());
    }

    @FXML
    private void handleGuardar() {
        String strImporte = txtImporte.getText().trim();
        LocalDate fecha = dpFecha.getValue();
        String nombreCategoria = cbCategoria.getValue();

        if (strImporte.isEmpty() || fecha == null || nombreCategoria == null) {
            mostrarAlerta("Campos incompletos", "Por favor, rellena todos los campos.");
            return;
        }

        try {
            double importe = Double.parseDouble(strImporte);
            
            // Validación extra: no permitir gastos negativos (HU 1.1)
            if (importe <= 0) {
                mostrarAlerta("Importe no válido", "El importe debe ser mayor que cero.");
                return;
            }

            ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();

            if (gastoAEditar == null) {
                ctrl.registrarGasto(importe, fecha, nombreCategoria);
            } else {
                gastoAEditar.setImporte(importe);
                gastoAEditar.setFecha(fecha);
                gastoAEditar.setCategoria(new Categoria(nombreCategoria));
                ctrl.modificarGasto(gastoAEditar);
            }

            cerrarVentana();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "Introduce un número válido para el importe.");
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        // 1. Obtenemos el Stage (ventana) a través de cualquier componente, por ejemplo el TextField
        javafx.stage.Stage stage = (javafx.stage.Stage) txtImporte.getScene().getWindow();
        
        // 2. Usamos close() que es la forma estándar de cerrar una ventana hija
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