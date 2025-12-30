package umu.tds.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Categoria {
	
	@JsonProperty("id")
	private String id;
	
	public Categoria() {
		this(null);
	}
	
	public Categoria(String id) {
		this.id = id;
	}
	
	public String getCategoria() {
		return id;
	}
	
	public void setCategoria(String cat_id) {
		this.id = cat_id;
	}
	
	@Override
    public String toString() {
		return id;
	}

}
