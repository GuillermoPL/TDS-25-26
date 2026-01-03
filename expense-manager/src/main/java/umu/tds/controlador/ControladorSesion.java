package umu.tds.controlador;

import umu.tds.modelo.Usuario;

public class ControladorSesion {
    private static ControladorSesion unicaInstancia;
    private Usuario usuarioActual;

    private ControladorSesion() {

    }

    public static ControladorSesion getInstancia() {
        if (unicaInstancia == null) unicaInstancia = new ControladorSesion();
        return unicaInstancia;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public boolean isSesionIniciada() {
        return usuarioActual != null;
    }
}