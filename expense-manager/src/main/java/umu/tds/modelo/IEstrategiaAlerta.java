package umu.tds.modelo;

import java.util.List;

public interface IEstrategiaAlerta {
	public boolean verificar(Gasto gasto, List<Gasto> gastos);
}
