package umu.tds.modelo;

import java.util.HashMap;

import java.util.Map;
import java.util.Set;

public class CuentaCompartida {
	private String nombre;
	private Map<Usuario, Double> saldosPorUsuario;
	private EstrategiaReparto estrategia;
	
	public CuentaCompartida() {}
	
	public CuentaCompartida(String nombre, EstrategiaReparto estrategia, Set<Usuario> usuarios) {
		this.nombre = nombre;
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

	@Override
	public String toString() {
		return this.nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	

}
