package com.municipio.eventos.models;

public class Organizador extends Persona {

    public Organizador(String nombreCompleto, String dni, String telefono, String email) {
        super(nombreCompleto, dni, telefono, email);
    }

    @Override
    public String toString() {
        return "Organizador: " + super.toString();
    }
}
