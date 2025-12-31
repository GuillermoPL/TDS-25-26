package umu.tds.modelo;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
	
	//TODO: POR AHORA IGNORO LA CUENTA
	@JsonIgnore
	private CuentaCompartida cuenta;
	
	
	public Gasto() {
		this(null, 0.0, null, null, null);
	}
	
	public Gasto(String id, double importe, Categoria categoria, Usuario pagador) {
		this.id = id;
		this.importe = importe;
		fecha = LocalDate.now();
		this.categoria = categoria;
		this.pagador = pagador;
		this.cuenta = null;
	}
	
	public Gasto(String id, double importe, LocalDate fecha, Categoria categoria, Usuario pagador) {
		this(id, importe, categoria, pagador);
		this.fecha = fecha;
	}
	
	public Gasto(String id, double importe, LocalDate fecha, Categoria categoria, Usuario pagador, CuentaCompartida cuenta) {
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

	public Usuario getPagador() {
		return pagador;
	}

	public void setPagador(Usuario pagador) {
		this.pagador = pagador;
	}

	public CuentaCompartida getCuenta() {
		return cuenta;
	}

	public void setCuenta(CuentaCompartida cuenta) {
		this.cuenta = cuenta;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Gasto other = (Gasto) obj;
        // Asumimos que dos gastos son iguales si tienen el mismo ID
        return id != null && id.equals(other.id);
    }
	
	@Override
    public int hashCode() {
		if (id != null) {
			return 0;
		}
		return id.hashCode();
    }
    
    @Override
    public String toString() {
        return "Gasto [id=" + id + ", importe=" + importe + ", categoría=" + categoria + "]";
    }
	
	
}
