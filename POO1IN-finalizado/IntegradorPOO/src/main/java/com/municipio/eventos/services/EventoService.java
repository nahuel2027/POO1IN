package com.municipio.eventos.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.municipio.eventos.dao.EventoDAO;
import com.municipio.eventos.models.Persona;
import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.enums.EstadoEvento;
import com.municipio.eventos.utils.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class EventoService {

    private EventoDAO eventoDAO;

    public EventoService() {
        this.eventoDAO = new EventoDAO();
    }

    // --- Métodos para ser usados con su propia transacción (desde la UI) ---
    // Estos métodos NO aceptan EntityManager como parámetro, y llaman a los métodos del DAO sin EM.
    public boolean agregarEvento(Evento evento) {
        return eventoDAO.save(evento); // Llama al save del DAO sin EM
    }

    public Optional<Evento> buscarEvento(Long id) {
        return eventoDAO.findById(id); // Llama al findById del DAO sin EM
    }

    public Optional<Evento> buscarEvento(String nombre, LocalDate fechaInicio) {
        return eventoDAO.findByNameAndDate(nombre, fechaInicio); // Llama al findByNameAndDate del DAO sin EM
    }

    public List<Evento> getTodosLosEventos() {
        return eventoDAO.findAll(); // Llama al findAll del DAO sin EM
    }

    public boolean modificarEvento(Evento evento) {
        return eventoDAO.update(evento); // Llama al update del DAO sin EM
    }

    public boolean eliminarEvento(Long id) {
        return eventoDAO.delete(id); // Llama al delete del DAO sin EM
    }

    public boolean cambiarEstadoEvento(Evento evento, EstadoEvento nuevoEstado) {
        EntityManager em = JPAUtil.getEntityManager(); // Abre un EM para esta operación específica
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Evento managedEvento = em.find(Evento.class, evento.getId());
            if (managedEvento != null) {
                managedEvento.setEstado(nuevoEstado);
                em.merge(managedEvento);
                tx.commit();
                System.out.println("DEBUG: Estado del evento " + managedEvento.getNombre() + " cambiado a " + nuevoEstado);
                return true;
            } else {
                System.err.println("ERROR: No se encontró el evento con ID " + evento.getId() + " para cambiar su estado.");
                tx.rollback();
                return false;
            }
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.err.println("Error al cambiar el estado del evento en EventoService (con transacción propia): " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public boolean registrarParticipanteAEvento(Evento evento, Persona participante) {
        EntityManager em = JPAUtil.getEntityManager(); // Abre un EM para esta operación específica
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Evento managedEvento = em.find(Evento.class, evento.getId());
            Persona managedParticipante = em.find(Persona.class, participante.getDni());

            if (managedEvento == null) {
                System.err.println("ERROR: Evento no encontrado para registrar participante.");
                tx.rollback();
                return false;
            }
            if (managedParticipante == null) {
                System.err.println("ERROR: Participante no encontrado para registrar en evento.");
                tx.rollback();
                return false;
            }
            
            if (!managedParticipante.hasRol(com.municipio.eventos.models.enums.TipoRol.PARTICIPANTE)) {
                System.err.println("ERROR: La persona " + managedParticipante.getNombreCompleto() + " no tiene el rol de PARTICIPANTE.");
                tx.rollback();
                return false;
            }

            boolean added = managedEvento.agregarParticipante(managedParticipante);
            
            if (added) {
                em.merge(managedEvento);
                tx.commit();
                System.out.println("DEBUG: Participante " + managedParticipante.getNombreCompleto() + " registrado en evento " + managedEvento.getNombre());
                return true;
            } else {
                tx.rollback();
                return false;
            }
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.err.println("Error al registrar participante en evento en EventoService (con transacción propia): " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Persona> getParticipantesPorEvento(Evento evento) {
        EntityManager em = JPAUtil.getEntityManager(); // Abre un EM para esta operación específica
        try {
            Evento managedEvento = em.find(Evento.class, evento.getId());
            if (managedEvento != null) {
                managedEvento.getInscripciones().size(); // Forzar inicialización
                return managedEvento.getParticipantes();
            }
            return List.of();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<com.municipio.eventos.models.Pelicula> getTodasLasPeliculasDeCiclos() {
        return eventoDAO.findAllPeliculas(); // Llama al findAllPeliculas del DAO sin EM
    }

    // --- Métodos sobrecargados para usar un EntityManager existente (para cargarDatosDePrueba) ---
    // Estos métodos SÍ aceptan EntityManager como parámetro, y llaman a los métodos del DAO con EM.
    public boolean agregarEvento(Evento evento, EntityManager em) {
        return eventoDAO.save(evento, em);
    }

    public Optional<Evento> buscarEvento(Long id, EntityManager em) {
        return eventoDAO.findById(id, em);
    }

    public Optional<Evento> buscarEvento(String nombre, LocalDate fechaInicio, EntityManager em) {
        return eventoDAO.findByNameAndDate(nombre, fechaInicio, em);
    }

    public List<Evento> getTodosLosEventos(EntityManager em) {
        return eventoDAO.findAll(em);
    }

    public boolean modificarEvento(Evento evento, EntityManager em) {
        return eventoDAO.update(evento, em);
    }

    public boolean eliminarEvento(Long id, EntityManager em) {
        return eventoDAO.delete(id, em);
    }

    public boolean cambiarEstadoEvento(Evento evento, EstadoEvento nuevoEstado, EntityManager em) {
        Evento managedEvento = em.find(Evento.class, evento.getId());
        if (managedEvento != null) {
            managedEvento.setEstado(nuevoEstado);
            em.merge(managedEvento);
            System.out.println("DEBUG: Estado del evento " + managedEvento.getNombre() + " cambiado a " + nuevoEstado + " (dentro de transacción compartida)");
            return true;
        } else {
            System.err.println("ERROR: No se encontró el evento con ID " + evento.getId() + " para cambiar su estado (dentro de transacción compartida).");
            return false;
        }
    }

    public boolean registrarParticipanteAEvento(Evento evento, Persona participante, EntityManager em) {
        Evento managedEvento = em.find(Evento.class, evento.getId());
        Persona managedParticipante = em.find(Persona.class, participante.getDni());

        if (managedEvento == null) {
            System.err.println("ERROR: Evento no encontrado para registrar participante (dentro de transacción compartida).");
            return false;
        }
        if (managedParticipante == null) {
            System.err.println("ERROR: Participante no encontrado para registrar en evento (dentro de transacción compartida).");
            return false;
        }
        
        if (!managedParticipante.hasRol(com.municipio.eventos.models.enums.TipoRol.PARTICIPANTE)) {
            System.err.println("ERROR: La persona " + managedParticipante.getNombreCompleto() + " no tiene el rol de PARTICIPANTE (dentro de transacción compartida).");
            return false;
        }

        boolean added = managedEvento.agregarParticipante(managedParticipante);
        
        if (added) {
            em.merge(managedEvento);
            System.out.println("DEBUG: Participante " + managedParticipante.getNombreCompleto() + " registrado en evento " + managedEvento.getNombre() + " (dentro de transacción compartida)");
            return true;
        } else {
            return false;
        }
    }

    public List<Persona> getParticipantesPorEvento(Evento evento, EntityManager em) {
        Evento managedEvento = em.find(Evento.class, evento.getId());
        if (managedEvento != null) {
            managedEvento.getInscripciones().size(); // Forzar inicialización
            return managedEvento.getParticipantes();
        }
        return List.of();
    }

    public List<com.municipio.eventos.models.Pelicula> getTodasLasPeliculasDeCiclos(EntityManager em) {
        return eventoDAO.findAllPeliculas(em);
    }
}
