package umu.tds.modelo;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Categoria {
	
	@JsonProperty("id")
	private String id; // El nombre de la categoría actúa como ID
	
	// Constructor vacío para Jackson
	public Categoria() {
		// Jackson inicializa esto luego mediante reflexión/setters
	}
	
	// Constructor con validación (Contrato)
	public Categoria(String nombre) {
		setId(nombre);
	}
	
	public String getId() {
		return id;
	}
	
	// Setter validado
	public void setId(String id) {
		if (id == null || id.trim().isEmpty()) {
			throw new IllegalArgumentException("El nombre de la categoría no puede ser nulo o vacío");
		}
		this.id = id;
	}
	
	@Override
	public String toString() {
		return id;
	}
	
	// --- IDENTIDAD ---
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		Categoria other = (Categoria) obj;
		
		// Usamos Objects.equals para evitar NullPointerException si this.id fuera null (aunque el setter lo protege)
		return Objects.equals(this.id, other.id);
	}
}