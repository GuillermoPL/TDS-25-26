package umu.tds.modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;    
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CuentaCompartida {
    private String nombre;
    private Map<Usuario, Double> saldosPorUsuario;
    private EstrategiaReparto estrategia;
    
    private List<Gasto> gastos; 
    
    public CuentaCompartida() {
        this.gastos = new ArrayList<>();
    }
    
    public CuentaCompartida(String nombre, EstrategiaReparto estrategia, Set<Usuario> usuarios) {
        this.nombre = nombre;
        this.estrategia = estrategia;
        this.saldosPorUsuario = new HashMap<Usuario, Double>();
        for(Usuario u : usuarios) {
            saldosPorUsuario.put(u, 0.0);
        }
        this.gastos = new ArrayList<>();
    }
    
    public Map<Usuario, Double> getSaldosPorUsuario(){
        return saldosPorUsuario;
    }
    
    public void setSaldosPorUsuario(Map<Usuario, Double> saldosPorUsuario) {
        this.saldosPorUsuario = saldosPorUsuario;
    }

    public void setEstrategia(EstrategiaReparto estrategia) {
        this.estrategia = estrategia;
    }

    public EstrategiaReparto getEstrategia() {
        return estrategia;
    }
    
    // 3. CAMBIO IMPORTANTE: Guardamos el gasto en la lista Y calculamos
    public void addGasto(Gasto gastoNuevo) {
        if (this.gastos == null) this.gastos = new ArrayList<>();
        this.gastos.add(gastoNuevo); // Guardamos el objeto
        estrategia.calcular(gastoNuevo, saldosPorUsuario); // Calculamos deuda
    }

    // 4. NUEVO: El getter que te pedía el controlador
    public List<Gasto> getGastos() {
        if (this.gastos == null) this.gastos = new ArrayList<>();
        return this.gastos;
    }
    
    // Setter necesario para que Jackson (JSON) pueda cargar los gastos si fuera necesario
    public void setGastos(List<Gasto> gastos) {
        this.gastos = gastos;
    }

    @Override
    public String toString() {
        return this.nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    @JsonIgnore
    public Map<Usuario, Double> getPorcentajesEstrategia() {
        return estrategia.getPorcentajes(); 
    }

	public boolean yaTieneGasto(Gasto gasto) {
		return getGastos().stream()
						.anyMatch(g -> g.getId().equals(gasto.getId()));
	}
}