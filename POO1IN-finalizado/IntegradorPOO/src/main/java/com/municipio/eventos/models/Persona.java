package com.municipio.eventos.models;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List; // Asegúrate de que esta importación esté presente

@Entity
public class Persona {

    @Id
    private String dni; // DNI como clave primaria

    private String nombreCompleto;
    private String telefono;
    private String email;

    @OneToMany(mappedBy = "persona", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Rol> roles = new HashSet<>(); 

    @OneToMany(mappedBy = "participante", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Inscripcion> inscripciones = new HashSet<>();

    public Persona() {
        this.roles = new HashSet<>();
        this.inscripciones = new HashSet<>();
    }

    public Persona(String dni, String nombreCompleto, String telefono, String email) {
        this.dni = dni;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.email = email;
        this.roles = new HashSet<>();
        this.inscripciones = new HashSet<>();
    }

    // Getters y Setters
    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreComplepleto) {
        this.nombreCompleto = nombreComplepleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    public Set<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    public void setInscripciones(Set<Inscripcion> inscripciones) {
        this.inscripciones = inscripciones;
    }

    // Métodos de conveniencia para roles
    public Persona addRol(com.municipio.eventos.models.enums.TipoRol tipoRol) { 
        if (tipoRol != null && !hasRol(tipoRol)) {
            Rol rol = new Rol(tipoRol, this); // Crea una nueva instancia de la entidad 'Rol'
            this.roles.add(rol); // Añade el objeto 'Rol' a la colección 'roles'
        }
        return this; // ¡AÑADIDO: permite encadenar llamadas!
    }

    public void removeRol(com.municipio.eventos.models.enums.TipoRol tipoRol) {
        if (tipoRol != null) {
            this.roles.removeIf(rol -> rol.getTipoRol().equals(tipoRol));
        }
    }

    public boolean hasRol(com.municipio.eventos.models.enums.TipoRol tipoRol) {
        return roles.stream().anyMatch(rol -> rol.getTipoRol().equals(tipoRol));
    }

    @Override
    public String toString() {
        return "Persona{" +
               "dni='" + dni + '\'' +
               ", nombreCompleto='" + nombreCompleto + '\'' +
               ", telefono='" + telefono + '\'' +
               ", email='" + email + '\'' +
               ", roles=" + roles.stream().map(r -> r.getTipoRol().name()).collect(Collectors.joining(", ")) +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Persona persona = (Persona) o;
        return Objects.equals(dni, persona.dni); // Comparar por DNI ya que es el ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni); // Usar DNI para el hashCode
    }
}
