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

public class RepositorioAlertasJSON implements RepositorioAlertas{

	private static final Logger log = LogManager.getLogger();

	private List<Alerta> alertas = null;
	private String rutaFichero;

	private void cargaAlertas() throws ErrorPersistenciaException {
		try {
			rutaFichero = Configuracion.getInstancia().getRutaAlertas();
			this.alertas = cargarAlertas(rutaFichero);
		} catch (Exception e) {
			log.error("Error cargando las alertas ", e);
			throw new ErrorPersistenciaException(e);
		}
	}

	private List<Alerta> cargarAlertas(String rutaFichero)
			throws StreamReadException, DatabindException, IOException {

		// Intentamos abrir el fichero como recurso
		InputStream ficheroStream = getClass().getResourceAsStream(rutaFichero);

		// Usamos Jackson para leer
		ObjectMapper mapper = new ObjectMapper();

		// Leemos el JSON y lo convertimos directamente a una Lista de Alertas
		List<Alerta> alertasCargadas = mapper.readValue(ficheroStream, new TypeReference<List<Alerta>>() {
		});

		return alertasCargadas;

	}

	@Override
	public List<Alerta> getAlertas() {
		if (alertas == null) {
			try {
				cargaAlertas();
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
			guardarAlertas(alertas, rutaFichero);
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
			guardarAlertas(alertas, rutaFichero);
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
			guardarAlertas(alertas, rutaFichero);
		} catch (Exception e) {
			log.error("Error actualizando la alerta {}", alerta, e);
			throw new ErrorPersistenciaException(e);
		}

	}
	
	// Metodo para guardar por completo en el fichero json
	private void guardarAlertas(List<Alerta> alertas, String rutaFichero)
			throws Exception {

		// Se carga mediante URL para prevenir problemas con rutas con espacios en
		// blanco o caracteres no estandar
		URL url = getClass().getResource(rutaFichero);
		
		if (url == null) {
			// Comprobamos por si el fichero fue eliminado con la aplicación abierta
	        log.error("El fichero ha desaparecido en tiempo de ejecución.");
	        throw new RuntimeException("El fichero de datos ha sido eliminado.");
	    }
		
		try {
			// Cargo el fichero a partir de la URL local
			File ficheroJSon = Paths.get(url.toURI()).toFile();
	        
	        ObjectMapper mapper = new ObjectMapper();
	        mapper.registerModule(new JavaTimeModule());
	        
	        mapper.writerWithDefaultPrettyPrinter().writeValue(ficheroJSon, alertas);
	        
	        this.alertas = alertas;
	        
	        log.info("Alertas guardadas correctamente en: " + ficheroJSon.getAbsolutePath());
			
		} catch (IOException | URISyntaxException e) {
			log.error("Error persistiendo en fichero", e);
			throw e;
		}
	}

}
