package umu.tds.adapters.repository;

import java.util.List;

import umu.tds.adapters.repository.exceptions.ElementoExistenteException;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;
import umu.tds.modelo.*;

public interface RepositorioGastos {
	
	public List<Gasto> getGastos();
	
	//public List<Gasto> getGastos(Predicate<Gasto> condicion);
	
	public void addGasto(Gasto gasto) throws ElementoExistenteException, ErrorPersistenciaException;
	
	public void removeGasto(Gasto gasto) throws ErrorPersistenciaException;
	

	public void updateGasto(Gasto gasto) throws ErrorPersistenciaException;
	
	public List<Categoria> getCategorias();
	
	public Categoria getCategoria(String nombreCat);
	
	public void addCategoria(Categoria categoria) throws ElementoExistenteException, ErrorPersistenciaException;

	
	
}
