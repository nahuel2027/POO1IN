package com.municipio.eventos.dao;

import java.util.List;
import java.util.Optional;

import com.municipio.eventos.models.Persona;
import com.municipio.eventos.models.enums.TipoRol;
import com.municipio.eventos.utils.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class PersonaDAO {

    // --- Métodos para ser usados con su propio EntityManager (desde el Service) ---
    // Estos métodos abren y cierran su propio EntityManager
    public boolean save(Persona persona) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (persona.getDni() != null && em.find(Persona.class, persona.getDni()) != null) {
                em.merge(persona);
            } else {
                em.persist(persona);
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al guardar/actualizar persona en PersonaDAO (sin EM): " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public Optional<Persona> findById(String dni) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Persona persona = em.find(Persona.class, dni);
            return Optional.ofNullable(persona);
        } catch (Exception e) {
            System.err.println("Error al buscar persona por DNI en PersonaDAO (sin EM): " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Persona> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Persona> query = em.createQuery("SELECT p FROM Persona p", Persona.class);
            List<Persona> personas = query.getResultList();
            System.out.println("DEBUG: PersonaDAO.findAll() (sin EM) - Personas recuperadas: " + personas.size());
            return personas;
        } catch (Exception e) {
            System.err.println("Error al listar todas las personas en PersonaDAO (sin EM): " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public boolean update(Persona persona) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(persona);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al actualizar persona en PersonaDAO (sin EM): " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public boolean delete(String dni) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Persona persona = em.find(Persona.class, dni);
            if (persona != null) {
                em.remove(persona);
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al eliminar persona en PersonaDAO (sin EM): " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Persona> findByRol(TipoRol tipoRol) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT p FROM Persona p JOIN p.roles r WHERE r.tipoRol = :tipoRol";
            TypedQuery<Persona> query = em.createQuery(jpql, Persona.class);
            query.setParameter("tipoRol", tipoRol);
            List<Persona> personas = query.getResultList();
            System.out.println("DEBUG: PersonaDAO.findByRol(" + tipoRol.name() + ") (sin EM) - Personas recuperadas: " + personas.size());
            return personas;
        } catch (NoResultException e) {
            System.out.println("DEBUG: No se encontraron personas con el rol " + tipoRol.name() + ".");
            return List.of();
        } catch (Exception e) {
            System.err.println("Error al buscar personas por rol en PersonaDAO (sin EM): " + e.getMessage());
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
    public boolean save(Persona persona, EntityManager em) {
        try {
            if (persona.getDni() != null && em.find(Persona.class, persona.getDni()) != null) {
                em.merge(persona);
            } else {
                em.persist(persona);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar/actualizar persona en PersonaDAO (con EM): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Optional<Persona> findById(String dni, EntityManager em) {
        try {
            Persona persona = em.find(Persona.class, dni);
            return Optional.ofNullable(persona);
        } catch (Exception e) {
            System.err.println("Error al buscar persona por DNI en PersonaDAO (con EM): " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public List<Persona> findAll(EntityManager em) {
        try {
            TypedQuery<Persona> query = em.createQuery("SELECT p FROM Persona p", Persona.class);
            List<Persona> personas = query.getResultList();
            System.out.println("DEBUG: PersonaDAO.findAll() (con EM) - Personas recuperadas: " + personas.size());
            return personas;
        } catch (Exception e) {
            System.err.println("Error al listar todas las personas en PersonaDAO (con EM): " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public boolean update(Persona persona, EntityManager em) {
        try {
            em.merge(persona);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar persona en PersonaDAO (con EM): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String dni, EntityManager em) {
        try {
            Persona persona = em.find(Persona.class, dni);
            if (persona != null) {
                em.remove(persona);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error al eliminar persona en PersonaDAO (con EM): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Persona> findByRol(TipoRol tipoRol, EntityManager em) {
        try {
            String jpql = "SELECT p FROM Persona p JOIN p.roles r WHERE r.tipoRol = :tipoRol";
            TypedQuery<Persona> query = em.createQuery(jpql, Persona.class);
            query.setParameter("tipoRol", tipoRol);
            List<Persona> personas = query.getResultList();
            System.out.println("DEBUG: PersonaDAO.findByRol(" + tipoRol.name() + ") (con EM) - Personas recuperadas: " + personas.size());
            return personas;
        } catch (NoResultException e) {
            System.out.println("DEBUG: No se encontraron personas con el rol " + tipoRol.name() + ".");
            return List.of();
        } catch (Exception e) {
            System.err.println("Error al buscar personas por rol en PersonaDAO (con EM): " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}
