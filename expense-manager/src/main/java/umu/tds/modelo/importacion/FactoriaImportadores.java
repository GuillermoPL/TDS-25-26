package umu.tds.modelo.importacion;

import java.util.Optional;

import umu.tds.adapters.importacion.ImportadorCSV;
import umu.tds.modelo.importacion.exceptions.FormatoNoValidoException;

/**
 * Factoría que gestiona la creación de importadores basándose en la extensión del fichero.
 * Implementa el patrón Singleton.
 */
public class FactoriaImportadores {
	
	// Singleton: Inicialización Eager (Ansiosa). 
	// Es Thread-Safe por defecto y más limpio que el Lazy Init con if(null).
	private static final FactoriaImportadores INSTANCE = new FactoriaImportadores();
	
	// Constructor privado para evitar instanciación externa
	private FactoriaImportadores() {}
	
	public static FactoriaImportadores getInstancia() {
		return INSTANCE;
	}
	
	/**
	 * Crea un importador adecuado según la extensión del archivo proporcionado.
	 * @param rutaFichero Ruta absoluta o relativa del archivo.
	 * @return Una instancia de ImportadorGastos capaz de leer ese formato.
	 * @throws FormatoNoValidoException Si el formato no es soportado o la extensión no existe.
	 */
	public ImportadorGastos crearImportador(String rutaFichero) throws FormatoNoValidoException {
		// 1. Diseño por Contrato: Validación de precondiciones
		if (rutaFichero == null || rutaFichero.trim().isEmpty()) {
			throw new IllegalArgumentException("La ruta del fichero no puede ser nula o vacía.");
		}

		// 2. Extraer extensión (Lógica encapsulada en método privado para mejorar legibilidad)
		String extension = obtenerExtension(rutaFichero)
				.orElseThrow(() -> new FormatoNoValidoException("El fichero no tiene extensión."));

		// 3. Selección de implementación (Switch)
		// Nota: Para cumplir OCP estricto, aquí se podría usar Reflexión o un Map<String, Supplier>,
		// pero el Switch es aceptable en factorías simples académicas.
		switch(extension) {
			case "csv":
				return new ImportadorCSV();
			// Aquí añadiríamos: case "xml": return new ImportadorXML();
			default:
				throw new FormatoNoValidoException("Formato no soportado: " + extension);
		}
	}

	// Método auxiliar para limpiar la lógica del método principal (Cohesión)
	// Usa Optional para evitar devolver nulls (Clean Code)
	private Optional<String> obtenerExtension(String rutaFichero) {
		int i = rutaFichero.lastIndexOf('.');
		// Aseguramos que hay punto y que no es el último carácter (ej: "archivo.")
		if (i > 0 && i < rutaFichero.length() - 1) {
			return Optional.of(rutaFichero.substring(i + 1).toLowerCase());
		}
		return Optional.empty();
	}

}