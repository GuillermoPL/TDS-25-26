package umu.tds.modelo;

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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return id.equals(other.id);
	}

	public boolean isUsuario(String nombre) {
		return this.id.equals(nombre);
	}
	
	@JsonIgnore
	public String getLogin() {
	    return this.id; 
	}
}
