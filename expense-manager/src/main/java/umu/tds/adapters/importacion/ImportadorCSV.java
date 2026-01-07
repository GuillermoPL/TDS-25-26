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
        
        // 1. Validaciones previas...
        if (campos.length < 7) throw new Exception("Faltan columnas...");

        // ... (Tu código de parsing de FECHA, CATEGORÍA, PAGADOR, IMPORTE sigue igual) ...
        String fechaStr = campos[0].trim();
        LocalDate fecha = LocalDateTime.parse(fechaStr, FORMATO_FECHA_HORA).toLocalDate();
        String nombreCategoria = campos[3].trim();
        Categoria categoriaTemp = new Categoria(nombreCategoria);
        String pagadorStr = campos[5].trim();
        Usuario pagador = new Usuario(pagadorStr);
        String importeStr = campos[6].trim();
        double importe = Double.parseDouble(importeStr);

        // --- NUEVO CÓDIGO AQUÍ ---
        
        // A. Detectamos el tipo de cuenta leyendo la Columna 1 ("Account")
        String nombreCuenta = campos[1].trim(); // ej: "Personal" o "Piso Estudiantes"
        
        // B. Definimos el prefijo según el tipo de cuenta
        // Si NO es "Personal", forzamos que empiece por "G-COMP" para que el controlador lo filtre.
        String prefijoID;
        if ("Personal".equalsIgnoreCase(nombreCuenta)) {
            prefijoID = "G-IMP-"; // Gasto personal importado
        } else {
            prefijoID = "G-COMP-IMP-"; // Gasto COMPartido IMPortado
        }

        // C. Generamos la semilla y el ID
        String semilla = fechaStr + nombreCategoria + pagadorStr + importeStr + nombreCuenta;
        String id = prefijoID + Math.abs(semilla.hashCode());

        // -------------------------

        return new Gasto(id, importe, fecha, categoriaTemp, pagador);
    }
}