package umu.tds.adapters.importacion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import umu.tds.modelo.Categoria;
import umu.tds.modelo.Gasto;
import umu.tds.modelo.Usuario;
import umu.tds.modelo.importacion.ImportadorGastos;
import umu.tds.modelo.importacion.exceptions.ImportacionException;

public class ImportadorCSV implements ImportadorGastos {

    private static final String SEPARADOR = ",";
    
    // FORMATO: M/d/yyyy H:mm (Encaja con "3/2/2022 10:11" y "2/26/2022 1:39")
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");

    @Override
    public Map<String, List<Gasto>> leerGastos(String rutaFichero) throws ImportacionException {
        
    	Map<String, List<Gasto>> gastosPorCuenta = new HashMap<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(rutaFichero))) {          
            String linea;
            boolean esCabecera = true; 

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue; 
                
                // Detectamos la cabecera buscando una palabra clave conocida
                if (esCabecera) {
                	// Si la línea empieza por "Date" es la cabecera, la saltamos
                	if (linea.startsWith("Date")) {
                		esCabecera = false;
                		continue;
                	}
                }

                try {
                	String[] tokens = linea.split(",");
                    
                    // 1. Extraemos el NOMBRE de la cuenta (Columna 1)
                    String nombreCuenta = tokens[1].trim(); // "Personal", "Compartida 1"
                    
                    // 2. Parseamos el Gasto (con su Categoría temporal)
                    Gasto gasto = parsearLinea(linea); // Método auxiliar que devuelve el Gasto
                    
                    // 3. Lo metemos en el mapa bajo su nombre de cuenta
                    gastosPorCuenta.putIfAbsent(nombreCuenta, new ArrayList<>());
                    gastosPorCuenta.get(nombreCuenta).add(gasto);
                } catch (Exception e) {
                    System.err.println("Línea no válida o corrupta:" + linea + " [" + e.getMessage() + "]");
                }
            }
            
        } catch (IOException e) {
            throw new ImportacionException("Error leyendo el fichero CSV: " + rutaFichero);
        }

        return gastosPorCuenta;
    }

    private Gasto parsearLinea(String linea) throws Exception {
        String[] campos = linea.split(SEPARADOR);
        
        // El CSV tiene 8 columnas. Validamos que al menos vengan las 7 primeras (la divisa nos da igual).
        if (campos.length < 7) {
            throw new Exception("Faltan columnas. Se encontraron " + campos.length);
        }

        // FECHA
        String fechaStr = campos[0].trim();
        LocalDate fecha;
        try {
        	// Parseamos con hora (LocalDateTime) y nos quedamos solo con la fecha (toLocalDate)
            fecha = LocalDateTime.parse(fechaStr, FORMATO_FECHA_HORA).toLocalDate();
        } catch (Exception e) {
             throw new Exception("Fecha inválida: " + fechaStr);
        }
        

        // CATEGORÍA (Usamos la columna 4 'Subcategory')
        // Esta categoría es temporal, en el controlador se busca en el repositorio.
        String nombreCategoria = campos[3].trim();
        Categoria categoriaTemp = new Categoria(nombreCategoria);
        
        // PAGADOR (Usamos la columna 6 'Payer')
        String pagadorStr = campos[5].trim();
        Usuario pagador = new Usuario(pagadorStr);

        // IMPORTE (Usamos la columna 7 'Amount')
        String importeStr = campos[6].trim();
        double importe = Double.parseDouble(importeStr);
        
        // El id lo generamos por el momento actual en milisegundos.
        String id = "G-IMPORT" + System.currentTimeMillis();
        return new Gasto(id, importe, fecha, categoriaTemp, pagador);
    }
}