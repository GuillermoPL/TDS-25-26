package umu.tds.controlador;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import umu.tds.Configuracion;
import umu.tds.adapters.repository.RepositorioGastos;
import umu.tds.modelo.Gasto;

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
	
	public List<String> getNombreCategorias() {
	    return repositorio.getGastos().stream()
	            .map(gasto -> gasto.getCategoria().toString()) // Usa tu toString() que devuelve el id
	            .distinct()
	            .sorted()
	            .collect(Collectors.toList());
	}
}
