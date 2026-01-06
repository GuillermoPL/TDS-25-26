package umu.tds.modelo;

import java.time.LocalDate;

public class Notificacion {
	private String mensaje;
    private LocalDate fecha;
	private Alerta alertaOrigen;
    
    public Notificacion() {}
	
    public Notificacion(String mensaje, LocalDate fecha, Alerta alerta) {
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.alertaOrigen = alerta;
    }
    
    public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public Alerta getAlertaOrigen() {
		return alertaOrigen;
	}

	public void setAlertaOrigen(Alerta alertaOrigen) {
		this.alertaOrigen = alertaOrigen;
	}
	

    @Override
    public String toString() {
        return "[" + fecha + "] " + mensaje;
    }
}
