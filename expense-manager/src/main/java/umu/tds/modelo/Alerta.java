package umu.tds.modelo;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Alerta {
	private double limite;
	private IEstrategiaAlerta estrategia;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Categoria categoria = null;
	
	public Alerta() {
		this(0.0, null);
	}
	
	public Alerta(double limite, IEstrategiaAlerta estrategia) {
		this.limite = limite;
		this.estrategia = estrategia;
	}
	
	public Alerta(double limite, IEstrategiaAlerta estrategia, Categoria categoria) {
		this(limite, estrategia);
		this.categoria = categoria;
	}

	public double getLimite() {
		return limite;
	}

	public void setLimite(double limite) {
		this.limite = limite;
	}

	public IEstrategiaAlerta getEstrategia() {
		return estrategia;
	}

	public void setEstrategia(IEstrategiaAlerta estrategia) {
		this.estrategia = estrategia;
	}
	
	public Categoria getCategoria() {
		return categoria;
	}
	
    public void setCategoria(Categoria categoria) {
    	this.categoria = categoria;
    }
    
    
    public boolean verificarSiSuperada(List<Gasto> todosLosGastos) {
    	List<Gasto> gastosAAnalizar;
    	
    	if (this.categoria == null) {
    		gastosAAnalizar = todosLosGastos;
    	} else {
    		gastosAAnalizar = todosLosGastos.stream()
    										.filter(gasto -> gasto.isCategoria(this.categoria))
    										.collect(Collectors.toList());
    	}
    	
    	return estrategia.verificar(gastosAAnalizar, this.limite);
    }
	
	
}
