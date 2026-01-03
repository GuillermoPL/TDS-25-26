package umu.tds.vista;

import java.util.List;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import umu.tds.controlador.ControladorAppGastos;
import umu.tds.modelo.EventoSistema;
import umu.tds.modelo.Gasto;
import javafx.scene.control.DatePicker;
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
        // Configuración de columnas
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colConcepto.setCellValueFactory(new PropertyValueFactory<>("id"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));
        colPagador.setCellValueFactory(new PropertyValueFactory<>("pagador"));

        // Cargar nombres de categorías para el filtro [cite: 9, 16]
        List<String> categorias = ControladorAppGastos.getInstancia().getNombreCategorias(); 
        cbCategoria.getItems().add("Todas");
        cbCategoria.getItems().addAll(categorias);
        cbCategoria.setValue("Todas");

        ControladorAppGastos.getInstancia().registrarObservador(this);
        
        refrescarTabla();
    }

    private void refrescarTabla() {
        tablaGastos.getItems().setAll(
            ControladorAppGastos.getInstancia().getGastosPorCondicion(g -> true)
        );
    }

    @Override
    public void actualizar(EventoSistema evento, Object datos) {
        // Si el controlador dice que hay un nuevo gasto, refrescamos
        if (evento == EventoSistema.NUEVO_GASTO) {
            refrescarTabla();
            System.out.println("Vista de Gastos: ¡Tabla actualizada!");
        }
    }
    // --- MÉTODOS DE ACCIÓN  ---

    @FXML
    private void handleFiltrar() {
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();
        String catSeleccionada = cbCategoria.getValue();

        // Definimos la condición (Predicado)
        java.util.function.Predicate<Gasto> condicion = g -> {
            // Filtro de Fecha "Desde"
            if (desde != null && g.getFecha().isBefore(desde)) return false;
            
            // Filtro de Fecha "Hasta"
            if (hasta != null && g.getFecha().isAfter(hasta)) return false;
            
            // Filtro de Categoría
            if (catSeleccionada != null && !catSeleccionada.equals("Todas")) {
                if (!g.getCategoria().toString().equals(catSeleccionada)) return false;
            }
            
            return true; // Si pasa todos los filtros, el gasto se queda
        };

        // Actualizamos la tabla pidiendo al controlador solo los que cumplen la condición
        List<Gasto> filtrados = ControladorAppGastos.getInstancia().getGastosPorCondicion(condicion);
        tablaGastos.getItems().setAll(filtrados);
    }

    @FXML
    private void handleNuevoGasto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/umu/tds/NuevoGastoView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nuevo Gasto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal hasta cerrar esta
            stage.showAndWait();
            
            // Al volver, refrescamos la tabla. redundante, pero no hace daño
            refrescarTabla();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditarGasto() {
        Gasto seleccionado = tablaGastos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            System.out.println("Editando: " + seleccionado); 
        }
    }

    @FXML
    private void handleBorrarGasto() {
        Gasto seleccionado = tablaGastos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            System.out.println("Borrando: " + seleccionado); 
            // Llamar al controlador de negocio para eliminarlo
        }
    }
}