package com.municipio.eventos.services;

import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.Organizador;
import com.municipio.eventos.models.Participante;
import com.municipio.eventos.models.Inscripcion;
import com.municipio.eventos.models.enums.EstadoEvento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Para manejar la posible ausencia de un evento

public class EventoService {

    private List<Evento> eventos; // Una lista en memoria para almacenar los eventos por ahora

    public EventoService() {
        this.eventos = new ArrayList<>();
    }

    /**
     * Registra un nuevo evento en el sistema.
     * @param evento El objeto Evento a registrar.
     * @return true si el evento se registró exitosamente, false si ya existe un evento con el mismo nombre y fecha.
     */
    public boolean agregarEvento(Evento evento) {
        // Validación básica: no permitir dos eventos con el mismo nombre y fecha de inicio
        boolean existe = eventos.stream().anyMatch(e ->
            e.getNombre().equalsIgnoreCase(evento.getNombre()) &&
            e.getFechaInicio().equals(evento.getFechaInicio())
        );
        if (existe) {
            System.out.println("Error: Ya existe un evento con el nombre '" + evento.getNombre() + "' para la fecha " + evento.getFechaInicio() + ".");
            return false;
        }
        eventos.add(evento);
        System.out.println("Evento '" + evento.getNombre() + "' agregado con éxito.");
        return true;
    }

    /**
     * Modifica un evento existente.
     * @param eventoModificado El objeto Evento con los datos actualizados.
     * @return true si el evento se modificó exitosamente, false si el evento no se encontró.
     */
    public boolean modificarEvento(Evento eventoModificado) {
        for (int i = 0; i < eventos.size(); i++) {
            if (eventos.get(i).getNombre().equalsIgnoreCase(eventoModificado.getNombre()) &&
                eventos.get(i).getFechaInicio().equals(eventoModificado.getFechaInicio())) { // Identificar por nombre y fecha
                eventos.set(i, eventoModificado);
                System.out.println("Evento '" + eventoModificado.getNombre() + "' modificado con éxito.");
                return true;
            }
        }
        System.out.println("Error: Evento '" + eventoModificado.getNombre() + "' no encontrado para modificar.");
        return false;
    }

    /**
     * Elimina un evento del sistema.
     * @param nombreEvento El nombre del evento a eliminar.
     * @param fechaInicio La fecha de inicio del evento a eliminar.
     * @return true si el evento se eliminó exitosamente, false si no se encontró.
     */
    public boolean eliminarEvento(String nombreEvento, LocalDate fechaInicio) {
        boolean removido = eventos.removeIf(e ->
            e.getNombre().equalsIgnoreCase(nombreEvento) &&
            e.getFechaInicio().equals(fechaInicio)
        );
        if (removido) {
            System.out.println("Evento '" + nombreEvento + "' eliminado con éxito.");
        } else {
            System.out.println("Error: Evento '" + nombreEvento + "' no encontrado para eliminar.");
        }
        return removido;
    }

    /**
     * Busca un evento por su nombre y fecha de inicio.
     * @param nombreEvento El nombre del evento.
     * @param fechaInicio La fecha de inicio del evento.
     * @return Un objeto Optional que contiene el Evento si se encuentra, o un Optional vacío si no.
     */
    public Optional<Evento> buscarEvento(String nombreEvento, LocalDate fechaInicio) {
        return eventos.stream()
                      .filter(e -> e.getNombre().equalsIgnoreCase(nombreEvento) && e.getFechaInicio().equals(fechaInicio))
                      .findFirst();
    }

    /**
     * Asocia un organizador a un evento.
     * @param evento El evento al que se agregará el organizador.
     * @param organizador El organizador a asociar.
     * @return true si se asoció correctamente, false si el evento no existe o el organizador ya está asociado.
     */
    public boolean asociarOrganizadorAEvento(Evento evento, Organizador organizador) {
        if (eventos.contains(evento)) {
            if (!evento.getResponsables().contains(organizador)) {
                evento.agregarResponsable(organizador);
                System.out.println("Organizador '" + organizador.getNombreCompleto() + "' asociado al evento '" + evento.getNombre() + "'.");
                return true;
            } else {
                System.out.println("Error: El organizador '" + organizador.getNombreCompleto() + "' ya es responsable del evento '" + evento.getNombre() + "'.");
            }
        } else {
            System.out.println("Error: El evento '" + evento.getNombre() + "' no existe en el sistema.");
        }
        return false;
    }

