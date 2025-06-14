package com.municipio.eventos.models;

import com.municipio.eventos.models.abstractas.Evento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CicloDeCine extends Evento {
    private List<Pelicula> peliculasAProyectar;
    private boolean tieneCharlasPosteriores;

    public CicloDeCine(String nombre, LocalDate fechaInicio, int duracionEstimadaMinutos,
                       boolean requiereInscripcion, int cupoMaximo,
                       boolean tieneCharlasPosteriores) {
        super(nombre, fechaInicio, duracionEstimadaMinutos, requiereInscripcion, cupoMaximo);
        this.peliculasAProyectar = new ArrayList<>();
        this.tieneCharlasPosteriores = tieneCharlasPosteriores;
    }

    // Getters y Setters
    public List<Pelicula> getPeliculasAProyectar() {
        // Devolvemos una copia inmutable para evitar modificaciones externas directas
        return new ArrayList<>(peliculasAProyectar);
    }

    public boolean isTieneCharlasPosteriores() {
        return tieneCharlasPosteriores;
    }

    public void setTieneCharlasPosteriores(boolean tieneCharlasPosteriores) {
        this.tieneCharlasPosteriores = tieneCharlasPosteriores;
    }

    // Método para agregar películas, manteniendo el orden
    public void agregarPelicula(Pelicula pelicula) {
        if (pelicula != null && !peliculasAProyectar.contains(pelicula)) {
            peliculasAProyectar.add(pelicula);
            // Opcional: ordenar la lista después de agregar si el orden es crítico
            peliculasAProyectar.sort(Comparator.comparingInt(Pelicula::getOrdenProyeccion));
        }
    }

    // Método para remover películas
    public void removerPelicula(Pelicula pelicula) {
        peliculasAProyectar.remove(pelicula);
    }

    @Override
    public String toString() {
        String infoPeliculas = peliculasAProyectar.isEmpty() ? "Ninguna" :
                               peliculasAProyectar.stream()
                                                  .map(p -> p.getTitulo() + " (Orden: " + p.getOrdenProyeccion() + ")")
                                                  .collect(Collectors.joining(", "));
        return "Ciclo de Cine: " + super.toString() +
               ", Películas: [" + infoPeliculas + "]" +
               ", Charlas Posteriores: " + (tieneCharlasPosteriores ? "Sí" : "No");
    }
}