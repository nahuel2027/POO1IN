package com.municipio.eventos.models;

import java.time.LocalDateTime;
import java.util.Objects;

import com.municipio.eventos.models.abstractas.Evento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "inscripciones", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"evento_id", "participante_dni"})
})
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participante_dni", nullable = false) // 'dni' es ahora el @Id de Persona/Participante
    private Participante participante;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDateTime fechaInscripcion;

    // Constructores
    public Inscripcion() {
    }

    // Constructor que acepta Evento, Participante y LocalDateTime
    public Inscripcion(Evento evento, Participante participante, LocalDateTime fechaInscripcion) {
        this.evento = evento;
        this.participante = participante;
        this.fechaInscripcion = fechaInscripcion;
    }

    // Constructor simplificado que usa LocalDateTime.now() por defecto
    public Inscripcion(Evento evento, Participante participante) {
        this(evento, participante, LocalDateTime.now());
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }
    public Participante getParticipante() { return participante; }
    public void setParticipante(Participante participante) { this.participante = participante; }
    public LocalDateTime getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDateTime fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inscripcion that = (Inscripcion) o;
        // La igualdad se basa en el ID si ya está persistido, o en la combinación Evento+Participante si no
        return Objects.equals(id, that.id) ||
               (Objects.equals(evento, that.evento) && Objects.equals(participante, that.participante));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, evento, participante);
    }

    @Override
    public String toString() {
        return "Inscripcion{" +
               "id=" + id +
               ", evento=" + (evento != null ? evento.getNombre() : "null") +
               ", participante=" + (participante != null ? participante.getNombreCompleto() : "null") +
               ", fechaInscripcion=" + fechaInscripcion +
               '}';
    }
}
