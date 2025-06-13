package main.java.com.municipio.eventos.models;

import com.municipio.eventos.models.abstractas.Persona;

public class Artista extends Persona {

    public Artista(String nombreCompleto, String dni, String telefono, String email) {
        super(nombreCompleto, dni, telefono, email);
    }

    @Override
    public String toString() {
        return "Artista: " + super.toString();
    }
}
