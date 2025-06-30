package com.municipio.eventos.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.enums.TipoEntradaConcierto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Concierto extends Evento {

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "concierto_artista",
        joinColumns = @JoinColumn(name = "concierto_id"),
        inverseJoinColumns = @JoinColumn(name = "artista_dni") // 'dni' es ahora el @Id de Persona/Artista
    )
    private List<Artista> artistas;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entrada")
    private TipoEntradaConcierto tipoEntrada;

    public Concierto() {
        super();
        this.artistas = new ArrayList<>();
    }

    public Concierto(String nombre, LocalDate fechaInicio, int duracionEstimadaMinutos,
                     boolean requiereInscripcion, int cupoMaximo,
                     TipoEntradaConcierto tipoEntrada) {
        super(nombre, fechaInicio, duracionEstimadaMinutos, requiereInscripcion, cupoMaximo);
        this.tipoEntrada = tipoEntrada;
        this.artistas = new ArrayList<>();
    }

    // Getters y Setters
    public List<Artista> getArtistas() {
        return artistas;
    }

    public void setArtistas(List<Artista> artistas) {
        this.artistas = artistas;
    }

    public TipoEntradaConcierto getTipoEntrada() {
        return tipoEntrada;
    }

    public void setTipoEntrada(TipoEntradaConcierto tipoEntrada) {
        this.tipoEntrada = tipoEntrada;
    }

    // Método para agregar artistas
    public void agregarArtista(Artista artista) {
        if (artista != null && !artistas.contains(artista)) {
            artistas.add(artista);
            // Si Artista tuviera una lista de Conciertos (relación bidireccional), se añadiría aquí:
            // artista.addConcierto(this);
        }
    }

    // Método para remover artistas
    public void removerArtista(Artista artista) {
        if (artistas.contains(artista)) {
            artistas.remove(artista);
            // Si Artista tuviera una lista de Conciertos (relación bidireccional), se removería aquí:
            // artista.removeConcierto(this);
        }
    }

    @Override
    public String toString() {
        String infoArtistas = artistas.isEmpty() ? "Ninguno" : artistas.stream().map(Artista::getNombreCompleto).collect(Collectors.joining(", "));
        return "Concierto: " + super.toString() +
               ", Artistas: " + infoArtistas +
               ", Tipo de Entrada: " + tipoEntrada;
    }
}
