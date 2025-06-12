package municipio.eventos.modelo.Evento;


import municipio.eventos.modelo.EstadoEvento;
// Update the import path to match the actual location of Pelicula
import municipio.eventos.modelo.Pelicula;

import java.util.List;

public class CicloCine extends Evento {
    private List<Pelicula> peliculas;
    private boolean hayCharlaPosterior;

    public CicloCine(String nombre, List<Pelicula> peliculas, boolean hayCharlaPosterior) {
        super(nombre, null, 1);
        this.peliculas = peliculas;
        this.hayCharlaPosterior = hayCharlaPosterior;
    }

    // Getters y setters

@Override
public EstadoEvento getEstado() {
    return this.estado;
}
}
