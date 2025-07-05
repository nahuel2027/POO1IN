package com.municipio.eventos.dao;

import java.util.List;
import java.util.Optional;

import com.municipio.eventos.models.Inscripcion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

public class InscripcionDAO {

    private final EntityManagerFactory emf;

    public InscripcionDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Guarda una nueva inscripción en la base de datos.
     * @param inscripcion La inscripción a guardar.
     * @return true si la operación fue exitosa, false en caso contrario.
     */
    public boolean guardar(Inscripcion inscripcion) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(inscripcion);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al guardar inscripción en DAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Busca una inscripción por su ID.
     * @param id El ID de la inscripción a buscar.
     * @return Un Optional que contiene la Inscripcion si se encuentra, o un Optional vacío.
     */
    public Optional<Inscripcion> buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Inscripcion.class, id));
        } finally {
            em.close();
        }
    }

    /**
     * Lista todas las inscripciones de la base de datos.
     * @return Una lista de todas las Inscripciones.
     */
    public List<Inscripcion> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Inscripcion> query = em.createQuery("SELECT i FROM Inscripcion i", Inscripcion.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Elimina una inscripción de la base de datos por su ID.
     * @param id El ID de la inscripción a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminar(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Inscripcion inscripcion = em.find(Inscripcion.class, id);
            if (inscripcion != null) {
                em.remove(inscripcion);
                em.getTransaction().commit();
                return true;
            } else {
                System.out.println("Inscripción con ID " + id + " no encontrada para eliminar.");
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al eliminar inscripción en DAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}
