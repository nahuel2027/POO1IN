package com.municipio.eventos.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.municipio.eventos.models.abstractas.Evento;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class EventoDAO {

    private final EntityManagerFactory emf;

    public EventoDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Guarda un nuevo evento en la base de datos o actualiza uno existente.
     * Este método reemplaza el antiguo 'actualizar'.
     * Si el evento no tiene ID, se persiste (nuevo). Si tiene ID, se fusiona (actualiza).
     * @param evento El evento a guardar/actualizar.
     * @return true si la operación fue exitosa, false en caso contrario.
     */
    public boolean guardar(Evento evento) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (evento.getId() == null) { // Si es un nuevo evento
                em.persist(evento);
            } else { // Si es un evento existente, lo fusiona (actualiza)
                em.merge(evento);
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al guardar/actualizar evento en DAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Busca un evento por su ID.
     * @param id El ID del evento a buscar.
     * @return Un Optional que contiene el Evento si se encuentra, o un Optional vacío.
     */
    public Optional<Evento> buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Evento.class, id));
        } finally {
            em.close();
        }
    }

    /**
     * Busca un evento por su nombre y fecha de inicio.
     * @param nombre El nombre del evento a buscar.
     * @param fechaInicio La fecha de inicio del evento a buscar.
     * @return Un Optional que contiene el Evento si se encuentra, o un Optional vacío.
     */
    public Optional<Evento> buscarPorNombreYFecha(String nombre, LocalDate fechaInicio) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Evento> query = em.createQuery(
                "SELECT e FROM Evento e WHERE e.nombre = :nombre AND e.fechaInicio = :fechaInicio", Evento.class);
            query.setParameter("nombre", nombre);
            query.setParameter("fechaInicio", fechaInicio);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty(); // No se encontró ningún resultado
        } catch (Exception e) {
            System.err.println("Error al buscar evento por nombre y fecha en DAO: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Lista todos los eventos de la base de datos.
     * @return Una lista de todos los Eventos.
     */
    public List<Evento> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Evento> query = em.createQuery("SELECT e FROM Evento e", Evento.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Elimina un evento de la base de datos por su ID.
     * @param id El ID del evento a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminar(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Evento evento = em.find(Evento.class, id);
            if (evento != null) {
                em.remove(evento);
                em.getTransaction().commit();
                return true;
            } else {
                System.out.println("Evento con ID " + id + " no encontrado para eliminar.");
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al eliminar evento en DAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}