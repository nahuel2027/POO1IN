package com.municipio.eventos.models;

import java.time.LocalDate;

import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.enums.ModalidadTaller;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "id") // Mapea la clave primaria de Evento
public class Taller extends Evento {

    // Relación ManyToOne con Persona para instructor
    // Usamos CascadeType.MERGE porque la Persona ya existe y está gestionada
    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "instructor_dni") // Columna de clave foránea en la tabla taller
    private Persona instructor;

    @Enumerated(EnumType.STRING)
    private ModalidadTaller modalidad;

    public Taller() {
        super();
    }

    public Taller(String nombre, LocalDate fechaInicio, int duracionEstimadaMinutos, boolean requiereInscripcion, int cupoMaximo, Persona instructor, ModalidadTaller modalidad) {
        super(nombre, fechaInicio, duracionEstimadaMinutos, requiereInscripcion, cupoMaximo);
        this.instructor = instructor;
        this.modalidad = modalidad;
    }

    // Getters y Setters
    public Persona getInstructor() {
        return instructor;
    }

    public void setInstructor(Persona instructor) {
        this.instructor = instructor;
    }

    public ModalidadTaller getModalidad() {
        return modalidad;
    }

    public void setModalidad(ModalidadTaller modalidad) {
        this.modalidad = modalidad;
    }

    @Override
    public String toString() {
        return "Taller{" +
               super.toString() +
               ", instructor=" + (instructor != null ? instructor.getNombreCompleto() : "N/A") +
               ", modalidad=" + modalidad +
               '}';
    }
}
