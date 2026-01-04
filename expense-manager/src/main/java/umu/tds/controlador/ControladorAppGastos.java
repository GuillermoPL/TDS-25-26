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
import umu.tds.adapters.repository.RepositorioAlertas;
import umu.tds.adapters.repository.RepositorioCuentas;
import umu.tds.adapters.repository.RepositorioGastos;
import umu.tds.adapters.repository.exceptions.ElementoExistenteException;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;

import umu.tds.modelo.CuentaCompartida;
import umu.tds.modelo.EstrategiaReparto;
import umu.tds.modelo.FactoriaEstrategia;
import umu.tds.modelo.Gasto;
import umu.tds.modelo.IEstrategiaAlerta;
import umu.tds.modelo.RepartoPorcentual;
import umu.tds.modelo.Alerta;
import umu.tds.modelo.Categoria;
import umu.tds.modelo.EventoSistema;
import umu.tds.vista.IObservador;
import umu.tds.modelo.Usuario;



public class ControladorAppGastos {
    private RepositorioGastos repoGastos;
    private RepositorioCuentas repoCuentas;
    private RepositorioAlertas repoAlertas;
    private Set<String> categoriasPersonalizadas = new HashSet<>();
    private List<IObservador> observadores = new LinkedList<>();
    // Constructor
    public ControladorAppGastos(RepositorioGastos repoGastos, RepositorioCuentas repoCuentas, RepositorioAlertas repoAlertas) {
        this.repoGastos = repoGastos;
        this.repoCuentas = repoCuentas;
        this.repoAlertas = repoAlertas;
    }
	
    public List<CuentaCompartida> getCuentasCompartidas() {
        return repoCuentas.getCuentas();
    }
    
    public List<String> getLoginsUsuarios() {
        return repoCuentas.getUsuarios().stream()
                .map(Usuario::getLogin)
                .collect(Collectors.toList());
    }
    
    public void crearAlerta(double limite, String periodo) {
        try {
            // 1. Crear estrategia mediante la factoría
            IEstrategiaAlerta est = FactoriaEstrategia.getInstancia().crearEstrategiaAlerta(periodo);
            
            // 2. Crear y persistir la alerta
            Alerta nueva = new Alerta(limite, est);
            repoAlertas.addAlerta(nueva);
            
            // 3. Notificar para refrescar la lista en la vista de Alertas
            this.notificarCambio(EventoSistema.NUEVA_ALERTA, nueva);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarAlerta(Alerta alerta) {
        try {
            repoAlertas.removeAlerta(alerta);
            this.notificarCambio(EventoSistema.NUEVA_ALERTA, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Alerta> getAlertas() {
        return repoAlertas.getAlertas();
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
	public void crearCuentaCompartida(String nombre, String tipoEstrategia, Map<String, Double> datosVista) 
	        throws ElementoExistenteException, ErrorPersistenciaException {
	    
	    Map<Usuario, Double> porcentajesUsuarios = null;
	    Set<Usuario> usuarios = new HashSet<>();
	    
	    // 1. Obtenemos los objetos Usuario y preparamos los datos
	    for (String login : datosVista.keySet()) {
	        Usuario u = repoCuentas.getUsuario(login);
	        if (u == null) {
	            u = new Usuario(login); 
	            repoCuentas.addUsuario(u); 
	        }
	        usuarios.add(u); 
	        
	        if (tipoEstrategia.equalsIgnoreCase("PORCENTUAL")) {
	            if (porcentajesUsuarios == null) porcentajesUsuarios = new HashMap<>();
	            porcentajesUsuarios.put(u, datosVista.get(login));
	        }
	    }

	    // 2. Creamos la estrategia usando la factoría
	    EstrategiaReparto estrategia = FactoriaEstrategia.getInstancia()
	                                    .crearEstrategia(tipoEstrategia, porcentajesUsuarios);

	    // 3. Delegamos totalmente en el objeto estrategia
	    if (!estrategia.esSumaValida()) {
	        throw new IllegalArgumentException("La configuración del reparto no es válida (los porcentajes deben sumar 100%).");
	    }

	    // 4. Creamos la cuenta y persistimos
	    CuentaCompartida nuevaCuenta = new CuentaCompartida(nombre, estrategia, usuarios);
	    repoCuentas.addCuenta(nuevaCuenta);
	    
	    // 5. Notificamos el cambio para actualizar la UI
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
	
	private void verificarAlertas() {
	    List<Gasto> todos = repoGastos.getGastos();
	    for (Alerta alerta : repoAlertas.getAlertas()) {
	        // Usamos el método que ya tienes en tu clase Alerta
	        if (alerta.verificarSiSuperada(todos)) {
	            // Si se supera, disparamos el evento global
	            this.notificarCambio(EventoSistema.ALERTA_DISPARADA, alerta);
	        }
	    }
	}
	
	public List<String> getNombreCategorias() {
	    Set<String> nombres = repoGastos.getGastos().stream()
	            .map(g -> g.getCategoria().toString())
	            .collect(Collectors.toSet());
	    nombres.addAll(categoriasPersonalizadas);
	    
	    return nombres.stream().sorted().collect(Collectors.toList());
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
	    verificarAlertas();
	}
	public void registrarCategoria(String nombre) throws ElementoExistenteException {
	    List<String> existentes = getNombreCategorias();
	    if (existentes.contains(nombre)) {
	        throw new ElementoExistenteException("La categoría '" + nombre + "' ya existe.");
	    }

	    categoriasPersonalizadas.add(nombre);
	    
	    // Notificamos el cambio para que las vistas se enteren
	    this.notificarCambio(EventoSistema.NUEVA_CATEGORIA, nombre);
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
	        verificarAlertas();
	    } catch (ErrorPersistenciaException e) {
	        e.printStackTrace();
	    }
	}
}
