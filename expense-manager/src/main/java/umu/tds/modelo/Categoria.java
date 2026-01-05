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
	
	public String getId() {
		return id;
	}
	
	public void setId(String cat_id) {
		this.id = cat_id;
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
		Categoria other = (Categoria) obj;
		return id.equals(other.id);
	}
	
	

}
