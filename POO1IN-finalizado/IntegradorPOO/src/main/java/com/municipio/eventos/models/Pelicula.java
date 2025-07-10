package com.municipio.eventos.models;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private int ordenProyeccion;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetch es común para el lado "muchos"
    @JoinColumn(name = "ciclo_de_cine_id") // Columna de clave foránea en la tabla Pelicula
    private CicloDeCine cicloDeCine;

    public Pelicula() {
    }

    public Pelicula(String titulo, int ordenProyeccion) {
        this.titulo = titulo;
        this.ordenProyeccion = ordenProyeccion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    // No setter para ID si es generado automáticamente
    // public void setId(Long id) { this.id = id; }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getOrdenProyeccion() {
        return ordenProyeccion;
    }

    public void setOrdenProyeccion(int ordenProyeccion) {
        this.ordenProyeccion = ordenProyeccion;
    }

    public CicloDeCine getCicloDeCine() {
        return cicloDeCine;
    }

    public void setCicloDeCine(CicloDeCine cicloDeCine) {
        this.cicloDeCine = cicloDeCine;
    }

    @Override
    public String toString() {
        return "Pelicula{" +
               "id=" + id +
               ", titulo='" + titulo + '\'' +
               ", ordenProyeccion=" + ordenProyeccion +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pelicula pelicula = (Pelicula) o;
        return Objects.equals(titulo, pelicula.titulo) &&
               Objects.equals(ordenProyeccion, pelicula.ordenProyeccion); // Considerar título y orden para unicidad lógica
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo, ordenProyeccion);
    }
}
