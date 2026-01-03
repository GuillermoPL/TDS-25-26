package umu.tds.vista;

import umu.tds.modelo.EventoSistema; 

public interface IObservador {
    void actualizar(EventoSistema evento, Object datos);
}