package com.municipio.eventos.models;

public class Curador extends Persona {

    public Curador(String nombreCompleto, String dni, String telefono, String email) {
        super(nombreCompleto, dni, telefono, email);
    }

    @Override
    public String toString() {
        return "Curador: " + super.toString();
    }
}
