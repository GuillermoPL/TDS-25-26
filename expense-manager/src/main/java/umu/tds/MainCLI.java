package umu.tds;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import umu.tds.controlador.ControladorAppGastos;
import umu.tds.controlador.ControladorSesion;
import umu.tds.modelo.Gasto;
import umu.tds.modelo.Usuario;

public class MainCLI {
    public static void main(String[] args) {
        Configuracion config = new ConfiguracionImpl();
        Configuracion.setInstancia(config);
        ControladorAppGastos ctrl = config.getControladorAppGastos();

        Usuario usuarioCli = new Usuario("yo");
        ControladorSesion.getInstancia().setUsuarioActual(usuarioCli);

        Scanner scanner = new Scanner(System.in);
        System.out.println("--- GESTOR DE GASTOS (MODO CONSOLA) ---");
        System.out.println("Usuario activo: " + usuarioCli.getLogin());

        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- MENÚ ---");
            System.out.println("1. Ver todos los gastos");
            System.out.println("2. Registrar nuevo gasto");
            System.out.println("3. Editar un gasto (por ID)");
            System.out.println("4. Borrar un gasto (por ID)");
            System.out.println("0. Salir");
            System.out.print("Selecciona una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    List<Gasto> gastos = ctrl.getGastosPorCondicion(g -> true);
                    if (gastos.isEmpty()) {
                        System.out.println("No hay gastos registrados.");
                    } else {
                        System.out.println("Listado de gastos:");
                        for (Gasto g : gastos) {
                            System.out.println(" > [" + g.getId() + "] " 
                                + g.getImporte() + "€ en " 
                                + g.getCategoria().getId() + " (" + g.getFecha() + ")");
                        }
                    }
                    break;

                case "2": //REGISTRAR GASTO
                    try {
                        System.out.print("Importe: ");
                        double imp = Double.parseDouble(scanner.nextLine());
                        System.out.print("Categoría: ");
                        String cat = scanner.nextLine();
                        System.out.print("Fecha (AAAA-MM-DD, ejemplo 2024-12-31): "); // Solicitud de fecha
                        LocalDate fecha = LocalDate.parse(scanner.nextLine()); 
                        
                        // VALIDACIONES
                        if (!ctrl.isImporteValido(imp)) {
                            System.out.println("Error: El importe debe ser mayor que cero.");
                        } else if (!ctrl.categoriaExists(cat)) {
                            System.out.println("Error: La categoría '" + cat + "' no existe.");
                        } else {
                            ctrl.registrarGasto(imp, fecha, cat); // Registro con la fecha introducida
                            System.out.println("¡Gasto registrado correctamente!");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: El importe debe ser un número.");
                    } catch (DateTimeParseException e) {
                        System.out.println("Error: El formato de fecha no es válido (use AAAA-MM-DD)."); // Validación de formato de fecha
                    }
                    break;

                case "3": // EDITAR GASTO
                    System.out.print("ID del gasto a editar: ");
                    String idEditar = scanner.nextLine();
                    List<Gasto> paraEditar = ctrl.getGastosPorCondicion(g -> g.getId().equals(idEditar));

                    if (!paraEditar.isEmpty()) {
                        Gasto g = paraEditar.get(0);
                        try {
                            System.out.print("Nuevo importe (actual: " + g.getImporte() + "): ");
                            double nuevoImp = Double.parseDouble(scanner.nextLine());
                            
                            System.out.print("Nueva categoría (actual: " + g.getCategoria().getId() + "): ");
                            String nuevaCat = scanner.nextLine();
                            
                            System.out.print("Nueva fecha (actual: " + g.getFecha() + ", use AAAA-MM-DD): "); // Solicitud de nueva fecha
                            LocalDate nuevaFecha = LocalDate.parse(scanner.nextLine());

                            // VALIDACIONES
                            if (!ctrl.isImporteValido(nuevoImp)) {
                                System.out.println("Error: El nuevo importe debe ser mayor que cero.");
                            } else if (!ctrl.categoriaExists(nuevaCat)) {
                                System.out.println("Error: La categoría '" + nuevaCat + "' no existe.");
                            } else {
                                g.setImporte(nuevoImp); // Actualización de importe
                                g.setFecha(nuevaFecha); // Actualización de fecha
                                g.setCategoria(new umu.tds.modelo.Categoria(nuevaCat)); // Actualización de categoría
                                ctrl.modificarGasto(g); // Persistencia del cambio
                                System.out.println("Gasto modificado con éxito.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Error: Importe no válido.");
                        } catch (DateTimeParseException e) {
                            System.out.println("Error: El formato de fecha no es válido (use AAAA-MM-DD).");
                        }
                    } else {
                        System.out.println("ID no encontrado.");
                    }
                    break;

                case "4": // BORRAR GASTO
                    System.out.print("ID del gasto a borrar: ");
                    String idBorrar = scanner.nextLine();
                    List<Gasto> encontrados = ctrl.getGastosPorCondicion(g -> g.getId().equals(idBorrar));
                    
                    if (!encontrados.isEmpty()) {
                        ctrl.eliminarGasto(encontrados.get(0));
                        System.out.println("Gasto eliminado.");
                    } else {
                        System.out.println("No se encontró ningún gasto con ese ID.");
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