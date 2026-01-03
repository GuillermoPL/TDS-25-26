package umu.tds.modelo;

import java.util.List;
import java.util.stream.Collectors;

public class AlertaMensual implements IEstrategiaAlerta{

	@Override
	public boolean verificar(List<Gasto> gastosAAnalizar, double limite) {
		
		List<Gasto> gastosUltimoMes = gastosAAnalizar.stream()
													.filter(Gasto::realizadoEnUltimoMes)
													.collect(Collectors.toList());
		double gastoAcumulado = gastosUltimoMes.stream()
												.collect(Collectors.summingDouble(Gasto::getImporte));
		
		
		return gastoAcumulado < limite;
	}

}
