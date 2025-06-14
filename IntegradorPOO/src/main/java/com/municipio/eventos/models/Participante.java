package com.municipio.eventos.models;

import com.municipio.eventos.models.abstractas.Persona;

public class Participante extends Persona {
    // Puedes añadir un campo específico si "contacto" se refiere a algo más allá de telefono/email
    // Por ahora, simplemente extiende Persona

    public Participante(String nombreCompleto, String dni, String telefono, String email) {
        super(nombreCompleto, dni, telefono, email);
    }

    @Override
    public String toString() {
        return "Participante: " + super.toString();
    }
}
