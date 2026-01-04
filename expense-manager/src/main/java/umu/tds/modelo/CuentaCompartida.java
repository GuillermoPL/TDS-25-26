package umu.tds.modelo;

import java.util.HashMap;

import java.util.Map;
import java.util.Set;

public class CuentaCompartida {
	
	private Map<Usuario, Double> saldosPorUsuario;
	private EstrategiaReparto estrategia;
	
	public CuentaCompartida() {}
	
	public CuentaCompartida(EstrategiaReparto estrategia, Set<Usuario> usuarios) {
		this.estrategia = estrategia;
		this.saldosPorUsuario = new HashMap<Usuario, Double>();
		for(Usuario u : usuarios) {
			saldosPorUsuario.put(u, 0.0);
		}
	}
	
	public Map<Usuario, Double> getSaldosPorUsuario(){
		return saldosPorUsuario;
	}
	
	public void setSaldosPorUsuario(Map<Usuario, Double> saldosPorUsuario) {
		this.saldosPorUsuario = saldosPorUsuario;
	}

	public void setEstrategia(EstrategiaReparto estrategia) {
		this.estrategia = estrategia;
	}

	public EstrategiaReparto getEstrategia() {
		return estrategia;
	}
	
	public void calcularGasto(Gasto gasto) {
		estrategia.calcular(gasto, saldosPorUsuario);
	}
}
