package umu.tds.modelo.importacion;

import java.util.List;
import java.util.Map;

import umu.tds.modelo.Gasto;
import umu.tds.modelo.importacion.exceptions.ImportacionException;

/**
 * Interfaz que define el contrato para los adaptadores de importación de gastos.
 * (Patrón Adapter - Target)
 */
public interface ImportadorGastos {
	
	/**
	 * Lee un fichero de gastos y los convierte a objetos del dominio.
	 * * @param rutaFichero Ruta absoluta o relativa del fichero a importar.
	 * @return Un Mapa donde:
	 * - Key: Nombre de la cuenta (ej: "Personal", "Piso Estudiantes").
	 * - Value: Lista de gastos asociados a esa cuenta.
	 * @throws ImportacionException Si el formato no es válido, el fichero no existe o hay errores de lectura.
	 */
	Map<String, List<Gasto>> leerGastos(String rutaFichero) throws ImportacionException;

}