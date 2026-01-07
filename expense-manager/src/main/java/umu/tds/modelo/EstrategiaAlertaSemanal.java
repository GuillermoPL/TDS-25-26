package umu.tds.modelo;

import java.util.List;

public class EstrategiaAlertaSemanal implements IEstrategiaAlerta {
    
    // Constructor vacío (Jackson)
    public EstrategiaAlertaSemanal() {}

    @Override
    public boolean verificar(List<Gasto> gastos, double limite) {
        if (gastos == null || gastos.isEmpty()) return false;

        // OPTIMIZACIÓN: Stream en una sola pasada.
        double gastoAcumulado = gastos.stream()
                .filter(g -> g != null) // Robustez extra
                .filter(Gasto::realizadoEnUltimaSemana) // Delegamos la lógica temporal a Gasto
                .mapToDouble(Gasto::getImporte) // Mapeamos a primitivo double
                .sum(); // Suma eficiente
        
        return gastoAcumulado > limite;
    }
    
    @Override
    public String toString() {
        return "Semanal";
    }
}