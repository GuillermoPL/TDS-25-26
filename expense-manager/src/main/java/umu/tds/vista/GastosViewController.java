package umu.tds.vista;

import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import umu.tds.controlador.ControladorAppGastos;
import umu.tds.modelo.Gasto;

public class GastosViewController {

    // Identificadores de la tabla y columnas
    @FXML private TableView<Gasto> tablaGastos;
    @FXML private TableColumn<Gasto, LocalDate> colFecha;
    @FXML private TableColumn<Gasto, String> colCategoria;
    @FXML private TableColumn<Gasto, String> colConcepto;
    @FXML private TableColumn<Gasto, Double> colImporte;
    @FXML private TableColumn<Gasto, String> colPagador;

    // Identificadores de los filtros
    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    @FXML private ComboBox<String> cbCategoria;

    @FXML
    public void initialize() {
        // 1. Vincular las columnas con los atributos de la clase Gasto
        // Importante: los strings deben coincidir con los nombres de los atributos en Gasto.java
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colConcepto.setCellValueFactory(new PropertyValueFactory<>("concepto"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));
        colPagador.setCellValueFactory(new PropertyValueFactory<>("pagador"));

        // 2. Cargar datos iniciales
        refrescarTabla();
    }

    private void refrescarTabla() {
        // Obtenemos todos los gastos del controlador de negocio
        tablaGastos.getItems().setAll(
            ControladorAppGastos.getInstancia().getGastosPorCondicion(g -> true)
        );
    }

    // --- MÉTODOS DE ACCIÓN (Los que salían en rojo) ---

    @FXML
    private void handleFiltrar() {
        System.out.println("Filtrando gastos...");
        // Aquí implementarás la lógica de filtrado por fecha y categoría 
    }

    @FXML
    private void handleNuevoGasto() {
        System.out.println("Abriendo formulario de nuevo gasto...");
        // Aquí abrirás un diálogo para registrar un nuevo gasto [cite: 9]
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