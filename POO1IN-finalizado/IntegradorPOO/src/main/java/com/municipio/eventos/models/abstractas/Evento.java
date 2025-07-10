package com.municipio.eventos.models.abstractas;

import com.municipio.eventos.models.Inscripcion;
import com.municipio.eventos.models.Persona;
import com.municipio.eventos.models.enums.EstadoEvento;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "evento")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "duracion_estimada_minutos", nullable = false)
    private int duracionEstimacionMinutos;

    @Column(name = "requiere_inscripcion", nullable = false)
    private boolean requiereInscripcion;

    @Column(name = "cupo_maximo", nullable = false)
    private int cupoMaximo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEvento estado;

    // Relación ManyToMany con Persona para los responsables
    // Se cambia CascadeType.PERSIST a CascadeType.MERGE
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
        name = "evento_responsable",
        joinColumns = @JoinColumn(name = "evento_id"),
        inverseJoinColumns = @JoinColumn(name = "persona_dni")
    )
    private Set<Persona> responsables = new HashSet<>();

    // Relación OneToMany con Inscripcion
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Inscripcion> inscripciones = new HashSet<>();

    public Evento() {
        this.estado = EstadoEvento.PLANIFICACION; // Estado inicial por defecto
    }

    public Evento(String nombre, LocalDate fechaInicio, int duracionEstimacionMinutos,
                  boolean requiereInscripcion, int cupoMaximo) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.duracionEstimacionMinutos = duracionEstimacionMinutos;
        this.requiereInscripcion = requiereInscripcion;
        this.cupoMaximo = cupoMaximo;
        this.estado = EstadoEvento.PLANIFICACION; // Estado inicial por defecto
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public int getDuracionEstimacionMinutos() {
        return duracionEstimacionMinutos;
    }

    public void setDuracionEstimacionMinutos(int duracionEstimacionMinutos) {
        this.duracionEstimacionMinutos = duracionEstimacionMinutos;
    }

    public boolean isRequiereInscripcion() {
        return requiereInscripcion;
    }

    public void setRequiereInscripcion(boolean requiereInscripcion) {
        this.requiereInscripcion = requiereInscripcion;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public EstadoEvento getEstado() {
        return estado;
    }

    public void setEstado(EstadoEvento estado) {
        this.estado = estado;
    }

    public Set<Persona> getResponsables() {
        return responsables;
    }

    public void setResponsables(Set<Persona> responsables) {
        this.responsables = responsables;
    }

    public Set<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    public void setInscripciones(Set<Inscripcion> inscripciones) {
        this.inscripciones = inscripciones;
    }

    // Métodos de negocio
    public void agregarResponsable(Persona responsable) {
        if (responsable != null) {
            this.responsables.add(responsable);
        }
    }

    public void removerResponsable(Persona responsable) {
        if (responsable != null) {
            this.responsables.remove(responsable);
        }
    }

    public boolean agregarParticipante(Persona participante) {
        if (participante == null || !participante.hasRol(com.municipio.eventos.models.enums.TipoRol.PARTICIPANTE)) {
            System.err.println("ERROR: La persona no es un participante válido.");
            return false;
        }
        if (!this.requiereInscripcion) {
            System.err.println("ERROR: Este evento no requiere inscripción previa.");
            return false;
        }
        if (this.estado != EstadoEvento.CONFIRMADO) {
            System.err.println("ERROR: El evento no está en estado CONFIRMADO para inscripciones.");
            return false;
        }
        if (this.cupoMaximo > 0 && this.inscripciones.size() >= this.cupoMaximo) {
            System.err.println("ERROR: El cupo máximo para este evento ha sido alcanzado.");
            return false;
        }
        
        // Verificar si el participante ya está inscrito
        boolean yaInscrito = this.inscripciones.stream()
                                .anyMatch(insc -> insc.getParticipante().equals(participante));
        if (yaInscrito) {
            System.err.println("ERROR: El participante " + participante.getNombreCompleto() + " ya está inscrito en este evento.");
            return false;
        }

        Inscripcion nuevaInscripcion = new Inscripcion(this, participante);
        this.inscripciones.add(nuevaInscripcion);
        participante.getInscripciones().add(nuevaInscripcion); // Asegurar bidireccionalidad
        return true;
    }

    public void removerParticipante(Persona participante) {
        if (participante != null) {
            Inscripcion inscripcionARemover = null;
            for (Inscripcion insc : this.inscripciones) {
                if (insc.getParticipante().equals(participante)) {
                    inscripcionARemover = insc;
                    break;
                }
            }
            if (inscripcionARemover != null) {
                this.inscripciones.remove(inscripcionARemover);
                participante.getInscripciones().remove(inscripcionARemover); // Asegurar bidireccionalidad
            }
        }
    }

    public List<Persona> getParticipantes() {
        return this.inscripciones.stream()
                .map(Inscripcion::getParticipante)
                .collect(Collectors.toList());
    }

    public boolean isActivo() {
        return this.estado == EstadoEvento.CONFIRMADO || this.estado == EstadoEvento.EN_EJECUCION;
    }

    public boolean isFinalizado() {
        return this.estado == EstadoEvento.FINALIZADO || this.estado == EstadoEvento.CANCELADO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return Objects.equals(id, evento.id) && Objects.equals(nombre, evento.nombre) && Objects.equals(fechaInicio, evento.fechaInicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, fechaInicio);
    }

    @Override
    public String toString() {
        return "Evento{" +
               "id=" + id +
               ", nombre='" + nombre + '\'' +
               ", fechaInicio=" + fechaInicio +
               ", duracionEstimacionMinutos=" + duracionEstimacionMinutos +
               ", requiereInscripcion=" + requiereInscripcion +
               ", cupoMaximo=" + cupoMaximo +
               ", estado=" + estado +
               ", responsables=" + responsables.stream().map(Persona::getNombreCompleto).collect(Collectors.joining(", ")) +
               '}';
    }
}
