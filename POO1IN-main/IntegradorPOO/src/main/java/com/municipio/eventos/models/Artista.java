package com.municipio.eventos.models;

public class Artista extends Persona {

    public Artista(String nombreCompleto, String dni, String telefono, String email) {
        super(nombreCompleto, dni, telefono, email);
    }

    @Override
    public String toString() {
        return "Artista: " + super.toString();
    }
}
