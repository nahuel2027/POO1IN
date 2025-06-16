package com.municipio.eventos.services;

import com.municipio.eventos.dao.EventoDAO;
import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.enums.EstadoEvento;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.municipio.eventos.models.*;
import com.municipio.eventos.models.abstractas.Evento;

public class EventoService {
    private EventoDAO eventoDAO = new EventoDAO();

    public boolean agregarEvento(Evento evento) {
        // Validación: no permitir dos eventos con el mismo nombre y fecha de inicio
        List<Evento> existentes = eventoDAO.obtenerTodos();
        boolean existe = existentes.stream().anyMatch(e ->
            e.getNombre().equalsIgnoreCase(evento.getNombre()) &&
            e.getFechaInicio().equals(evento.getFechaInicio())
        );
        if (existe) {
            System.out.println("Error: Ya existe un evento con ese nombre y fecha.");
            return false;
        }
        eventoDAO.guardar(evento);
        System.out.println("Evento '" + evento.getNombre() + "' agregado con éxito.");
        return true;
    }

    public boolean modificarEvento(Evento evento) {
        eventoDAO.actualizar(evento);
        System.out.println("Evento '" + evento.getNombre() + "' modificado con éxito.");
        return true;
    }

    public boolean eliminarEvento(Evento evento) {
        eventoDAO.eliminar(evento);
        System.out.println("Evento '" + evento.getNombre() + "' eliminado con éxito.");
        return true;
    }

    public Evento buscarEventoPorId(Long id) {
        return eventoDAO.buscarPorId(id);
    }

    public List<Evento> getTodosLosEventos() {
        return eventoDAO.obtenerTodos();
    }


    public List<Evento> buscarEventosPorNombre(String nombre) {
        return eventoDAO.obtenerTodos().stream()
            .filter(e -> e.getNombre().equalsIgnoreCase(nombre))
            .toList();
    }


    // Asociar organizador a cualquier evento
    public boolean asociarOrganizador(Evento evento, Organizador organizador) {
        if (evento == null || organizador == null) return false;
        evento.agregarResponsable(organizador);
        eventoDAO.actualizar(evento);
        return true;
    }

    // Asociar artista a concierto
    public boolean asociarArtistaAConcierto(Concierto concierto, Artista artista) {
        if (concierto == null || artista == null) return false;
        concierto.agregarArtista(artista);
        eventoDAO.actualizar(concierto);
        return true;
    }

    // Asociar curador a exposición
    public boolean asociarCuradorAExposicion(Exposicion exposicion, Curador curador) {
        if (exposicion == null || curador == null) return false;
        exposicion.setCurador(curador);
        eventoDAO.actualizar(exposicion);
        return true;
    }

    // Asociar instructor a taller
    public boolean asociarInstructorATaller(Taller taller, Instructor instructor) {
        if (taller == null || instructor == null) return false;
        taller.setInstructor(instructor);
        eventoDAO.actualizar(taller);
        return true;
    }

    // Cambiar estado de evento
    public boolean cambiarEstadoEvento(Evento evento, EstadoEvento nuevoEstado) {
        if (evento == null || nuevoEstado == null) return false;
        evento.setEstado(nuevoEstado);
        eventoDAO.actualizar(evento);
        System.out.println("Estado del evento '" + evento.getNombre() + "' cambiado a " + nuevoEstado);
        return true;
    }

    // Registrar participante a evento
    public boolean registrarParticipanteAEvento(Evento evento, Participante participante) {
        if (evento == null || participante == null) return false;
        if (evento.getEstado() != EstadoEvento.CONFIRMADO) {
            System.out.println("Error: No se puede inscribir en un evento que no está confirmado.");
            return false;
        }
        if (evento instanceof Taller && ((Taller) evento).getCupoMaximo() > 0 &&
            evento.getInscripciones().size() >= ((Taller) evento).getCupoMaximo()) {
            System.out.println("Error: El taller '" + evento.getNombre() + "' ya ha alcanzado su cupo máximo.");
            return false;
        }
        Inscripcion inscripcion = new Inscripcion(participante, evento);
        eventoDAO.guardarInscripcion(inscripcion);
        System.out.println("Participante '" + participante.getNombreCompleto() + "' inscrito en el evento '" + evento.getNombre() + "'.");
        return true;
    }

 //Registrar participante a evento con fecha específica
    public boolean registrarParticipanteAEventoConFecha(Evento evento, Participante participante, LocalDate fechaInscripcion) {
        if (evento == null || participante == null || fechaInscripcion == null) return false;
        if (evento.getEstado() != EstadoEvento.CONFIRMADO) {
            System.out.println("Error: No se puede inscribir en un evento que no está confirmado.");
            return false;
        }
        if (evento instanceof Taller && ((Taller) evento).getCupoMaximo() > 0 &&
            evento.getInscripciones().size() >= ((Taller) evento).getCupoMaximo()) {
            System.out.println("Error: El taller '" + evento.getNombre() + "' ya ha alcanzado su cupo máximo.");
            return false;
        }
        Inscripcion inscripcion = new Inscripcion(participante, evento);
        inscripcion.setFechaInscripcion(fechaInscripcion);
        eventoDAO.guardarInscripcion(inscripcion);
        System.out.println("Participante '" + participante.getNombreCompleto() + "' inscrito en el evento '" + evento.getNombre() + "' con fecha " + fechaInscripcion);
        return true;
    }



    // Obtener participantes de un evento

    public List<Participante> getParticipantesPorEvento(Evento evento) {
        if (evento == null) return List.of();
        return evento.getInscripciones().stream()
            .map(Inscripcion::getParticipante)
            .toList();
    }

    // Buscar evento por nombre y fecha
    public Optional<Evento> buscarEvento(String nombre, LocalDate fecha) {
        return getTodosLosEventos().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(nombre) && e.getFechaInicio().equals(fecha))
                .findFirst();

   }
}