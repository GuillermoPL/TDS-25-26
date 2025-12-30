package umu.tds.adapters.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import umu.tds.modelo.*;

public abstract class RepositorioGastos {
	protected static RepositorioGastos unicaInstancia = null;
	private List<Gasto> gastos;
	private List<Usuario> usuarios;
	private List<Categoria> categorias;
	private List<CuentaCompartida> cuentas;
	
	protected RepositorioGastos() {
		this.gastos = getGastos();
		this.usuarios = new LinkedList<Usuario>();
		this.categorias = new LinkedList<Categoria>();
		this.cuentas = new LinkedList<CuentaCompartida>();
	}
	
	public static RepositorioGastos getInstancia() {
		return unicaInstancia;
	}
	
	public void agregarGasto(Gasto gasto) {
		//TODO
	}
	
	public void modificarGasto(Gasto gasto) {
		//TODO
	}
	
	public void eliminarGasto(String gasto) {
		//TODO
	}
	
	public List<Gasto> getGastos(Predicate<Gasto> condicion){
		List<Gasto> resultado = new LinkedList<Gasto>();
		for(Gasto gasto: gastos) {
			if(condicion.test(gasto)) {
				resultado.add(gasto);
			}
		}
		return resultado;
	}
	
	public abstract List<Gasto> getGastos();
	public abstract void addGasto(Gasto gasto);
	public abstract void removeGasto(Gasto gasto);
	public abstract void updateGasto(Gasto gasto); 
}
