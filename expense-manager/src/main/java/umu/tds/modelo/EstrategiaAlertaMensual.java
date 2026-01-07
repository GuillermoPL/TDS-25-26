package umu.tds.modelo;

import java.time.LocalDate;
import java.util.List;

public class EstrategiaAlertaMensual implements IEstrategiaAlerta {

    // Constructor vacío (Jackson)
    public EstrategiaAlertaMensual() {}

    @Override
    public boolean verificar(List<Gasto> gastos, double limite) {
        if (gastos == null || gastos.isEmpty()) return false;

        LocalDate ahora = LocalDate.now();

        // 1. filter: Nos quedamos solo con los de este mes y este año.
        // 2. mapToDouble: Extraemos el importe directamente.
        // 3. sum: Sumamos.
        // Todo en una sola pasada, sin listas intermedias.
        
        double gastoAcumulado = gastos.stream()
            .filter(g -> g.getFecha() != null)
            .filter(g -> g.getFecha().getMonth() == ahora.getMonth() && 
                         g.getFecha().getYear() == ahora.getYear())
            .mapToDouble(Gasto::getImporte)
            .sum();
        
        return gastoAcumulado > limite;
    }
}