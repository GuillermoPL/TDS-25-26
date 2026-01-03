package umu.tds.controlador;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.time.LocalDate;
import umu.tds.Configuracion;
import umu.tds.adapters.repository.RepositorioGastos;
import umu.tds.modelo.Gasto;
import umu.tds.modelo.Categoria;
import umu.tds.modelo.EventoSistema;
import umu.tds.vista.IObservador;


//PATRON SINGLETON
public class ControladorAppGastos {
	private static ControladorAppGastos unicaInstancia;
    private RepositorioGastos repositorio;
    private List<IObservador> observadores = new LinkedList<>();
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
		List<Gasto> gastos = repositorio.getGastos();
		return gastos.stream()
				.filter(g -> condicion.test(g))
				.collect(Collectors.toList());
	}
	
	public List<String> getNombreCategorias() {
	    return repositorio.getGastos().stream()
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
	        String id = "G-" + System.currentTimeMillis();
	        Categoria cat = new Categoria(nombreCat);
	        // Asume que tienes un usuario actual (puedes obtenerlo de un controlador de sesión)
	        Gasto nuevo = new Gasto(id, importe, fecha, cat, null); 

	        repositorio.addGasto(nuevo);

	        this.notificarCambio(EventoSistema.NUEVO_GASTO, nuevo);
	        
	    } catch (umu.tds.adapters.repository.exceptions.ElementoExistenteException e) {
	        System.err.println("Error: El gasto ya existe en el sistema.");
	        e.printStackTrace();
	    } catch (umu.tds.adapters.repository.exceptions.ErrorPersistenciaException e) {
	        System.err.println("Error crítico al guardar en el JSON.");
	        e.printStackTrace();
	    }
	}
}
