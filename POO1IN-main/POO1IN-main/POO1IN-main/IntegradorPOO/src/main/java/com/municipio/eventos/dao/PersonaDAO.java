package com.municipio.eventos.dao;

import java.util.List;
import java.util.Optional;

import com.municipio.eventos.models.Persona;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

public class PersonaDAO {

    private final EntityManagerFactory emf;

    public PersonaDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Guarda una nueva persona en la base de datos o actualiza una existente.
     * Utiliza el DNI como identificador.
     * @param persona La persona a guardar/actualizar.
     * @return true si la operación fue exitosa, false en caso contrario.
     */
    public boolean guardar(Persona persona) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            // Si la persona ya existe (por su DNI, que es el @Id), se hace merge. Si no, se persiste.
            Persona managedPersona = em.find(Persona.class, persona.getDni());
            if (managedPersona == null) {
                em.persist(persona); // Nueva persona
            } else {
                // Si la persona ya existe, actualiza los campos del objeto gestionado
                managedPersona.setNombreCompleto(persona.getNombreCompleto());
                managedPersona.setTelefono(persona.getTelefono());
                managedPersona.setEmail(persona.getEmail());
                // No es necesario merge explícito aquí si managedPersona ya es un objeto gestionado,
                // pero si el objeto 'persona' pasado como parámetro es detached, merge lo adjuntará.
                // Sin embargo, si managedPersona ya es gestionado, se guardarán los cambios al hacer commit.
                // Para ser explícitos y seguros si 'persona' es detached:
                em.merge(managedPersona); 
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al guardar persona en DAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Busca una persona por su DNI (que ahora es el ID).
     * @param dni El DNI de la persona a buscar.
     * @return Un Optional que contiene la Persona si se encuentra, o un Optional vacío.
     */
    public Optional<Persona> buscarPorDni(String dni) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Persona.class, dni)); // Busca directamente por DNI (ID)
        } finally {
            em.close();
        }
    }

    /**
     * Lista todas las personas de la base de datos.
     * @return Una lista de todas las Personas.
     */
    public List<Persona> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Persona> query = em.createQuery("SELECT p FROM Persona p", Persona.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Elimina una persona de la base de datos por su DNI (que ahora es el ID).
     * @param dni El DNI de la persona a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminar(String dni) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Persona persona = em.find(Persona.class, dni); // Busca directamente por DNI (ID)
            if (persona != null) {
                em.remove(persona);
                em.getTransaction().commit();
                return true;
            } else {
                System.out.println("Persona con DNI " + dni + " no encontrada para eliminar.");
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al eliminar persona en DAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}
