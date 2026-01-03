package umu.tds.vista;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import umu.tds.Configuracion;
import umu.tds.controlador.ControladorAppGastos;
import umu.tds.modelo.CuentaCompartida;
import umu.tds.modelo.EventoSistema;

public class CuentasViewController implements IObservador {

    @FXML private ListView<CuentaCompartida> listaCuentasExistentes;
    @FXML private TextField txtNombreCuenta;
    @FXML private TextField txtLoginUsuario;
    @FXML private ComboBox<String> cbEstrategia;
    
    @FXML private TableView<MiembroAux> tablaMiembros;
    @FXML private TableColumn<MiembroAux, String> colUsuario;
    @FXML private TableColumn<MiembroAux, Double> colPorcentaje;

    private ObservableList<MiembroAux> miembrosTemp = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        ctrl.registrarObservador(this);

        cbEstrategia.getItems().addAll("EQUIVALENTE", "PORCENTUAL");
        cbEstrategia.setValue("EQUIVALENTE");

        // CONFIGURACIÓN DE COLUMNAS
        // Usamos PropertyValueFactory que busca los métodos 'getLogin' y 'getPorcentaje'
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("login"));
        colPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));

        // Hacer la columna de porcentaje editable
        tablaMiembros.setEditable(true);
        colPorcentaje.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        
        // Cuando el usuario termina de escribir el número, actualizamos el objeto
        colPorcentaje.setOnEditCommit(event -> {
            MiembroAux miembro = event.getRowValue();
            miembro.setPorcentaje(event.getNewValue());
        });

        tablaMiembros.setItems(miembrosTemp);
        refrescarCuentas();
    }

    @FXML
    private void handleAniadirUsuario() {
        String login = txtLoginUsuario.getText().trim();
        if (login.isEmpty()) return;
        
        // Evitar duplicados en la lista temporal
        if (miembrosTemp.stream().anyMatch(m -> m.login.equals(login))) return;

        miembrosTemp.add(new MiembroAux(login, 0.0));
        txtLoginUsuario.clear();
    }

    @FXML
    private void handleCrearCuenta() {
        String nombre = txtNombreCuenta.getText().trim();
        String estrategia = cbEstrategia.getValue();

        if (nombre.isEmpty() || miembrosTemp.isEmpty()) {
            mostrarAlerta("Datos incompletos", "Debe dar un nombre y añadir al menos un miembro.");
            return;
        }

        // Convertimos nuestra lista temporal al mapa que espera el controlador
        Map<String, Double> datosParaControlador = new HashMap<>();
        for (MiembroAux m : miembrosTemp) {
            datosParaControlador.put(m.login, m.porcentaje);
        }

        try {
            Configuracion.getInstancia().getControladorAppGastos()
                .crearCuentaCompartida(estrategia, datosParaControlador);
            handleLimpiar();
            mostrarInformacion("Éxito", "Cuenta compartida creada correctamente.");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo crear la cuenta: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimpiar() {
        txtNombreCuenta.clear();
        miembrosTemp.clear();
        txtLoginUsuario.clear();
    }

    private void refrescarCuentas() {
        listaCuentasExistentes.getItems().setAll(
            Configuracion.getInstancia().getControladorAppGastos().getCuentasCompartidas()
        );
    }

    @Override
    public void actualizar(EventoSistema evento, Object datos) {
        if (evento == EventoSistema.SALDO_ACTUALIZADO) {
            javafx.application.Platform.runLater(() -> refrescarCuentas());
        }
    }

    // Clase auxiliar interna para la tabla
    public static class MiembroAux {
        private String login;
        private Double porcentaje;

        public MiembroAux(String login, Double porcentaje) {
            this.login = login;
            this.porcentaje = porcentaje;
        }

        // Los GETTERS son obligatorios para la tabla
        public String getLogin() { return login; }
        public Double getPorcentaje() { return porcentaje; }
        
        // El SETTER es necesario para que la edición se guarde
        public void setPorcentaje(Double porcentaje) { this.porcentaje = porcentaje; }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        new Alert(Alert.AlertType.WARNING, mensaje).showAndWait();
    }
    
    private void mostrarInformacion(String titulo, String mensaje) {
        new Alert(Alert.AlertType.INFORMATION, mensaje).showAndWait();
    }
}