package com.municipio.eventos.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Curador")
public class Curador extends Persona {

    public Curador() {
        super();
    }

    public Curador(String nombreCompleto, String dni, String telefono, String email) {
        super(nombreCompleto, dni, telefono, email);
    }

    @Override
    public String toString() {
        return "Curador: " + super.toString();
    }
}
