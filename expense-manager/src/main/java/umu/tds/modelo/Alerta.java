package umu.tds.modelo;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Alerta {
	private double limite;
	private IEstrategiaAlerta estrategia;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Categoria categoria = null;

    // NUEVO: Atributo para controlar si ya se avisó al usuario para este periodo/estado
    private boolean fueNotificada = false; 
	
	public Alerta() {
		this(0.0, null);
	}
	
	public Alerta(double limite, IEstrategiaAlerta estrategia) {
		this.limite = limite;
		this.estrategia = estrategia;
        this.fueNotificada = false; // Por defecto no ha sido notificada
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

    // NUEVOS MÉTODOS para el control de notificaciones
    public boolean isFueNotificada() {
        return fueNotificada;
    }

    public void setFueNotificada(boolean fueNotificada) {
        this.fueNotificada = fueNotificada;
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
    	
        // Comprobamos si se supera el límite mediante la estrategia (semanal/mensual)
    	boolean superada = estrategia.verificar(gastosAAnalizar, this.limite);

        // Si el total baja del límite (por ejemplo, al borrar un gasto), 
        // reseteamos el estado para que pueda volver a saltar el aviso en el futuro.
        if (!superada) {
            this.fueNotificada = false;
        }

        return superada;
    }
	
	@Override
	public String toString() {
	    String periodo = (estrategia == null) ? "Sin estrategia" : estrategia.getDescripcionPeriodo();
	    String textoCat = (categoria == null) ? "Todas" : categoria.toString();
	    return String.format("Límite: %.2f€ | Periodo: %s | Cat: %s", limite, periodo, textoCat);
	}
}