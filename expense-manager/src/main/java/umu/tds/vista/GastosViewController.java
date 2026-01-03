package umu.tds.vista;

import java.util.List;
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
        // 1. Configuración de columnas
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colConcepto.setCellValueFactory(new PropertyValueFactory<>("id"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));
        colPagador.setCellValueFactory(new PropertyValueFactory<>("pagador"));

        // 2. Acceso al controlador de negocio mediante el Service Locator de la Configuración
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();

        // 3. Rellenar filtros
        List<String> categorias = ctrl.getNombreCategorias(); 
        cbCategoria.getItems().add("Todas");
        cbCategoria.getItems().addAll(categorias);
        cbCategoria.setValue("Todas");

        // 4. Registro como observador
        ctrl.registrarObservador(this);
        
        refrescarTabla();
    }

    private void refrescarTabla() {
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        // Cargamos todos los gastos inicialmente
        tablaGastos.getItems().setAll(ctrl.getGastosPorCondicion(g -> true));
    }

    @Override
    public void actualizar(EventoSistema evento, Object datos) {
        // Refrescamos si hay cambios. Asegúrate de que estos nombres están en tu Enum
        if (evento == EventoSistema.NUEVO_GASTO || 
            evento == EventoSistema.GASTO_ELIMINADO || 
            evento == EventoSistema.GASTO_MODIFICADO) {
            
            // Es importante ejecutar esto en el hilo de la UI de JavaFX
            javafx.application.Platform.runLater(() -> refrescarTabla());
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
            if (desde != null && g.getFecha().isBefore(desde)) return false;
            if (hasta != null && g.getFecha().isAfter(hasta)) return false;
            if (catSeleccionada != null && !catSeleccionada.equals("Todas")) {
                if (!g.getCategoria().toString().equals(catSeleccionada)) return false;
            }
            return true;
        });

        tablaGastos.getItems().setAll(filtrados);
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
            // El SceneManager se encarga de todo lo feo del FXMLLoader
            Configuracion.getInstancia().getSceneManager().showEditarGasto(seleccionado);
        }
    }

    @FXML
    private void handleBorrarGasto() {
        Gasto seleccionado = tablaGastos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert confirm = new Alert(AlertType.CONFIRMATION, "¿Borrar gasto de " + seleccionado.getImporte() + "€?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    // ACCESO CORRECTO A TRAVÉS DE CONFIGURACIÓN
                    Configuracion.getInstancia().getControladorAppGastos().eliminarGasto(seleccionado);
                }
            });
        }
    }

    
}