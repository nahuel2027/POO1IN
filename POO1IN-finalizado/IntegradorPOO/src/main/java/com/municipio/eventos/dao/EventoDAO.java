package com.municipio.eventos.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.municipio.eventos.models.Pelicula;
import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.utils.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class EventoDAO {

    // --- Métodos para ser usados con su propio EntityManager (desde el Service) ---
    // Estos métodos abren y cierran su propio EntityManager
    public boolean save(Evento evento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (evento.getId() != null && em.find(Evento.class, evento.getId()) != null) {
                em.merge(evento);
            } else {
                em.persist(evento);
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al guardar/actualizar evento en EventoDAO (sin EM): " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public Optional<Evento> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Evento evento = em.find(Evento.class, id);
            return Optional.ofNullable(evento);
        } catch (Exception e) {
            System.err.println("Error al buscar evento por ID en EventoDAO (sin EM): " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public Optional<Evento> findByNameAndDate(String nombre, LocalDate fechaInicio) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Evento e WHERE e.nombre = :nombre AND e.fechaInicio = :fechaInicio";
            TypedQuery<Evento> query = em.createQuery(jpql, Evento.class);
            query.setParameter("nombre", nombre);
            query.setParameter("fechaInicio", fechaInicio);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            System.out.println("DEBUG: No se encontró evento con nombre '" + nombre + "' y fecha '" + fechaInicio + "'.");
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error al buscar evento por nombre y fecha en EventoDAO (sin EM): " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Evento> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Evento e";
            TypedQuery<Evento> query = em.createQuery(jpql, Evento.class);
            List<Evento> eventos = query.getResultList();
            System.out.println("DEBUG: EventoDAO.findAll() (sin EM) - Eventos recuperados: " + eventos.size());
            return eventos;
        } catch (Exception e) {
            System.err.println("Error al listar todos los eventos en EventoDAO (sin EM): " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
    
    public boolean update(Evento evento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(evento);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al actualizar evento en EventoDAO (sin EM): " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public boolean delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Evento evento = em.find(Evento.class, id);
            if (evento != null) {
                em.remove(evento);
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al eliminar evento en EventoDAO (sin EM): " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Pelicula> findAllPeliculas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Pelicula> query = em.createQuery("SELECT p FROM Pelicula p", Pelicula.class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error al listar todas las películas en EventoDAO (sin EM): " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    // --- Métodos sobrecargados para ser usados con un EntityManager existente (desde cargarDatosDePrueba) ---
    // Estos métodos NO gestionan su propia transacción ni cierran el EntityManager
    public boolean save(Evento evento, EntityManager em) {
        try {
            if (evento.getId() != null && em.find(Evento.class, evento.getId()) != null) {
                em.merge(evento);
            } else {
                em.persist(evento);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar/actualizar evento en EventoDAO (con EM): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Optional<Evento> findById(Long id, EntityManager em) {
        try {
            Evento evento = em.find(Evento.class, id);
            return Optional.ofNullable(evento);
        } catch (Exception e) {
            System.err.println("Error al buscar evento por ID en EventoDAO (con EM): " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<Evento> findByNameAndDate(String nombre, LocalDate fechaInicio, EntityManager em) {
        try {
            String jpql = "SELECT e FROM Evento e WHERE e.nombre = :nombre AND e.fechaInicio = :fechaInicio";
            TypedQuery<Evento> query = em.createQuery(jpql, Evento.class);
            query.setParameter("nombre", nombre);
            query.setParameter("fechaInicio", fechaInicio);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            System.out.println("DEBUG: No se encontró evento con nombre '" + nombre + "' y fecha '" + fechaInicio + "'.");
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error al buscar evento por nombre y fecha en EventoDAO (con EM): " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public List<Evento> findAll(EntityManager em) {
        try {
            String jpql = "SELECT e FROM Evento e";
            TypedQuery<Evento> query = em.createQuery(jpql, Evento.class);
            List<Evento> eventos = query.getResultList();
            System.out.println("DEBUG: EventoDAO.findAll() (con EM) - Eventos recuperados: " + eventos.size());
            return eventos;
        } catch (Exception e) {
            System.err.println("Error al listar todos los eventos en EventoDAO (con EM): " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    public boolean update(Evento evento, EntityManager em) {
        try {
            em.merge(evento);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar evento en EventoDAO (con EM): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id, EntityManager em) {
        try {
            Evento evento = em.find(Evento.class, id);
            if (evento != null) {
                em.remove(evento);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error al eliminar evento en EventoDAO (con EM): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Pelicula> findAllPeliculas(EntityManager em) {
        try {
            TypedQuery<Pelicula> query = em.createQuery("SELECT p FROM Pelicula p", Pelicula.class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error al listar todas las películas en EventoDAO (con EM): " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}
