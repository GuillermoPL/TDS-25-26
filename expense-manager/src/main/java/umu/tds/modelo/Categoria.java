package umu.tds.modelo;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) //
public class Categoria {
	
	@JsonProperty("id")
	private String id;
	
	public Categoria() {
		this(null);
	}
	
	public Categoria(String id) {
		this.id = id;
	}
	
	public String getNombre() {
		return this.id; 
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Categoria other = (Categoria) obj;
		return Objects.equals(id, other.id); // Mejor usar Objects.equals para evitar null pointers
	}
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}