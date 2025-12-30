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
import umu.tds.adapters.repository.exceptions.ElementoExistenteException;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;
import umu.tds.adapters.repository.RepositorioGastos;
import umu.tds.modelo.Gasto;

public class RepositorioGastosJSON implements RepositorioGastos {

	private static final Logger log = LogManager.getLogger();

	private List<Gasto> gastos = null;
	private String rutaFichero;

	private void cargaGastos() throws ErrorPersistenciaException {
		try {
			rutaFichero = Configuracion.getInstancia().getRutaFicheroGastos();
			this.gastos = cargarGastos(rutaFichero);
		} catch (Exception e) {
			log.error("Error cargando los gastos ", e);
			throw new ErrorPersistenciaException(e);
		}
	}

	private List<Gasto> cargarGastos(String rutaFichero)
			throws StreamReadException, DatabindException, IOException {

		// Intentamos abrir el fichero como recurso
		InputStream ficheroStream = getClass().getResourceAsStream(rutaFichero);

		// Usamos Jackson para leer
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		// Leemos el JSON y lo convertimos directamente a una Lista de Gastos
		List<Gasto> gastosCargados = mapper.readValue(ficheroStream, new TypeReference<List<Gasto>>() {
		});

		return gastosCargados;

	}

	@Override
	public List<Gasto> getGastos() {
		if (gastos == null) {
			try {
				cargaGastos();
			} catch (ErrorPersistenciaException e) {
				// Manejo la excepcion pero no la propago porque en este sitio es donde se puede
				// gestionar mejor
				log.error("No se han podido cargar los gastos ", e);
				throw new RuntimeException(
						"CRITICAL_LOAD_ERROR: No se pudo cargar el fichero de gastos.", e);
			}
		}
		return gastos;
	}

	/*
	@Override
	public List<Gasto> getGastos(Predicate<Gasto> condicion) {
		List<Gasto> gastos = getGastos();
		return gastos.stream().filter(condicion).collect(Collectors.toList());
	}
	*/

	@Override
	public void addGasto(Gasto gasto) throws ElementoExistenteException, ErrorPersistenciaException{
		// Si el producto ya existe no puedo insertarlo
		if (gastos.contains(gasto)) {
			// TODO: Describir mejor el error de que ya esté el gasto registrado
			throw new ElementoExistenteException("El gasto ya ha sido registrado");
		}
		gastos.add(gasto);
		try {
			guardarGastos(gastos, rutaFichero);
		} catch (Exception e) {
			// Hacemos rollback ya que asumimos que no se ha podido guardar el gasto
			gastos.remove(gasto);
			log.error("Error persistiendo el gasto {}", gasto, e);
			// Capturo las excepciones genericas lanzadas al persistir y lanzo una propia
			// encapsulando la excepcion real
			throw new ErrorPersistenciaException(e);
		}

	}

	@Override
	public void removeGasto(Gasto gasto) throws ErrorPersistenciaException{
		if (!gastos.contains(gasto)) {
			return;
		}

		gastos.remove(gasto);
		try {
			guardarGastos(gastos, rutaFichero);
		} catch (Exception e) {
			// Hacemos rollback ya que asumimos que no se ha podido eliminar el gasto
			gastos.add(gasto);
			log.error("Error eliminando el gasto {}", gasto, e);
			throw new ErrorPersistenciaException(e);
		}

	}

	@Override
	public void updateGasto(Gasto gasto) throws ErrorPersistenciaException {
		// La modificación la hacemos en el controladorGastos
		try {
			guardarGastos(gastos, rutaFichero);
		} catch (Exception e) {
			log.error("Error actualizando el gasto {}", gasto, e);
			throw new ErrorPersistenciaException(e);
		}

	}
	
	// Metodo para guardar por completo en el fichero json
	private void guardarGastos(List<Gasto> gastos, String rutaFichero)
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
	        
	        mapper.writerWithDefaultPrettyPrinter().writeValue(ficheroJSon, gastos);
	        
	        this.gastos = gastos;
	        
	        log.info("Gastos guardados correctamente en: " + ficheroJSon.getAbsolutePath());
			
		} catch (IOException | URISyntaxException e) {
			log.error("Error persistiendo en fichero", e);
			throw e;
		}
	}

}
