package umu.tds.modelo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import umu.tds.Configuracion;

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
    
    public boolean verificar() {
    	List<Gasto> gastosAAnalizar;
    	
    	if (this.categoria == null) {
    		gastosAAnalizar = Configuracion.getInstancia().getRepositorioGastos().getGastos();
    	} else {
    		gastosAAnalizar = Configuracion.getInstancia().getRepositorioGastos()
    														.getGastosPorCategoria(this.categoria);
    	}
    	
    	return estrategia.verificar(gastosAAnalizar, this.limite);
    }
	
	
}
