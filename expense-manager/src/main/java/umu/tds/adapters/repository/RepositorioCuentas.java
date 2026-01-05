package umu.tds.adapters.repository;

import java.util.List;

import umu.tds.adapters.repository.exceptions.ElementoExistenteException;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;
import umu.tds.modelo.CuentaCompartida;
import umu.tds.modelo.Usuario;

public interface RepositorioCuentas {
	
	public List<CuentaCompartida> getCuentas();
	
	public CuentaCompartida getCuenta(String nombre);
	
	public void addCuenta(CuentaCompartida cuenta) throws ElementoExistenteException, ErrorPersistenciaException;
	
	public List<Usuario> getUsuarios();
	
	public Usuario getUsuario(String nombre);
	
	public void addUsuario(Usuario u) throws ElementoExistenteException, ErrorPersistenciaException;

}
