package umu.tds.vista;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import umu.tds.Configuracion;
import umu.tds.controlador.ControladorAppGastos;
import umu.tds.modelo.EventoSistema;
import umu.tds.modelo.Gasto;

public class EstadisticasViewController implements IObservador {

    @FXML private PieChart graficoCategorias;
    @FXML private Label lblTotal;

    @FXML
    public void initialize() {
        // 1. Nos registramos en el controlador de negocio para recibir cambios
        Configuracion.getInstancia().getControladorAppGastos().registrarObservador(this);
        
        // 2. Cargamos los datos iniciales
        refrescarGrafico();
    }

    private void refrescarGrafico() {
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
    
        Map<String, Double> totalesPorCategoria = ctrl.getTotalesPorCategoria();
        double totalGlobal = ctrl.getTotalGlobal();
    
        // Construimos las porciones con la etiqueta enriquecida
        graficoCategorias.getData().clear();
        totalesPorCategoria.forEach((nombre, total) -> {
            String etiqueta = String.format("%s — %.2f €", nombre, total);
            graficoCategorias.getData().add(new PieChart.Data(etiqueta, total));
        });
    
        lblTotal.setText(String.format("Total acumulado: %.2f €", totalGlobal));
    }

    @Override
    public void actualizar(EventoSistema evento, Object datos) {
        // Si hay cualquier cambio en los gastos, el gráfico se actualiza solo
        if (evento == EventoSistema.NUEVO_GASTO || 
            evento == EventoSistema.GASTO_ELIMINADO || 
            evento == EventoSistema.GASTO_MODIFICADO) {
            
            Platform.runLater(() -> refrescarGrafico());
        }
    }
}