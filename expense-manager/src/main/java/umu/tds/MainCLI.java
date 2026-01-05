package umu.tds;

import java.util.Scanner;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import umu.tds.controlador.ControladorAppGastos;
import umu.tds.controlador.ControladorSesion;
import umu.tds.modelo.Gasto;
import umu.tds.modelo.Usuario;

public class MainCLI {
    public static void main(String[] args) {
        // 1. Inicializar el sistema
        Configuracion config = new ConfiguracionImpl();
        Configuracion.setInstancia(config);
        ControladorAppGastos ctrl = config.getControladorAppGastos();

        // 2. Simular login (CLI User)
        // Esto es vital para que registrarGasto no falle por falta de usuario
        Usuario usuarioCli = new Usuario("cli_user");
        ControladorSesion.getInstancia().setUsuarioActual(usuarioCli);

        Scanner scanner = new Scanner(System.in);
        System.out.println("--- GESTOR DE GASTOS (MODO CONSOLA) ---");
        System.out.println("Usuario activo: " + usuarioCli.getLogin());

        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- MENÚ ---");
            System.out.println("1. Ver todos los gastos");
            System.out.println("2. Registrar nuevo gasto");
            System.out.println("3. Borrar un gasto (por ID)");
            System.out.println("0. Salir");
            System.out.print("Selecciona una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    List<Gasto> gastos = ctrl.getGastosPorCondicion(g -> true);
                    if (gastos.isEmpty()) {
                        System.out.println("No hay gastos registrados.");
                    } else {
                        // Imprimimos con ID para poder borrarlos luego
                        System.out.println("Listado de gastos:");
                        for (Gasto g : gastos) {
                            System.out.println(" > [" + g.getId() + "] " 
                                + g.getImporte() + "€ en " 
                                + g.getCategoria().getNombre() + " (" + g.getFecha() + ")");
                        }
                    }
                    break;

                case "2":
                    try {
                        System.out.print("Importe: ");
                        double imp = Double.parseDouble(scanner.nextLine());
                        
                        System.out.print("Categoría: ");
                        String cat = scanner.nextLine();
                        
                        // Usamos LocalDate.now() por simplicidad, como en tu hint
                        ctrl.registrarGasto(imp, LocalDate.now(), cat);
                        System.out.println("✅ ¡Gasto registrado correctamente!");
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Error: El importe debe ser un número.");
                    } catch (Exception e) {
                        System.out.println("❌ Error al guardar: " + e.getMessage());
                    }
                    break;

                case "3":
                    System.out.print("Introduce el ID del gasto a borrar (ej: G-173...): ");
                    String idBorrar = scanner.nextLine();
                    
                    // Buscamos el objeto Gasto real usando el filtro del controlador
                    List<Gasto> encontrados = ctrl.getGastosPorCondicion(g -> g.getId().equals(idBorrar));
                    
                    if (!encontrados.isEmpty()) {
                        ctrl.eliminarGasto(encontrados.get(0));
                        System.out.println("🗑️ Gasto eliminado.");
                    } else {
                        System.out.println("⚠️ No se encontró ningún gasto con ese ID.");
                    }
                    break;

                case "0":
                    salir = true;
                    break;

                default:
                    System.out.println("Opción no reconocida.");
            }
        }
        
        System.out.println("Fin de la ejecución CLI.");
        scanner.close();
    }
}