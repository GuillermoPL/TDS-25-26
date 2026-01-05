package umu.tds.adapters.repository.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import umu.tds.Configuracion;
import umu.tds.adapters.repository.RepositorioAlertas;
import umu.tds.adapters.repository.exceptions.ElementoExistenteException;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;
import umu.tds.modelo.Alerta;
import umu.tds.modelo.Gasto;
import umu.tds.modelo.Notificacion;

public class RepositorioAlertasJSON implements RepositorioAlertas{

	private static final Logger log = LogManager.getLogger();

	private List<Alerta> alertas = null;
	private List<Notificacion> notificaciones = null;
	private String rutaAlertas;
	private String rutaNotificaciones;

	private void cargaAlertasYNotificaciones() throws ErrorPersistenciaException {
		try {
			rutaAlertas = Configuracion.getInstancia().getRutaAlertas();
			rutaNotificaciones = Configuracion.getInstancia().getRutaNotificaciones();
			this.alertas = cargar(rutaAlertas, 
								new TypeReference<List<Alerta>>() {});
			this.notificaciones = cargar(rutaNotificaciones, 
								new TypeReference<List<Notificacion>>() {});
		} catch (Exception e) {
			log.error("Error cargando las alertas o las notificaciones ", e);
			throw new ErrorPersistenciaException(e);
		}
	}
	
	private <T> T cargar(String rutaFichero, TypeReference<T> tipoReferencia)

			throws StreamReadException, DatabindException, IOException {

		InputStream ficheroStream = getClass().getResourceAsStream(rutaFichero);
		
		TypeReference<List<Gasto>> tipoListaGastos = new TypeReference<List<Gasto>>() {};

		ObjectMapper mapper = new ObjectMapper();
		if(tipoReferencia.getType().equals(tipoListaGastos.getType())) {
			mapper.registerModule(new JavaTimeModule());
		}

		T listaCargada = mapper.readValue(ficheroStream, tipoReferencia);

		return listaCargada;

	}

	@Override
	public List<Alerta> getAlertas() {
		if (alertas == null) {
			try {
				cargaAlertasYNotificaciones();
			} catch (ErrorPersistenciaException e) {
				// Manejo la excepcion y la propago como Excepcion en Tiempo de Ejecución.
				log.error("No se han podido cargar las alertas ", e);
				throw new RuntimeException(
						"CRITICAL_LOAD_ERROR: No se pudo cargar el fichero de alertas.", e);
			}
		}
		return alertas;
	}


	@Override
	public void addAlerta(Alerta alerta) throws ElementoExistenteException, ErrorPersistenciaException{
		// Si el producto ya existe no puedo insertarlo
		if (alertas.contains(alerta)) {
			// TODO: Describir mejor el error de que ya esté la alerta registrada
			throw new ElementoExistenteException("La alerta ya ha sido registrada");
		}
		alertas.add(alerta);
		try {
			guardar(alertas, rutaAlertas);
		} catch (Exception e) {
			// Hacemos rollback ya que asumimos que no se ha podido guardar la alerta
			alertas.remove(alerta);
			log.error("Error persistiendo la alerta {}", alerta, e);
			// Capturo las excepciones genericas lanzadas al persistir y lanzo una propia
			// encapsulando la excepcion real
			throw new ErrorPersistenciaException(e);
		}

	}

	@Override
	public void removeAlerta(Alerta alerta) throws ErrorPersistenciaException{
		if (!alertas.contains(alerta)) {
			return;
		}

		alertas.remove(alerta);
		try {
			guardar(alertas, rutaAlertas);
		} catch (Exception e) {
			// Hacemos rollback ya que asumimos que no se ha podido eliminar la alerta
			alertas.add(alerta);
			log.error("Error eliminando la alerta {}", alerta, e);
			throw new ErrorPersistenciaException(e);
		}

	}

	@Override
	public void updateAlerta(Alerta alerta) throws ErrorPersistenciaException {
		// La modificación la hacemos en el controladorGastos
		try {
			guardar(alertas, rutaAlertas);
		} catch (Exception e) {
			log.error("Error actualizando la alerta {}", alerta, e);
			throw new ErrorPersistenciaException(e);
		}

	}

	@Override
	public List<Notificacion> getNotificaciones() {
		if (notificaciones == null) {
			try {
				cargaAlertasYNotificaciones();
			} catch (ErrorPersistenciaException e) {
				// Manejo la excepcion y la propago como Excepcion en Tiempo de Ejecución.
				log.error("No se han podido cargar las notificaciones ", e);
				throw new RuntimeException(
						"CRITICAL_LOAD_ERROR: No se pudo cargar el fichero de alertas o de notificaciones.", e);
			}
		}
		return notificaciones;
	}

	@Override
	public void addNotificacion(Notificacion notificacion) throws ElementoExistenteException, ErrorPersistenciaException {
		if (notificaciones.contains(notificacion)) {
			// TODO: Describir mejor el error de que ya esté la notificacion registrada
			throw new ElementoExistenteException("La notificación ya ha sido registrada");
		}
		notificaciones.add(notificacion);
		try {
			guardar(notificaciones, rutaNotificaciones);
		} catch (Exception e) {
			// Hacemos rollback ya que asumimos que no se ha podido guardar la alerta
			notificaciones.remove(notificacion);
			log.error("Error persistiendo la notificación {}", notificacion, e);
			// Capturo las excepciones genericas lanzadas al persistir y lanzo una propia
			// encapsulando la excepcion real
			throw new ErrorPersistenciaException(e);
		}
		
	}
	
	
	// Metodo para guardar por completo en el fichero json correspondiente
	private <T> void guardar(List<T> elementos, String rutaFichero)
			throws Exception {

		URL url = getClass().getResource(rutaFichero);
		// Comprobamos por si el fichero fue eliminado con la aplicación abierta
		if (url == null) {
	        log.error("El fichero ha desaparecido en tiempo de ejecución.");
	        throw new RuntimeException("El fichero de datos ha sido eliminado.");
	    }
		
		try {
			// Cargo el fichero a partir de la URL local
			File ficheroJSon = Paths.get(url.toURI()).toFile();
	        
	        ObjectMapper mapper = new ObjectMapper();
	        
	        mapper.writerWithDefaultPrettyPrinter().writeValue(ficheroJSon, elementos);
	        
	        log.info("Alertas/Categorías guardadas correctamente en: " + ficheroJSon.getAbsolutePath());
			
		} catch (IOException | URISyntaxException e) {
			log.error("Error persistiendo en fichero", e);
			throw e;
		}
	}

}
