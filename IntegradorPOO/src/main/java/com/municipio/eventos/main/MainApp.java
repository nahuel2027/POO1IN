// src/main/java/com/municipio/eventos/main/MainApp.java
package com.municipio.eventos.main;

import com.municipio.eventos.models.*; // Importa todas las clases de modelos
import com.municipio.eventos.models.abstractas.Evento; // Importa explícitamente Evento abstracto
import com.municipio.eventos.models.enums.*; // Importa todas las enumeraciones
import com.municipio.eventos.services.EventoService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MainApp {

    public static void main(String[] args) {
        System.out.println("--- Iniciando la aplicación de Gestión de Eventos del Municipio ---");

        // 1. Inicializar el servicio de eventos
        EventoService eventoService = new EventoService();

        // 2. Crear algunas personas (organizadores, artistas, instructores, participantes)
        Organizador org1 = new Organizador("Juan Pérez", "12345678", "1122334455", "juan.perez@municipio.com");
        Organizador org2 = new Organizador("Maria García", "87654321", "1199887766", "maria.garcia@municipio.com");

        Artista art1 = new Artista("Los Rockeros", "22334455", "1133445566", "rockeros@banda.com");
        Artista art2 = new Artista("Solista Melódica", "33445566", "1166778899", "solista@cantante.com");

        Curador cur1 = new Curador("Ana Gómez", "44556677", "1155667788", "ana.gomez@museo.com");

        Instructor inst1 = new Instructor("Carlos Díaz", "55667788", "1111223344", "carlos.diaz@educacion.com");

        Participante part1 = new Participante("Laura Fernández", "98765432", "1144556677", "laura.f@email.com");
        Participante part2 = new Participante("Pedro Martínez", "11223344", "1177889900", "pedro.m@email.com");
        Participante part3 = new Participante("Sofía Ruíz", "66778899", "1100112233", "sofia.r@email.com");


        System.out.println("\n--- Creando y Agregando Eventos ---");

        // 3. Crear diferentes tipos de eventos
        Feria feriaMunicipal = new Feria(
            "Feria de Artesanías 2025", LocalDate.of(2025, 7, 20), 480, // 8 horas
            false, 0, // No requiere inscripción, sin cupo
            50, TipoUbicacionFeria.AIRE_LIBRE
        );
        feriaMunicipal.agregarResponsable(org1); // Asociar responsable

        Concierto conciertoRock = new Concierto(
            "Noche de Rock Local", LocalDate.of(2025, 8, 15), 180, // 3 horas
            true, 500, // Requiere inscripción, cupo de 500
            TipoEntradaConcierto.PAGA
        );
        conciertoRock.agregarResponsable(org2);
        conciertoRock.agregarArtista(art1); // Asociar artistas

        Exposicion expoFotografia = new Exposicion(
            "Miradas Urbanas", LocalDate.of(2025, 9, 10), 360, // 6 horas
            true, 100, // Requiere inscripción, cupo de 100 (para control de aforo)
            "Fotografía", cur1 // Asociar curador
        );
        expoFotografia.agregarResponsable(org1);

        Taller tallerProgramacion = new Taller(
            "Taller de Introducción a Java", LocalDate.of(2025, 10, 5), 240, // 4 horas
            true, 20, // Requiere inscripción, cupo de 20
            inst1, ModalidadTaller.VIRTUAL // Asociar instructor
        );
        tallerProgramacion.agregarResponsable(org2);

        CicloDeCine cicloClasicos = new CicloDeCine(
            "Ciclo de Cine Clásico", LocalDate.of(2025, 11, 1), 300, // 5 horas (aprox)
            false, 0, // No requiere inscripción, sin cupo
            true // Tiene charlas posteriores
        );
        cicloClasicos.agregarResponsable(org1);
        cicloClasicos.agregarPelicula(new Pelicula("Casablanca", 1));
        cicloClasicos.agregarPelicula(new Pelicula("El Padrino", 2));

        // 4. Agregar eventos al servicio
        eventoService.agregarEvento(feriaMunicipal);
        eventoService.agregarEvento(conciertoRock);
        eventoService.agregarEvento(expoFotografia);
        eventoService.agregarEvento(tallerProgramacion);
        eventoService.agregarEvento(cicloClasicos);

        // Intentar agregar un evento duplicado (debería fallar)
        System.out.println("\n--- Intentando agregar un evento duplicado (debe fallar) ---");
        eventoService.agregarEvento(new Feria(
            "Feria de Artesanías 2025", LocalDate.of(2025, 7, 20), 480,
            false, 0, 50, TipoUbicacionFeria.AIRE_LIBRE
        ));

        System.out.println("\n--- Cambiando Estados y Probando Inscripciones ---");

        // 5. Cambiar el estado de un evento
        System.out.println("Estado inicial Concierto Rock: " + conciertoRock.getEstado());
        eventoService.cambiarEstadoEvento(conciertoRock, EstadoEvento.CONFIRMADO);
        System.out.println("Estado actual Concierto Rock: " + conciertoRock.getEstado());

        // Intentar inscribir en evento no confirmado (debe fallar)
        System.out.println("\n--- Intentando inscribir en evento no CONFIRMADO (debe fallar) ---");
        eventoService.registrarParticipanteAEvento(expoFotografia, part1); // Expo aún en PLANIFICACION

        // Cambiar estado de exposicion a CONFIRMADO
        eventoService.cambiarEstadoEvento(expoFotografia, EstadoEvento.CONFIRMADO);
        System.out.println("Estado actual Exposición Fotografía: " + expoFotografia.getEstado());

        // 6. Registrar participantes a eventos
        System.out.println("\n--- Registrando participantes ---");
        eventoService.registrarParticipanteAEvento(conciertoRock, part1);
        eventoService.registrarParticipanteAEvento(conciertoRock, part2);
        eventoService.registrarParticipanteAEvento(expoFotografia, part1); // Laura se inscribe en la exposición también
        eventoService.registrarParticipanteAEvento(tallerProgramacion, part3);

        // Intentar inscribir el mismo participante dos veces (debe fallar)
        System.out.println("\n--- Intentando inscribir participante duplicado (debe fallar) ---");
        eventoService.registrarParticipanteAEvento(conciertoRock, part1);

        // Llenar el cupo del taller para probar la validación
        System.out.println("\n--- Llenando cupo del taller para prueba ---");
        Taller tallerDemoCupo = new Taller("Taller de Prueba Cupo", LocalDate.of(2025, 12, 1), 60, true, 2, inst1, ModalidadTaller.PRESENCIAL);
        eventoService.agregarEvento(tallerDemoCupo);
        eventoService.cambiarEstadoEvento(tallerDemoCupo, EstadoEvento.CONFIRMADO);
        eventoService.registrarParticipanteAEvento(tallerDemoCupo, part1);
        eventoService.registrarParticipanteAEvento(tallerDemoCupo, part2);
        System.out.println("Inscritos en Taller de Prueba Cupo: " + tallerDemoCupo.getInscripciones().size() + " de " + tallerDemoCupo.getCupoMaximo());
        System.out.println("Intentando inscribir un tercer participante (debe fallar por cupo)");
        eventoService.registrarParticipanteAEvento(tallerDemoCupo, part3); // Debe fallar, cupo lleno


        // 7. Listar eventos
        System.out.println("\n--- Listado de Todos los Eventos ---");
        List<Evento> todosLosEventos = eventoService.getTodosLosEventos();
        if (todosLosEventos.isEmpty()) {
            System.out.println("No hay eventos registrados.");
        } else {
            for (Evento e : todosLosEventos) {
                System.out.println(e);
            }
        }

        // 8. Listar participantes de un evento específico
        System.out.println("\n--- Listado de Participantes del Concierto de Rock ---");
        Optional<Evento> conciertoBuscado = eventoService.buscarEvento("Noche de Rock Local", LocalDate.of(2025, 8, 15));
        if (conciertoBuscado.isPresent()) {
            List<Participante> participantesConcierto = eventoService.getParticipantesPorEvento(conciertoBuscado.get());
            if (participantesConcierto.isEmpty()) {
                System.out.println("No hay participantes inscritos en este concierto.");
            } else {
                for (Participante p : participantesConcierto) {
                    System.out.println("- " + p.getNombreCompleto());
                }
            }
        } else {
            System.out.println("Concierto no encontrado.");
        }

        System.out.println("\n--- Listado de Participantes de la Exposición de Fotografía ---");
        Optional<Evento> expoBuscada = eventoService.buscarEvento("Miradas Urbanas", LocalDate.of(2025, 9, 10));
        if (expoBuscada.isPresent()) {
            List<Participante> participantesExpo = eventoService.getParticipantesPorEvento(expoBuscada.get());
            if (participantesExpo.isEmpty()) {
                System.out.println("No hay participantes inscritos en esta exposición.");
            } else {
                for (Participante p : participantesExpo) {
                    System.out.println("- " + p.getNombreCompleto());
                }
            }
        } else {
            System.out.println("Exposición no encontrada.");
        }

        // 9. Probar eliminación de un evento
        System.out.println("\n--- Eliminando un evento (Taller de Prueba Cupo) ---");
        eventoService.eliminarEvento("Taller de Prueba Cupo", LocalDate.of(2025, 12, 1));
        System.out.println("\n--- Verificando lista de eventos después de eliminación ---");
        for (Evento e : eventoService.getTodosLosEventos()) {
            System.out.println(e.getNombre() + " - " + e.getFechaInicio());
        }

        System.out.println("\n--- Fin de la aplicación ---");
    }
}