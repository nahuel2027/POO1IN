package com.municipio.eventos.dao;

import com.municipio.eventos.models.Inscripcion;
import jakarta.persistence.*;
import java.util.List;

public class InscripcionDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("EventosPU");

    public void guardar(Inscripcion inscripcion) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(inscripcion);
        em.getTransaction().commit();
        em.close();
    }

    public List<Inscripcion> obtenerTodas() {
        EntityManager em = emf.createEntityManager();
        List<Inscripcion> inscripciones = em.createQuery("SELECT i FROM Inscripcion i", Inscripcion.class).getResultList();
        em.close();
        return inscripciones;
    }

    public List<Inscripcion> buscarPorEvento(Long eventoId) {
        EntityManager em = emf.createEntityManager();
        List<Inscripcion> inscripciones = em.createQuery(
            "SELECT i FROM Inscripcion i WHERE i.evento.id = :eventoId", Inscripcion.class)
            .setParameter("eventoId", eventoId)
            .getResultList();
        em.close();
        return inscripciones;
    }

    public List<Inscripcion> buscarPorParticipante(Long participanteId) {
        EntityManager em = emf.createEntityManager();
        List<Inscripcion> inscripciones = em.createQuery(
            "SELECT i FROM Inscripcion i WHERE i.participante.id = :participanteId", Inscripcion.class)
            .setParameter("participanteId", participanteId)
            .getResultList();
        em.close();
        return inscripciones;
    }

    public void eliminar(Inscripcion inscripcion) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Inscripcion insc = em.find(Inscripcion.class, inscripcion.getId());
        if (insc != null) em.remove(insc);
        em.getTransaction().commit();
        em.close();
    }
}