package com.municipio.eventos.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.municipio.eventos.dao.EventoDAO;
import com.municipio.eventos.dao.PersonaDAO;
import com.municipio.eventos.models.CicloDeCine;
import com.municipio.eventos.models.Inscripcion;
import com.municipio.eventos.models.Participante;
import com.municipio.eventos.models.Pelicula;
import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.enums.EstadoEvento;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class EventoService {

    private EventoDAO eventoDAO;
    private PersonaDAO personaDAO; // Se necesita PersonaDAO para buscar participantes por DNI
    private static EntityManagerFactory emf;

    public EventoService() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory("EventosPU");
        }
        this.eventoDAO = new EventoDAO(emf);
        this.personaDAO = new PersonaDAO(emf); // Inicializar PersonaDAO
    }

    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("EntityManagerFactory cerrado.");
        }
    }

    public boolean agregarEvento(Evento evento) {
        Optional<Evento> existingEvent = eventoDAO.buscarPorNombreYFecha(evento.getNombre(), evento.getFechaInicio());
        if (existingEvent.isPresent()) {
            System.out.println("Error: Ya existe un evento con el nombre '" + evento.getNombre() + "' para la fecha " + evento.getFechaInicio() + ".");
            return false;
        }
        try {
            eventoDAO.guardar(evento);
            System.out.println("Evento '" + evento.getNombre() + "' agregado con éxito.");
            return true;
        } catch (Exception e) {
            System.err.println("Error al agregar evento '" + evento.getNombre() + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean modificarEvento(Evento eventoModificado) {
        if (eventoModificado.getId() == null) {
            System.out.println("Error: El evento a modificar no tiene un ID. No se puede actualizar.");
            return false;
        }
        try {
            eventoDAO.guardar(eventoModificado); // Usa guardar() que maneja persist y merge
            System.out.println("Evento '" + eventoModificado.getNombre() + "' modificado con éxito.");
            return true;
        } catch (Exception e) {
            System.err.println("Error al modificar evento '" + eventoModificado.getNombre() + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean bajaEvento(Long id) {
        try {
            return eventoDAO.eliminar(id);
        } catch (Exception e) {
            System.err.println("Error al eliminar evento con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Optional<Evento> buscarEvento(Long id) {
        return eventoDAO.buscarPorId(id);
    }

    public Optional<Evento> buscarEvento(String nombre, LocalDate fechaInicio) {
        return eventoDAO.buscarPorNombreYFecha(nombre, fechaInicio);
    }

    public List<Evento> getTodosLosEventos() {
        try {
            return eventoDAO.listarTodos();
        } catch (Exception e) {
            System.err.println("Error al listar todos los eventos: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean cambiarEstadoEvento(Evento evento, EstadoEvento nuevoEstado) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Recargar la entidad para asegurar que está gestionada
            Evento eventoGestionado = em.find(Evento.class, evento.getId());
            if (eventoGestionado == null) {
                System.err.println("Error: Evento no encontrado para cambiar estado.");
                tx.rollback();
                return false;
            }
            eventoGestionado.cambiarEstado(nuevoEstado);
            em.merge(eventoGestionado); // Usar merge para actualizar el estado
            tx.commit();
            System.out.println("Estado del evento '" + eventoGestionado.getNombre() + "' cambiado a " + nuevoEstado + ".");
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.err.println("Error al cambiar el estado del evento: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean registrarParticipanteAEvento(Evento evento, Participante participante) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Obtener instancias gestionadas del evento y participante
            Evento eventoGestionado = em.find(Evento.class, evento.getId());
            Participante participanteGestionado = em.find(Participante.class, participante.getDni()); // DNI es el @Id

            if (eventoGestionado == null) {
                System.err.println("Error al registrar participante: Evento no encontrado.");
                tx.rollback();
                return false;
            }
            if (participanteGestionado == null) {
                System.err.println("Error al registrar participante: Participante no encontrado. DNI: " + participante.getDni());
                tx.rollback();
                return false;
            }

            // Validar si el evento permite la inscripción (estado, cupo)
            if (!eventoGestionado.permitirInscripcion()) {
                tx.rollback();
                return false;
            }

            // Verificar si el participante ya está inscrito en este evento
            boolean yaInscrito = eventoGestionado.getInscripciones().stream()
                                                    .anyMatch(i -> i.getParticipante().equals(participanteGestionado));
            if (yaInscrito) {
                System.out.println("El participante " + participanteGestionado.getNombreCompleto() + " ya está inscrito en el evento " + eventoGestionado.getNombre());
                tx.rollback();
                return false;
            }

            // Crear la inscripción
            Inscripcion nuevaInscripcion = new Inscripcion(eventoGestionado, participanteGestionado, LocalDateTime.now());

            // Añadir la inscripción a las colecciones bidireccionales (se persistirán en cascada)
            eventoGestionado.getInscripciones().add(nuevaInscripcion);
            participanteGestionado.getInscripciones().add(nuevaInscripcion); // Asegurar bidireccionalidad

            em.persist(nuevaInscripcion); // Persistir la nueva inscripción
            em.merge(eventoGestionado); // Asegurarse de que los cambios en la colección del evento se guarden
            em.merge(participanteGestionado); // Asegurarse de que los cambios en la colección del participante se guarden

            tx.commit();
            System.out.println("Participante '" + participanteGestionado.getNombreCompleto() + "' inscrito con éxito en el evento '" + eventoGestionado.getNombre() + "'.");
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.err.println("Error al registrar participante al evento: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public List<Participante> getParticipantesPorEvento(Evento evento) {
        System.out.println("Buscando inscriptos para evento: " + evento.getNombre() + " (ID: " + evento.getId() + ")");
        if (evento.getId() == null) {
            System.out.println("Error: El evento no tiene ID para buscar participantes.");
            return new ArrayList<>();
        }
        EntityManager em = emf.createEntityManager();
        try {
            // Consulta que trae los participantes inscriptos al evento, inicializados
            List<Participante> participantes = em.createQuery(
                "SELECT p FROM Inscripcion i JOIN i.participante p WHERE i.evento.id = :eventoId", Participante.class)
                .setParameter("eventoId", evento.getId())
                .getResultList();
            System.out.println("Cantidad de inscriptos encontrados: " + participantes.size());
            return participantes;
        } catch (Exception e) {
            System.err.println("Error al obtener participantes para el evento '" + evento.getNombre() + "': " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public List<Pelicula> getTodasLasPeliculasDeCiclos() {
        List<Pelicula> todas = new ArrayList<>();
        for (Evento evento : getTodosLosEventos()) {
            if (evento instanceof CicloDeCine) {
                todas.addAll(((CicloDeCine) evento).getPeliculasAProyectar());
            }
        }
        return todas;
    }
}
