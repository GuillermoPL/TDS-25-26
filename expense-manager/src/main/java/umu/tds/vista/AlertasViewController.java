package umu.tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import umu.tds.Configuracion;
import umu.tds.modelo.Alerta;
import umu.tds.modelo.EventoSistema;
import java.util.Optional;

public class AlertasViewController implements IObservador {

    @FXML private TextField txtLimite;
    @FXML private ComboBox<String> cbPeriodo;
    @FXML private ListView<Alerta> listaAlertas;

    @FXML
    public void initialize() {
        Configuracion.getInstancia().getControladorAppGastos().registrarObservador(this);
        
        cbPeriodo.getItems().addAll("Semanal", "Mensual");
        cbPeriodo.setValue("Mensual");
        
        refrescarLista();
    }

    @FXML
    private void handleCrearAlerta() {
        String strLimite = txtLimite.getText().trim();
        String periodo = cbPeriodo.getValue();

        try {
            double limite = Double.parseDouble(strLimite);
            // El controlador de negocio se encargará de crear la estrategia adecuada
            Configuracion.getInstancia().getControladorAppGastos().crearAlerta(limite, periodo);
            txtLimite.clear();
            refrescarLista();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "El límite debe ser un número válido.").showAndWait();
        }
    }

    @FXML
    private void handleEliminarAlerta() {
        Alerta seleccionada = listaAlertas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;
        
        Configuracion.getInstancia().getControladorAppGastos().eliminarAlerta(seleccionada);
        refrescarLista();
    }

    private void refrescarLista() {
        listaAlertas.getItems().setAll(
            Configuracion.getInstancia().getControladorAppGastos().getAlertas()
        );
    }

    @Override
    public void actualizar(EventoSistema evento, Object datos) {
        // Podríamos reaccionar si las alertas cambian externamente
    }
}