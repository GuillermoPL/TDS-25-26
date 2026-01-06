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
import umu.tds.modelo.Categoria;
import umu.tds.modelo.Gasto;

public class RepositorioGastosJSON implements RepositorioGastos {

	private static final Logger log = LogManager.getLogger();

	private List<Gasto> gastos = null;
	private List<Categoria> categorias = null;
	private String rutaGastos;
	private String rutaCategorias;
	

	private void cargaGastosYCategorias() throws ErrorPersistenciaException {
		try {
			rutaGastos = Configuracion.getInstancia().getRutaGastos();
			rutaCategorias = Configuracion.getInstancia().getRutaCategorias();
			this.gastos = cargar(rutaGastos, 
								new TypeReference<List<Gasto>>() {});
			this.categorias = cargar(rutaCategorias, 
								new TypeReference<List<Categoria>>() {});
		} catch (Exception e) {
			log.error("Error cargando los gastos o las categorias ", e);
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
	public List<Gasto> getGastos() {
		if (gastos == null) {
			try {
				cargaGastosYCategorias();
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

	@Override
	public void addGasto(Gasto gasto) throws ElementoExistenteException, ErrorPersistenciaException{
		// Si el producto ya existe no puedo insertarlo
		if (gastos.contains(gasto)) {
			// TODO: Describir mejor el error de que ya esté el gasto registrado
			throw new ElementoExistenteException("El gasto ya ha sido registrado");
		}
		gastos.add(gasto);
		try {
			guardar(gastos, rutaGastos);
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
			guardar(gastos, rutaGastos);
		} catch (Exception e) {
			// Hacemos rollback ya que asumimos que no se ha podido eliminar el gasto
			gastos.add(gasto);
			log.error("Error eliminando el gasto {}", gasto, e);
			throw new ErrorPersistenciaException(e);
		}

	}

	@Override
	public void updateGasto(Gasto gasto) throws ErrorPersistenciaException {
		if (gastos == null) {
			getGastos();
		}
		// La modificación la hacemos en el controlador
		try {
			guardar(gastos, rutaGastos);
		} catch (Exception e) {
			log.error("Error actualizando el gasto {}", gasto, e);
			throw new ErrorPersistenciaException(e);
		}

	}
	
	
	
	@Override
	public List<Categoria> getCategorias() {
		if (categorias == null) {
			try {
				cargaGastosYCategorias();
			} catch (ErrorPersistenciaException e) {
				// Manejo la excepcion y la propago como Excepcion en Tiempo de Ejecución.
				log.error("No se han podido cargar las categorías ", e);
				throw new RuntimeException(
						"CRITICAL_LOAD_ERROR: No se pudo cargar el fichero de gastos o de categorías.", e);
			}
		}
		return categorias;
	}

	public Categoria getCategoria(String nombreBuscado) {
	    if (nombreBuscado == null) return null;
	    
	    // Aseguramos que las categorías estén cargadas
	    if (categorias == null) getCategorias();
	    
	    return categorias.stream()
	            .filter(c -> c != null && c.getId() != null) // Evitamos elementos corruptos
	            .filter(c -> c.getId().equalsIgnoreCase(nombreBuscado)) // Búsqueda sin distinguir mayúsculas
	            .findFirst()
	            .orElse(null);
	}

	@Override
	public void addCategoria(Categoria categoria) throws ElementoExistenteException, ErrorPersistenciaException {
		if (categorias == null) {
			getCategorias();
		}
		// Si el producto ya existe no puedo insertarlo
		if (categorias.contains(categoria)) {
			// TODO: Describir mejor el error de que ya esté la categoria registrada
			throw new ElementoExistenteException("La categoría ya ha sido registrada");
		}
		categorias.add(categoria);
		try {
			guardar(categorias, rutaCategorias);
		} catch (Exception e) {
			// Hacemos rollback ya que asumimos que no se ha podido guardar el gasto
			categorias.remove(categoria);
			log.error("Error persistiendo la categoria {}", categoria, e);
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
	        mapper.registerModule(new JavaTimeModule());
	        
	        mapper.writerWithDefaultPrettyPrinter().writeValue(ficheroJSon, elementos);
	        
	        log.info("Usuarios o cuentas guardadas correctamente en: " + ficheroJSon.getAbsolutePath());
			
		} catch (IOException | URISyntaxException e) {
			log.error("Error persistiendo en fichero", e);
			throw e;
		}
	}
	

}
