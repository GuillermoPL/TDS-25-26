package umu.tds.modelo;

import java.util.HashMap;
import java.util.Map;

public class CuentaCompartida {
	
	private Map<String, Double> saldosPorUsuario;
	private EstrategiaReparto estrategia;
	
	public CuentaCompartida(EstrategiaReparto reparto) {
		this.saldosPorUsuario = new HashMap<String, Double>();
		this.estrategia = estrategia;
	}
	
	public Map<String, Double> getSaldosPorUsuario(){
		return new HashMap<String, Double>(saldosPorUsuario);
	}
	
	public EstrategiaReparto getEstrategia() {
		return estrategia;
	}
}
