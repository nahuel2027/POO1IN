package municipio.eventos.modelo.Evento;

import java.time.LocalDate;

import municipio.eventos.modelo.EstadoEvento;

public class Feria extends Evento {
    private int cantidadStands;
    private boolean alAireLibre;

    public Feria(String nombre, LocalDate fechaInicio, int cantidadStands, boolean alAireLibre) {
        super(nombre, fechaInicio, cantidadStands);
        this.cantidadStands = cantidadStands;
        this.alAireLibre = alAireLibre;
    }

    // Getters y setters
    @Override
public EstadoEvento getEstado() {
    return this.estado;
}

}
