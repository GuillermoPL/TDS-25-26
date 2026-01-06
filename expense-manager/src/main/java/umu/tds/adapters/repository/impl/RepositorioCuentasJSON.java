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
import umu.tds.Configuracion;
import umu.tds.adapters.repository.RepositorioCuentas;
import umu.tds.adapters.repository.exceptions.ElementoExistenteException;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;
import umu.tds.modelo.CuentaCompartida;
import umu.tds.modelo.Gasto;
import umu.tds.modelo.Usuario;

public class RepositorioCuentasJSON implements RepositorioCuentas{
	
	private static final Logger log = LogManager.getLogger();
	
	private List<CuentaCompartida> cuentas = null;
	private List<Usuario> usuarios = null;
	
	private String rutaCuentas;
	private String rutaUsuarios;
	
	// Al leer por primera vez leeremos todo a la vez
	private void cargaCuentasYUsuarios() throws ErrorPersistenciaException {
		try {
			rutaCuentas = Configuracion.getInstancia().getRutaCuentas();
			rutaUsuarios = Configuracion.getInstancia().getRutaUsuarios();
			this.cuentas = cargar(rutaCuentas,
								new TypeReference<List<CuentaCompartida>>() {});
			this.usuarios = cargar(rutaUsuarios,
								new TypeReference<List<Usuario>>() {});
		} catch (Exception e) {
			log.error("Error cargando las cuentas o los usuarios ", e);
			throw new ErrorPersistenciaException(e);
		}
	}
	
	private <T> T cargar(String rutaFichero, TypeReference<T> tipoReferencia)
			throws StreamReadException, DatabindException, IOException {

		InputStream ficheroStream = getClass().getResourceAsStream(rutaFichero);

		ObjectMapper mapper = new ObjectMapper();

		T listaCargada = mapper.readValue(ficheroStream, tipoReferencia);

		return listaCargada;

	}
	
	@Override
	public List<CuentaCompartida> getCuentas(){
		if (cuentas == null) {
			try {
				cargaCuentasYUsuarios();
			} catch (ErrorPersistenciaException e) {
				// Manejo la excepcion y la propago como Excepcion en Tiempo de Ejecución.
				log.error("No se han podido cargar las cuentas y usuarios", e);
				throw new RuntimeException(
						"CRITICAL_LOAD_ERROR: No se pudo cargar el fichero de cuentas o el de usuarios.", e);
			}
		}
		return cuentas;
	}
	
	@Override
	public CuentaCompartida getCuenta(String nombre) {
		if (cuentas == null) {
			getCuentas();
		}
		CuentaCompartida cuenta = null;
		for (CuentaCompartida c : cuentas) {
			if (c.getNombre().equals(nombre)) {
				cuenta = c;
			}
		}
		return cuenta;
		
	}
	
	@Override
	public void addCuenta(CuentaCompartida cuenta) throws ElementoExistenteException, ErrorPersistenciaException {
		if (cuentas == null) {
			getCuentas();
		}
		if (cuentas.contains(cuenta)) {
			// TODO: Describir mejor el error de que ya esté la cuenta registrada
			throw new ElementoExistenteException("La cuenta ya ha sido registrada");
		}
		cuentas.add(cuenta);
		try {
			guardar(cuentas, rutaCuentas);
		} catch (Exception e) {
			// Hacemos rollback ya que asumimos que no se ha podido guardar la cuenta
			cuentas.remove(cuenta);
			log.error("Error persistiendo la cuenta {}", cuenta, e);
			// Capturo las excepciones genericas lanzadas al persistir y lanzo una propia
			// encapsulando la excepcion real
			throw new ErrorPersistenciaException(e);
		}
	}
	
	@Override
	public void updateCuenta(CuentaCompartida cuenta) throws ErrorPersistenciaException {
		if (cuentas == null) {
			getCuentas();
		}
		// La modificación la hacemos en el controlador
		try {
			guardar(cuentas, rutaCuentas);
		} catch (Exception e) {
			log.error("Error actualizando la cuenta {}", cuenta, e);
			throw new ErrorPersistenciaException(e);
		}

	}
	
	@Override
	public List<Usuario> getUsuarios(){
		if (usuarios == null) {
			try {
				cargaCuentasYUsuarios();
			} catch (ErrorPersistenciaException e) {
				// Manejo la excepcion y la propago como Excepcion en Tiempo de Ejecución.
				log.error("No se han podido cargar las cuentas y usuarios", e);
				throw new RuntimeException(
						"CRITICAL_LOAD_ERROR: No se pudo cargar el fichero de cuentas o el de usuarios.", e);
			}
		}
		return usuarios;
	}
	
	@Override
	public Usuario getUsuario(String nombre) {
		if (usuarios == null) {
			getUsuarios();
		}
		Usuario usuario = null;
		for(Usuario u : usuarios) {
			if(u.isUsuario(nombre)) {
				usuario = u;
			}
		}
		return usuario;
	}
	
	@Override
	public void addUsuario(Usuario u) throws ElementoExistenteException, ErrorPersistenciaException{
		if (usuarios == null) {
			getUsuarios();
		}
		if (usuarios.contains(u)) {
			// TODO: Describir mejor el error de que ya esté el usuario registrado
			throw new ElementoExistenteException("El usuario ya ha sido registrado");
		}
		usuarios.add(u);
		try {
			guardar(usuarios, rutaUsuarios);
		} catch (Exception e) {
			// Hacemos rollback ya que asumimos que no se ha podido guardar el usuario
			usuarios.remove(u);
			log.error("Error persistiendo el usuario {}", u, e);
			// Capturo las excepciones genericas lanzadas al persistir y lanzo una propia
			// encapsulando la excepcion real
			throw new ErrorPersistenciaException(e);
		}
	}
	
	
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
	        
	        log.info("Cuentas guardadas correctamente en: " + ficheroJSon.getAbsolutePath());
			
		} catch (IOException | URISyntaxException e) {
			log.error("Error persistiendo en fichero", e);
			throw e;
		}
	}

}
