package umu.tds.vista;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import umu.tds.Configuracion;
import umu.tds.controlador.ControladorAppGastos;
import umu.tds.modelo.EventoSistema;
import umu.tds.modelo.Gasto;
import umu.tds.modelo.importacion.exceptions.ImportacionException;

public class GastosViewController implements IObservador {

    // --- Elementos de la Vista (FXML) ---
    @FXML private TableView<Gasto> tablaGastos;
    @FXML private TableColumn<Gasto, LocalDate> colFecha;
    @FXML private TableColumn<Gasto, Object> colCategoria;
    @FXML private TableColumn<Gasto, String> colConcepto;
    @FXML private TableColumn<Gasto, Double> colImporte;
    @FXML private TableColumn<Gasto, Object> colPagador;
    
    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    
    // Componentes para selección múltiple (HU 2.4)
    @FXML private MenuButton mbMeses;
    @FXML private MenuButton mbCategorias;

    // --- Estado Interno ---
    private List<CheckBox> listaChecksMeses = new ArrayList<>();
    private List<CheckBox> listaChecksCategorias = new ArrayList<>();

    // --- Inicialización ---
    @FXML
    public void initialize() {
        // 1. Configuración de columnas
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colConcepto.setCellValueFactory(new PropertyValueFactory<>("id"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));
        colPagador.setCellValueFactory(new PropertyValueFactory<>("pagador"));

        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        
        // 2. Cargar los filtros dinámicos
        inicializarFiltroMeses();
        actualizarFiltroCategorias();

        // 3. Registrar observador
        ctrl.registrarObservador(this);
        
        // 4. Carga inicial de datos
        refrescarTabla();
    }
    
    // --- Configuración de Filtros Múltiples ---

    private void inicializarFiltroMeses() {
        mbMeses.getItems().clear();
        listaChecksMeses.clear();

        // Generamos los 12 meses en español
        for (Month mes : Month.values()) {
            String nombreMes = mes.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
            nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
            
            CheckBox cb = new CheckBox(nombreMes);
            cb.setUserData(mes); // Guardamos el Enum Month para usarlo al filtrar
            
            CustomMenuItem item = new CustomMenuItem(cb);
            item.setHideOnClick(false); // Mantiene el menú abierto al hacer clic
            
            mbMeses.getItems().add(item);
            listaChecksMeses.add(cb);
        }
    }

    private void actualizarFiltroCategorias() {
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        List<String> nombresCategorias = ctrl.getNombreCategorias();
        
        mbCategorias.getItems().clear();
        listaChecksCategorias.clear();

        for (String nombreCat : nombresCategorias) {
            CheckBox cb = new CheckBox(nombreCat);
            cb.setUserData(nombreCat); // Guardamos el String del nombre
            
            CustomMenuItem item = new CustomMenuItem(cb);
            item.setHideOnClick(false);
            
            mbCategorias.getItems().add(item);
            listaChecksCategorias.add(cb);
        }
    }

    // --- Lógica de Filtrado Principal (HU 2.4) ---

    @FXML
    private void handleFiltrar() {
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();

        // 1. Obtener meses seleccionados
        List<Month> mesesSeleccionados = new ArrayList<>();
        for (CheckBox cb : listaChecksMeses) {
            if (cb.isSelected()) mesesSeleccionados.add((Month) cb.getUserData());
        }
        actualizarTextoBoton(mbMeses, "Meses", mesesSeleccionados.size());

        // 2. Obtener categorías seleccionadas
        List<String> categoriasSeleccionadas = new ArrayList<>();
        for (CheckBox cb : listaChecksCategorias) {
            if (cb.isSelected()) categoriasSeleccionadas.add((String) cb.getUserData());
        }
        actualizarTextoBoton(mbCategorias, "Categorías", categoriasSeleccionadas.size());

        // 3. Aplicar predicado compuesto
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();

        List<Gasto> filtrados = ctrl.getGastosPorCondicion(g -> {
            // A. SEGURIDAD: Solo mostrar gastos personales
            if (!ctrl.esGastoPersonal(g)) return false;

            // B. Filtro Rango Fechas
            if (desde != null && g.getFecha().isBefore(desde)) return false;
            if (hasta != null && g.getFecha().isAfter(hasta)) return false;

            // C. Filtro Lista Meses (Si la lista no está vacía)
            if (!mesesSeleccionados.isEmpty()) {
                if (!mesesSeleccionados.contains(g.getFecha().getMonth())) return false;
            }

            // D. Filtro Lista Categorías (Si la lista no está vacía)
            if (!categoriasSeleccionadas.isEmpty()) {
                if (!categoriasSeleccionadas.contains(g.getCategoria().getId())) return false;
            }
            
            return true;
        });

        tablaGastos.getItems().setAll(filtrados);
    }

