package com.municipio.eventos.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Participante") // Asegúrate que el valor coincide con el tipo_persona de la tabla
public class Participante extends Persona {

    public Participante() {
        super();
    }

    public Participante(String nombreCompleto, String dni, String telefono, String email) {
        super(nombreCompleto, dni, telefono, email);
    }
}
