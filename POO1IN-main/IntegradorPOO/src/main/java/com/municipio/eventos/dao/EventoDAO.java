package com.municipio.eventos.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import com.municipio.eventos.models.abstractas.Evento;
import java.util.List;

public class EventoDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("EventosPU");

    public void guardar(Evento evento) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(evento);
        em.getTransaction().commit();
        em.close();
    }

    public void actualizar(Evento evento) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(evento);
        em.getTransaction().commit();
        em.close();
    }

    public void eliminar(Evento evento) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Evento ev = em.find(Evento.class, evento.getId());
        if (ev != null) em.remove(ev);
        em.getTransaction().commit();
        em.close();
    }

    public Evento buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        Evento evento = em.find(Evento.class, id);
        em.close();
        return evento;
    }

    public List<Evento> obtenerTodos() {
        EntityManager em = emf.createEntityManager();
        List<Evento> eventos = em.createQuery("SELECT e FROM Evento e", Evento.class).getResultList();
        em.close();
        return eventos;
    }
}