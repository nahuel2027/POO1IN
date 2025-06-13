package main.java.com.municipio.eventos.models.abstractas;

import com.municipio.eventos.models.enums.EstadoEvento;
import com.municipio.eventos.models.Organizador; // Importar la clase Organizador
import com.municipio.eventos.models.Inscripcion; // Importar la clase Inscripcion

import java.time.LocalDate; // Para manejar fechas
import java.util.ArrayList; // Para manejar listas
import java.util.List;

public abstract class Evento {
    protected String nombre;
    protected LocalDate fechaInicio;
    protected int duracionEstimadaMinutos; // Duración en minutos
    protected EstadoEvento estado;
    protected boolean requiereInscripcion;
    protected int cupoMaximo; // 0 si no tiene cupo
    protected List<Organizador> responsables; // Lista de organizadores
    protected List<Inscripcion> inscripciones; // Lista de inscripciones

    // Constructor
    public Evento(String nombre, LocalDate fechaInicio, int duracionEstimadaMinutos,
                  boolean requiereInscripcion, int cupoMaximo) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.duracionEstimadaMinutos = duracionEstimadaMinutos;
        this.estado = EstadoEvento.PLANIFICACION; // Estado inicial por defecto
        this.requiereInscripcion = requiereInscripcion;
        this.cupoMaximo = cupoMaximo;
        this.responsables = new ArrayList<>();
        this.inscripciones = new ArrayList<>();
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public int getDuracionEstimadaMinutos() {
        return duracionEstimadaMinutos;
    }

    public void setDuracionEstimadaMinutos(int duracionEstimadaMinutos) {
        this.duracionEstimadaMinutos = duracionEstimadaMinutos;
    }

    public EstadoEvento getEstado() {
        return estado;
    }

    public void setEstado(EstadoEvento estado) {
        this.estado = estado;
    }

    public boolean isRequiereInscripcion() {
        return requiereInscripcion;
    }

    public void setRequiereInscripcion(boolean requiereInscripcion) {
        this.requiereInscripcion = requiereInscripcion;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public List<Organizador> getResponsables() {
        return responsables;
    }

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    // Métodos específicos del enunciado
    public void agregarResponsable(Organizador organizador) {
        if (organizador != null && !responsables.contains(organizador)) {
            responsables.add(organizador);
        }
    }

    public void removerResponsable(Organizador organizador) {
        responsables.remove(organizador);
    }

    /**
     * Permite cambiar el estado del evento.
     * @param nuevoEstado El nuevo estado al que se desea cambiar el evento.
     */
    public void cambiarEstado(EstadoEvento nuevoEstado) {
        this.estado = nuevoEstado;
        System.out.println("El estado del evento '" + this.nombre + "' ha cambiado a: " + nuevoEstado);
    }

    /**
     * Verifica si se permite la inscripción a este evento según su estado y cupo.
     * @return true si se puede inscribir, false en caso contrario.
     */
    public boolean permitirInscripcion() {
        // No se permite inscribir si el evento no está confirmado o si ya está finalizado
        if (this.estado != EstadoEvento.CONFIRMADO) {
            System.out.println("No se puede inscribir al evento '" + this.nombre + "'. El evento no está CONFIRMADO o ya ha FINALIZADO.");
            return false;
        }

        // Si requiere inscripción y tiene cupo, verificar si hay espacio
        if (this.requiereInscripcion && this.cupoMaximo > 0) {
            if (this.inscripciones.size() >= this.cupoMaximo) {
                System.out.println("No se puede inscribir al evento '" + this.nombre + "'. Se ha alcanzado el cupo máximo.");
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String infoBasica = "Nombre: " + nombre +
                            ", Fecha Inicio: " + fechaInicio +
                            ", Duración: " + duracionEstimadaMinutos + " minutos" +
                            ", Estado: " + estado +
                            ", Requiere Inscripción: " + (requiereInscripcion ? "Sí" : "No") +
                            ", Cupo Máximo: " + (cupoMaximo > 0 ? cupoMaximo : "N/A") +
                            ", Responsables: " + responsables.size() + " (" + responsables.stream().map(Persona::getNombreCompleto).reduce((a, b) -> a + ", " + b).orElse("Ninguno") + ")";
        return infoBasica;
    }
}
