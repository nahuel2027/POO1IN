package com.municipio.eventos.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Artista")
public class Artista extends Persona {

    public Artista() {
        super();
    }

    public Artista(String nombreCompleto, String dni, String telefono, String email) {
        super(nombreCompleto, dni, telefono, email);
    }

    @Override
    public String toString() {
        return "Artista: " + super.toString();
    }
}
