package umu.tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import umu.tds.Configuracion; // Importante
import umu.tds.controlador.ControladorAppGastos;
import umu.tds.modelo.EventoSistema;
import umu.tds.adapters.repository.exceptions.ElementoExistenteException;

public class CategoriasViewController implements IObservador {
    @FXML private TextField txtNombreCategoria;
    @FXML private ListView<String> listaCategorias;

    @FXML
    public void initialize() {
        // CAMBIO: Acceso mediante Configuracion
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        ctrl.registrarObservador(this);
        refrescarLista();
    }

    @FXML
    private void handleGuardar() {
        String nombre = txtNombreCategoria.getText().trim();
        if (nombre.isEmpty()) return;

        try {
            // CAMBIO: Acceso mediante Configuracion
            Configuracion.getInstancia().getControladorAppGastos().registrarCategoria(nombre); 
            txtNombreCategoria.clear();
        } catch (ElementoExistenteException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    private void refrescarLista() {
        // CAMBIO: Acceso mediante Configuracion
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        listaCategorias.getItems().setAll(ctrl.getNombreCategorias());
    }

    @Override
    public void actualizar(EventoSistema evento, Object datos) {
        if (evento == EventoSistema.NUEVA_CATEGORIA) {
            // Importante para que se vea el cambio inmediatamente
            javafx.application.Platform.runLater(() -> refrescarLista());
        }
    }
}