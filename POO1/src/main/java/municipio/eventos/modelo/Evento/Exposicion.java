package municipio.eventos.modelo.Evento;

import municipio.eventos.modelo.EstadoEvento;
import municipio.eventos.modelo.Persona;

public class Exposicion extends Evento {
    private String tipoArte;
    private Persona curador;

    public Exposicion(String nombre, String tipoArte, Persona curador) {
        super(nombre, null, 1);
        this.tipoArte = tipoArte;
        this.curador = curador;
    }

    // Getters y setters
    @Override
public EstadoEvento getEstado() {
    return this.estado;
}

}
