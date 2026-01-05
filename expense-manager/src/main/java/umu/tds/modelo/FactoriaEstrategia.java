package umu.tds.modelo;

import java.util.Map;
import java.util.Set;

public class FactoriaEstrategia {
	// 1. Instancia única privada
    private static FactoriaEstrategia unicaInstancia;

    // 2. Constructor privado para evitar instanciación externa
    private FactoriaEstrategia() {}

    // 3. Método estático para obtener la instancia
    public static FactoriaEstrategia getInstancia() {
        if (unicaInstancia == null) {
            unicaInstancia = new FactoriaEstrategia();
        }
        return unicaInstancia;
    }

    // Método para crear la estrategia (Factory Method)
    public EstrategiaReparto crearEstrategia(String tipo, Map<Usuario, Double> porcentajes, Set<Usuario> usuarios) {
        if (tipo.equalsIgnoreCase("PORCENTUAL")) {
            return new RepartoPorcentual(porcentajes);
        } 
        
        if (tipo.equalsIgnoreCase("EQUITATIVO")) {
            // Usamos el Set de usuarios que ya tenemos en el controlador
            return new RepartoEquitativo(usuarios);
        }

        throw new IllegalArgumentException("Tipo de estrategia no reconocido: " + tipo);
    }
    
    public IEstrategiaAlerta crearEstrategiaAlerta(String tipo) {
        if (tipo.equalsIgnoreCase("SEMANAL")) {
            return new EstrategiaAlertaSemanal();
        }
        if (tipo.equalsIgnoreCase("MENSUAL")) {
            return new EstrategiaAlertaMensual();
        }
        throw new IllegalArgumentException("Periodo de alerta no reconocido: " + tipo);
    }
    
}
