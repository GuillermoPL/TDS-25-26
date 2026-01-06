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
import umu.tds.modelo.Notificacion;
import umu.tds.modelo.Alerta;
import umu.tds.modelo.Categoria;
import umu.tds.modelo.EventoSistema;
import umu.tds.vista.IObservador;
import umu.tds.modelo.Usuario;
import umu.tds.modelo.importacion.FactoriaImportadores;
import umu.tds.modelo.importacion.ImportadorGastos;
import umu.tds.modelo.importacion.exceptions.ImportacionException;



public class ControladorAppGastos {
    private RepositorioGastos repoGastos;
    private RepositorioCuentas repoCuentas;
    private RepositorioAlertas repoAlertas;
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
    
    private void inicializarCategoriasPredefinidas() throws ErrorPersistenciaException {
        String[] predefinidas = {"Alimentación", "Transporte", "Entretenimiento"};
        
        for (String nombre : predefinidas) {
            try {
                // Intentamos registrarlas. Si ya existen, nuestro método lanzará ElementoExistenteException
                this.registrarCategoria(nombre);
            } catch (ElementoExistenteException e) {
                // No hacemos nada, ya existe en el sistema
            }
        }
    }
    
    public void inicializarDatos() throws ErrorPersistenciaException {
        inicializarCategoriasPredefinidas();
    }
    
