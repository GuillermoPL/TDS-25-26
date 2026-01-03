package umu.tds;

import umu.tds.controlador.ControladorAppGastos;
import umu.tds.adapters.repository.impl.RepositorioGastosJSON;
import umu.tds.adapters.repository.impl.RepositorioCuentasJSON;

public class ConfiguracionImpl extends Configuracion {
    private ControladorAppGastos controlador;

    public ConfiguracionImpl() {
        // Inyectamos los repositorios concretos al controlador
        this.controlador = new ControladorAppGastos(
            new RepositorioGastosJSON(), 
            new RepositorioCuentasJSON()
        );
    }

    @Override
    public ControladorAppGastos getControladorAppGastos() {
        return controlador;
    }

    @Override public String getRutaGastos() { return "/data/gastos.json"; }
    @Override public String getRutaCuentas() { return "/data/cuentas.json"; }
    @Override public String getRutaUsuarios() { return "/data/usuarios.json"; }
}