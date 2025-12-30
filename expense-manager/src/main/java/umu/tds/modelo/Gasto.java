package umu.tds.modelo;

import java.time.LocalDate;
import java.util.Objects;

public class Gasto {
	private String id;
	private double importe;
	private LocalDate fecha;
	private Categoria categoria;
	private Usuario pagador;
	private CuentaCompartida cuenta;
	
	public Gasto(String id, double importe, LocalDate fecha, Categoria categoria, Usuario pagador) {
		this.id = id;
		this.importe = importe;
		fecha = LocalDate.now();
		this.categoria = categoria;
		this.pagador = pagador;
		this.cuenta = null;
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
	
	
}
