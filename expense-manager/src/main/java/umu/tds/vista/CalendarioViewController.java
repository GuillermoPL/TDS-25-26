package umu.tds.vista;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import umu.tds.Configuracion;
import umu.tds.controlador.ControladorAppGastos;
import umu.tds.modelo.EventoSistema;
import umu.tds.modelo.Gasto;

public class CalendarioViewController implements IObservador {

    @FXML
    private BorderPane contenedorCalendario;

    private CalendarView calendarView;
    private Calendar gastosCalendar;

    @FXML
    public void initialize() {
        // 1. Instanciar el controlador principal
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        ctrl.registrarObservador(this);

        // 2. Configurar CalendarFX
        calendarView = new CalendarView();
        
        // Creamos una "Fuente" de calendarios (ej: Google, Local, etc.)
        CalendarSource myCalendarSource = new CalendarSource("Mis Finanzas");
        
        // Creamos nuestro calendario específico de Gastos
        gastosCalendar = new Calendar("Gastos");
        gastosCalendar.setStyle(Style.STYLE1); // Color rojo/rosado por defecto
        gastosCalendar.setReadOnly(true);      // Para que no editen arrastrando en el calendario
        
        myCalendarSource.getCalendars().add(gastosCalendar);
        calendarView.getCalendarSources().setAll(myCalendarSource);

        // 3. Limpieza visual (Opcional: quitar paneles laterales si molestan)
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);
        calendarView.setShowPageToolBarControls(false);

        // 4. Inyectarlo en el FXML
        contenedorCalendario.setCenter(calendarView);

        // 5. Cargar los datos iniciales
        cargarGastosEnCalendario();
    }

    private void cargarGastosEnCalendario() {
        gastosCalendar.clear(); // Limpiar anteriores
        
        ControladorAppGastos ctrl = Configuracion.getInstancia().getControladorAppGastos();
        
        // Recorremos tus gastos y creamos "Entries"
        for (Gasto g : ctrl.getGastosPorCondicion(x -> true)) {
            // Título: Cantidad + Categoría
            String titulo = String.format("%.2f€ - %s", g.getImporte(), g.getCategoria().getId());
            
            Entry<Gasto> entry = new Entry<>(titulo);
            
            // Asignamos la fecha (CalendarFX usa LocalDateTime o LocalDate)
            entry.setInterval(g.getFecha()); 
            // Como Gasto no tiene hora, asumimos todo el día o una hora fija
            entry.setFullDay(true); 
            
            // Guardamos el objeto Gasto real dentro por si hacemos click luego
            entry.setUserObject(g);
            
            gastosCalendar.addEntry(entry);
        }
    }

    @Override
    public void actualizar(EventoSistema evento, Object datos) {
        // Si se añade o borra un gasto, refrescamos el calendario
        if (evento == EventoSistema.NUEVO_GASTO || 
            evento == EventoSistema.GASTO_ELIMINADO || 
            evento == EventoSistema.GASTO_MODIFICADO) {
            
            // Importante: CalendarFX a veces requiere correr en el hilo de FX
            javafx.application.Platform.runLater(this::cargarGastosEnCalendario);
        }
    }
}