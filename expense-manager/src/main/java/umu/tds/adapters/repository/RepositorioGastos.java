package umu.tds.adapters.repository;

import java.util.List;
//import java.util.function.Predicate;

import umu.tds.adapters.repository.exceptions.ElementoExistenteException;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;
import umu.tds.modelo.*;

public interface RepositorioGastos {
	
	public List<Gasto> getGastos();
	
	public List<Gasto> getGastosPorCategoria(Categoria categoria); 
	
	public void addGasto(Gasto gasto) throws ElementoExistenteException, ErrorPersistenciaException;
	
	public void removeGasto(Gasto gasto) throws ErrorPersistenciaException;
	
	public void updateGasto(Gasto gasto) throws ErrorPersistenciaException;

	public void addCuenta(CuentaCompartida cuenta);
	
	public List<CuentaCompartida> getCuentas();
	
	public List<Usuario> getUsuarios();
	
	public Usuario getUsuario(String nombre);
	
	public void addUsuario(Usuario u);
	
	
}
