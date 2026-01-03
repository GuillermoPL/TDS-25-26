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
        List<Gasto> todosLosGastos = ctrl.getGastosPorCondicion(g -> true);

        // --- LÓGICA DE STREAMS (Clave en TDS) ---
        // Agrupamos los gastos por el nombre de su categoría y sumamos los importes
        Map<String, Double> totalesPorCategoria = todosLosGastos.stream()
                .collect(Collectors.groupingBy(
                        g -> g.getCategoria().toString(), // Usamos el nombre de la categoría
                        Collectors.summingDouble(Gasto::getImporte)
                ));

        // Calculamos el total general para el label
        double totalGlobal = todosLosGastos.stream()
                .mapToDouble(Gasto::getImporte)
                .sum();

        // Actualizamos la UI
        graficoCategorias.getData().clear();
        totalesPorCategoria.forEach((nombre, total) -> {
            graficoCategorias.getData().add(new PieChart.Data(nombre, total));
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