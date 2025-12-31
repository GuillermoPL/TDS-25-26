package umu.tds;

import umu.tds.adapters.repository.RepositorioGastos;
import umu.tds.adapters.repository.impl.RepositorioGastosJSON;

public class Configuracion {
    private static Configuracion unicaInstancia;
    private RepositorioGastos repoGastos; // Guardamos la interfaz

    // Constructor Privado
    private Configuracion() {
        // Aquí decidimos la tecnología concreta (JSON)
        this.repoGastos = new RepositorioGastosJSON();
    }

    public static Configuracion getInstancia() {
        if (unicaInstancia == null) unicaInstancia = new Configuracion();
        return unicaInstancia;
    }

    // Método para que los demás pidan la herramienta
    public RepositorioGastos getRepositorioGastos() {
        return repoGastos;
    }

	public String getRutaGastos() {
		return "/data/gastos.json";
	}
}