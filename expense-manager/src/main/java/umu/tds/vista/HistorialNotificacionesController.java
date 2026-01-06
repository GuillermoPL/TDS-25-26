package umu.tds.vista;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import umu.tds.Configuracion;
import umu.tds.modelo.Notificacion;

public class HistorialNotificacionesController {
    @FXML private ListView<Notificacion> listaHistorial;

    @FXML
    public void initialize() {
        // Configuramos cómo se ve cada fila
        listaHistorial.setCellFactory(lv -> new ListCell<Notificacion>() {
            @Override
            protected void updateItem(Notificacion item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("[" + item.getFecha() + "] " + item.getMensaje());
                }
            }
        });

        // Cargamos los datos desde el repositorio
        listaHistorial.getItems().setAll(
            Configuracion.getInstancia().getControladorAppGastos().getHistorialNotificaciones()
        );
    }
}