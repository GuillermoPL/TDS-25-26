package umu.tds.modelo;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Interfaz del Patrón Strategy para definir algoritmos de reparto de gastos.
 * Utiliza anotaciones de Jackson para permitir polimorfismo en la persistencia JSON.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = RepartoEquitativo.class, name = "equitativo"),
    @JsonSubTypes.Type(value = RepartoPorcentual.class, name = "porcentual")
})
public interface EstrategiaReparto {
	
	/**
	 * Calcula la parte correspondiente a cada usuario y actualiza sus saldos.
	 * @param nuevoGasto El gasto que se acaba de añadir.
	 * @param saldosActuales Mapa (Usuario -> Deuda) que será MODIFICADO por este método.
	 */
	void calcular(Gasto nuevoGasto, Map<Usuario, Double> saldosActuales);
	
	/**
	 * Valida si la configuración de la estrategia es correcta.
	 * @return true si la estrategia es válida (ej: porcentajes suman 100%), false en caso contrario.
	 */
	boolean esSumaValida();
	
	/**
	 * Obtiene la configuración de porcentajes si la estrategia lo requiere.
	 * @return Un mapa con los porcentajes asignados a cada usuario. 
	 * Devuelve un mapa vacío si la estrategia no usa porcentajes explícitos (ej: Equitativo).
	 */
	Map<Usuario, Double> getPorcentajes();
}