    /**
     * Registra un participante en un evento.
     * @param evento El evento al que se desea inscribir el participante.
     * @param participante El participante a inscribir.
     * @return true si la inscripción fue exitosa, false en caso contrario (evento no confirmado/finalizado, cupo lleno, ya inscrito).
     */
    public boolean registrarParticipanteAEvento(Evento evento, Participante participante) {
        if (!eventos.contains(evento)) {
            System.out.println("Error: El evento '" + evento.getNombre() + "' no existe en el sistema.");
            return false;
        }

        // Validar si el evento permite la inscripción (estado y cupo)
        if (!evento.permitirInscripcion()) {
            return false; // El método permitirInscripcion() ya imprime el error
        }

        // Verificar si el participante ya está inscrito en este evento
        boolean yaInscrito = evento.getInscripciones().stream()
                                  .anyMatch(insc -> insc.getParticipante().equals(participante)); // Asegúrate de implementar equals en Participante si DNI es el identificador único
        if (yaInscrito) {
            System.out.println("Error: El participante '" + participante.getNombreCompleto() + "' ya está inscrito en el evento '" + evento.getNombre() + "'.");
            return false;
        }

        // Realizar la inscripción
        Inscripcion nuevaInscripcion = new Inscripcion(participante, evento);
        evento.getInscripciones().add(nuevaInscripcion);
        System.out.println("Participante '" + participante.getNombreCompleto() + "' inscrito con éxito en el evento '" + evento.getNombre() + "'.");
        return true;
    }

    /**
     * Cambia el estado de un evento.
     * @param evento El evento cuyo estado se desea cambiar.
     * @param nuevoEstado El nuevo estado del evento.
     * @return true si el estado se cambió, false si el evento no existe.
     */
    public boolean cambiarEstadoEvento(Evento evento, EstadoEvento nuevoEstado) {
        if (eventos.contains(evento)) {
            evento.cambiarEstado(nuevoEstado);
            return true;
        }
        System.out.println("Error: El evento '" + evento.getNombre() + "' no existe en el sistema para cambiar su estado.");
        return false;
    }

    /**
     * Lista todos los eventos.
     * @return Una lista de todos los eventos registrados.
     */
    public List<Evento> getTodosLosEventos() {
        return new ArrayList<>(eventos); // Devolver una copia para evitar modificaciones externas directas
    }

    /**
     * Lista los participantes inscritos en un evento específico.
     * @param evento El evento del que se quieren listar los participantes.
     * @return Una lista de Participantes inscritos en el evento.
     */
    public List<Participante> getParticipantesPorEvento(Evento evento) {
        if (eventos.contains(evento)) {
            List<Participante> participantes = new ArrayList<>();
            for (Inscripcion insc : evento.getInscripciones()) {
                participantes.add(insc.getParticipante());
            }
            return participantes;
        }
        System.out.println("Error: El evento '" + evento.getNombre() + "' no existe en el sistema.");
        return new ArrayList<>(); // Retorna lista vacía si el evento no existe
    }

    /**
     * Valida el cupo máximo de un evento. (La lógica ya está en Evento.permitirInscripcion())
     * Este método es más bien una interfaz para llamar a la validación interna.
     * @param evento El evento a validar.
     * @return true si hay cupo disponible o si no aplica cupo, false si el cupo está lleno.
     */
    public boolean validarCupoEvento(Evento evento) {
        if (evento.isRequiereInscripcion() && evento.getCupoMaximo() > 0) {
            return evento.getInscripciones().size() < evento.getCupoMaximo();
        }
        return true; // No requiere inscripción o no tiene cupo máximo, por lo tanto, no hay problema de cupo
    }

    // --- Métodos pendientes para la gestión de Personas si es necesario ---
    // En un sistema más grande, tendrías un 'PersonaService' o 'OrganizadorService', 'ParticipanteService' etc.
    // Por ahora, si solo necesitas registrar personas para asociarlas a eventos, puedes manejarlas aquí o crear un servicio más simple.
    // Para simplificar, asumiremos que las Personas (Organizadores, Artistas, etc.) se gestionan en una lista aparte
    // y se "buscan" para asociarlas a eventos. Por ahora, no se implementa una gestión CRUD completa de Personas.
    // Si necesitamos un alta/baja/modificación general de personas, podemos añadir listas aquí o en un nuevo servicio.
}