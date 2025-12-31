package umu.tds.modelo;

import java.util.Map;

public class RepartoPorcentual implements EstrategiaReparto {
	private Map<Usuario, Double> porcentajes;
	
	public RepartoPorcentual(Map<Usuario, Double> porcentajes) {
		this.porcentajes = porcentajes;
	}
	@Override
	public void calcular(Gasto nuevoGasto, Map<Usuario, Double> saldosActuales) {
		double importeTotal = nuevoGasto.getImporte();
        Usuario pagador = nuevoGasto.getPagador();

        for (Usuario usuario : saldosActuales.keySet()) {
            // Obtenemos el porcentaje del usuario (ej: 30.0)
            double porcentaje = porcentajes.getOrDefault(usuario, 0.0);
            // Calculamos su deuda real: (Importe * Porcentaje / 100)
            double cuotaCorrespondiente = (importeTotal * porcentaje) / 100.0;
            
            double saldoAnterior = saldosActuales.get(usuario);

            if (usuario.equals(pagador)) {
                // El pagador recupera lo que pagó de más: 
                // Total pagado menos su propia parte proporcional
                double loQueLeDeben = importeTotal - cuotaCorrespondiente;
                saldosActuales.put(usuario, saldoAnterior + loQueLeDeben);
            } else {
                // A los demás se les resta la parte que les correspondía pagar
                saldosActuales.put(usuario, saldoAnterior - cuotaCorrespondiente);
            }
        }

	}

}
