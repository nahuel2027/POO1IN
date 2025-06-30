package com.municipio.eventos.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.municipio.eventos.dao.PersonaDAO;
import com.municipio.eventos.models.Persona;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class PersonaService {
    
    private PersonaDAO personaDAO;
    private static EntityManagerFactory emf;

    public PersonaService() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory("EventosPU");
        }
        this.personaDAO = new PersonaDAO(emf);
    }

    /**
     * Registra una nueva persona en el sistema.
     * @param persona El objeto Persona a registrar.
     * @return true si la persona se registró exitosamente, false si ya existe una persona con el mismo DNI.
     */
    public boolean altaPersona(Persona persona) {
        Optional<Persona> existingPersona = personaDAO.buscarPorDni(persona.getDni());
        if (existingPersona.isPresent()) {
            System.err.println("Error: Ya existe una persona con el DNI " + persona.getDni());
            return false;
        }
        try {
            personaDAO.guardar(persona); // Usar el DAO para guardar
            System.out.println("Persona '" + persona.getNombreCompleto() + "' agregada con éxito.");
            return true;
        } catch (Exception e) {
            System.err.println("Error al dar de alta persona: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Modifica una persona existente.
     * @param persona El objeto Persona con los datos actualizados.
     * @return true si la persona se modificó exitosamente, false si la persona no se encontró.
     */
    public boolean modificarPersona(Persona persona) {
        try {
            // El método guardar del DAO ya maneja la lógica de buscar y hacer merge si existe
            // o persistir si no. Solo necesitamos llamarlo.
            // Asegúrate de que el DNI no cambie, ya que es el ID.
            if (personaDAO.guardar(persona)) { // El método guardar ya maneja el merge/update
                System.out.println("Persona '" + persona.getNombreCompleto() + "' modificada con éxito.");
                return true;
            } else {
                System.err.println("Error: No se pudo modificar la persona con DNI " + persona.getDni() + ".");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error al modificar persona: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una persona del sistema por su DNI.
     * @param dni El DNI de la persona a eliminar.
     * @return true si la persona se eliminó exitosamente, false si no se encontró.
     */
    public boolean bajaPersona(String dni) {
        try {
            return personaDAO.eliminar(dni); // Usar el método eliminar por DNI del DAO
        } catch (Exception e) {
            System.err.println("Error al dar de baja persona con DNI " + dni + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca una persona por su DNI.
     * @param dni El DNI de la persona a buscar.
     * @return Un objeto Optional que contiene la Persona si se encuentra, o un Optional vacío si no.
     */
    public Optional<Persona> buscarPersonaPorDni(String dni) {
        return personaDAO.buscarPorDni(dni); // Usar el método buscarPorDNI del DAO
    }

    /**
     * Lista todas las personas registradas en el sistema.
     * @return Una lista de todas las Personas.
     */
    public List<Persona> listarPersonas() {
        try {
            return personaDAO.listarTodos(); // Usar el DAO para listar
        } catch (Exception e) {
            System.err.println("Error al listar personas: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Cierra el EntityManagerFactory al finalizar la aplicación.
     */
    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
            System.out.println("EntityManagerFactory de PersonaService cerrado.");
        }
    }
}
