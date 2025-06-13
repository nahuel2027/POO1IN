package main.java.com.municipio.eventos.models;

import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.enums.TipoUbicacionFeria;

import java.time.LocalDate;

public class Feria extends Evento {
    private int cantidadStands;
    private TipoUbicacionFeria tipoUbicacion;

    public Feria(String nombre, LocalDate fechaInicio, int duracionEstimadaMinutos,
                 boolean requiereInscripcion, int cupoMaximo,
                 int cantidadStands, TipoUbicacionFeria tipoUbicacion) {
        super(nombre, fechaInicio, duracionEstimadaMinutos, requiereInscripcion, cupoMaximo);
        this.cantidadStands = cantidadStands;
        this.tipoUbicacion = tipoUbicacion;
    }

    // Getters y Setters
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
        return "Feria: " + super.toString() +
               ", Cantidad de Stands: " + cantidadStands +
               ", Ubicación: " + tipoUbicacion;
    }
}
