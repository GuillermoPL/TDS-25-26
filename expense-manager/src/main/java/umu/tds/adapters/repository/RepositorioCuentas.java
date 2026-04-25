package umu.tds.adapters.repository;

import java.util.List;
import umu.tds.adapters.repository.exceptions.ElementoExistenteException;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;
import umu.tds.modelo.CuentaCompartida;
import umu.tds.modelo.Usuario;

/**
 * Interfaz para la gestión de la persistencia de Usuarios y Cuentas Compartidas.
 * Sigue el patrón Repository / DAO.
 */
public interface RepositorioCuentas {
    
    // --- GESTIÓN DE CUENTAS COMPARTIDAS ---
    
    /**
     * Recupera todas las cuentas compartidas del sistema.
     * @return Lista de cuentas (nunca null, puede estar vacía).
     */
    List<CuentaCompartida> getCuentas();
    
    /**
     * Busca una cuenta por su nombre o identificador único.
     * @param nombre Nombre identificativo de la cuenta.
     * @return La cuenta si existe, null si no.
     */
    CuentaCompartida getCuenta(String nombre);
    
    /**
     * Persiste una nueva cuenta compartida.
     * @param cuenta La cuenta a guardar (no debe ser null).
     * @throws ElementoExistenteException Si ya existe una cuenta con ese identificador.
     * @throws ErrorPersistenciaException Si ocurre un error de escritura.
     */
    void addCuenta(CuentaCompartida cuenta) throws ElementoExistenteException, ErrorPersistenciaException;
    
    /**
     * Actualiza el estado de una cuenta (ej: nuevos gastos, cambio de saldos).
     * @param cuenta La cuenta con los datos modificados.
     * @throws ErrorPersistenciaException Si ocurre un error de escritura.
     */
    void updateCuenta(CuentaCompartida cuenta) throws ErrorPersistenciaException;
    
    // --- GESTIÓN DE USUARIOS ---
    
    /**
     * Recupera todos los usuarios registrados.
     * @return Lista de usuarios (nunca null).
     */
    List<Usuario> getUsuarios();
    
    /**
     * Busca un usuario por su login o nombre.
     * @param nombre Login del usuario.
     * @return El usuario si existe, null si no.
     */
    Usuario getUsuario(String nombre);
    
    /**
     * Registra un nuevo usuario en el sistema.
     * @param u El usuario a registrar.
     * @throws ElementoExistenteException Si el login ya está en uso.
     * @throws ErrorPersistenciaException Si ocurre un error de escritura.
     */
    void addUsuario(Usuario u) throws ElementoExistenteException, ErrorPersistenciaException;

    /**
     * Recupera los logins de todos los usuarios registrados.
     * <p>
     * Se ofrece como método propio del repositorio (en lugar de que el cliente
     * itere {@link #getUsuarios()}) para respetar el patrón Experto en Información:
     * la transformación la realiza quien posee la colección.
     *
     * @return Lista de logins (nunca null, puede estar vacía).
     */
    List<String> getLoginsUsuarios();

}   