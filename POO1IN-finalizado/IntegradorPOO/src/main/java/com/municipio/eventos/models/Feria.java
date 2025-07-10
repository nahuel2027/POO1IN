package com.municipio.eventos.models;

import java.time.LocalDate;
import java.util.Objects;

import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.enums.TipoUbicacionFeria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Feria extends Evento {

    @Column(name = "cantidad_stands") // Mapeo explícito al nombre de columna en snake_case
    private int cantidadStands;

    @Column(name = "tipo_ubicacion") // Mapeo explícito para asegurar consistencia
    private TipoUbicacionFeria tipoUbicacion;

    public Feria() {
        // Constructor sin argumentos requerido por JPA
    }

    public Feria(String nombre, LocalDate fechaInicio, int duracionEstimadaMinutos, boolean requiereInscripcion, int cupoMaximo, int cantidadStands, TipoUbicacionFeria tipoUbicacion) {
        super(nombre, fechaInicio, duracionEstimadaMinutos, requiereInscripcion, cupoMaximo);
        this.cantidadStands = cantidadStands;
        this.tipoUbicacion = tipoUbicacion;
    }

    public int getCantidadStands() {
        return cantidadStands;
    }

    public void setCantidadStands(int cantidadStands) {
        this.cantidadStands = cantidadStands;
    }

    public TipoUbicacionFeria getTipoUbicacion() {
        return tipoUbicacion;
    }

    public void setTipoUbicacion(TipoUbicacionFeria tipoUbicacion) {
        this.tipoUbicacion = tipoUbicacion;
    }

    @Override
    public String toString() {
        return "Feria{" +
               "id=" + getId() +
               ", nombre='" + getNombre() + '\'' +
               ", fechaInicio=" + getFechaInicio() +
               ", estado=" + getEstado() +
               ", cantidadStands=" + cantidadStands +
               ", tipoUbicacion=" + tipoUbicacion +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false; // Llama al equals de la superclase
        Feria feria = (Feria) o;
        return cantidadStands == feria.cantidadStands &&
               tipoUbicacion == feria.tipoUbicacion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cantidadStands, tipoUbicacion);
    }
}
