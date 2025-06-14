package com.municipio.eventos.models;

import com.municipio.eventos.models.abstractas.Persona;

public class Curador extends Persona {

    public Curador(String nombreCompleto, String dni, String telefono, String email) {
        super(nombreCompleto, dni, telefono, email);
    }

    @Override
    public String toString() {
        return "Curador: " + super.toString();
    }
}
