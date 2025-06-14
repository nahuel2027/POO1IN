package com.municipio.eventos.models;

import java.time.LocalDate;

import com.municipio.eventos.models.abstractas.Evento;

public class Exposicion extends Evento {
    private String tipoArte;
    private Curador curador; // Un curador por exposición

    public Exposicion(String nombre, LocalDate fechaInicio, int duracionEstimadaMinutos,
                      boolean requiereInscripcion, int cupoMaximo,
                      String tipoArte, Curador curador) {
        super(nombre, fechaInicio, duracionEstimadaMinutos, requiereInscripcion, cupoMaximo);
        this.tipoArte = tipoArte;
        this.curador = curador;
    }

    // Getters y Setters
    public String getTipoArte() {
        return tipoArte;
    }

    public void setTipoArte(String tipoArte) {
        this.tipoArte = tipoArte;
    }

    public Curador getCurador() {
        return curador;
    }

    public void setCurador(Curador curador) {
        this.curador = curador;
    }

    @Override
    public String toString() {
        String infoCurador = (curador != null) ? curador.getNombreCompleto() : "N/A";
        return "Exposición: " + super.toString() +
               ", Tipo de Arte: " + tipoArte +
               ", Curador: " + infoCurador;
    }
}