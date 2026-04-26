package umu.tds.controlador;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import umu.tds.adapters.repository.RepositorioAlertas;
import umu.tds.adapters.repository.RepositorioCuentas;
import umu.tds.adapters.repository.RepositorioGastos;
import umu.tds.adapters.repository.exceptions.ElementoExistenteException;
import umu.tds.adapters.repository.exceptions.ErrorPersistenciaException;
import umu.tds.modelo.Alerta;
import umu.tds.modelo.Categoria;
import umu.tds.modelo.CuentaCompartida;
import umu.tds.modelo.EstrategiaReparto;
import umu.tds.modelo.EventoSistema;
import umu.tds.modelo.FactoriaEstrategia;
import umu.tds.modelo.Gasto;
import umu.tds.modelo.IEstrategiaAlerta;
import umu.tds.modelo.Notificacion;
import umu.tds.modelo.Usuario;
import umu.tds.modelo.importacion.FactoriaImportadores;
import umu.tds.modelo.importacion.ImportadorGastos;
import umu.tds.modelo.importacion.exceptions.ImportacionException;
import umu.tds.vista.IObservador;

public class ControladorAppGastos {
    
    // --- CONSTANTES (Evitamos Magic Strings) ---
    private static final String CUENTA_PERSONAL = "Personal";
    private static final String PREFIJO_PERSONAL = "G-";
    private static final String PREFIJO_COMPARTIDO = "G-COMP-";
    
    private RepositorioGastos repoGastos;
    private RepositorioCuentas repoCuentas;
    private RepositorioAlertas repoAlertas;
    
    private List<IObservador> observadores = new LinkedList<>();

    // Constructor
    public ControladorAppGastos(RepositorioGastos repoGastos, RepositorioCuentas repoCuentas, RepositorioAlertas repoAlertas) {
        this.repoGastos = repoGastos;
        this.repoCuentas = repoCuentas;
        this.repoAlertas = repoAlertas;
    }
    
    // --- GESTIÓN DE USUARIOS Y CUENTAS ---

    public List<CuentaCompartida> getCuentasCompartidas() {
        return repoCuentas.getCuentas();
    }
    
    public List<String> getLoginsUsuarios() {
        return repoCuentas.getLoginsUsuarios();
    }

    public void crearCuentaCompartida(String nombre, String tipoEstrategia, Map<String, Double> datosVista) 
            throws ElementoExistenteException, ErrorPersistenciaException {
        
        Map<Usuario, Double> porcentajesUsuarios = new HashMap<>();
        Set<Usuario> usuarios = new HashSet<>();
        
        for (String login : datosVista.keySet()) {
            Usuario u = obtenerOCrearUsuario(login);
            usuarios.add(u);
            
            if ("PORCENTUAL".equalsIgnoreCase(tipoEstrategia)) {
                porcentajesUsuarios.put(u, datosVista.get(login));
            }
        }

        EstrategiaReparto estrategia = FactoriaEstrategia.getInstancia()
                                    .crearEstrategia(tipoEstrategia, porcentajesUsuarios, usuarios);

        if (!estrategia.esSumaValida()) {
            throw new IllegalArgumentException("La configuración del reparto no es válida (la suma no es correcta).");
        }

        CuentaCompartida nuevaCuenta = new CuentaCompartida(nombre, estrategia, usuarios);
        repoCuentas.addCuenta(nuevaCuenta);
        notificarCambio(EventoSistema.NUEVA_CUENTA, nuevaCuenta);
    }
    
    // Método auxiliar para limpiar lógica
    private Usuario obtenerOCrearUsuario(String login) throws ErrorPersistenciaException {
        Usuario u = repoCuentas.getUsuario(login);
        if (u == null) {
            u = new Usuario(login); 
            try {
                repoCuentas.addUsuario(u);
            } catch (ElementoExistenteException e) {
                // No debería pasar si acabamos de comprobar que es null, pero por seguridad
            }
        }
        return u;
    }
    
    // --- INICIALIZACIÓN ---
    
    public void inicializarDatos() throws ErrorPersistenciaException {
        String[] predefinidas = {"Alimentación", "Transporte", "Entretenimiento"};
        
        for (String nombre : predefinidas) {
            try {
                registrarCategoria(nombre);
            } catch (ElementoExistenteException e) {
                // Ignoramos si ya existe
            }
        }
    }

