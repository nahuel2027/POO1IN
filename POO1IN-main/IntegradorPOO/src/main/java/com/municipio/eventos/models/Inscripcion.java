package com.municipio.eventos.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.municipio.eventos.models.abstractas.Evento;

@Entity
public class Inscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaInscripcion;

    @ManyToOne
    @JoinColumn(name = "participante_id")
    private Participante participante;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;

    public Inscripcion() {
        // Constructor vacío requerido por JPA
    }

    public Inscripcion(Participante participante, Evento evento) {
        this.participante = participante;
        this.evento = evento;
        this.fechaInscripcion = LocalDate.now();
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public Participante getParticipante() {
        return participante;
    }

    public void setParticipante(Participante participante) {
        this.participante = participante;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    @Override
    public String toString() {
        return "Inscripción de " + participante.getNombreCompleto() +
               " en el evento '" + evento.getNombre() +
               "' el " + fechaInscripcion;
    }
}