package umu.tds.modelo;

import java.util.HashMap;

import java.util.Map;
import java.util.Set;

public class CuentaCompartida {
	
	private Map<Usuario, Double> saldosPorUsuario;
	private EstrategiaReparto estrategia;
	
	public CuentaCompartida(EstrategiaReparto estrategia, Set<Usuario> usuarios) {
		this.estrategia = estrategia;
		this.saldosPorUsuario = new HashMap<Usuario, Double>();
		for(Usuario u : usuarios) {
			saldosPorUsuario.put(u, 0.0);
		}
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
