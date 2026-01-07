package umu.tds.adapters.repository;

import java.util.List;

import umu.tds.adapters.repository.exceptions.ElementoExistenteException;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;
import umu.tds.modelo.Alerta;
import umu.tds.modelo.Notificacion;

/**
 * Interfaz para la gestión de la persistencia de Alertas y Notificaciones.
 * Patrón Repository.
 */
public interface RepositorioAlertas {
    
    // --- GESTIÓN DE ALERTAS ---

    /**
     * Recupera todas las alertas configuradas en el sistema.
     * @return Lista de alertas (nunca null, puede estar vacía).
     */
    List<Alerta> getAlertas();
    
    /**
     * Persiste una nueva alerta.
     * @param alerta La alerta a guardar (no debe ser null).
     * @throws ElementoExistenteException Si la alerta ya existe en el repositorio.
     * @throws ErrorPersistenciaException Si ocurre un error de escritura (I/O).
     */
    void addAlerta(Alerta alerta) throws ElementoExistenteException, ErrorPersistenciaException;
    
    /**
     * Elimina una alerta existente.
     * @param alerta La alerta a eliminar.
     * @throws ErrorPersistenciaException Si ocurre un error de escritura.
     */
    void removeAlerta(Alerta alerta) throws ErrorPersistenciaException;
    
    /**
     * Actualiza el estado de una alerta (ej: cambiar si fue notificada o no).
     * @param alerta La alerta con los datos modificados.
     * @throws ErrorPersistenciaException Si ocurre un error de escritura.
     */
    void updateAlerta(Alerta alerta) throws ErrorPersistenciaException;
    
    // --- GESTIÓN DE NOTIFICACIONES ---
    
    /**
     * Recupera el historial de notificaciones generadas.
     * @return Lista de notificaciones (nunca null).
     */
    List<Notificacion> getNotificaciones();
    
    /**
     * Registra una nueva notificación en el historial.
     * @param notificacion La notificación a guardar.
     * @throws ElementoExistenteException Si ya existe (poco probable por ID, pero posible).
     * @throws ErrorPersistenciaException Si ocurre un error de escritura.
     */
    void addNotificacion(Notificacion notificacion) throws ElementoExistenteException, ErrorPersistenciaException;

}