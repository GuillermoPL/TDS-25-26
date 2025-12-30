package umu.tds.adapters.repository.impl;

import java.util.List;


import umu.tds.adapters.repository.RepositorioGastos;
import umu.tds.modelo.Gasto;

public class RepositorioGastosJSON extends RepositorioGastos {

	private RepositorioGastosJSON() {
		super();
	}
	
	public static RepositorioGastos getInstancia() {
		if(unicaInstancia == null) {
			unicaInstancia = new RepositorioGastosJSON();
		}
		return unicaInstancia;
	}
	@Override
	public List<Gasto> getGastos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addGasto(Gasto gasto) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeGasto(Gasto gasto) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateGasto(Gasto gasto) {
		// TODO Auto-generated method stub

	}

}
