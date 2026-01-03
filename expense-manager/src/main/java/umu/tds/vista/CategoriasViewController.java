package umu.tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import umu.tds.controlador.ControladorAppGastos;
import umu.tds.modelo.EventoSistema;
import umu.tds.adapters.repository.exceptions.ElementoExistenteException;

public class CategoriasViewController implements IObservador {
    @FXML private TextField txtNombreCategoria;
    @FXML private ListView<String> listaCategorias;

    @FXML
    public void initialize() {
        ControladorAppGastos.getInstancia().registrarObservador(this);
        refrescarLista();
    }

    @FXML
    private void handleGuardar() {
        String nombre = txtNombreCategoria.getText().trim();
        if (nombre.isEmpty()) return;

        try {
            ControladorAppGastos.getInstancia().registrarCategoria(nombre); // 
            txtNombreCategoria.clear();
        } catch (ElementoExistenteException e) {
            // Cumplimos el criterio de error por duplicado 
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    private void refrescarLista() {
        listaCategorias.getItems().setAll(ControladorAppGastos.getInstancia().getNombreCategorias());
    }

    @Override
    public void actualizar(EventoSistema evento, Object datos) {
        if (evento == EventoSistema.NUEVA_CATEGORIA) {
            refrescarLista();
        }
    }
}