package umu.tds.modelo;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = RepartoEquitativo.class, name = "equitativo"),
    @JsonSubTypes.Type(value = RepartoPorcentual.class, name = "porcentual")
})
public interface EstrategiaReparto {
	public void calcular(Gasto nuevoGasto, Map<Usuario, Double> saldosActuales);
	
	public boolean esSumaValida();
}
