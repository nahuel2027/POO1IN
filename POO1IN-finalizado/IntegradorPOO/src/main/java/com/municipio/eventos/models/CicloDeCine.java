package com.municipio.eventos.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.municipio.eventos.models.abstractas.Evento;

import jakarta.persistence.CascadeType; // Importar Collectors
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "id") // Mapea la clave primaria de Evento
public class CicloDeCine extends Evento {

    private boolean tieneCharlasPosteriores;

    // Relación OneToMany con Pelicula
    // CascadeType.ALL es apropiado aquí porque la vida de Pelicula depende del CicloDeCine
    @OneToMany(mappedBy = "cicloDeCine", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("ordenProyeccion ASC") // Asegura que las películas se recuperen en orden
    private List<Pelicula> peliculasAProyectar = new ArrayList<>();

    public CicloDeCine() {
        super();
        this.peliculasAProyectar = new ArrayList<>();
    }

    public CicloDeCine(String nombre, LocalDate fechaInicio, int duracionEstimadaMinutos, boolean requiereInscripcion, int cupoMaximo, boolean tieneCharlasPosteriores) {
        super(nombre, fechaInicio, duracionEstimadaMinutos, requiereInscripcion, cupoMaximo);
        this.tieneCharlasPosteriores = tieneCharlasPosteriores;
        this.peliculasAProyectar = new ArrayList<>();
    }

    // Getters y Setters
    public boolean isTieneCharlasPosteriores() {
        return tieneCharlasPosteriores;
    }

    public void setTieneCharlasPosteriores(boolean tieneCharlasPosteriores) {
        this.tieneCharlasPosteriores = tieneCharlasPosteriores;
    }

    public List<Pelicula> getPeliculasAProyectar() {
        return peliculasAProyectar;
    }

    public void setPeliculasAProyectar(List<Pelicula> peliculasAProyectar) {
        this.peliculasAProyectar = peliculasAProyectar;
    }

    // Métodos de conveniencia para películas
    public void agregarPelicula(Pelicula pelicula) {
        if (pelicula != null && !this.peliculasAProyectar.contains(pelicula)) {
            pelicula.setCicloDeCine(this); // Establece la relación inversa
            this.peliculasAProyectar.add(pelicula);
        }
    }

    public void removerPelicula(Pelicula pelicula) {
        if (this.peliculasAProyectar.remove(pelicula)) {
            pelicula.setCicloDeCine(null); // Rompe la relación inversa
        }
    }

    @Override
    public String toString() {
        return "CicloDeCine{" +
               super.toString() +
               ", tieneCharlasPosteriores=" + tieneCharlasPosteriores +
               ", peliculasAProyectar=" + peliculasAProyectar.stream().map(Pelicula::getTitulo).collect(Collectors.joining(", ")) +
               '}';
    }
}
