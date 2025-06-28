package com.municipio.eventos.main;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.municipio.eventos.models.Artista;
import com.municipio.eventos.models.CicloDeCine;
import com.municipio.eventos.models.Concierto;
import com.municipio.eventos.models.Curador;
import com.municipio.eventos.models.Exposicion;
import com.municipio.eventos.models.Feria;
import com.municipio.eventos.models.Instructor;
import com.municipio.eventos.models.Organizador;
import com.municipio.eventos.models.Participante;
import com.municipio.eventos.models.Pelicula;
import com.municipio.eventos.models.Taller;
import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.enums.EstadoEvento;
import com.municipio.eventos.models.enums.ModalidadTaller;
import com.municipio.eventos.models.enums.TipoEntradaConcierto;
import com.municipio.eventos.models.enums.TipoUbicacionFeria;
import com.municipio.eventos.services.EventoService;
import com.municipio.eventos.services.PersonaService;

public class MainApp {

    private static EventoService eventoService = new EventoService();
    private static PersonaService personaService = new PersonaService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Carga de datos de prueba al inicio de la aplicación
        cargarDatosDePrueba();

        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = obtenerOpcionUsuario();

            switch (opcion) {
                case 1:
                    // La funcionalidad de creación de eventos ahora está en la GUI.
                    // Aquí podríamos dejar un mensaje o invocar un método dummy si el usuario quiere algo por consola.
                    System.out.println("La creación de eventos se gestiona desde la interfaz gráfica de usuario.");
                    break;
                case 2:
                    // La funcionalidad de gestión de personas ahora está en la GUI.
                    System.out.println("La gestión de personas se gestiona desde la interfaz gráfica de usuario.");
                    break;
                case 3:
                    // La funcionalidad de inscripción de participantes ahora está en la GUI.
                    System.out.println("La inscripción de participantes se gestiona desde la interfaz gráfica de usuario.");
                    break;
                case 4:
                    listarEventos();
                    break;
                case 5:
                    cambiarEstadoEvento();
                    break;
                case 6:
                    listarParticipantesPorEvento();
                    break;
                case 0:
                    System.out.println("Saliendo de la aplicación. ¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
            System.out.println("\n--- Presione Enter para continuar ---");
            scanner.nextLine(); // Consumir el salto de línea
        } while (opcion != 0);

        // Cerrar los EntityManagerFactory al finalizar la aplicación de consola
        EventoService.closeEntityManagerFactory();
        PersonaService.closeEntityManagerFactory();
        scanner.close();
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n--- Menú Principal (Consola) ---");
        System.out.println("1. (GUI) Crear Evento");
        System.out.println("2. (GUI) Gestionar Personas (Alta, Modificación, Baja, Listado)");
        System.out.println("3. (GUI) Inscribir Participante a Evento");
        System.out.println("4. Listar Eventos (Consola)");
        System.out.println("5. Cambiar Estado de Evento (Consola)");
        System.out.println("6. Listar Participantes por Evento (Consola)");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static int obtenerOpcionUsuario() {
        while (!scanner.hasNextInt()) {
            System.out.println("Entrada no válida. Por favor, ingrese un número.");
            scanner.next(); // Consumir la entrada no válida
            System.out.print("Seleccione una opción: ");
        }
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        return opcion;
    }

    private static void cargarDatosDePrueba() {
        System.out.println("Cargando datos de prueba...");

        // Personas
        Organizador org1 = new Organizador("Juan Pérez", "12345678", "1122334455", "juan.perez@municipio.com");
        Organizador org2 = new Organizador("Maria García", "87654321", "1199887766", "maria.garcia@municipio.com");
        Artista art1 = new Artista("Los Rockeros", "22334455", "1133445566", "rockeros@banda.com");
        Curador cur1 = new Curador("Ana Gómez", "44556677", "1155667788", "ana.gomez@museo.com");
        Instructor inst1 = new Instructor("Carlos Díaz", "55667788", "1111223344", "carlos.diaz@educacion.com");
        Participante part1 = new Participante("Laura Fernández", "98765432", "1144556677", "laura.f@email.com");
        Participante part2 = new Participante("Pedro Martínez", "11223344", "1177889900", "pedro.m@email.com");
        Participante part3 = new Participante("Sofía Ruíz", "66778899", "1100112233", "sofia.r@email.com");

        // Guarda las personas en el PersonaService (para que se puedan seleccionar en los ComboBox de la GUI)
        // Solo agrega si no existen ya (usando el DNI como clave de unicidad)
        if (personaService.buscarPersonaPorDni(org1.getDni()).isEmpty()) personaService.altaPersona(org1);
        if (personaService.buscarPersonaPorDni(org2.getDni()).isEmpty()) personaService.altaPersona(org2);
        if (personaService.buscarPersonaPorDni(art1.getDni()).isEmpty()) personaService.altaPersona(art1);
        if (personaService.buscarPersonaPorDni(cur1.getDni()).isEmpty()) personaService.altaPersona(cur1);
        if (personaService.buscarPersonaPorDni(inst1.getDni()).isEmpty()) personaService.altaPersona(inst1);
        if (personaService.buscarPersonaPorDni(part1.getDni()).isEmpty()) personaService.altaPersona(part1);
        if (personaService.buscarPersonaPorDni(part2.getDni()).isEmpty()) personaService.altaPersona(part2);
        if (personaService.buscarPersonaPorDni(part3.getDni()).isEmpty()) personaService.altaPersona(part3);


        // Eventos (verificamos si ya existen antes de agregar)
        if (eventoService.buscarEvento("Feria de Artesanías 2025", LocalDate.of(2025, 7, 20)).isEmpty()) {
            Feria feriaMunicipal = new Feria(
                "Feria de Artesanías 2025", LocalDate.of(2025, 7, 20), 480,
                false, 0, 50, TipoUbicacionFeria.AIRE_LIBRE
            );
            // Asegurarse de que los organizadores obtenidos del servicio son los persistidos
            personaService.buscarPersonaPorDni(org1.getDni()).ifPresent(p -> feriaMunicipal.agregarResponsable((Organizador) p));
            eventoService.agregarEvento(feriaMunicipal);
        }

        if (eventoService.buscarEvento("Noche de Rock Local", LocalDate.of(2025, 8, 15)).isEmpty()) {
            Concierto conciertoRock = new Concierto(
                "Noche de Rock Local", LocalDate.of(2025, 8, 15), 180,
                true, 500, TipoEntradaConcierto.PAGA
            );
            personaService.buscarPersonaPorDni(org2.getDni()).ifPresent(p -> conciertoRock.agregarResponsable((Organizador) p));
            personaService.buscarPersonaPorDni(art1.getDni()).ifPresent(p -> conciertoRock.agregarArtista((Artista) p));

            eventoService.agregarEvento(conciertoRock); // Persistir el concierto primero
            // Confirmar y registrar participantes SOLO si el evento se agregó
            eventoService.buscarEvento(conciertoRock.getNombre(), conciertoRock.getFechaInicio()).ifPresent(managedConcierto -> {
                eventoService.cambiarEstadoEvento(managedConcierto, EstadoEvento.CONFIRMADO); // Confirmar
                personaService.buscarPersonaPorDni(part1.getDni()).ifPresent(p -> eventoService.registrarParticipanteAEvento(managedConcierto, (Participante) p));
                personaService.buscarPersonaPorDni(part2.getDni()).ifPresent(p -> eventoService.registrarParticipanteAEvento(managedConcierto, (Participante) p));
            });
        }

        if (eventoService.buscarEvento("Miradas Urbanas", LocalDate.of(2025, 9, 10)).isEmpty()) {
            Exposicion expoFotografia = new Exposicion(
                "Miradas Urbanas", LocalDate.of(2025, 9, 10), 360,
                true, 100,
                "Fotografía", null // Curador se asigna después de obtenerlo del servicio
            );
            personaService.buscarPersonaPorDni(cur1.getDni()).ifPresent(p -> expoFotografia.setCurador((Curador) p));
            personaService.buscarPersonaPorDni(org1.getDni()).ifPresent(p -> expoFotografia.agregarResponsable((Organizador) p));

            eventoService.agregarEvento(expoFotografia);
            eventoService.buscarEvento(expoFotografia.getNombre(), expoFotografia.getFechaInicio()).ifPresent(managedExpo -> {
                eventoService.cambiarEstadoEvento(managedExpo, EstadoEvento.CONFIRMADO);
                personaService.buscarPersonaPorDni(part1.getDni()).ifPresent(p -> eventoService.registrarParticipanteAEvento(managedExpo, (Participante) p));
            });
        }

        if (eventoService.buscarEvento("Taller de Introducción a Java", LocalDate.of(2025, 10, 5)).isEmpty()) {
            Taller tallerProgramacion = new Taller(
                "Taller de Introducción a Java", LocalDate.of(2025, 10, 5), 240,
                true, 20,
                null, ModalidadTaller.VIRTUAL // Instructor se asigna después
            );
            personaService.buscarPersonaPorDni(inst1.getDni()).ifPresent(p -> tallerProgramacion.setInstructor((Instructor) p));
            personaService.buscarPersonaPorDni(org2.getDni()).ifPresent(p -> tallerProgramacion.agregarResponsable((Organizador) p));

            eventoService.agregarEvento(tallerProgramacion);
            eventoService.buscarEvento(tallerProgramacion.getNombre(), tallerProgramacion.getFechaInicio()).ifPresent(managedTaller -> {
                eventoService.cambiarEstadoEvento(managedTaller, EstadoEvento.CONFIRMADO);
                personaService.buscarPersonaPorDni(part3.getDni()).ifPresent(p -> eventoService.registrarParticipanteAEvento(managedTaller, (Participante) p));
            });
        }

        if (eventoService.buscarEvento("Ciclo de Cine Clásico", LocalDate.of(2025, 11, 1)).isEmpty()) {
            CicloDeCine cicloClasicos = new CicloDeCine(
                "Ciclo de Cine Clásico", LocalDate.of(2025, 11, 1), 300,
                false, 0,
                true
            );
            personaService.buscarPersonaPorDni(org1.getDni()).ifPresent(p -> cicloClasicos.agregarResponsable((Organizador) p));
            cicloClasicos.agregarPelicula(new Pelicula("Casablanca", 1));
            cicloClasicos.agregarPelicula(new Pelicula("El Padrino", 2));
            eventoService.agregarEvento(cicloClasicos);
            
            eventoService.buscarEvento(cicloClasicos.getNombre(), cicloClasicos.getFechaInicio()).ifPresent(managedCiclo -> {
                 eventoService.cambiarEstadoEvento(managedCiclo, EstadoEvento.CONFIRMADO);
            });
        }

        System.out.println("Datos de prueba cargados.");
    }

    private static void crearEvento() {
        System.out.println("\n--- Crear Nuevo Evento ---");
        // ... (Tu lógica existente para crear eventos, adaptada si es necesario para interactuar con el servicio)
        System.out.println("Funcionalidad de creación de eventos implementada en la GUI.");
    }

    private static void gestionarPersonas() {
        System.out.println("\n--- Gestionar Personas ---");
        // ... (Tu lógica existente para gestionar personas, adaptada si es necesario para interactuar con el servicio)
        System.out.println("Funcionalidad de gestión de personas implementada en la GUI.");
    }

    private static void inscribirParticipante() {
        System.out.println("\n--- Inscribir Participante a Evento ---");
        // ... (Tu lógica existente para inscribir participantes, adaptada si es necesario para interactuar con el servicio)
        System.out.println("Funcionalidad de inscripción de participantes implementada en la GUI.");
    }

    private static void listarEventos() {
        System.out.println("\n--- Lista de Eventos ---");
        List<Evento> eventos = eventoService.getTodosLosEventos();
        if (eventos.isEmpty()) {
            System.out.println("No hay eventos registrados.");
            return;
        }
        for (Evento evento : eventos) {
            System.out.println(evento.toString());
        }
    }

    private static void cambiarEstadoEvento() {
        System.out.println("\n--- Cambiar Estado de Evento ---");
        System.out.print("Ingrese el nombre del evento: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese la fecha de inicio del evento (YYYY-MM-DD): ");
        LocalDate fecha = LocalDate.parse(scanner.nextLine());

        Optional<Evento> eventoOpt = eventoService.buscarEvento(nombre, fecha);
        if (eventoOpt.isPresent()) {
            Evento evento = eventoOpt.get();
            System.out.println("Evento actual: " + evento);
            System.out.println("Estados disponibles: " + java.util.Arrays.toString(EstadoEvento.values()));
            System.out.print("Ingrese el nuevo estado: ");
            String nuevoEstadoStr = scanner.nextLine().toUpperCase();
            try {
                EstadoEvento nuevoEstado = EstadoEvento.valueOf(nuevoEstadoStr);
                if (eventoService.cambiarEstadoEvento(evento, nuevoEstado)) {
                    System.out.println("Estado del evento cambiado con éxito.");
                } else {
                    System.out.println("No se pudo cambiar el estado del evento.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Estado no válido. Intente de nuevo.");
            }
        } else {
            System.out.println("Evento no encontrado.");
        }
    }

    private static void listarParticipantesPorEvento() {
        System.out.println("\n--- Listar Participantes por Evento ---");
        System.out.print("Ingrese el nombre del evento: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese la fecha de inicio del evento (YYYY-MM-DD): ");
        LocalDate fecha = LocalDate.parse(scanner.nextLine());

        Optional<Evento> eventoOpt = eventoService.buscarEvento(nombre, fecha);
        if (eventoOpt.isPresent()) {
            Evento evento = eventoOpt.get();
            System.out.println("Participantes para el evento: " + evento.getNombre());
            List<Participante> participantes = eventoService.getParticipantesPorEvento(evento);
            if (participantes.isEmpty()) {
                System.out.println("No hay participantes inscritos en este evento.");
            } else {
                for (Participante p : participantes) {
                    System.out.println("- " + p.getNombreCompleto() + " (DNI: " + p.getDni() + ")");
                }
            }
        } else {
            System.out.println("Evento no encontrado.");
        }
    }
}
