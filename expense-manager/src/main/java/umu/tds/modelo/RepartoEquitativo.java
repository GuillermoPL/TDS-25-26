package umu.tds.modelo;

import java.util.Map;

public class RepartoEquitativo implements EstrategiaReparto {

	
	public RepartoEquitativo() {}
	
	@Override
	public void calcular(Gasto nuevoGasto, Map<Usuario, Double> saldosActuales) {
		// 1. Obtener los datos básicos del gasto
        double importeTotal = nuevoGasto.getImporte();
        Usuario pagador = nuevoGasto.getPagador();
        
        // 2. Calcular la cuota equitativa (Importe / número de participantes)
        int numParticipantes = saldosActuales.size();
        if (numParticipantes == 0) return; // Evitar división por cero
        
        double cuota = importeTotal / numParticipantes;

        // 3. Actualizar los saldos en el Map
        for (Usuario usuario : saldosActuales.keySet()) {
            double saldoAnterior = saldosActuales.get(usuario);
            
            if (usuario.equals(pagador)) {
                // Al pagador se le suma lo que los demás le deben:
                // (Importe Total - Su propia cuota)
                double loQueLeDeben = importeTotal - cuota;
                saldosActuales.put(usuario, saldoAnterior + loQueLeDeben);
            } else {
                // A los demás se les resta su cuota
                saldosActuales.put(usuario, saldoAnterior - cuota);
            }
        }
	}
	
	@Override
	public boolean esSumaValida() {
		return true;
	}

}
