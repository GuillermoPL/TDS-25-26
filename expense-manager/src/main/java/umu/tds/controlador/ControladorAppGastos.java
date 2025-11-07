package umu.tds.controlador;
//importacion de las clases del paquete modelo. asi como de utilidades java.



//PATRON SINGLETON
public class ControladorAppGastos {
	private static ControladorAppGastos unicaInstancia;
	
	private ControladorAppGastos() {
		//inicializacion
    }
	
	public static ControladorAppGastos getInstancia() {
        if (unicaInstancia == null) {
            unicaInstancia = new ControladorAppGastos();
        }
        return unicaInstancia;
    }
	
	
}
