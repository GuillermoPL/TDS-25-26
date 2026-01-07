package umu.tds.vista;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.converter.DoubleStringConverter;
import umu.tds.App;
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
        
        // 1. Registrar el observador para enterarnos de cambios en el sistema
        ctrl.registrarObservador(this);

        // 2. Configurar el ComboBox de Estrategia
        cbEstrategia.getItems().addAll("EQUITATIVO", "PORCENTUAL");
        // REQUISITO: Reparto equitativo seleccionado por defecto
        cbEstrategia.setValue("EQUITATIVO");

        // 3. Configurar las columnas de la tabla
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("login"));
        colPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldo"));

        // 4. Permitir la edición de la columna de porcentajes
        tablaMiembros.setEditable(true);
        colPorcentaje.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colPorcentaje.setOnEditCommit(event -> {
            MiembroAux miembro = event.getRowValue();
            miembro.setPorcentaje(event.getNewValue());
        });

        // 5. Listener para cuando se selecciona una cuenta de la lista de la izquierda
        listaCuentasExistentes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                cargarCuentaExistente(newSel);
            } else {
                // Si se deselecciona, volvemos al estado de "Nueva Cuenta"
                handleLimpiar();
            }
        });

        // 6. Vincular la lista observable a la tabla
        tablaMiembros.setItems(miembrosTemp);

        // 7. Cargar las cuentas que ya existen en el sistema
        refrescarCuentas();

        // 8. Estado inicial: preparar para crear una cuenta nueva con el usuario actual
        reiniciarMiembrosConUsuarioActual();
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

        // 1. Verificación de selección previa
        if (cuentaActual == null || pagador == null) {
            UIUtils.mostrarAlerta(AlertType.WARNING, "Selección necesaria", null, "Seleccione cuenta y pagador.");
            return;
        }

        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        List<String> categorias = ctrl.getNombreCategorias();

        // 2. Diálogo de selección de categoría
        ChoiceDialog<String> catDialog = new ChoiceDialog<>(categorias.get(0), categorias);
        catDialog.setTitle("Registrar Gasto");
        catDialog.setHeaderText("Seleccione categoría");
        catDialog.getDialogPane().getStyleClass().add("confirmation");

        catDialog.showAndWait().ifPresent(cat -> {
            // 3. Diálogo de introducción de importe
            TextInputDialog impDialog = new TextInputDialog("0.00");
            impDialog.setTitle("Importe del Gasto");
            impDialog.setHeaderText("Importe para " + cat);
            impDialog.getDialogPane().getStyleClass().add("confirmation");
            
            impDialog.showAndWait().ifPresent(strImp -> {
                try {
                    // Soportamos comas convirtiéndolas a puntos para evitar errores de parseo
                    double importe = Double.parseDouble(strImp.replace(",", "."));
                    
                    // 4. Validación de negocio desacoplada en el controlador
                    if (!ctrl.isImporteValido(importe)) {
                        UIUtils.mostrarAlerta(AlertType.WARNING, "Importe no válido", null, "El importe debe ser mayor que cero.");
                        return;
                    }

                    // 5. Registro final del gasto
                    ctrl.registrarGastoEnCuenta(importe, LocalDate.now(), cat, pagador.getLogin(), cuentaActual);
                    UIUtils.mostrarAlerta(AlertType.INFORMATION, "Éxito", null, "Gasto registrado correctamente.");
                    
                } catch (NumberFormatException e) {
                    UIUtils.mostrarAlerta(AlertType.ERROR, "Error de formato", null, "El importe introducido no es un número válido.");
                }
            });
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
            UIUtils.mostrarAlerta(AlertType.WARNING, "Datos incompletos", null, "Debe dar un nombre y añadir miembros.");
            return;
        }

        // --- Validación de Porcentajes (HU 4.3) ---
        if ("PORCENTUAL".equals(estrategia)) {
            double sumaTotal = miembrosTemp.stream()
                                .mapToDouble(MiembroAux::getPorcentaje)
                                .sum();

            // Verificamos que sume 100 (con un pequeño margen de error por los decimales)
            if (Math.abs(sumaTotal - 100.0) > 0.01) {
                UIUtils.mostrarAlerta(AlertType.WARNING, "Error de Validación", null, "Los porcentajes suman " + String.format("%.2f", sumaTotal) + "%. Deben sumar exactamente 100%.");
                return; // Cortamos la ejecución aquí
            }
        }
        // -------------------------------------------------

        Map<String, Double> datos = new HashMap<>();
        for (MiembroAux m : miembrosTemp) {
            datos.put(m.getLogin(), m.getPorcentaje());
        }

        try {
            Configuracion.getInstancia().getControladorAppGastos()
                .crearCuentaCompartida(nombre, estrategia, datos);
            
            UIUtils.mostrarAlerta(AlertType.INFORMATION, "Cuenta Creada", null, "La cuenta compartida se ha guardado correctamente.");
            handleLimpiar(); // Limpiamos y restauramos al usuario actual
            
        } catch (Exception e) {
            UIUtils.mostrarAlerta(AlertType.WARNING, "Error", null, e.getMessage());
        }
    }

    @FXML
    private void handleLimpiar() {
        txtNombreCuenta.clear();
        txtLoginUsuario.clear();
        cbEstrategia.setValue("EQUITATIVO"); // Reset al valor por defecto
        reiniciarMiembrosConUsuarioActual();
        listaCuentasExistentes.getSelectionModel().clearSelection();
        
        // Rehabilitar componentes bloqueados
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

 // Método auxiliar para resetear la tabla añadiendo siempre al usuario actual
    private void reiniciarMiembrosConUsuarioActual() {
        miembrosTemp.clear();
        Usuario usuarioActual = umu.tds.controlador.ControladorSesion.getInstancia().getUsuarioActual();
        if (usuarioActual != null) {
            // Se añade con porcentaje 0 y saldo 0 por defecto
            miembrosTemp.add(new MiembroAux(usuarioActual.getLogin(), 0.0, 0.0));
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

}