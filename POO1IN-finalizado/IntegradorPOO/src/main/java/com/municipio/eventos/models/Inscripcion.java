package com.municipio.eventos.models;

import java.time.LocalDateTime;
import java.util.Objects;

import com.municipio.eventos.models.abstractas.Evento;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetch es común para el lado "muchos"
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetch es común para el lado "muchos"
    @JoinColumn(name = "persona_dni", nullable = false)
    private Persona participante;

    private LocalDateTime fechaInscripcion;

    public Inscripcion() {
        this.fechaInscripcion = LocalDateTime.now();
    }

    public Inscripcion(Evento evento, Persona participante) {
        this.evento = evento;
        this.participante = participante;
        this.fechaInscripcion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    // No setter para ID si es generado automáticamente
    // public void setId(Long id) { this.id = id; }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Persona getParticipante() {
        return participante;
    }

    public void setParticipante(Persona participante) {
        this.participante = participante;
    }

    public LocalDateTime getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDateTime fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    @Override
    public String toString() {
        return "Inscripcion{" +
               "id=" + id +
               ", evento=" + (evento != null ? evento.getNombre() : "N/A") +
               ", participante=" + (participante != null ? participante.getNombreCompleto() : "N/A") +
               ", fechaInscripcion=" + fechaInscripcion +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inscripcion that = (Inscripcion) o;
        return Objects.equals(evento, that.evento) &&
               Objects.equals(participante, that.participante);
    }

    @Override
    public int hashCode() {
        return Objects.hash(evento, participante);
    }
}
