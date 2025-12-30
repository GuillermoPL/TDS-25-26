package umu.tds;


import umu.tds.controlador.ControladorAppGastos;

/**
 * Almacena los diferentes controladores y servicios de la aplicación.
 * 
 * El programa principal debe utilizar una subclase de {@link Configuracion} 
 * donde se configuran las subclases concretas de los servicios de la aplicación.
 * 
 * <pre>
 * 		Configuracion.setInstance(configuracionConcreta)
 * </pre>
 * 
 * Las clases de la aplicación pueden acceder a esta configuración para obtener
 * estos objetos.
 */
public abstract class Configuracion {

	private static Configuracion instancia;
	
	/**
	 * Solo debe ser invocado desde App
	 */
	static void setInstancia(Configuracion impl) {
		Configuracion.instancia = impl; 
	}
	
	public static Configuracion getInstancia() {
		return Configuracion.instancia;
	}

	
	public abstract ControladorAppGastos getControladorGastos();
	
	public abstract String getRutaFicheroGastos();

}

