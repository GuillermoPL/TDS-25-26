package umu.tds.modelo;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Usuario {

	@JsonProperty("user_id")
	private String id;

	public Usuario() {
		this(null);
	}

	public Usuario(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
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
}
