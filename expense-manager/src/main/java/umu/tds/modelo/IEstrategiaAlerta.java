package umu.tds.modelo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// Diferenciamos las alertas según su tipo
@JsonTypeInfo(
 use = JsonTypeInfo.Id.NAME, 
 include = JsonTypeInfo.As.PROPERTY, 
 property = "tipo"
)

@JsonSubTypes({
 @JsonSubTypes.Type(value = AlertaSemanal.class, name = "semanal"),
 @JsonSubTypes.Type(value = AlertaMensual.class, name = "mensual")
})
public interface IEstrategiaAlerta {
	public boolean verificar(List<Gasto> gastosAAnalizar, double limite);
}