    private void actualizarTextoBoton(MenuButton btn, String titulo, int seleccionados) {
        if (seleccionados == 0) btn.setText(titulo + " (Todos)");
        else btn.setText(titulo + " (" + seleccionados + ")");
    }

    @FXML
    private void handleLimpiarFiltros() {
        dpDesde.setValue(null);
        dpHasta.setValue(null);
        
        listaChecksMeses.forEach(cb -> cb.setSelected(false));
        mbMeses.setText("Meses (Todos)");
        
        listaChecksCategorias.forEach(cb -> cb.setSelected(false));
        mbCategorias.setText("Categorías (Todas)");
        
        refrescarTabla();
    }

    private void refrescarTabla() {
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        // Por defecto mostramos TODOS los personales
        List<Gasto> personales = ctrl.getGastosPorCondicion(g -> ctrl.esGastoPersonal(g));
        tablaGastos.getItems().setAll(personales);
    }

    // --- Acciones CRUD ---

    @FXML
    private void handleNuevoGasto() {
        Configuracion.getInstancia().getSceneManager().showNuevoGasto();
    }

    @FXML
    private void handleEditarGasto() {
        Gasto seleccionado = tablaGastos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
            if (!ctrl.esGastoPersonal(seleccionado)) {
                UIUtils.mostrarAlerta(AlertType.WARNING, "Acción no permitida", null, "Los gastos de cuentas compartidas no se pueden editar aquí.");
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
            
            if (!ctrl.esGastoPersonal(seleccionado)) {
                UIUtils.mostrarAlerta(AlertType.WARNING, "Acción no permitida", null, "No se pueden eliminar gastos de cuentas compartidas desde esta vista.");
                return;
            }
            
            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar Borrado");
            confirm.setHeaderText(null);
            confirm.setContentText("¿Estás seguro de borrar este gasto?");
            
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                ctrl.eliminarGasto(seleccionado);
            }
        }
    }

    // --- Importación (HU 5.1) ---

    @FXML
    private void handleImportarGastos() {
         FileChooser fileChooser = new FileChooser();
         fileChooser.setTitle("Importar Gastos");
         fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
         
         File file = fileChooser.showOpenDialog(tablaGastos.getScene().getWindow());

         if (file != null) {
             ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
             try {
                 // Llamamos al método que devuelve el número de ignorados
                 int numIgnorados = ctrl.importarGastos(file.getAbsolutePath());
                 
                 if (numIgnorados > 0) {
                     UIUtils.mostrarAlerta(AlertType.WARNING, 
                         "Importación con Observaciones", 
                         "Proceso finalizado", 
                         "Se han cargado los gastos válidos.\nSe ignoraron " + numIgnorados + " registros (duplicados, cuenta inexistente o usuario inválido).");
                 } else {
                     UIUtils.mostrarAlerta(AlertType.INFORMATION, 
                         "Importación Exitosa", 
                         "Éxito", 
                         "Todos los gastos del fichero han sido importados correctamente.");
                 }
             } catch (ImportacionException e) {
                 UIUtils.mostrarAlerta(AlertType.ERROR, "Error de Importación", "Fallo al leer fichero", e.getMessage());
             } catch (Exception e) {
                 e.printStackTrace();
                 UIUtils.mostrarAlerta(AlertType.ERROR, "Error del Sistema", "Error inesperado", e.getMessage());
             }
         }
    }

    // --- Patrón Observador ---

    @Override
    public void actualizar(EventoSistema evento, Object datos) {
        // Ejecutar en el hilo de JavaFX para evitar excepciones gráficas
        Platform.runLater(() -> {
            
            // Si cambian los gastos, refrescamos la tabla
            if (evento == EventoSistema.NUEVO_GASTO || 
                evento == EventoSistema.GASTO_MODIFICADO || 
                evento == EventoSistema.GASTO_ELIMINADO) {
                refrescarTabla();
            }

            // Si hay nuevas categorías (ej: tras importar), actualizamos el filtro
            if (evento == EventoSistema.NUEVA_CATEGORIA) {
                actualizarFiltroCategorias();
            }
        });
    }
}