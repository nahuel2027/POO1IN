package com.municipio.eventos.services;

import com.municipio.eventos.models.Persona;
import com.municipio.eventos.models.abstractas.Evento;


import  jakarta.persistence.*;
import java.util.List;

public class PersonaService {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("tuUnidadPersistencia");

    public void altaPersona(Persona persona) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(persona);
        em.getTransaction().commit();
        em.close();
    }

    public void modificarPersona(Persona persona) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(persona);
        em.getTransaction().commit();
        em.close();
    }

    public void bajaPersona(Long id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Persona persona = em.find(Persona.class, id);
        if (persona != null) {
            em.remove(persona);
        }
        em.getTransaction().commit();
        em.close();
    }

    public Persona buscarPersonaPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        Persona persona = em.find(Persona.class, id);
        em.close();
        return persona;
    }

    public List<Persona> listarPersonas() {
        EntityManager em = emf.createEntityManager();
        List<Persona> personas = em.createQuery("SELECT p FROM Persona p", Persona.class).getResultList();
        em.close();
        return personas;
    }
}