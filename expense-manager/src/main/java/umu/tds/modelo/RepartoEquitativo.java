package umu.tds.modelo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore; // Importante si no queremos guardar esto en JSON redundante

public class RepartoEquitativo implements EstrategiaReparto {

    // Cache para la vista (mostrar 33%, 33%...)
    private Map<Usuario, Double> porcentajes;
    
    // Constructor vacío (Jackson)
    public RepartoEquitativo() {
        this.porcentajes = new HashMap<>();
    }
    
    // Constructor de uso normal
    public RepartoEquitativo(Set<Usuario> usuarios) {
        this.porcentajes = new HashMap<>();
        if (usuarios != null && !usuarios.isEmpty()) {
            // Evitamos división por cero y magic numbers
            double cuotaPorcentual = 100.0 / usuarios.size();
            usuarios.forEach(u -> porcentajes.put(u, cuotaPorcentual));
        }
    }

    @Override
    public void calcular(Gasto nuevoGasto, Map<Usuario, Double> saldosActuales) {
        // 1. Contrato
        if (nuevoGasto == null || saldosActuales == null || saldosActuales.isEmpty()) {
            return; //Return es seguro aquí.
        }

        double importeTotal = nuevoGasto.getImporte();
        Usuario pagador = nuevoGasto.getPagador();
        int numParticipantes = saldosActuales.size();
        
        // Cuota a pagar por cada uno (en dinero)
        double cuotaIndividual = importeTotal / numParticipantes;

        saldosActuales.replaceAll((usuario, saldoActual) -> {
            
            if (usuario.equals(pagador)) {
                // El pagador "recupera" lo que puso menos su propia parte
                // Ejemplo: Pagué 30€, somos 3 (10€ c/u). Me deben 20€ (30 - 10).
                // Saldo += 20.
                return saldoActual + (importeTotal - cuotaIndividual);
            } else {
                // Los demás restan su cuota (deben dinero)
                return saldoActual - cuotaIndividual;
            }
        });
    }

    @Override
    public boolean esSumaValida() {
        return true; // En reparto equitativo, la matemática siempre cuadra
    }

    @Override
    public Map<Usuario, Double> getPorcentajes() {
        // Devolvemos la configuración inicial
    	return Collections.unmodifiableMap(this.porcentajes);
    }
}