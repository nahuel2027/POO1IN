package com.municipio.eventos.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Organizador")
public class Organizador extends Persona {

    public Organizador() {
        super();
    }

    public Organizador(String nombreCompleto, String dni, String telefono, String email) {
        super(nombreCompleto, dni, telefono, email);
    }

    @Override
    public String toString() {
        return "Organizador: " + super.toString();
    }
}
