package umu.tds.adapters.repository;

import java.util.List;

import umu.tds.adapters.repository.exceptions.ElementoExistenteException;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;
import umu.tds.modelo.Categoria;
import umu.tds.modelo.Gasto;

/**
 * Interfaz para la persistencia de Gastos y Categorías.
 * Aplica el patrón Repository para desacoplar el dominio de la tecnología de almacenamiento (JSON/BBDD).
 */
public interface RepositorioGastos {
	
	// --- GESTIÓN DE GASTOS ---

	/**
	 * Recupera todos los gastos del sistema.
	 * @return Lista de gastos (puede estar vacía, nunca null).
	 */
	List<Gasto> getGastos();
	
	/**
	 * Persiste un nuevo gasto.
	 * @param gasto El gasto a guardar.
	 * @throws ElementoExistenteException Si ya existe un gasto con ese ID.
	 * @throws ErrorPersistenciaException Si hay un error de escritura/conexión.
	 */
	void addGasto(Gasto gasto) throws ElementoExistenteException, ErrorPersistenciaException;
	
	/**
	 * Elimina un gasto existente.
	 * @param gasto El gasto a eliminar.
	 * @throws ErrorPersistenciaException Si hay un error de escritura.
	 */
	void removeGasto(Gasto gasto) throws ErrorPersistenciaException;
	
	/**
	 * Actualiza los datos de un gasto existente.
	 * @param gasto El gasto con los datos modificados.
	 * @throws ErrorPersistenciaException Si hay un error de escritura.
	 */
	void updateGasto(Gasto gasto) throws ErrorPersistenciaException;
	
	// --- GESTIÓN DE CATEGORÍAS ---
	
	/**
	 * Recupera todas las categorías disponibles.
	 * @return Lista de categorías.
	 */
	List<Categoria> getCategorias();
	
	/**
	 * Busca una categoría por su nombre (ID).
	 * @param nombre Nombre de la categoría.
	 * @return El objeto Categoria si existe, o null si no se encuentra.
	 */
	Categoria getCategoria(String nombre);
	
	/**
	 * Persiste una nueva categoría.
	 * @param categoria La categoría a guardar.
	 * @throws ElementoExistenteException Si la categoría ya existe.
	 * @throws ErrorPersistenciaException Si hay un error de escritura.
	 */
	void addCategoria(Categoria categoria) throws ElementoExistenteException, ErrorPersistenciaException;

	/**
     * Recupera los nombres (identificadores) de todas las categorías existentes,
     * ordenados alfabéticamente.
     * <p>
     * Se ofrece como método propio del repositorio para respetar el patrón
     * Experto en Información: la transformación la realiza quien posee la
     * colección de categorías.
     *
     * @return Lista de nombres de categoría ordenada (nunca null, puede estar vacía).
     */
    List<String> getNombreCategorias();

}