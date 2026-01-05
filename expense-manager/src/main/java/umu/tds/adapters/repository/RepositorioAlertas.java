package umu.tds.adapters.repository;

import java.util.List;

import umu.tds.adapters.repository.exceptions.ElementoExistenteException;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;
import umu.tds.modelo.Alerta;
import umu.tds.modelo.Notificacion;

public interface RepositorioAlertas {
	
	public List<Alerta> getAlertas();
	
	public void addAlerta(Alerta alerta) throws ElementoExistenteException, ErrorPersistenciaException;
	
	public void removeAlerta(Alerta alerta) throws ErrorPersistenciaException;
	
	public void updateAlerta(Alerta alerta) throws ErrorPersistenciaException;
	
	public List<Notificacion> getNotificaciones();
	
	public void addNotificacion(Notificacion notificacion) throws ElementoExistenteException, ErrorPersistenciaException;

}
