package municipio.eventos.modelo;
import municipio.eventos.modelo.Evento.Evento;

// Este código define la clase Participante que hereda de Persona y permite a un participante inscribirse en un evento.
// La inscripción se realiza verificando si el evento permite inscripciones a través del método puedeInscribir().

public class Participante extends Persona {

    public boolean inscribirse(Evento evento) {
        return evento.puedeInscribir();
    }
}
