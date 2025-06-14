package com.municipio.eventos.models;

import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.enums.ModalidadTaller;

import java.time.LocalDate;

public class Taller extends Evento {
    // cupoMaximo ya está en Evento, así que solo lo usamos.
    private Instructor instructor;
    private ModalidadTaller modalidad;

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