package umu.tds.modelo;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Usuario {

    @JsonProperty("user_id")
    private String id; // En este dominio, el ID actúa como Login/Email

    // Constructor vacío requerido por Jackson
    public Usuario() {
    }

    // Constructor con validación (Contrato)
    public Usuario(String id) {
        setId(id); // Delegamos en el setter para no repetir la validación
    }

    public String getId() {
        return id;
    }
    
    // Setter protegido por Contrato
    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del usuario (login) no puede ser nulo ni vacío.");
        }
        this.id = id;
    }

    // Método de utilidad para buscar por nombre/login sin exponer la lógica de comparación
    public boolean isUsuario(String identificador) {
        if (identificador == null) return false;
        return this.id.equalsIgnoreCase(identificador); // IgnoreCase es más amigable para logins
    }
    
    // Alias semántico (Mejora la legibilidad en el resto del código)
    @JsonIgnore
    public String getLogin() {
        return this.id; 
    }

    @Override
    public String toString() {
        return id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id); 
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Usuario other = (Usuario) obj;
        return Objects.equals(this.id, other.id);
    }
}