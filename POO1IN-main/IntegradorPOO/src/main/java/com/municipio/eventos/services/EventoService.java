package com.municipio.eventos.services;

import com.municipio.eventos.dao.EventoDAO;
import com.municipio.eventos.models.abstractas.Evento;
import java.util.List;

public class EventoService {
    private EventoDAO eventoDAO = new EventoDAO();

    public boolean agregarEvento(Evento evento) {
        // Validación: no permitir dos eventos con el mismo nombre y fecha de inicio
        List<Evento> existentes = eventoDAO.obtenerTodos();
        boolean existe = existentes.stream().anyMatch(e ->
            e.getNombre().equalsIgnoreCase(evento.getNombre()) &&
            e.getFechaInicio().equals(evento.getFechaInicio())
        );
        if (existe) {
            System.out.println("Error: Ya existe un evento con ese nombre y fecha.");
            return false;
        }
        eventoDAO.guardar(evento);
        System.out.println("Evento '" + evento.getNombre() + "' agregado con éxito.");
        return true;
    }

    public boolean modificarEvento(Evento evento) {
        eventoDAO.actualizar(evento);
        System.out.println("Evento '" + evento.getNombre() + "' modificado con éxito.");
        return true;
    }

    public boolean eliminarEvento(Evento evento) {
        eventoDAO.eliminar(evento);
        System.out.println("Evento '" + evento.getNombre() + "' eliminado con éxito.");
        return true;
    }

    public Evento buscarEventoPorId(Long id) {
        return eventoDAO.buscarPorId(id);
    }

    public List<Evento> getTodosLosEventos() {
        return eventoDAO.obtenerTodos();
    }


    public List<Evento> buscarEventosPorNombre(String nombre) {
        return eventoDAO.obtenerTodos().stream()
            .filter(e -> e.getNombre().equalsIgnoreCase(nombre))
            .toList();
    }
}