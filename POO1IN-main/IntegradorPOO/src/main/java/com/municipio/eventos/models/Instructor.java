package com.municipio.eventos.models;

public class Instructor extends Persona {

    public Instructor(String nombreCompleto, String dni, String telefono, String email) {
        super(nombreCompleto, dni, telefono, email);
    }

    @Override
    public String toString() {
        return "Instructor: " + super.toString();
    }
}
