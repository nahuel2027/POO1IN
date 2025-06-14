package com.municipio.eventos.models;

import jakarta.persistence.Entity;

@Entity
public class Pelicula {
    private String titulo;
    private int ordenProyeccion; // Para registrar el orden en el ciclo de cine

    public Pelicula(String titulo, int ordenProyeccion) {
        this.titulo = titulo;
        this.ordenProyeccion = ordenProyeccion;
    }

    // Getters y Setters
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

    @Override
    public String toString() {
        return "Pelicula: " + titulo + " (Orden: " + ordenProyeccion + ")";
    }
}