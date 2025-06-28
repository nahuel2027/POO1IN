package com.municipio.eventos.models;

import java.time.LocalDate;

import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.enums.ModalidadTaller;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Taller extends Evento {

    @ManyToOne
    @JoinColumn(name = "instructor_dni") // 'dni' es ahora el @Id de Persona/Instructor
    private Instructor instructor;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidad", nullable = false)
    private ModalidadTaller modalidad;

    public Taller() {
        super();
    }

    public Taller(String nombre, LocalDate fechaInicio, int duracionEstimadaMinutos,
                  boolean requiereInscripcion, int cupoMaximo,
                  Instructor instructor, ModalidadTaller modalidad) {
        super(nombre, fechaInicio, duracionEstimadaMinutos, requiereInscripcion, cupoMaximo);
        this.instructor = instructor;
        this.modalidad = modalidad;
    }

    // Getters y Setters
    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
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
        String infoInstructor = (instructor != null) ? instructor.getNombreCompleto() : "N/A";
        return "Taller: " + super.toString() +
               ", Instructor: " + infoInstructor +
               ", Modalidad: " + modalidad;
    }
}
