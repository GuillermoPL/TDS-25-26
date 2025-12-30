package umu.tds;

import umu.tds.controlador.ControladorAppGastos;

public class ConfiguracionImpl extends Configuracion {

	private ControladorAppGastos controlador;

	public ConfiguracionImpl() {
		this.controlador = ControladorAppGastos.getInstancia();
	}
	
	@Override
	public ControladorAppGastos getControladorGastos() {
		return controlador;
	}
		
	@Override
	public String getRutaFicheroGastos() {
		return "/data/gastos.json";
	}


}
