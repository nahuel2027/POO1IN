package municipio.eventos.modelo.Evento;
import municipio.eventos.modelo.EstadoEvento;
import municipio.eventos.modelo.Persona;

import municipio.eventos.modelo.TipoEntrada;
import java.util.List;

public class Concierto extends Evento {
    private TipoEntrada tipoEntrada;
    private List<Persona> artistas;

    public Concierto(String nombre, TipoEntrada tipoEntrada, List<Persona> artistas) {
        super(nombre, null, 1);
        this.tipoEntrada = tipoEntrada;
        this.artistas = artistas;
    }

    // Getters y setters

@Override
public EstadoEvento getEstado() {
    return this.estado;
}
}
