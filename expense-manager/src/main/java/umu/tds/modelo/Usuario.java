package umu.tds.modelo;

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
}
