package umu.tds.adapters.repository.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import umu.tds.Configuracion;
import umu.tds.adapters.repository.RepositorioGastos;
import umu.tds.adapters.repository.exceptions.ElementoExistenteException;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;
import umu.tds.modelo.Categoria;
import umu.tds.modelo.Gasto;

/**
 * Implementación de repositorio usando ficheros JSON locales.
 * Utiliza Jackson para serialización/deserialización.
 */
public class RepositorioGastosJSON implements RepositorioGastos {

    private static final Logger log = LogManager.getLogger();

    private List<Gasto> gastos = null;
    private List<Categoria> categorias = null;
    
    private String rutaGastos;
    private String rutaCategorias;
    
    // --- LECTURA ---

    private void cargaGastosYCategorias() throws ErrorPersistenciaException {
        try {
            rutaGastos = Configuracion.getInstancia().getRutaGastos();
            rutaCategorias = Configuracion.getInstancia().getRutaCategorias();
            
            // Usamos TypeReference anónimo para capturar genéricos en tiempo de ejecución
            this.gastos = cargar(rutaGastos, new TypeReference<List<Gasto>>() {});
            this.categorias = cargar(rutaCategorias, new TypeReference<List<Categoria>>() {});
            
        } catch (Exception e) {
            log.error("Error crítico cargando datos iniciales", e);
            throw new ErrorPersistenciaException(e);
        }
    }
    
    private <T> T cargar(String rutaFichero, TypeReference<T> tipoReferencia)
            throws StreamReadException, DatabindException, IOException {

        InputStream ficheroStream = getClass().getResourceAsStream(rutaFichero);
        if (ficheroStream == null) {
            throw new IOException("Fichero no encontrado en classpath: " + rutaFichero);
        }

        ObjectMapper mapper = new ObjectMapper();
        // SIEMPRE registramos el módulo de tiempo. No hace daño y evita bugs.
        mapper.registerModule(new JavaTimeModule());

        return mapper.readValue(ficheroStream, tipoReferencia);
    }
    
    // --- IMPLEMENTACIÓN DE LA INTERFAZ (GASTOS) ---

    @Override
    public List<Gasto> getGastos() {
        if (gastos == null) {
            try {
                cargaGastosYCategorias();
            } catch (ErrorPersistenciaException e) {
                log.error("Fallo al inicializar repositorio de gastos", e);
                // En un caso real, aquí quizás devolveríamos lista vacía para no crashear la UI,
                // pero lanzar RuntimeException alerta al desarrollador.
                throw new RuntimeException("CRITICAL: No se pueden leer los datos.", e);
            }
        }
        return gastos;
    }

    @Override
    public void addGasto(Gasto gasto) throws ElementoExistenteException, ErrorPersistenciaException {
        // Aseguramos carga (Lazy Loading seguro)
        if (gastos == null) getGastos();
        
        if (gastos.contains(gasto)) {
            throw new ElementoExistenteException("El gasto con ID " + gasto.getId() + " ya existe.");
        }
        
        gastos.add(gasto);
        
        try {
            guardar(gastos, rutaGastos);
        } catch (Exception e) {
            gastos.remove(gasto); // Rollback en memoria
            log.error("Error guardando gasto: " + gasto, e);
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public void removeGasto(Gasto gasto) throws ErrorPersistenciaException {
        if (gastos == null) getGastos();
        
        if (!gastos.contains(gasto)) return; // Idempotencia: borrar lo que no existe no es error

        gastos.remove(gasto);
        
        try {
            guardar(gastos, rutaGastos);
        } catch (Exception e) {
            gastos.add(gasto); // Rollback
            log.error("Error borrando gasto: " + gasto, e);
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public void updateGasto(Gasto gasto) throws ErrorPersistenciaException {
        // Nota: Como trabajamos con referencias en memoria, si modificaste el objeto 'gasto'
        // y ese objeto ya está en la lista 'gastos', solo hace falta guardar.
        if (gastos == null) getGastos();
        
        try {
            guardar(gastos, rutaGastos);
        } catch (Exception e) {
            log.error("Error actualizando fichero tras modificar gasto", e);
            throw new ErrorPersistenciaException(e);
        }
    }
    
    // --- IMPLEMENTACIÓN DE LA INTERFAZ (CATEGORÍAS) ---
    
    @Override
    public List<Categoria> getCategorias() {
        if (categorias == null) {
            try {
                cargaGastosYCategorias();
            } catch (ErrorPersistenciaException e) {
                throw new RuntimeException("CRITICAL: No se pueden leer las categorías.", e);
            }
        }
        return categorias;
    }

    @Override
    public Categoria getCategoria(String nombreBuscado) {
        if (nombreBuscado == null) return null;
        
        // Lazy loading seguro
        getCategorias(); 
        
        return categorias.stream()
                .filter(c -> c != null && c.getId() != null)
                .filter(c -> c.getId().equalsIgnoreCase(nombreBuscado))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addCategoria(Categoria categoria) throws ElementoExistenteException, ErrorPersistenciaException {
        if (categorias == null) getCategorias();
        
        if (categorias.contains(categoria)) {
            throw new ElementoExistenteException("La categoría '" + categoria.getId() + "' ya existe.");
        }
        
        categorias.add(categoria);
        
        try {
            guardar(categorias, rutaCategorias);
        } catch (Exception e) {
            categorias.remove(categoria); // Rollback
            log.error("Error guardando categoría", e);
            throw new ErrorPersistenciaException(e);
        }
    }

    // --- ESCRITURA GENÉRICA ---
        
    private <T> void guardar(List<T> elementos, String rutaFichero) throws Exception {
        URL url = getClass().getResource(rutaFichero);
        
        if (url == null) {
            throw new IOException("El fichero de recursos no se encuentra: " + rutaFichero);
        }
        
        try {
            File ficheroJSon = Paths.get(url.toURI()).toFile();
            
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // Vital para fechas
            
            // Escribimos con Pretty Printer para que sea legible por humanos
            mapper.writerWithDefaultPrettyPrinter().writeValue(ficheroJSon, elementos);
            
            log.info("Persistencia OK: " + ficheroJSon.getName());
            
        } catch (IOException | URISyntaxException e) {
            log.error("Error escribiendo en disco", e);
            throw e;
        }
    }
}
