package umu.tds.modelo;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Usuario {

	@JsonProperty("user_id")
	private String id;

	public Usuario() {}

	public Usuario(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String user_id) {
		this.id = user_id;
	}

	@Override
	public String toString() {
		return id;
	}
	
	@Override
	public int hashCode() {
	    // Usamos el id como identificador único
	    return Objects.hash(this.id); 
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(this.id, other.id);
	}

	public boolean isUsuario(String nombre) {
		return this.id.equals(nombre);
	}
	
	@JsonIgnore
	public String getLogin() {
	    return this.id; 
	}
}
