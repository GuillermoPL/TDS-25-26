package umu.tds.vista;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
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
import umu.tds.modelo.Usuario;

public class CuentasViewController implements IObservador {

    @FXML private ListView<CuentaCompartida> listaCuentasExistentes;
    @FXML private TextField txtNombreCuenta;
    @FXML private TextField txtLoginUsuario;
    @FXML private ComboBox<String> cbEstrategia;
    
    @FXML private TableView<MiembroAux> tablaMiembros;
    @FXML private TableColumn<MiembroAux, String> colUsuario;
    @FXML private TableColumn<MiembroAux, Double> colPorcentaje;
    @FXML private TableColumn<MiembroAux, Double> colSaldo;
    @FXML private Button btnAniadirUsuario;
    @FXML private Button btnCrearCuenta;

    private ObservableList<MiembroAux> miembrosTemp = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        ctrl.registrarObservador(this);

        cbEstrategia.getItems().addAll("EQUITATIVO", "PORCENTUAL");
        cbEstrategia.setValue("EQUITATIVO");

        colUsuario.setCellValueFactory(new PropertyValueFactory<>("login"));
        colPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldo")); // Nueva columna de saldo

        tablaMiembros.setEditable(true);
        colPorcentaje.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        
        colPorcentaje.setOnEditCommit(event -> {
            MiembroAux miembro = event.getRowValue();
            miembro.setPorcentaje(event.getNewValue());
        });

        listaCuentasExistentes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                cargarCuentaExistente(newSel);
            }
        });

        tablaMiembros.setItems(miembrosTemp);
        refrescarCuentas();
    }

    private void cargarCuentaExistente(CuentaCompartida cuenta) {
        miembrosTemp.clear();
        txtNombreCuenta.setText(cuenta.getNombre());
        
        txtNombreCuenta.setEditable(false);
        txtLoginUsuario.setDisable(true);
        btnAniadirUsuario.setDisable(true);
        cbEstrategia.setDisable(true);
        if (btnCrearCuenta != null) btnCrearCuenta.setDisable(true);

        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        cbEstrategia.setValue(cuenta.getEstrategia().toString().toUpperCase());

        Map<Usuario, Double> saldos = ctrl.getSaldosPorUsuarioCuenta(cuenta);
        Map<Usuario, Double> porcentajes = ctrl.getPorcentajesUsuarioCuenta(cuenta);

        for (Usuario u : saldos.keySet()) {
            Double porc = porcentajes.getOrDefault(u, 0.0);
            Double saldoActual = saldos.get(u);
            miembrosTemp.add(new MiembroAux(u.getLogin(), porc, saldoActual));
        }
    }

    @FXML
    private void handleRegistrarGastoEnCuenta() {
        CuentaCompartida cuentaActual = listaCuentasExistentes.getSelectionModel().getSelectedItem();
        MiembroAux pagador = tablaMiembros.getSelectionModel().getSelectedItem();

        if (cuentaActual == null || pagador == null) {
            mostrarAlerta("Selección necesaria", "Seleccione una cuenta en la lista y el pagador en la tabla.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("0.00");
        dialog.setTitle("Registrar Gasto - " + cuentaActual.getNombre());
        dialog.setHeaderText("Pagador: " + pagador.getLogin());
        dialog.setContentText("Importe del gasto compartido (€):");

        dialog.showAndWait().ifPresent(strImporte -> {
            try {
                double importe = Double.parseDouble(strImporte);
                ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
                
                // Registramos el gasto en la cuenta seleccionada
                ctrl.registrarGastoEnCuenta(importe, LocalDate.now(), "Gasto Común", 
                                            pagador.getLogin(), cuentaActual);
                
                mostrarInformacion("Éxito", "Gasto registrado y saldos actualizados.");
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "Importe no válido.");
            }
        });
    }

    @FXML
    private void handleAniadirUsuario() {
        String login = txtLoginUsuario.getText().trim();
        if (login.isEmpty()) return;
        
        if (miembrosTemp.stream().anyMatch(m -> m.getLogin().equals(login))) return;

        miembrosTemp.add(new MiembroAux(login, 0.0, 0.0));
        
        txtLoginUsuario.clear();
    }

    @FXML
    private void handleCrearCuenta() {
        String nombre = txtNombreCuenta.getText().trim();
        String estrategia = cbEstrategia.getValue();

        if (nombre.isEmpty() || miembrosTemp.isEmpty()) {
            mostrarAlerta("Datos incompletos", "Debe dar un nombre y añadir miembros.");
            return;
        }

        Map<String, Double> datos = new HashMap<>();
        for (MiembroAux m : miembrosTemp) {
            datos.put(m.getLogin(), m.getPorcentaje());
        }

        try {
            Configuracion.getInstancia().getControladorAppGastos()
                .crearCuentaCompartida(nombre, estrategia, datos);
            handleLimpiar();
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void handleLimpiar() {
        txtNombreCuenta.clear();
        txtLoginUsuario.clear();
        miembrosTemp.clear();
        listaCuentasExistentes.getSelectionModel().clearSelection();
        
        // REHABILITAR EDICIÓN
        txtNombreCuenta.setEditable(true);
        txtLoginUsuario.setDisable(false);
        btnAniadirUsuario.setDisable(false);
        cbEstrategia.setDisable(false);
        if (btnCrearCuenta != null) btnCrearCuenta.setDisable(false);
    }

    private void refrescarCuentas() {
        listaCuentasExistentes.getItems().setAll(
            Configuracion.getInstancia().getControladorAppGastos().getCuentasCompartidas()
        );
    }

    @Override
    public void actualizar(EventoSistema evento, Object datos) {
        if (evento == EventoSistema.NUEVA_CUENTA || evento == EventoSistema.SALDO_ACTUALIZADO) {
            javafx.application.Platform.runLater(this::refrescarCuentas);
        }
    }

    public static class MiembroAux {
        private String login;
        private Double porcentaje;
        private Double saldo; // Campo necesario para la visualización en la tabla

        public MiembroAux(String login, Double porcentaje, Double saldo) {
            this.login = login;
            this.porcentaje = porcentaje;
            this.saldo = saldo;
        }

        public String getLogin() { return login; }
        public Double getPorcentaje() { return porcentaje; }
        public Double getSaldo() { return saldo; } // Getter para colSaldo
        
        public void setPorcentaje(Double porcentaje) { this.porcentaje = porcentaje; }
    }

    private void mostrarAlerta(String t, String m) { new Alert(Alert.AlertType.WARNING, m).showAndWait(); }
    private void mostrarInformacion(String t, String m) { new Alert(Alert.AlertType.INFORMATION, m).showAndWait(); }
}