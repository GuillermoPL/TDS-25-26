package umu.tds.modelo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RepartoPorcentual implements EstrategiaReparto {
    
    private Map<Usuario, Double> porcentajes;
    
    // Constructor vacío (Jackson)
    public RepartoPorcentual() {
        this.porcentajes = new HashMap<>();
    }
    
    // Constructor normal
    public RepartoPorcentual(Map<Usuario, Double> porcentajes) {
        if (porcentajes == null) {
            throw new IllegalArgumentException("El mapa de porcentajes no puede ser nulo.");
        }
        // Hacemos copia defensiva para que nadie toque los porcentajes desde fuera después de crearla
        this.porcentajes = new HashMap<>(porcentajes);
    }

    @Override
    public void calcular(Gasto nuevoGasto, Map<Usuario, Double> saldosActuales) {
        if (nuevoGasto == null || saldosActuales == null) return;

        double importeTotal = nuevoGasto.getImporte();
        Usuario pagador = nuevoGasto.getPagador();

        // REFACTORIZACIÓN CON LAMBDA (Map.replaceAll)
        // Eliminamos el bucle for explícito. Mucho más limpio y moderno.
        saldosActuales.replaceAll((usuario, saldoActual) -> {
            
            // 1. Obtenemos porcentaje (0.0 si el usuario no está en la config de porcentajes)
            double porcentaje = porcentajes.getOrDefault(usuario, 0.0);
            
            // 2. Calculamos la cuota
            double cuotaCorrespondiente = (importeTotal * porcentaje) / 100.0;
            
            // 3. Aplicamos lógica de deuda
            if (usuario.equals(pagador)) {
                // Pagador: recupera (Total - su_parte)
                // Saldo aumenta (a su favor)
                return saldoActual + (importeTotal - cuotaCorrespondiente);
            } else {
                // Resto: deben su parte
                // Saldo disminuye (deuda)
                return saldoActual - cuotaCorrespondiente;
            }
        });
    }
    
    @Override
    public boolean esSumaValida() {
        if (porcentajes == null || porcentajes.isEmpty()) return false;
        
        // Versión simplificada con Streams (mapToDouble + sum)
        double suma = porcentajes.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        
        // Margen de error para punto flotante (Epsilon)
        return Math.abs(suma - 100.0) < 0.01;
    }
    
    @Override
    public Map<Usuario, Double> getPorcentajes(){
        // Devolvemos vista inmodificable para proteger el atributo
        return Collections.unmodifiableMap(porcentajes);
    }

}