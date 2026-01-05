package umu.tds.modelo.importacion;

import umu.tds.adapters.importacion.ImportadorCSV;
import umu.tds.modelo.importacion.exceptions.FormatoNoValidoException;

public class FactoriaImportadores {
	
	private static FactoriaImportadores unicaInstancia = null;
	
	public static FactoriaImportadores getInstancia() {
		if (unicaInstancia == null) unicaInstancia = new FactoriaImportadores();
		return unicaInstancia;
	}
	
	public ImportadorGastos crearImportador(String rutaFichero) throws FormatoNoValidoException{
		
		String extension = "";
        
        int i = rutaFichero.lastIndexOf('.');
        if (i > 0) {
            extension = rutaFichero.substring(i + 1).toLowerCase();
        }
		
		switch(extension) {
		case "csv":
			return new ImportadorCSV();
		default:
			throw new FormatoNoValidoException("Error: Tipo de archivo no válido.");
		}
	}

}
