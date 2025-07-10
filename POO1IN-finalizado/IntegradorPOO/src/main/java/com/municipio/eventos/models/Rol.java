package com.municipio.eventos.models;

import java.util.Objects;

import com.municipio.eventos.models.enums.TipoRol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles") // Nombre explícito de la tabla en la base de datos
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_rol") // Nombre explícito de la columna para el enum
    private TipoRol tipoRol;

    // Relación ManyToOne con Persona: muchos roles pueden pertenecer a una Persona
    @ManyToOne(fetch = FetchType.LAZY) // LAZY es apropiado aquí para evitar ciclos de carga
    @JoinColumn(name = "persona_dni", nullable = false) // Clave foránea que apunta al DNI de Persona
    private Persona persona;

    public Rol() {
        // Constructor sin argumentos requerido por JPA
    }

    public Rol(TipoRol tipoRol, Persona persona) {
        this.tipoRol = tipoRol;
        this.persona = persona;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoRol getTipoRol() {
        return tipoRol;
    }

    public void setTipoRol(TipoRol tipoRol) {
        this.tipoRol = tipoRol;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rol rol = (Rol) o;
        // Para la igualdad de un Rol, consideramos el tipo de rol y la persona a la que pertenece
        return tipoRol == rol.tipoRol && Objects.equals(persona.getDni(), rol.persona.getDni());
    }

    @Override
    public int hashCode() {
        // Para el hashCode, también usamos el tipo de rol y el DNI de la persona
        return Objects.hash(tipoRol, persona.getDni());
    }

    @Override
    public String toString() {
        return "Rol{" +
               "id=" + id +
               ", tipoRol=" + tipoRol +
               ", personaDni='" + (persona != null ? persona.getDni() : "null") + '\'' +
               '}';
    }
}
