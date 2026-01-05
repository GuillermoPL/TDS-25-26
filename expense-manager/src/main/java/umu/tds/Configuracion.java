package umu.tds;

import umu.tds.controlador.ControladorAppGastos;
import umu.tds.vista.SceneManager;

public abstract class Configuracion {
    private static Configuracion instancia;
    private final SceneManager sceneManager = new SceneManager();

    // Solo invocado desde App
    static void setInstancia(Configuracion impl) {
        Configuracion.instancia = impl;
    }

    public static Configuracion getInstancia() {
        return Configuracion.instancia;
    }

    // Métodos que cada implementación debe definir
    public abstract ControladorAppGastos getControladorAppGastos();
    public abstract String getRutaGastos();
    public abstract String getRutaCategorias();
    public abstract String getRutaCuentas();
    public abstract String getRutaUsuarios();
    public abstract String getRutaAlertas();
    public SceneManager getSceneManager() {
        return sceneManager;
    }
}