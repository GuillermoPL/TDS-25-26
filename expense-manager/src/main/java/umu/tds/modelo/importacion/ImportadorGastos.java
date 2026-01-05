package umu.tds.modelo.importacion;

import java.util.List;
import java.util.Map;

import umu.tds.modelo.Gasto;

public interface ImportadorGastos {
	
	public Map<String, List<Gasto>> leerGastos(String rutaFichero) throws Exception;

}
