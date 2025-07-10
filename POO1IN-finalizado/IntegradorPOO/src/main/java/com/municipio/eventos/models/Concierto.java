package com.municipio.eventos.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.enums.TipoEntradaConcierto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "concierto")
public class Concierto extends Evento {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entrada", nullable = false)
    private TipoEntradaConcierto tipoEntrada;

    // Cambiado de ManyToOne a ManyToMany para permitir múltiples artistas
    // Se cambia CascadeType.PERSIST a CascadeType.MERGE
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
        name = "concierto_artista",
        joinColumns = @JoinColumn(name = "concierto_id"),
        inverseJoinColumns = @JoinColumn(name = "artista_dni")
    )
    private Set<Persona> artistas = new HashSet<>(); // Usamos Set para asegurar unicidad

    public Concierto() {
        // Constructor por defecto requerido por JPA
    }

    public Concierto(String nombre, LocalDate fechaInicio, int duracionEstimacionMinutos,
                     boolean requiereInscripcion, int cupoMaximo, TipoEntradaConcierto tipoEntrada) {
        super(nombre, fechaInicio, duracionEstimacionMinutos, requiereInscripcion, cupoMaximo);
        this.tipoEntrada = tipoEntrada;
    }

    public TipoEntradaConcierto getTipoEntrada() {
        return tipoEntrada;
    }

    public void setTipoEntrada(TipoEntradaConcierto tipoEntrada) {
        this.tipoEntrada = tipoEntrada;
    }

    public Set<Persona> getArtistas() {
        return artistas;
    }

    public void setArtistas(Set<Persona> artistas) {
        this.artistas = artistas;
    }

    public void agregarArtista(Persona artista) {
        if (artista != null) {
            this.artistas.add(artista);
        }
    }

    public void removerArtista(Persona artista) {
        if (artista != null) {
            this.artistas.remove(artista);
        }
    }

    @Override
    public String toString() {
        return "Concierto{" +
               "id=" + getId() +
               ", nombre='" + getNombre() + '\'' +
               ", fechaInicio=" + getFechaInicio() +
               ", estado=" + getEstado() +
               ", tipoEntrada=" + tipoEntrada +
               ", artistas=" + artistas.stream().map(Persona::getNombreCompleto).collect(java.util.stream.Collectors.joining(", ")) +
               '}';
    }
}
