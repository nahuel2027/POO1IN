package com.municipio.eventos.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id; // DNI ahora es el @Id
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "personas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_persona", discriminatorType = DiscriminatorType.STRING)
public class Persona {

    @Id // El DNI es ahora la clave primaria de la entidad Persona
    @Column(name = "dni", unique = true, nullable = false)
    protected String dni; // Cambiado a protected para que las subclases accedan

    @Column(name = "nombre_completo", nullable = false)
    protected String nombreCompleto; // Cambiado a protected

    @Column(name = "telefono")
    protected String telefono; // Cambiado a protected

    @Column(name = "email")
    protected String email; // Cambiado a protected

    @OneToMany(mappedBy = "participante", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    protected List<Inscripcion> inscripciones = new ArrayList<>(); // Cambiado a protected

    // Constructores
    public Persona() {
        this.inscripciones = new ArrayList<>();
    }

    public Persona(String nombreCompleto, String dni, String telefono, String email) {
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.inscripciones = new ArrayList<>();
    }

    // Getters y Setters
    // Se elimina getId() y setId(Long id) ya que DNI es el ID
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Inscripcion> getInscripciones() { return inscripciones; }
    public void setInscripciones(List<Inscripcion> inscripciones) { this.inscripciones = inscripciones; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Persona persona = (Persona) o;
        return Objects.equals(dni, persona.dni); // La unicidad y la igualdad se basan en el DNI
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni); // El hashcode se basa en el DNI
    }

    @Override
    public String toString() {
        return "DNI: '" + dni + '\'' +
               ", Nombre: '" + nombreCompleto + '\'' +
               ", Teléfono: '" + telefono + '\'' +
               ", Email: '" + email + '\'';
    }
}
