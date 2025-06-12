package municipio.eventos.modelo.Evento;
import municipio.*;
import municipio.eventos.modelo.EstadoEvento;
import municipio.eventos.modelo.Persona;
import municipio.eventos.modelo.Participante;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Evento {
    protected Long id;
    protected String nombre;
    protected LocalDate fechaInicio;
    protected int duracionEstimada;
    protected EstadoEvento estado;
    protected List<Persona> responsables = new ArrayList<>();
    protected List<Participante> inscriptos = new ArrayList<>();

    public Evento(String nombre, LocalDate fechaInicio, int duracionEstimada) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.duracionEstimada = duracionEstimada;
        this.estado = EstadoEvento.PLANIFICACION;
    }

    public void cambiarEstado(EstadoEvento nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public boolean puedeInscribir() {
        return this.estado == EstadoEvento.CONFIRMADO;
    }

    public void agregarResponsable(Persona p) {
        this.responsables.add(p);
    }

    public boolean agregarParticipante(Participante p) {
        if (puedeInscribir()) {
            inscriptos.add(p);
            return true;
        }
        return false;
    }

    public abstract EstadoEvento getEstado();

    // Getters y setters
}
