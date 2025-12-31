package umu.tds.modelo;

import java.util.HashMap;

import java.util.Map;

public class CuentaCompartida {
	
	private Map<Usuario, Double> saldosPorUsuario;
	private EstrategiaReparto estrategia;
	
	public CuentaCompartida(EstrategiaReparto estrategia) {
		this.saldosPorUsuario = new HashMap<Usuario, Double>();
		this.estrategia = estrategia;
	}
	
	public Map<Usuario, Double> getSaldosPorUsuario(){
		return new HashMap<Usuario, Double>(saldosPorUsuario);
	}
	
	public EstrategiaReparto getEstrategia() {
		return estrategia;
	}
	
	public void calcularGasto(Gasto gasto) {
		estrategia.calcular(gasto, saldosPorUsuario);
	}
}
