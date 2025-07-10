package com.municipio.eventos.services;

import java.util.List;
import java.util.Optional;

import com.municipio.eventos.dao.PersonaDAO;
import com.municipio.eventos.models.Persona;
import com.municipio.eventos.models.enums.TipoRol;

import jakarta.persistence.EntityManager;

public class PersonaService {

    private PersonaDAO personaDAO;

    public PersonaService() {
        this.personaDAO = new PersonaDAO();
    }

    // --- Métodos para ser usados con su propia transacción (desde la UI) ---
    // Estos métodos NO aceptan EntityManager como parámetro, y llaman a los métodos del DAO sin EM.
    public boolean altaPersona(Persona persona) {
        return personaDAO.save(persona); // Llama al save del DAO sin EM
    }

    public Optional<Persona> buscarPersonaPorDni(String dni) {
        return personaDAO.findById(dni); // Llama al findById del DAO sin EM
    }

    public List<Persona> listarPersonas() {
        return personaDAO.findAll(); // Llama al findAll del DAO sin EM
    }

    public boolean modificarPersona(Persona persona) {
        return personaDAO.update(persona); // Llama al update del DAO sin EM
    }

    public boolean bajaPersona(String dni) {
        return personaDAO.delete(dni); // Llama al delete del DAO sin EM
    }

    public List<Persona> getPersonasByRol(TipoRol tipoRol) {
        return personaDAO.findByRol(tipoRol); // Llama al findByRol del DAO sin EM
    }

    // --- Métodos sobrecargados para usar un EntityManager existente (para cargarDatosDePrueba) ---
    // Estos métodos SÍ aceptan EntityManager como parámetro, y llaman a los métodos del DAO con EM.
    public boolean altaPersona(Persona persona, EntityManager em) {
        return personaDAO.save(persona, em);
    }

    public Optional<Persona> buscarPersonaPorDni(String dni, EntityManager em) {
        return personaDAO.findById(dni, em);
    }

    public List<Persona> listarPersonas(EntityManager em) {
        return personaDAO.findAll(em);
    }

    public boolean modificarPersona(Persona persona, EntityManager em) {
        return personaDAO.update(persona, em);
    }

    public boolean bajaPersona(String dni, EntityManager em) {
        return personaDAO.delete(dni, em);
    }

    public List<Persona> getPersonasByRol(TipoRol tipoRol, EntityManager em) {
        return personaDAO.findByRol(tipoRol, em);
    }
}