    // --- GESTIÓN DE GASTOS (CRUD) ---

    public void registrarGasto(double importe, LocalDate fecha, String nombreCat) {
        try {
            Usuario pagador = ControladorSesion.getInstancia().getUsuarioActual();
            if (pagador == null) throw new IllegalStateException("No hay sesión activa.");

            Categoria cat = repoGastos.getCategoria(nombreCat); // Asumimos que la vista envía una cat válida
            
            // Usamos UUID para evitar colisiones de ID
            String id = PREFIJO_PERSONAL + UUID.randomUUID().toString().substring(0, 8);
            Gasto nuevo = new Gasto(id, importe, fecha, cat, pagador); 

            repoGastos.addGasto(nuevo);

            notificarCambio(EventoSistema.NUEVO_GASTO, nuevo);
            verificarAlertas();

        } catch (Exception e) {
            // Aquí imprimimos porque la firma del método es void y no lanza excepciones
            // Idealmente deberíamos cambiar la firma a throws Exception
            System.err.println("Error registrando gasto: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void registrarGastoEnCuenta(double importe, LocalDate fecha, String nombreCat, String loginPagador, CuentaCompartida cuenta) {
        try {
            Usuario pagador = repoCuentas.getUsuario(loginPagador);
            
            // ID único para compartido
            String id = PREFIJO_COMPARTIDO + UUID.randomUUID().toString().substring(0, 8);
            Categoria cat = repoGastos.getCategoria(nombreCat);
            
            Gasto nuevoGasto = new Gasto(id, importe, fecha, cat, pagador, cuenta.getNombre());
            
            // Lógica transaccional (primero cuenta, luego repo general)
            CuentaCompartida cuentaReal = repoCuentas.getCuenta(cuenta.getNombre());
            cuentaReal.addGasto(nuevoGasto); // Aquí se recalculan saldos

            repoGastos.addGasto(nuevoGasto);
            repoCuentas.updateCuenta(cuentaReal);
            
            notificarCambio(EventoSistema.SALDO_ACTUALIZADO, cuentaReal);
            verificarAlertas();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean eliminarGasto(Gasto gasto) {
        try {
            if (!esGastoPersonal(gasto)) return false;
            
            repoGastos.removeGasto(gasto); 
            notificarCambio(EventoSistema.GASTO_ELIMINADO, gasto); 
            return true;
        } catch (ErrorPersistenciaException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean modificarGasto(Gasto gasto) {
        try {
            if (!esGastoPersonal(gasto)) return false;
            
            repoGastos.updateGasto(gasto); 
            notificarCambio(EventoSistema.GASTO_MODIFICADO, gasto);
            verificarAlertas();
            return true;
        } catch (ErrorPersistenciaException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- CONSULTAS Y FILTROS (STREAMS) ---

    public List<Gasto> getGastosPorCondicion(Predicate<Gasto> condicion) {
        return repoGastos.getGastos().stream()
                .filter(condicion)
                .collect(Collectors.toList());
    }
    
    public List<Gasto> getGastosPersonales() {
        return getGastosPorCondicion(this::esGastoPersonal);
    }
    
    public boolean esGastoPersonal(Gasto g) {
        if (g == null || g.getId() == null) return false;
        return !g.getId().startsWith(PREFIJO_COMPARTIDO); // Uso de constante
    }

    // --- ALERTAS ---

    public void crearAlerta(double limite, String periodo, String nombreCategoria) {
        try {
            IEstrategiaAlerta est = FactoriaEstrategia.getInstancia().crearEstrategiaAlerta(periodo);
            
            Alerta nueva;
            if (nombreCategoria == null || nombreCategoria.isEmpty()) {
                nueva = new Alerta(limite, est);
            } else {
                Categoria cat = new Categoria(nombreCategoria);
                nueva = new Alerta(limite, est, cat);
            }
            
            repoAlertas.addAlerta(nueva);
            notificarCambio(EventoSistema.NUEVA_ALERTA, nueva);
            
            // Verificamos inmediatamente por si la alerta ya se cumple al crearla
            verificarAlertas();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void verificarAlertas() {
        List<Gasto> todosLosGastos = repoGastos.getGastos();
        List<Alerta> alertas = repoAlertas.getAlertas();

        for (Alerta alerta : alertas) {
            // Si ya fue notificada, no gastamos CPU calculando nada (Optimización)
            if (alerta.isFueNotificada()) continue;

            if (alerta.verificarSiSuperada(todosLosGastos)) {
                triggerNotificacion(alerta);
            } 
        }
    }
    
    private void triggerNotificacion(Alerta alerta) {
        alerta.setFueNotificada(true);
        try {
            repoAlertas.updateAlerta(alerta);

            String mensaje = "Límite de " + String.format("%.2f", alerta.getLimite()) + "€ superado";
            if (alerta.getCategoria() != null) {
                mensaje += " en " + alerta.getCategoria().getId();
            }
            
            Notificacion n = new Notificacion(mensaje, LocalDate.now(), alerta);
            repoAlertas.addNotificacion(n);
            
            notificarCambio(EventoSistema.ALERTA_DISPARADA, alerta);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void eliminarAlerta(Alerta alerta) {
        try {
            repoAlertas.removeAlerta(alerta);
            notificarCambio(EventoSistema.NUEVA_ALERTA, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Alerta> getAlertas() { return repoAlertas.getAlertas(); }
    public List<Notificacion> getHistorialNotificaciones() { return repoAlertas.getNotificaciones(); }

    // --- CATEGORÍAS ---
    
    public void registrarCategoria(String nombre) throws ElementoExistenteException, ErrorPersistenciaException {
        repoGastos.addCategoria(new Categoria(nombre));
        notificarCambio(EventoSistema.NUEVA_CATEGORIA, nombre);
    }
    
    public List<String> getNombreCategorias() {
        return repoGastos.getNombreCategorias();
    }
    
    public boolean categoriaExists(String nombreCategoria) {
        return getNombreCategorias().contains(nombreCategoria);
    }

    // --- IMPORTACIÓN (REFACTORIZADA) ---
    // Dividida en métodos más pequeños para mejorar legibilidad

    public int importarGastos(String rutaFichero) throws ImportacionException {
        int ignoradosTotales = 0;

        try {
            ImportadorGastos importador = FactoriaImportadores.getInstancia().crearImportador(rutaFichero);
            Map<String, List<Gasto>> mapa = importador.leerGastos(rutaFichero);
            
            for (String nombreCuenta : mapa.keySet()) {
                ignoradosTotales += procesarCuentaImportada(nombreCuenta, mapa.get(nombreCuenta));
            }
            
            notificarCambio(EventoSistema.NUEVO_GASTO, null);
            return ignoradosTotales;

        } catch (Exception e) {
            throw new ImportacionException("Error crítico importando: " + e.getMessage());
        }
    }
    
    private int procesarCuentaImportada(String nombreCuenta, List<Gasto> gastos) {
        int ignorados = 0;
        CuentaCompartida cuentaReal = null;

        // 1. Verificar existencia de cuenta compartida
        if (!CUENTA_PERSONAL.equals(nombreCuenta)) {
            cuentaReal = repoCuentas.getCuenta(nombreCuenta);
            if (cuentaReal == null) {
                System.err.println("AVISO: Cuenta inexistente '" + nombreCuenta + "'. Ignorando " + gastos.size() + " gastos.");
                return gastos.size(); // Ignoramos todos
            }
        }

        // 2. Procesar cada gasto
        for (Gasto gasto : gastos) {
            if (!procesarGastoIndividual(gasto, cuentaReal, nombreCuenta)) {
                ignorados++;
            }
        }
        return ignorados;
    }

    /**
     * Procesa un único gasto importado.
     * @return true si se importó correctamente, false si fue ignorado.
     */
    private boolean procesarGastoIndividual(Gasto gasto, CuentaCompartida cuentaReal, String nombreCuenta) {
        // A. Validar pertenencia usuario
        if (cuentaReal != null) {
            String loginPagador = gasto.getPagador().getLogin();
            boolean esMiembro = cuentaReal.getSaldosPorUsuario().keySet().stream()
                    .anyMatch(u -> u.getLogin().equalsIgnoreCase(loginPagador));
            
            if (!esMiembro) {
                System.err.println("AVISO: Usuario intruso '" + loginPagador + "' en cuenta '" + nombreCuenta + "'.");
                return false;
            }
        }

        // B. Gestión Categoría
        Categoria catFantasma = gasto.getCategoria();
        if (repoGastos.getCategoria(catFantasma.getId()) != null) {
            gasto.setCategoria(repoGastos.getCategoria(catFantasma.getId()));
        } else {
            try {
                // Si falla al guardar la categoria, mejor abortar este gasto
                registrarCategoria(catFantasma.getId());
            } catch (Exception e) { e.printStackTrace(); }
        }

        // C. Persistencia General
        try {
            repoGastos.addGasto(gasto);
        } catch (ElementoExistenteException e) {
            System.out.println("Info: Duplicado global " + gasto.getId());
            // Si es personal y ya existe, paramos aquí
            if (cuentaReal == null) return false;
        } catch (Exception e) {
            return false;
        }

        // D. Vinculación Cuenta Compartida
        if (cuentaReal != null) {
            gasto.setCuenta(cuentaReal.getNombre());
            
            boolean yaVinculado = cuentaReal.getGastos().stream()
                    .anyMatch(g -> g.getId().equals(gasto.getId()));

            if (!yaVinculado) {
                cuentaReal.addGasto(gasto);
                try {
                    repoCuentas.updateCuenta(cuentaReal);
                } catch (ErrorPersistenciaException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false; // Ya estaba vinculado
            }
        }
        
        return true;
    }

    /**
     * Devuelve los gastos personales del usuario que cumplen los criterios
     * de filtrado indicados. Cualquier criterio nulo o vacío se ignora
     * (equivale a "no filtrar por ese campo").
     *
     * @param desde fecha mínima inclusive, o {@code null} para no acotar.
     * @param hasta fecha máxima inclusive, o {@code null} para no acotar.
     * @param meses meses a incluir; lista vacía o {@code null} = todos.
     * @param categorias nombres de categoría a incluir; lista vacía o {@code null} = todas.
     * @return lista de gastos personales que pasan todos los filtros.
     */
    public List<Gasto> filtrarGastosPersonales(LocalDate desde, LocalDate hasta,
                                               List<Month> meses, List<String> categorias) {
        return repoGastos.getGastos().stream()
                .filter(this::esGastoPersonal)
                .filter(g -> desde == null || !g.getFecha().isBefore(desde))
                .filter(g -> hasta == null || !g.getFecha().isAfter(hasta))
                .filter(g -> meses == null || meses.isEmpty()
                              || meses.contains(g.getFecha().getMonth()))
                .filter(g -> categorias == null || categorias.isEmpty()
                              || categorias.contains(g.getCategoria().getId()))
                .collect(Collectors.toList());
    }


    /**
     * Calcula el importe total acumulado por cada categoría a partir de
     * todos los gastos del sistema.
     *
     * @return mapa nombre de categoría → importe total. Nunca null.
     */
    public Map<String, Double> getTotalesPorCategoria() {
        return repoGastos.getGastos().stream()
                .filter(g -> g.getCategoria() != null)
                .collect(Collectors.groupingBy(
                        g -> g.getCategoria().getId(),
                        Collectors.summingDouble(Gasto::getImporte)
                ));
    }
    
    /**
     * Calcula el importe total acumulado de todos los gastos del sistema.
     *
     * @return suma de todos los importes.
     */
    public double getTotalGlobal() {
        return repoGastos.getGastos().stream()
                .mapToDouble(Gasto::getImporte)
                .sum();
    }
    // --- OBSERVADOR ---
    
    public void registrarObservador(IObservador obs) {
        observadores.removeIf(o -> o.getClass().equals(obs.getClass()));
        observadores.add(obs);
    }

    public void eliminarObservador(IObservador obs) {
        observadores.remove(obs);
    }
    
    private void notificarCambio(EventoSistema evento, Object datos) {
        observadores.forEach(obs -> obs.actualizar(evento, datos));
    }
    
    public Map<Usuario, Double> getPorcentajesUsuarioCuenta(CuentaCompartida cuenta) {
        return cuenta.getPorcentajesEstrategia();
    }
    
    public Map<Usuario, Double> getSaldosPorUsuarioCuenta(CuentaCompartida cuenta){
        return cuenta.getSaldosPorUsuario();
    }
    
 // --- MÉTODOS AUXILIARES PARA LA VISTA ---

    public boolean isImporteValido(double importe) {
        // Regla de negocio: El importe debe ser positivo
        return importe > 0;
    }
}