    public void crearAlerta(double limite, String periodo, String nombreCategoria) {
        try {
            // 1. Crear estrategia mediante la factoría
            IEstrategiaAlerta est = FactoriaEstrategia.getInstancia().crearEstrategiaAlerta(periodo);
            
            // 2. Determinar si hay categoría específica
            Alerta nueva;
            if (nombreCategoria == null) {
                nueva = new Alerta(limite, est);
            } else {
                Categoria cat = new Categoria(nombreCategoria);
                nueva = new Alerta(limite, est, cat);
            }
            
            // 3. Persistir y notificar
            repoAlertas.addAlerta(nueva);
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
    	// Primero eliminamos si hay alguna instancia de la vista que vamos a registrar, para que
    	// no se guarden duplicados y por tanto no se notifique muchas veces a la misma vista.
    	observadores.removeIf(o -> o.getClass().equals(obs.getClass()));
    	
        observadores.add(obs);
    }

    public void eliminarObservador(IObservador obs) {
        observadores.remove(obs);
    }
	//Devuelve gastos que pasan un filtro o varios
	public List<Gasto> getGastosPorCondicion(Predicate<Gasto> condicion) {
		List<Gasto> gastos = repoGastos.getGastos();
		return gastos.stream()
				.filter(condicion)
				.collect(Collectors.toList());
	}
	
	// Añadimos las excepciones a la firma del método
	public void crearCuentaCompartida(String nombre, String tipoEstrategia, Map<String, Double> datosVista) 
	        throws ElementoExistenteException, ErrorPersistenciaException {
	    
	    Map<Usuario, Double> porcentajesUsuarios = null;
	    Set<Usuario> usuarios = new HashSet<>();
	    
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

	    EstrategiaReparto estrategia = FactoriaEstrategia.getInstancia()
	                                    .crearEstrategia(tipoEstrategia, porcentajesUsuarios, usuarios);

	    if (!estrategia.esSumaValida()) {
	        throw new IllegalArgumentException("La configuración del reparto no es válida.");
	    }

	    CuentaCompartida nuevaCuenta = new CuentaCompartida(nombre, estrategia, usuarios);
	    repoCuentas.addCuenta(nuevaCuenta);
	    this.notificarCambio(EventoSistema.NUEVA_CUENTA, nuevaCuenta);
	}
	
	//Registra un gasto en una CuentaCompartida
	public void registrarGastoEnCuenta(double importe, LocalDate fecha, String nombreCat, String loginPagador, CuentaCompartida cuenta) {
	    try {
	        // 1. Identificamos al usuario pagador real
	        Usuario pagador = repoCuentas.getUsuario(loginPagador);
	        
	        // 2. Creamos el objeto Gasto
	        String id = "G-COMP-" + System.currentTimeMillis();
	        Categoria cat = new Categoria(nombreCat);
	        Gasto nuevoGasto = new Gasto(id, importe, fecha, cat, pagador);

	        // 3. Delegamos el cálculo a la cuenta (aplica su estrategia)
	        cuenta.calcularGasto(nuevoGasto);

	        // 4. Persistencia
	        repoGastos.addGasto(nuevoGasto);
	        
	        // 5. Notificación para refrescar saldos en la UI
	        this.notificarCambio(EventoSistema.SALDO_ACTUALIZADO, cuenta);
	        
	        // 6. Verificación de alertas globales
	        verificarAlertas();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	// En ControladorAppGastos.java
	public Map<Usuario, Double> getPorcentajesUsuarioCuenta(CuentaCompartida cuenta) {
	    // Delegamos en la cuenta
	    return cuenta.getPorcentajesEstrategia();
	}
	
	public Map<Usuario, Double> getSaldosPorUsuarioCuenta(CuentaCompartida cuenta){
		return cuenta.getSaldosPorUsuario();
	}
	
	private void verificarAlertas() {
	    List<Gasto> todosLosGastos = repoGastos.getGastos();
	    List<Alerta> alertas = repoAlertas.getAlertas();

	    for (Alerta alerta : alertas) {
	        boolean superada = alerta.verificarSiSuperada(todosLosGastos);

	        // Se supera el límite y NO habíamos avisado antes
	        if (superada && !alerta.isFueNotificada()) {
	            alerta.setFueNotificada(true); // Bloqueamos para que no repita
	            
	            try {
	                repoAlertas.updateAlerta(alerta); // Guardamos que ya avisamos

	                // Crear y guardar notificación en el historial
	                String mensaje = "Límite de " + String.format("%.2f", alerta.getLimite()) + "€ superado";
	                if (alerta.getCategoria() != null) {
	                    mensaje += " en " + alerta.getCategoria().getId();
	                }
	                
	                Notificacion n = new Notificacion(mensaje, LocalDate.now(), alerta);
	                repoAlertas.addNotificacion(n);
	                
	                // Disparar evento para que la UI muestre el Alert
	                this.notificarCambio(EventoSistema.ALERTA_DISPARADA, alerta);
	                
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        } 
	    }
	}
	
	public List<Notificacion> getHistorialNotificaciones() {
	    return repoAlertas.getNotificaciones();
	}
	
	public List<String> getNombreCategorias() {
	    Set<String> nombres = repoGastos.getCategorias().stream()
	            .map(c -> c.getId())
	            .collect(Collectors.toSet());
	    
	    return nombres.stream().sorted().collect(Collectors.toList());
	}
	
	private void notificarCambio(EventoSistema evento, Object datos) {
        observadores.forEach(obs -> obs.actualizar(evento, datos));
    }
	
	public void registrarGasto(double importe, LocalDate fecha, String nombreCat) {
	    try {
	        // 1. Obtener el usuario de la sesión
	        Usuario pagador = ControladorSesion.getInstancia().getUsuarioActual();
	        
	        if (pagador == null) {
	            throw new RuntimeException("Error: No hay una sesión de usuario activa.");
	        }

	        // 2. recuperar categoría
	        Categoria cat = repoGastos.getCategoria(nombreCat);

	        // 3. Crear el objeto Gasto con la categoría real
	        String id = "G-" + System.currentTimeMillis();
	        Gasto nuevo = new Gasto(id, importe, fecha, cat, pagador); 

	        // 4. Persistencia en el repositorio de gastos
	        repoGastos.addGasto(nuevo);

	        // 5. Notificar a los observadores para refrescar tablas
	        this.notificarCambio(EventoSistema.NUEVO_GASTO, nuevo);
	        
	        // 6. Lanzar verificación de alertas globales
	        verificarAlertas();

	    } catch (ElementoExistenteException e) {
	        System.err.println("Error: El ID del gasto ya existe.");
	        e.printStackTrace();
	    } catch (ErrorPersistenciaException e) {
	        System.err.println("Error crítico al guardar en el archivo JSON.");
	        e.printStackTrace();
	    } catch (Exception e) {
	        System.err.println("Error inesperado: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	public void registrarCategoria(String nombre) throws ElementoExistenteException, ErrorPersistenciaException {
		repoGastos.addCategoria(new Categoria(nombre));
		
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
	
	public void importarGastos(String rutaFichero) throws ImportacionException{
	    try {
	    	ImportadorGastos importador = FactoriaImportadores.getInstancia().crearImportador(rutaFichero);
	    	
	        // 1. El importador nos da el mapa crudo con objetos temporales
	        Map<String, List<Gasto>> mapa = importador.leerGastos(rutaFichero);

	        // Recorremos las cuentas que venían en el CSV
	        for (String nombreCuenta : mapa.keySet()) {
	            
	            // Buscamos la CUENTA REAL (en caso de que no sea la cuenta personal)
	            CuentaCompartida cuentaReal = repoCuentas.getCuenta(nombreCuenta);
	            if (!nombreCuenta.equals("Personal") && cuentaReal == null) {
	                throw new ImportacionException("No existe la cuenta " + nombreCuenta +".");
	            }

	            // B. Procesamos los gastos de esa cuenta
	            List<Gasto> gastosNuevos = mapa.get(nombreCuenta);
	            
	            for (Gasto gasto : gastosNuevos) {
	                
	                // 1. Sacamos la categoría temporal que creó el importador
	                Categoria catFantasma = gasto.getCategoria();
	                
	                // 2. Preguntamos al Repo si ya existe una con ese nombre
	                // (Asumo que tienes un método buscarPorNombre o getCategorias() y filtras)
	                Categoria catReal = repoGastos.getCategoria(catFantasma.getId());
	                
	                if (catReal != null) {
	                    // Si ya existía:
	                    // Tiramos la temporal y le ponemos la real al gasto.
	                    gasto.setCategoria(catReal);
	                    
	                } else {
	                    // Si no existía (es nueva)
	                    // La registramos en el sistema.
	                    repoGastos.addCategoria(catFantasma);
	                    // El gasto se queda con ella (ahora ya es real).
	                }

	                // 3. Finalmente, añadimos el gasto limpio a la cuenta y viceversa, en caso
	                //    de que esta no sea la cuenta personal.
	                if (!nombreCuenta.equals("Personal")) {
	                	gasto.setCuenta(cuentaReal);
	                	cuentaReal.addGasto(gasto);
	                }
	            }
	        }
	        
	        System.out.println("Importación finalizada y reconciliada.");

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public boolean isImporteValido(double importe) {
	    return importe > 0;
	}
}
