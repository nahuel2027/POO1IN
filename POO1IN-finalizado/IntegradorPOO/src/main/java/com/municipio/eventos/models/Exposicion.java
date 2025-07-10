package com.municipio.eventos.models;

import java.time.LocalDate;

import com.municipio.eventos.models.abstractas.Evento;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "id") // Mapea la clave primaria de Evento
public class Exposicion extends Evento {

    private String tipoArte;

    // Relación ManyToOne con Persona para curador
    // Usamos CascadeType.MERGE porque la Persona ya existe y está gestionada
    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "curador_dni") // Columna de clave foránea en la tabla exposicion
    private Persona curador;

    public Exposicion() {
        super();
    }

    public Exposicion(String nombre, LocalDate fechaInicio, int duracionEstimadaMinutos, boolean requiereInscripcion, int cupoMaximo, String tipoArte, Persona curador) {
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

    public Persona getCurador() {
        return curador;
    }

    public void setCurador(Persona curador) {
        this.curador = curador;
    }

    @Override
    public String toString() {
        return "Exposicion{" +
               super.toString() +
               ", tipoArte='" + tipoArte + '\'' +
               ", curador=" + (curador != null ? curador.getNombreCompleto() : "N/A") +
               '}';
    }
}
