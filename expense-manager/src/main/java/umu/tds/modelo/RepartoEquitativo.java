package umu.tds.modelo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RepartoEquitativo implements EstrategiaReparto {
    // Atributo para almacenar los porcentajes calculados (todos los usuarios tienen el mismo porcentaje)
    private Map<Usuario, Double> porcentajes;
    
    
    public RepartoEquitativo() {}
    
    // Constructor que recibe los usuarios para pre-calcular el reparto equitativo
    public RepartoEquitativo(Set<Usuario> usuarios) {
        this.porcentajes = new HashMap<>();
        if (usuarios != null && !usuarios.isEmpty()) {
            double porcentajeEquitativo = 100.0 / usuarios.size();
            for (Usuario u : usuarios) {
                porcentajes.put(u, porcentajeEquitativo);
            }
        }
    }

    @Override
    public void calcular(Gasto nuevoGasto, Map<Usuario, Double> saldosActuales) {
        double importeTotal = nuevoGasto.getImporte();
        Usuario pagador = nuevoGasto.getPagador();
        
        int numParticipantes = saldosActuales.size();
        if (numParticipantes == 0) return; 
        
        double cuota = importeTotal / numParticipantes;

        for (Usuario usuario : saldosActuales.keySet()) {
            double saldoAnterior = saldosActuales.get(usuario);
            
            if (usuario.equals(pagador)) {
                double loQueLeDeben = importeTotal - cuota;
                saldosActuales.put(usuario, saldoAnterior + loQueLeDeben);
            } else {
                saldosActuales.put(usuario, saldoAnterior - cuota);
            }
        }
    }

    @Override
    public boolean esSumaValida() {
        return true; // Siempre es válido
    }

    @Override
    public Map<Usuario, Double> getPorcentajes() {
        return this.porcentajes;
    }

}