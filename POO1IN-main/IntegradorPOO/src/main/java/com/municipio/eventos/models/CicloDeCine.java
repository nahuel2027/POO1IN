package com.municipio.eventos.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.municipio.eventos.models.abstractas.Evento;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class CicloDeCine extends Evento {

    @OneToMany(mappedBy = "cicloDeCine", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Pelicula> peliculasAProyectar;

    @Column(name = "tiene_charlas_posteriores")
    private boolean tieneCharlasPosteriores;

    public CicloDeCine() {
        super();
        this.peliculasAProyectar = new ArrayList<>();
    }

    public CicloDeCine(String nombre, LocalDate fechaInicio, int duracionEstimadaMinutos,
                       boolean requiereInscripcion, int cupoMaximo,
                       boolean tieneCharlasPosteriores) {
        super(nombre, fechaInicio, duracionEstimadaMinutos, requiereInscripcion, cupoMaximo);
        this.peliculasAProyectar = new ArrayList<>();
        this.tieneCharlasPosteriores = tieneCharlasPosteriores;
    }

    // Getters y Setters
    public List<Pelicula> getPeliculasAProyectar() {
        return peliculasAProyectar;
    }

    public void setPeliculasAProyectar(List<Pelicula> peliculasAProyectar) {
        this.peliculasAProyectar = peliculasAProyectar;
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
            pelicula.setCicloDeCine(this); // Establecer la relación bidireccional
            peliculasAProyectar.sort(Comparator.comparingInt(Pelicula::getOrdenProyeccion));
        }
    }

    // Método para remover películas
    public void removerPelicula(Pelicula pelicula) {
        if (peliculasAProyectar.remove(pelicula)) {
            pelicula.setCicloDeCine(null); // Romper la relación bidireccional
        }
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
    
    // Método para agregar una película y guardar el ciclo o la película según la lógica
    public void agregarYGuardarPelicula(Pelicula pelicula) {
        if (pelicula != null) {
            pelicula.setCicloDeCine(this);
            this.agregarPelicula(pelicula);
            // Luego guarda el ciclo o la película según tu lógica
        }
    }
}
