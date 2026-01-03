package umu.tds.controlador;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.time.LocalDate;

import umu.tds.Configuracion;
import umu.tds.adapters.repository.RepositorioCuentas;
import umu.tds.adapters.repository.RepositorioGastos;
import umu.tds.adapters.repository.exceptions.ElementoExistenteException;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;

import umu.tds.modelo.CuentaCompartida;
import umu.tds.modelo.EstrategiaReparto;
import umu.tds.modelo.FactoriaEstrategia;
import umu.tds.modelo.Gasto;
import umu.tds.modelo.Categoria;
import umu.tds.modelo.EventoSistema;
import umu.tds.vista.IObservador;
import umu.tds.modelo.Usuario;



public class ControladorAppGastos {
    private RepositorioGastos repoGastos;
    private RepositorioCuentas repoCuentas;
    private List<IObservador> observadores = new LinkedList<>();
    // Constructor
    public ControladorAppGastos(RepositorioGastos repoGastos, RepositorioCuentas repoCuentas) {
        this.repoGastos = repoGastos;
        this.repoCuentas = repoCuentas;
    }
	
	
	//gestion de la lista
	// Métodos para gestionar la lista
    public void registrarObservador(IObservador obs) {
        observadores.add(obs);
    }

    public void eliminarObservador(IObservador obs) {
        observadores.remove(obs);
    }
	//Devuelve gastos que pasan un filtro o varios
	public List<Gasto> getGastosPorCondicion(Predicate<Gasto> condicion) {
		List<Gasto> gastos = repoGastos.getGastos();
		return gastos.stream()
				.filter(g -> condicion.test(g))
				.collect(Collectors.toList());
	}
	
	// Añadimos las excepciones a la firma del método
	public void crearCuentaCompartida(String tipoEstrategia, Map<String, Double> datosVista) 
	        throws ElementoExistenteException, ErrorPersistenciaException {
	    
	    Map<Usuario, Double> porcentajesUsuarios = null;
	    Set<Usuario> usuarios = new HashSet<>();
	    
	    // 1. Obtenemos los objetos Usuario a partir de los logins de la vista
	    for (String login : datosVista.keySet()) {
	        Usuario u = repoCuentas.getUsuario(login);
	        if (u == null) {
	            u = new Usuario(login); 
	            // Aquí ya no marcará error porque el método propaga la excepción
	            repoCuentas.addUsuario(u); 
	        }
	        usuarios.add(u); 
	        
	        // 2. Solo si es porcentual, también llenamos el mapa de porcentajes
	        if (tipoEstrategia.equalsIgnoreCase("PORCENTUAL")) {
	            if (porcentajesUsuarios == null) porcentajesUsuarios = new HashMap<>();
	            porcentajesUsuarios.put(u, datosVista.get(login));
	        }
	    }

	    // 3. Usamos la factoría para obtener la estrategia de reparto (Patrón Estrategia)
	    EstrategiaReparto estrategia = FactoriaEstrategia.getInstancia()
	                                    .crearEstrategia(tipoEstrategia, porcentajesUsuarios);

	    // 4. Creamos e inscribimos la cuenta
	    CuentaCompartida nuevaCuenta = new CuentaCompartida(estrategia, usuarios);
	    repoCuentas.addCuenta(nuevaCuenta);
	    
	    // 5. IMPORTANTE: Notificar a los observadores que hay una nueva cuenta
	    // para que la UI se actualice (HU 4.1)
	    this.notificarCambio(EventoSistema.SALDO_ACTUALIZADO, nuevaCuenta);
	}
	
	//Registra un gasto en una CuentaCompartida
	public void añadirGastoACuentaCompartida(Gasto gasto, CuentaCompartida cuenta) {
	    // 1. Delegamos el cálculo a la cuenta (que ya tiene su estrategia)
	    cuenta.calcularGasto(gasto);

	    // 2. Guardamos el gasto en el repositorio para que persista
	    try {
			repoGastos.addGasto(gasto);
		} catch (ElementoExistenteException e) {
			e.printStackTrace();
		} catch (ErrorPersistenciaException e) {
			e.printStackTrace();
		}   
	}
	
	public List<String> getNombreCategorias() {
	    return repoGastos.getGastos().stream()
	            .map(gasto -> gasto.getCategoria().toString()) // Usa tu toString() que devuelve el id
	            .distinct()
	            .sorted()
	            .collect(Collectors.toList());
	}
	
	private void notificarCambio(EventoSistema evento, Object datos) {
        observadores.forEach(obs -> obs.actualizar(evento, datos));
    }
	public void registrarGasto(double importe, LocalDate fecha, String nombreCat) {
	    try {
	        // 1. Obtenemos el usuario de la sesión (Imprescindible para el modelo)
	        Usuario pagador = ControladorSesion.getInstancia().getUsuarioActual();
	        
	        if (pagador == null) {
	            throw new RuntimeException("Error: No hay una sesión de usuario activa.");
	        }

	        // 2. Preparamos los datos
	        String id = "G-" + System.currentTimeMillis();
	        Categoria cat = new Categoria(nombreCat);
	        
	        // 3. Creamos el objeto con su pagador real
	        Gasto nuevo = new Gasto(id, importe, fecha, cat, pagador); 

	        // 4. Persistencia
	        repoGastos.addGasto(nuevo);

	        // 5. Notificación (Magia del patrón Observador)
	        this.notificarCambio(EventoSistema.NUEVO_GASTO, nuevo);
	        
	    } catch (umu.tds.adapters.repository.exceptions.ElementoExistenteException e) {
	        System.err.println("Error: El ID del gasto ya existe.");
	        e.printStackTrace();
	    } catch (umu.tds.adapters.repository.exceptions.ErrorPersistenciaException e) {
	        System.err.println("Error crítico al guardar en el archivo JSON.");
	        e.printStackTrace();
	    } catch (Exception e) {
	        System.err.println("Error inesperado: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	public void registrarCategoria(String nombre) throws ElementoExistenteException {
	    // 1. Validar si ya existe (Criterio de Aceptación HU 1.2) 
	    List<String> existentes = getNombreCategorias();
	    if (existentes.contains(nombre)) {
	        throw new ElementoExistenteException("La categoría '" + nombre + "' ya existe.");
	    }

	    // 2. Crear y persistir
	    // Nota: Aquí podrías añadir un método addCategoria a tu RepositorioGastos 
	    // o simplemente crear un gasto ficticio/inicial para que Jackson la registre
	    Categoria nueva = new Categoria(nombre);
	    
	    // 3. Notificar a las vistas para que actualicen sus ComboBox
	    this.notificarCambio(EventoSistema.NUEVA_CATEGORIA, nueva);
	}
	public void eliminarGasto(Gasto gasto) {
	    try {
	        repoGastos.removeGasto(gasto); 
	        this.notificarCambio(EventoSistema.GASTO_ELIMINADO, gasto); 
	    } catch (ErrorPersistenciaException e) {
	        e.printStackTrace();
	    }
	}

	public void modificarGasto(Gasto gasto) {
	    try {
	        // El objeto ya viene modificado de la vista (gracias al setGasto del controller)
	        repoGastos.updateGasto(gasto); 
	        this.notificarCambio(EventoSistema.GASTO_MODIFICADO, gasto);
	    } catch (ErrorPersistenciaException e) {
	        e.printStackTrace();
	    }
	}
}
