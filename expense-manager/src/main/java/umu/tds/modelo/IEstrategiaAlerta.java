package umu.tds.modelo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Estrategia temporal para verificar si se ha superado un límite de gasto.
 * (Patrón Strategy).
 */
@JsonTypeInfo(
 use = JsonTypeInfo.Id.NAME, 
 include = JsonTypeInfo.As.PROPERTY, 
 property = "tipo"
)
@JsonSubTypes({
 @JsonSubTypes.Type(value = EstrategiaAlertaSemanal.class, name = "semanal"),
 @JsonSubTypes.Type(value = EstrategiaAlertaMensual.class, name = "mensual")
})
public interface IEstrategiaAlerta {
    
    /**
     * Verifica si la suma de los gastos en el periodo de tiempo definido por la estrategia
     * supera el límite establecido.
     * @param gastos Lista completa de gastos a filtrar por fecha.
     * @param limite Cantidad monetaria máxima permitida.
     * @return true si se supera el límite, false en caso contrario.
     */
    boolean verificar(List<Gasto> gastos, double limite);


    /**
     * Devuelve una descripción legible del periodo que evalúa esta estrategia
     * (por ejemplo, "Semanal" o "Mensual"). Se utiliza en mensajes de UI y logs.
     *
     * @return Texto descriptivo del periodo.
     */
    String getDescripcionPeriodo();
}