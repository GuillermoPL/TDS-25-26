package umu.tds.modelo;

import java.util.Map;
import java.util.Set;

/**
 * Factoría que centraliza la creación de estrategias (Reparto y Alertas).
 * Implementa el patrón Singleton y Simple Factory.
 */
public class FactoriaEstrategia {
    
    // 1. Singleton: Inicialización Eager (Thread-Safe y más limpia)
    private static final FactoriaEstrategia INSTANCE = new FactoriaEstrategia();

    // 2. Constructor privado
    private FactoriaEstrategia() {}

    // 3. Acceso global
    public static FactoriaEstrategia getInstancia() {
        return INSTANCE;
    }

    /**
     * Crea una estrategia de reparto según el tipo solicitado.
     * @param tipo "EQUITATIVO" o "PORCENTUAL".
     * @param porcentajes Mapa de porcentajes (necesario solo para PORCENTUAL).
     * @param usuarios Set de usuarios (necesario solo para EQUITATIVO).
     * @return La estrategia configurada.
     */
    public EstrategiaReparto crearEstrategia(String tipo, Map<Usuario, Double> porcentajes, Set<Usuario> usuarios) {
        // Contrato: Evitar NullPointerException
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de estrategia no puede ser nulo.");
        }

        // Usamos switch para mayor claridad (Clean Code)
        switch (tipo.toUpperCase().trim()) {
            case "PORCENTUAL":
                return new RepartoPorcentual(porcentajes);
            case "EQUITATIVO":
                return new RepartoEquitativo(usuarios);
            default:
                throw new IllegalArgumentException("Tipo de estrategia de reparto no reconocido: " + tipo);
        }
    }
    
    /**
     * Crea una estrategia de alerta temporal.
     * @param tipo "SEMANAL" o "MENSUAL".
     * @return La estrategia de alerta correspondiente.
     */
    public IEstrategiaAlerta crearEstrategiaAlerta(String tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El periodo de alerta no puede ser nulo.");
        }

        switch (tipo.toUpperCase().trim()) {
            case "SEMANAL":
                return new EstrategiaAlertaSemanal();
            case "MENSUAL":
                return new EstrategiaAlertaMensual();
            default:
                throw new IllegalArgumentException("Periodo de alerta no reconocido: " + tipo);
        }
    }
}