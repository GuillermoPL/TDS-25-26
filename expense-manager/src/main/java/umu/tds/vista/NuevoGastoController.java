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

        // 1. VALIDACIÓN DE CAMPOS: Usa la versión de 2 argumentos
        if (strImporte.isEmpty() || fecha == null || nombreCategoria == null) {
            UIUtils.mostrarAlerta(AlertType.WARNING, "Campos incompletos", null, "Por favor, rellena todos los campos.");
            return;
        }

        try {
            double importe = Double.parseDouble(strImporte);
            ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();

            // 2. VALIDACIÓN DE NEGOCIO: Usa la versión de 4 argumentos
            if (!ctrl.isImporteValido(importe)) {
                UIUtils.mostrarAlerta(AlertType.WARNING, "Importe no válido", null, "El importe debe ser mayor que cero.");
                return;
            }

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
            // Usa la versión de 2 argumentos
            UIUtils.mostrarAlerta(AlertType.WARNING, "Error de formato", null, "Introduce un número válido para el importe.");
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
    
}