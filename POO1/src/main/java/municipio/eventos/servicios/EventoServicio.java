package municipio.eventos.servicios;


import municipio.eventos.modelo.EstadoEvento;
import municipio.eventos.modelo.Participante;
import municipio.eventos.modelo.Evento.Evento;

import java.util.ArrayList;
import java.util.List;

public class EventoServicio {

    private List<Evento> eventos;

    public EventoServicio() {
        this.eventos = new ArrayList<>();
    }

    public void registrarEvento(Evento evento) {
        eventos.add(evento);
    }

    public List<Evento> obtenerEventos() {
        return eventos;
    }

    public List<Evento> obtenerEventosPorEstado(EstadoEvento estado) {
        List<Evento> filtrados = new ArrayList<>();
        for (Evento e : eventos) {
            if (e.getEstado() == estado) {
                filtrados.add(e);
            }
        }
        return filtrados;
    }

    public boolean inscribirParticipante(Evento evento, Participante participante) {
        if (evento.puedeInscribir()) {
            return evento.agregarParticipante(participante);
        }
        return false;
    }

    public boolean cambiarEstadoEvento(Evento evento, EstadoEvento nuevoEstado) {
        evento.cambiarEstado(nuevoEstado);
        return true;
    }
}
