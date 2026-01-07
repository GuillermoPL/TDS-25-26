package umu.tds.adapters.importacion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import umu.tds.modelo.Categoria;
import umu.tds.modelo.Gasto;
import umu.tds.modelo.Usuario;
import umu.tds.modelo.importacion.ImportadorGastos;
import umu.tds.modelo.importacion.exceptions.ImportacionException;

/**
 * Adaptador concreto para leer ficheros en formato CSV específico.
 * Formato esperado: Date, Account, Currency, Category, Note, Member, Amount
 */
public class ImportadorCSV implements ImportadorGastos {

    private static final String SEPARADOR = ",";
    
    // Constantes para índices de columnas (Mejora la legibilidad y mantenimiento)
    private static final int COL_DATE = 0;
    private static final int COL_ACCOUNT = 1;
    private static final int COL_CATEGORY = 3;
    private static final int COL_MEMBER = 5;
    private static final int COL_AMOUNT = 6;
    private static final int MIN_COLUMNS = 7;

    // FORMATO: M/d/yyyy H:mm (ej: "3/2/2022 10:11")
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");

    @Override
    public Map<String, List<Gasto>> leerGastos(String rutaFichero) throws ImportacionException {
        
        Map<String, List<Gasto>> gastosPorCuenta = new HashMap<>();
        
        // Try-with-resources asegura que el fichero se cierra siempre (Clean Code)
        try (BufferedReader br = new BufferedReader(new FileReader(rutaFichero))) {          
            String linea;
            boolean esCabecera = true; 

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue; 
                
                // Saltar cabecera
                if (esCabecera) {
                    if (linea.toLowerCase().startsWith("date")) {
                        esCabecera = false;
                        continue;
                    }
                }

                try {
                    Gasto gasto = parsearLinea(linea);
                    
                    // Extraemos el nombre de la cuenta directamente del gasto (que ya lo ha procesado)
                    // O lo leemos de nuevo si preferimos no ensuciar el objeto Gasto con "nombreCuenta" temporal
                    String nombreCuenta = extraerNombreCuenta(linea);
                    
                    gastosPorCuenta.putIfAbsent(nombreCuenta, new ArrayList<>());
                    gastosPorCuenta.get(nombreCuenta).add(gasto);
                    
                } catch (IllegalArgumentException | DateTimeParseException e) {
                    // Logueamos el error pero no detenemos la importación completa (Robustez)
                    System.err.println("AVISO: Línea ignorada por formato inválido: " + linea + " [" + e.getMessage() + "]");
                }
            }
            
        } catch (IOException e) {
            throw new ImportacionException("Error crítico leyendo el fichero CSV: " + e.getMessage());
        }

        return gastosPorCuenta;
    }
    
    // Método auxiliar puro (sin efectos secundarios)
    private String extraerNombreCuenta(String linea) {
        return linea.split(SEPARADOR)[COL_ACCOUNT].trim();
    }

    private Gasto parsearLinea(String linea) {
        String[] campos = linea.split(SEPARADOR);
        
        // 1. Validación estructural
        if (campos.length < MIN_COLUMNS) {
            throw new IllegalArgumentException("La línea tiene menos de " + MIN_COLUMNS + " columnas.");
        }

        // 2. Extracción y Parsing
        String fechaStr = campos[COL_DATE].trim();
        LocalDate fecha = LocalDateTime.parse(fechaStr, FORMATO_FECHA_HORA).toLocalDate();
        
        String nombreCategoria = campos[COL_CATEGORY].trim();
        Categoria categoriaTemp = new Categoria(nombreCategoria);
        
        String pagadorStr = campos[COL_MEMBER].trim();
        Usuario pagador = new Usuario(pagadorStr);
        
        String importeStr = campos[COL_AMOUNT].trim();
        double importe = Double.parseDouble(importeStr);
        
        String nombreCuenta = campos[COL_ACCOUNT].trim();

        // 3. Generación de ID determinista
        String id = generarIdUnico(fechaStr, nombreCategoria, pagadorStr, importeStr, nombreCuenta);

        // 4. Creación del objeto (Validado por el constructor de Gasto)
        return new Gasto(id, importe, fecha, categoriaTemp, pagador, nombreCuenta);
    }
    
    private String generarIdUnico(String fecha, String cat, String pagador, String importe, String cuenta) {
        String prefijo;
        if ("Personal".equalsIgnoreCase(cuenta)) {
            prefijo = "G-IMP-";
        } else {
            prefijo = "G-COMP-IMP-";
        }
        
        // Usamos hashcode para generar un ID consistente si se importa el mismo fichero 2 veces
        String semilla = fecha + cat + pagador + importe + cuenta;
        return prefijo + Math.abs(semilla.hashCode());
    }
}