package umu.tds;

import umu.tds.adapters.repository.RepositorioCuentas;
import umu.tds.adapters.repository.RepositorioGastos;
import umu.tds.adapters.repository.impl.RepositorioCuentasJSON;
import umu.tds.adapters.repository.impl.RepositorioGastosJSON;

public class Configuracion {
    private static Configuracion unicaInstancia;
    private RepositorioGastos repoGastos; // Guardamos la interfaz
    private RepositorioCuentas repoCuentas;

    // Constructor Privado
    private Configuracion() {
        // Aquí decidimos la tecnología concreta (JSON)
        this.repoGastos = new RepositorioGastosJSON();
        this.repoCuentas = new RepositorioCuentasJSON();
    }

    public static Configuracion getInstancia() {
        if (unicaInstancia == null) unicaInstancia = new Configuracion();
        return unicaInstancia;
    }

    // Método para que los demás pidan la herramienta
    public RepositorioGastos getRepositorioGastos() {
        return repoGastos;
    }
    
    public RepositorioCuentas getRepositorioCuentas() {
		return repoCuentas;
	}

	public String getRutaGastos() {
		return "/data/gastos.json";
	}

	public String getRutaAlertas() {
		return "/data/alertas.json";
	}

	public String getRutaCuentas() {
		return "/data/cuentas.json";
	}
	
	public String getRutaUsuarios() {
		return "/data/usuarios.json";
	}

	
}