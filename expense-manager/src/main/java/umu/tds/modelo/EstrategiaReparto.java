package umu.tds.modelo;

import java.util.Map;

public interface EstrategiaReparto {
	public void calcular(Gasto nuevoGasto, Map<Usuario, Double> saldosActuales);
}
