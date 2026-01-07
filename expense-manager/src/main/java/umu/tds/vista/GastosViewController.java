package umu.tds.vista;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import umu.tds.App;
import umu.tds.Configuracion;
import umu.tds.controlador.ControladorAppGastos;
import umu.tds.modelo.EventoSistema;
import umu.tds.modelo.Gasto;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.stage.FileChooser;
import java.io.File;
import umu.tds.modelo.importacion.exceptions.ImportacionException;

public class GastosViewController implements IObservador {

    @FXML private TableView<Gasto> tablaGastos;
    @FXML private TableColumn<Gasto, LocalDate> colFecha;
    @FXML private TableColumn<Gasto, Object> colCategoria;
    @FXML private TableColumn<Gasto, String> colConcepto;
    @FXML private TableColumn<Gasto, Double> colImporte;
    @FXML private TableColumn<Gasto, Object> colPagador;
    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    @FXML private ComboBox<String> cbCategoria;

    @FXML
    public void initialize() {
        // 1. Configuración de celdas de la tabla
        // El string debe coincidir exactamente con el nombre del atributo en Gasto.java
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colConcepto.setCellValueFactory(new PropertyValueFactory<>("id"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));
        colPagador.setCellValueFactory(new PropertyValueFactory<>("pagador"));

        // 2. Obtener el controlador de negocio a través del Service Locator (Configuracion)
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();

        // 3. Configurar el ComboBox de categorías para el filtro
        actualizarComboCategorias();

        // 4. Registrarse como observador para actualizaciones en tiempo real
        ctrl.registrarObservador(this);
        
        // 5. Carga inicial de datos
        refrescarTabla();
    }

    /**
     * Helper para rellenar el combo de categorías incluyendo la opción neutra
     */
    private void actualizarComboCategorias() {
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        List<String> categorias = ctrl.getNombreCategorias();
        
        cbCategoria.getItems().clear();
        cbCategoria.getItems().add("Todas");
        cbCategoria.getItems().addAll(categorias);
        cbCategoria.setValue("Todas");
    }

    /**
     * Carga todos los gastos del repositorio en la tabla
     */
    private void refrescarTabla() {
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();

        List<Gasto> personales = ctrl.getGastosPorCondicion(g -> ctrl.esGastoPersonal(g));
        
        tablaGastos.getItems().setAll(personales);
    }

    @Override
    public void actualizar(EventoSistema evento, Object datos) {
        // Escuchamos todos los eventos que afectan a la lista de gastos
        if (evento == EventoSistema.NUEVO_GASTO || 
            evento == EventoSistema.GASTO_MODIFICADO || 
            evento == EventoSistema.GASTO_ELIMINADO) {
            
            javafx.application.Platform.runLater(() -> {
                refrescarTabla();
            });
        }

        if (evento == EventoSistema.NUEVA_CATEGORIA) {
            javafx.application.Platform.runLater(() -> {
                actualizarComboCategorias();
            });
        }
    }
    // --- MÉTODOS DE ACCIÓN  ---

    @FXML
    private void handleFiltrar() {
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();
        String catSeleccionada = cbCategoria.getValue();

        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();

        List<Gasto> filtrados = ctrl.getGastosPorCondicion(g -> {
            if (!ctrl.esGastoPersonal(g)) {
                return false;
            }

            // 1. Filtro de fecha "Desde"
            if (desde != null && g.getFecha().isBefore(desde)) {
                return false;
            }
            
            // 2. Filtro de fecha "Hasta"
            if (hasta != null && g.getFecha().isAfter(hasta)) {
                return false;
            }
            
            // 3. Filtro de Categoría
            if (catSeleccionada != null && !catSeleccionada.equals("Todas")) {
                if (!g.getCategoria().toString().equals(catSeleccionada)) {
                    return false;
                }
            }
            
            return true;
        });

        tablaGastos.getItems().setAll(filtrados);
    }

    @FXML
    private void handleLimpiarFiltros() {
        dpDesde.setValue(null);
        dpHasta.setValue(null);
        cbCategoria.setValue("Todas");
        refrescarTabla(); // Esto vuelve a cargar todos los gastos
    }

    @FXML
    private void handleNuevoGasto() {
        // Mucho más limpio y sin errores de visibilidad
        Configuracion.getInstancia().getSceneManager().showNuevoGasto();
    }

    @FXML
    private void handleEditarGasto() {
        Gasto seleccionado = tablaGastos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
            
            // Comprobamos antes de abrir la ventana de edición
            if (!ctrl.esGastoPersonal(seleccionado)) {
                UIUtils.mostrarAlerta(AlertType.WARNING, "Acción no permitida", null, 
                    "Los gastos de cuentas compartidas no pueden editarse desde aquí.");
                return;
            }
            
            Configuracion.getInstancia().getSceneManager().showEditarGasto(seleccionado);
        }
    }

    @FXML
    private void handleBorrarGasto() {
        Gasto seleccionado = tablaGastos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();

            // Verificamos si es un gasto compartido
            if (!ctrl.esGastoPersonal(seleccionado)) {
                UIUtils.mostrarAlerta(AlertType.WARNING, "Acción no permitida", null, 
                    "No se pueden eliminar gastos de cuentas compartidas.");
                return;
            }

            // Si es personal, procedemos con la confirmación habitual
            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar Borrado");
            confirm.setHeaderText(null);
            confirm.setContentText("¿Borrar gasto de " + seleccionado.getImporte() + "€?");
            confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                ctrl.eliminarGasto(seleccionado);
            }
        }
    }
    @FXML
    private void handleImportarGastos() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar Gastos");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = fileChooser.showOpenDialog(tablaGastos.getScene().getWindow());

        if (file != null) {
            ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
            
            try {
                int numIgnorados = ctrl.importarGastos(file.getAbsolutePath());
                
                if (numIgnorados > 0) {
                    // Mensaje actualizado
                    UIUtils.mostrarAlerta(AlertType.WARNING, 
                        "Importación con Observaciones", 
                        "Proceso finalizado", 
                        "Se han procesado los gastos válidos.\n\nSe ignoraron " + numIgnorados + " registros por motivos de validación:\n"
                        + "- Ya existían previamente.\n"
                        + "- La cuenta compartida no existe.\n"
                        + "- El pagador no pertenece a la cuenta.");
                } else {
                    UIUtils.mostrarAlerta(AlertType.INFORMATION, 
                        "Importación Exitosa", 
                        "Correcto", 
                        "Todos los gastos han sido importados correctamente.");
                }
                
            } catch (ImportacionException e) {
                UIUtils.mostrarAlerta(AlertType.ERROR, "Error", "Fallo en importación", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}