package umu.tds.modelo;

import java.util.Map;

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
    public EstrategiaReparto crearEstrategia(String tipo, Map<Usuario, Double> porcentajes) {
        if (tipo.equalsIgnoreCase("PORCENTUAL")) {
            return new RepartoPorcentual(porcentajes);
        } 
        
        if (tipo.equalsIgnoreCase("EQUITATIVO")) {
            return new RepartoEquitativo();
        }

        // Si llega aquí, es un tipo que no conocemos. 
        // Lo más seguro es lanzar una excepción para avisar al programador o a la vista.
        throw new IllegalArgumentException("Tipo de estrategia no reconocido: " + tipo);
    }
}
