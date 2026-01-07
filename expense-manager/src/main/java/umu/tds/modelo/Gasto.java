package umu.tds.modelo;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Gasto {
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("importe")
	private double importe;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate fecha;
	
	@JsonProperty("categoria")
	private Categoria categoria;
	
	@JsonProperty("pagador")
	private Usuario pagador;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String cuenta;
	
	// Constructor vacío requerido por Jackson
	public Gasto() {
		// Jackson inyectará los valores mediante reflexión/setters
	}
	
	// Constructor de conveniencia (sin fecha explícita, usa "ahora", sin cuenta)
	public Gasto(String id, double importe, Categoria categoria, Usuario pagador) {
		this(id, importe, LocalDate.now(), categoria, pagador, null);
	}
	
	// Constructor de conveniencia (con fecha, sin cuenta)
	public Gasto(String id, double importe, LocalDate fecha, Categoria categoria, Usuario pagador) {
		this(id, importe, fecha, categoria, pagador, null);
	}
	
	// CONSTRUCTOR MAESTRO: Todos los caminos llevan a Roma (aquí)
	// Aquí es donde centralizamos las validaciones del Contrato.
	public Gasto(String id, double importe, LocalDate fecha, Categoria categoria, Usuario pagador, String cuenta) {
		validarDatos(id, importe, fecha, categoria, pagador);
		
		this.id = id;
		this.importe = importe;
		this.fecha = fecha;
		this.categoria = categoria;
		this.pagador = pagador;
		this.cuenta = cuenta;
	}

	// Método auxiliar para no repetir código en constructores y setters
	private void validarDatos(String id, double importe, LocalDate fecha, Categoria categoria, Usuario pagador) {
		if (id == null || id.trim().isEmpty()) {
			throw new IllegalArgumentException("El ID del gasto no puede ser nulo o vacío");
		}
		if (importe < 0) {
			throw new IllegalArgumentException("El importe no puede ser negativo");
		}
		if (fecha == null) {
			throw new IllegalArgumentException("La fecha es obligatoria");
		}
		if (categoria == null) {
			throw new IllegalArgumentException("La categoría es obligatoria");
		}
		if (pagador == null) {
			throw new IllegalArgumentException("El pagador es obligatorio");
		}
	}

	// --- Getters y Setters con Validaciones ---

	public String getId() {
		return id;
	}

	public void setId(String id) {
		// Permitimos que Jackson lo setee, pero validamos
		if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID inválido");
		this.id = id;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		if (importe < 0) throw new IllegalArgumentException("El importe no puede ser negativo");
		this.importe = importe;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		if (fecha == null) throw new IllegalArgumentException("La fecha no puede ser nula");
		this.fecha = fecha;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		if (categoria == null) throw new IllegalArgumentException("Categoría nula");
		this.categoria = categoria;
	}
	
	// "Tell, don't ask". Delegamos la comparación al objeto.
	public boolean isCategoria(Categoria categoria) {
		if (this.categoria == null || categoria == null) return false;
		return this.categoria.equals(categoria);
	}

	public Usuario getPagador() {
		return pagador;
	}

	public void setPagador(Usuario pagador) {
		if (pagador == null) throw new IllegalArgumentException("Pagador nulo");
		this.pagador = pagador;
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	
	// --- Métodos de Dominio (Domain Logic) ---
	
	public boolean realizadoEnUltimoMes() {
		if (this.fecha == null) return false;
		return this.fecha.isAfter(LocalDate.now().minusMonths(1));
	}
	
	public boolean realizadoEnUltimaSemana() {
		if (this.fecha == null) return false;
		return this.fecha.isAfter(LocalDate.now().minusWeeks(1));
	}

	// --- Identidad (HashCode & Equals) ---
	
	@Override
	public int hashCode() {
		return Objects.hash(id); 
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Gasto other = (Gasto) obj;
		return Objects.equals(id, other.id);
	}
    
    @Override
    public String toString() {
        return String.format("Gasto [id=%s, importe=%.2f, fecha=%s, cat=%s]", id, importe, fecha, categoria);
    }
}