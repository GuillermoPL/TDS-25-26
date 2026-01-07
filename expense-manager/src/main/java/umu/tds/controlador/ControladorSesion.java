package umu.tds.controlador;

import umu.tds.modelo.Usuario;

/**
 * Gestiona la sesión del usuario actual.
 * Implementa el patrón Singleton (Eager Initialization para Thread-Safety).
 */
public class ControladorSesion {
    
    // 1. Instancia estática final (Eager Loading)
    // Se crea al cargar la clase, garantizando que sea única y segura (Thread-Safe).
    private static final ControladorSesion INSTANCE = new ControladorSesion();
    
    private Usuario usuarioActual;

    // 2. Constructor privado
    private ControladorSesion() {}

    // 3. Acceso global
    public static ControladorSesion getInstancia() {
        return INSTANCE;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }
    
    // Método semántico para el Logout
    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public boolean isSesionIniciada() {
        return usuarioActual != null;
    }
}