package main.java.com.municipio.eventos.models;

import com.municipio.eventos.models.abstractas.Evento; // Necesitamos el Evento para la asociación
import java.time.LocalDate;

public class Inscripcion {
    private LocalDate fechaInscripcion;
    private Participante participante;
    private Evento evento;

    public Inscripcion(Participante participante, Evento evento) {
        this.participante = participante;
        this.evento = evento;
        this.fechaInscripcion = LocalDate.now(); // La fecha de inscripción es la actual
    }

    // Getters
    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public Participante getParticipante() {
        return participante;
    }

    public Evento getEvento() {
        return evento;
    }

    @Override
    public String toString() {
        return "Inscripción de " + participante.getNombreCompleto() +
               " en el evento '" + evento.getNombre() +
               "' el " + fechaInscripcion;
    }
}