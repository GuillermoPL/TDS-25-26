package umu.tds.modelo;

import java.util.Map;

public class RepartoPorcentual implements EstrategiaReparto {
	private Map<Usuario, Double> porcentajes;
	
	public RepartoPorcentual(Map<Usuario, Double> porcentajes) {
		this.porcentajes = porcentajes;
	}
	@Override
	public void calcular(Gasto nuevoGasto, Map<Usuario, Double> saldosActuales) {
		// TODO Auto-generated method stub

	}

}
