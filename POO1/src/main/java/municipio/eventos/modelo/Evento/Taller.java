package municipio.eventos.modelo.Evento;


import municipio.eventos.modelo.EstadoEvento;
import municipio.eventos.modelo.Modalidad;
import municipio.eventos.modelo.Persona;
import municipio.eventos.modelo.Participante;

import java.util.ArrayList;
import java.util.List;

public class Taller extends Evento {
    private Modalidad modalidad;
    private Persona instructor;
    private int cupoMaximo;

    // Assuming inscriptos is a list of Participante
    protected List<Participante> inscriptos = new ArrayList<>();

    public Taller(String nombre, Modalidad modalidad, Persona instructor, int cupoMaximo) {
        super(nombre, null, 1);
        this.modalidad = modalidad;
        this.instructor = instructor;
        this.cupoMaximo = cupoMaximo;
    }

    @Override
    public boolean puedeInscribir() {
        return super.puedeInscribir() && inscriptos.size() < cupoMaximo;
    }

    // Getters y setters
    @Override
public EstadoEvento getEstado() {
    return this.estado;
}

}
