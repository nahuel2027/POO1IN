package com.municipio.eventos.models;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "peliculas")
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "orden_proyeccion", nullable = false)
    private int ordenProyeccion;

    // Relación ManyToOne con CicloDeCine
    // Muchas películas pueden pertenecer a un mismo ciclo de cine.
    // La columna 'ciclo_de_cine_id' será la clave foránea en la tabla 'peliculas'
    @ManyToOne
    @JoinColumn(name = "ciclo_de_cine_id") // Columna FK en la tabla 'peliculas' que referencia 'ciclo_de_cines'
    private CicloDeCine cicloDeCine; // Referencia al ciclo de cine al que pertenece esta película

    // Constructor vacío requerido por JPA
    public Pelicula() {}

    public Pelicula(String titulo, int ordenProyeccion) {
        this.titulo = titulo;
        this.ordenProyeccion = ordenProyeccion;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public int getOrdenProyeccion() { return ordenProyeccion; }
    public void setOrdenProyeccion(int ordenProyeccion) { this.ordenProyeccion = ordenProyeccion; }
    public CicloDeCine getCicloDeCine() { return cicloDeCine; }
    public void setCicloDeCine(CicloDeCine cicloDeCine) { this.cicloDeCine = cicloDeCine; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pelicula pelicula = (Pelicula) o;
        // Para equals/hashCode, si el ID es nulo (no persistido aún), compara por atributos de negocio.
        // Si el ID existe, es el identificador primario.
        if (id != null && pelicula.id != null) {
            return Objects.equals(id, pelicula.id);
        }
        return Objects.equals(titulo, pelicula.titulo) &&
               ordenProyeccion == pelicula.ordenProyeccion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titulo, ordenProyeccion); // Incluye todos los campos relevantes
    }

    @Override
    public String toString() {
        return "Pelicula: " + titulo + " (Orden: " + ordenProyeccion + ")";
    }
}
