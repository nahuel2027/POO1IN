package com.municipio.eventos.models.abstractas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.municipio.eventos.models.Inscripcion;
import com.municipio.eventos.models.Organizador;
import com.municipio.eventos.models.enums.EstadoEvento;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Table(name = "eventos")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_evento")
public abstract class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "nombre", nullable = false)
    protected String nombre;

    @Column(name = "fecha_inicio", nullable = false)
    protected LocalDate fechaInicio;

    @Column(name = "duracion_minutos")
    protected int duracionEstimadaMinutos;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    protected EstadoEvento estado;

    @Column(name = "requiere_inscripcion")
    protected boolean requiereInscripcion;

    @Column(name = "cupo_maximo")
    protected int cupoMaximo;

    @ManyToMany
    @JoinTable(
        name = "evento_organizador",
        joinColumns = @JoinColumn(name = "evento_id"),
        inverseJoinColumns = @JoinColumn(name = "organizador_dni") // 'dni' es ahora el @Id de Persona/Organizador
    )
    protected List<Organizador> responsables;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<Inscripcion> inscripciones;

    public Evento() {
        this.responsables = new ArrayList<>();
        this.inscripciones = new ArrayList<>();
        this.estado = EstadoEvento.PLANIFICACION;
    }

    public Evento(String nombre, LocalDate fechaInicio, int duracionEstimadaMinutos,
                  boolean requiereInscripcion, int cupoMaximo) {
        this();
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.duracionEstimadaMinutos = duracionEstimadaMinutos;
        this.requiereInscripcion = requiereInscripcion;
        this.cupoMaximo = cupoMaximo;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public int getDuracionEstimadaMinutos() { return duracionEstimadaMinutos; }
    public void setDuracionEstimadaMinutos(int duracionEstimadaMinutos) { this.duracionEstimadaMinutos = duracionEstimadaMinutos; }
    public EstadoEvento getEstado() { return estado; }
    public void setEstado(EstadoEvento estado) { this.estado = estado; }
    public boolean isRequiereInscripcion() { return requiereInscripcion; }
    public void setRequiereInscripcion(boolean requiereInscripcion) { this.requiereInscripcion = requiereInscripcion; }
    public int getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(int cupoMaximo) { this.cupoMaximo = cupoMaximo; }
    public List<Organizador> getResponsables() { return responsables; }
    public void setResponsables(List<Organizador> responsables) { this.responsables = responsables; }
    public List<Inscripcion> getInscripciones() { return inscripciones; }
    public void setInscripciones(List<Inscripcion> inscripciones) { this.inscripciones = inscripciones; }

    public void agregarResponsable(Organizador organizador) {
        if (organizador != null && !responsables.contains(organizador)) {
            responsables.add(organizador);
        }
    }

    public void removerResponsable(Organizador organizador) {
        if (responsables.contains(organizador)) {
            responsables.remove(organizador);
        }
    }

    public void cambiarEstado(EstadoEvento nuevoEstado) {
        this.estado = nuevoEstado;
        System.out.println("El estado del evento '" + this.nombre + "' ha cambiado a: " + nuevoEstado);
    }

    public boolean permitirInscripcion() {
        if (this.estado != EstadoEvento.CONFIRMADO) {
            System.out.println("No se puede inscribir al evento '" + this.nombre + "'. El evento no está CONFIRMADO o ya ha FINALIZADO.");
            return false;
        }

        if (this.requiereInscripcion && this.cupoMaximo > 0) {
            if (this.inscripciones.size() >= this.cupoMaximo) {
                System.out.println("No se puede inscribir al evento '" + this.nombre + "'. Se ha alcanzado el cupo máximo.");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return id != null && Objects.equals(id, evento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String infoResponsables = responsables.isEmpty() ? "Ninguno" :
                                 responsables.stream()
                                             .map(Organizador::getNombreCompleto)
                                             .collect(Collectors.joining(", "));

        return "Nombre: " + nombre +
               ", Fecha Inicio: " + fechaInicio +
               ", Duración: " + duracionEstimadaMinutos + " minutos" +
               ", Estado: " + estado +
               ", Requiere Inscripción: " + (requiereInscripcion ? "Sí" : "No") +
               ", Cupo Máximo: " + (cupoMaximo > 0 ? cupoMaximo : "N/A") +
               ", Responsables: " + infoResponsables +
               ", Inscripciones: " + inscripciones.size();
    }
}
