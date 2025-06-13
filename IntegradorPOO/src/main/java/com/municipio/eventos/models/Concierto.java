package main.java.com.municipio.eventos.models;

import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.enums.TipoEntradaConcierto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Concierto extends Evento {
    private List<Artista> artistas;
    private TipoEntradaConcierto tipoEntrada;

    public Concierto(String nombre, LocalDate fechaInicio, int duracionEstimadaMinutos,
                     boolean requiereInscripcion, int cupoMaximo,
                     TipoEntradaConcierto tipoEntrada) {
        super(nombre, fechaInicio, duracionEstimadaMinutos, requiereInscripcion, cupoMaximo);
        this.artistas = new ArrayList<>();
        this.tipoEntrada = tipoEntrada;
    }

    // Getters y Setters
    public List<Artista> getArtistas() {
        return artistas;
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
        }
    }

    // Método para remover artistas
    public void removerArtista(Artista artista) {
        artistas.remove(artista);
    }

    @Override
    public String toString() {
        String infoArtistas = artistas.isEmpty() ? "Ninguno" : artistas.stream().map(Artista::getNombreCompleto).reduce((a, b) -> a + ", " + b).orElse("");
        return "Concierto: " + super.toString() +
               ", Artistas: " + infoArtistas +
               ", Tipo de Entrada: " + tipoEntrada;
    }
}