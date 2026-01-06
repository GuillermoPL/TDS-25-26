package umu.tds.modelo;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Gasto {
	
	// Consideraremos que el ID es el identificado ÚNICO de cada gasto
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
	
	
	public Gasto() {
		this(null, 0.0, null, null, null);
	}
	
	public Gasto(String id, double importe, Categoria categoria, Usuario pagador) {
		this.id = id;
		this.importe = importe;
		this.fecha = LocalDate.now();
		this.categoria = categoria;
		this.pagador = pagador;
		this.cuenta = null;
	}
	
	public Gasto(String id, double importe, LocalDate fecha, Categoria categoria, Usuario pagador) {
		this(id, importe, categoria, pagador);
		this.fecha = fecha;
	}
	
	public Gasto(String id, double importe, LocalDate fecha, Categoria categoria, Usuario pagador, String cuenta) {
		this(id, importe, fecha, categoria, pagador);
		this.cuenta = cuenta;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	
	public boolean isCategoria(Categoria categoria) {
		return this.categoria.equals(categoria);
	}

	public Usuario getPagador() {
		return pagador;
	}

	public void setPagador(Usuario pagador) {
		this.pagador = pagador;
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	
	@Override
	public int hashCode() {
	    return (id == null) ? 0 : id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;
	    Gasto other = (Gasto) obj;
	    return id != null && id.equals(other.id);
	}
    
    @Override
    public String toString() {
        return "Gasto [id=" + id + ", importe=" + importe + ", categoría=" + categoria + "]";
    }

	public boolean realizadoEnUltimoMes() {
		return this.fecha.isAfter(LocalDate.now().minusMonths(1));
	}
	
	public boolean realizadoEnUltimaSemana() {
		return this.fecha.isAfter(LocalDate.now().minusWeeks(1));
	}
	
	
}
