package umu.tds.modelo;

import java.util.List;
import java.util.stream.Collectors;

public class EstrategiaAlertaSemanal implements IEstrategiaAlerta{

	@Override
	public boolean verificar(List<Gasto> gastosAAnalizar, double limite) {
		
		List<Gasto> gastosUltimaSemana = gastosAAnalizar.stream()
													.filter(Gasto::realizadoEnUltimaSemana)
													.collect(Collectors.toList());
		double gastoAcumulado = gastosUltimaSemana.stream()
												.collect(Collectors.summingDouble(Gasto::getImporte));
		
		
		return gastoAcumulado > limite;
	}
}
