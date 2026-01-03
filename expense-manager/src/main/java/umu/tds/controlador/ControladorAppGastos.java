package umu.tds.controlador;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import umu.tds.Configuracion;
import umu.tds.adapters.repository.RepositorioGastos;
import umu.tds.adapters.repository.exceptions.ElementoExistenteException;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;
import umu.tds.modelo.CuentaCompartida;
import umu.tds.modelo.EstrategiaReparto;
import umu.tds.modelo.FactoriaEstrategia;
import umu.tds.modelo.Gasto;
import umu.tds.modelo.Usuario;

//importacion de las clases del paquete modelo. asi como de utilidades java.



//PATRON SINGLETON
public class ControladorAppGastos {
	private static ControladorAppGastos unicaInstancia;
    private RepositorioGastos repositorio;
    
    // Constructor privado
    private ControladorAppGastos() {
        this.repositorio = Configuracion.getInstancia().getRepositorioGastos();
    }
	
	public static ControladorAppGastos getInstancia() {
        if (unicaInstancia == null) {
            unicaInstancia = new ControladorAppGastos();
        }
        return unicaInstancia;
    }
	
	//Devuelve gastos que pasan un filtro o varios
	public List<Gasto> getGastosPorCondicion(Predicate<Gasto> condicion) {
		List<Gasto> gastos = repositorio.getGastos();
		return gastos.stream()
				.filter(g -> condicion.test(g))
				.collect(Collectors.toList());
	}
	
	//Crea instancia de CuentaCompartida
	public void crearCuentaCompartida(String tipoEstrategia, Map<String, Double> datosVista) {
		Map<Usuario, Double> porcentajesUsuarios = null;
	    Set<Usuario> usuarios = new HashSet<>();
	    
	    // 1. Obtenemos los objetos Usuario a partir de los logins de la vista
	    for (String login : datosVista.keySet()) {
	    	Usuario u = repositorio.getUsuario(login);
	    	if (u == null) {
	            u = new Usuario(login); // Asumiendo que el constructor de Usuario recibe el nombre
	            repositorio.addUsuario(u); // Registramos el nuevo usuario en el repositorio
	        }
	        usuarios.add(u); // Esto debe hacerse siempre para llenar el Set
	        
	        // 2. Solo si es porcentual, también llenamos el mapa de porcentajes
	        if (tipoEstrategia.equalsIgnoreCase("PORCENTUAL")) {
	            if (porcentajesUsuarios == null) porcentajesUsuarios = new HashMap<>();
	            porcentajesUsuarios.put(u, datosVista.get(login));
	        }
	    }

	    // 3. Usamos la factoría Singleton para obtener la estrategia
	    EstrategiaReparto estrategia = FactoriaEstrategia.getInstancia()
	                                    .crearEstrategia(tipoEstrategia, porcentajesUsuarios);

	    // 4. Creamos e inscribimos la cuenta pasándole el Set de usuarios
	    CuentaCompartida nuevaCuenta = new CuentaCompartida(estrategia, usuarios);
	    repositorio.addCuenta(nuevaCuenta);
	}
	
	//Registra un gasto en una CuentaCompartida
	public void añadirGastoACuentaCompartida(Gasto gasto, CuentaCompartida cuenta) {
	    // 1. Delegamos el cálculo a la cuenta (que ya tiene su estrategia)
	    // Esto actualizará los saldos según la lógica de image_fdc446 o image_fdc465
	    cuenta.calcularGasto(gasto);

	    // 2. Guardamos el gasto en el repositorio para que persista
	    try {
			repositorio.addGasto(gasto);
		} catch (ElementoExistenteException e) {
			e.printStackTrace();
		} catch (ErrorPersistenciaException e) {
			e.printStackTrace();
		}
	}
}
