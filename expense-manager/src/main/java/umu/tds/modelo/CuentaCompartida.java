package umu.tds.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CuentaCompartida {
    
    private String nombre;
    private Map<Usuario, Double> saldosPorUsuario;
    private EstrategiaReparto estrategia;
    private List<Gasto> gastos; 
    
    // Constructor vacío (Jackson)
    public CuentaCompartida() {
        this.gastos = new ArrayList<>();
        this.saldosPorUsuario = new HashMap<>();
    }
    
    // Constructor con Diseño por Contrato
    public CuentaCompartida(String nombre, EstrategiaReparto estrategia, Set<Usuario> usuarios) {
        setNombre(nombre);
        setEstrategia(estrategia);
        
        if (usuarios == null || usuarios.isEmpty()) {
            throw new IllegalArgumentException("Una cuenta compartida debe tener al menos un usuario.");
        }
        
        this.saldosPorUsuario = new HashMap<>();
        this.gastos = new ArrayList<>();
        
        // Inicializamos saldos a 0
        for(Usuario u : usuarios) {
            saldosPorUsuario.put(u, 0.0);
        }
    }
    
    // --- Getters y Setters ---

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la cuenta es obligatorio.");
        }
        this.nombre = nombre;
    }

    public EstrategiaReparto getEstrategia() {
        return estrategia;
    }
    
    public void setEstrategia(EstrategiaReparto estrategia) {
        if (estrategia == null) {
            throw new IllegalArgumentException("La cuenta necesita una estrategia de reparto.");
        }
        this.estrategia = estrategia;
    }

    // --- ENCAPSULAMIENTO --

    public Map<Usuario, Double> getSaldosPorUsuario() {
        // Devolvemos una COPIA. Si alguien la modifica fuera, no rompe nuestro estado interno.
        return new HashMap<>(saldosPorUsuario);
    }
    
    // Setter para Jackson (o para restaurar estado), validando nulos
    public void setSaldosPorUsuario(Map<Usuario, Double> saldosPorUsuario) {
        if (saldosPorUsuario == null) throw new IllegalArgumentException("El mapa de saldos no puede ser nulo");
        this.saldosPorUsuario = saldosPorUsuario;
    }
    
    public List<Gasto> getGastos() {
        if (this.gastos == null) this.gastos = new ArrayList<>();
        // Devolvemos vista inmodificable o copia.
        // Collections.unmodifiableList impide que hagan .add() o .remove() desde fuera.
        return Collections.unmodifiableList(this.gastos);
    }

    public void setGastos(List<Gasto> gastos) {
        if (gastos == null) throw new IllegalArgumentException("La lista de gastos no puede ser nula");
        this.gastos = gastos;
    }

    // --- LÓGICA DE NEGOCIO ---

    public void addGasto(Gasto gastoNuevo) {
        // 1. Contrato básico
        Objects.requireNonNull(gastoNuevo, "No se puede añadir un gasto nulo a la cuenta.");
        
        // 2. Validación de Negocio: ¿El pagador pertenece a la cuenta?
        if (!saldosPorUsuario.containsKey(gastoNuevo.getPagador())) {
            throw new IllegalArgumentException("El usuario pagador (" + gastoNuevo.getPagador().getId() + 
                                               ") no pertenece a esta cuenta compartida.");
        }

        // 3. Estado
        if (this.gastos == null) this.gastos = new ArrayList<>();
        this.gastos.add(gastoNuevo);
        
        // 4. Delegación (Strategy Pattern)
        estrategia.calcular(gastoNuevo, saldosPorUsuario);
    }
    
    @Override
    public String toString() {
        return this.nombre;
    }
    
    @JsonIgnore
    public Map<Usuario, Double> getPorcentajesEstrategia() {
        if (estrategia == null) return new HashMap<>();
        return estrategia.getPorcentajes(); 
    }

	public boolean yaTieneGasto(Gasto gasto) {
		return getGastos().stream()
						.anyMatch(g -> g.getId().equals(gasto.getId()));
	}
}