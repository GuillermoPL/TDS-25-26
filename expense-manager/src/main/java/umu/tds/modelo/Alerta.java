package umu.tds.modelo;

public class Alerta {
	private double limite;
	private IEstrategiaAlerta estrategia;
	
	public Alerta(double limite, IEstrategiaAlerta estrategia) {
		this.limite = limite;
		this.estrategia = estrategia;
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
	
	
}
