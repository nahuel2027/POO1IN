package com.municipio.eventos.ventanaprincipal;

import java.time.LocalDate;

import com.municipio.eventos.models.Artista;
import com.municipio.eventos.models.Concierto;
import com.municipio.eventos.models.Curador;
import com.municipio.eventos.models.Exposicion;
import com.municipio.eventos.models.Feria;
import com.municipio.eventos.models.Instructor;
import com.municipio.eventos.models.Organizador;
import com.municipio.eventos.models.Participante;
import com.municipio.eventos.models.Taller;
import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.services.EventoService;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox; // Para el ejemplo de listar eventos
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane; // Para listas de ejemplo
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VentanaPrincipal extends Application {

    // Instancia de EventoService que será compartida entre las pestañas
    private EventoService eventoService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestión de Eventos del Municipio");

        // --- Inicializar EventoService al inicio de la aplicación JavaFX ---
        eventoService = new EventoService();
        // Opcional: Cargar algunos datos de prueba al iniciar la GUI, similar a tu MainApp
        cargarDatosDePrueba();

        // Crear un TabPane para organizar las diferentes secciones
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // --- Pestaña: Crear Eventos ---
        Tab eventosTab = new Tab("Crear Eventos");
        VBox eventosContent = new VBox(10);
        eventosContent.setPadding(new Insets(10));
        // Aquí podrías empezar a diseñar el formulario para crear eventos
        eventosContent.getChildren().add(new Label("Formulario para crear nuevos eventos."));
        // Por ejemplo, un TextField para el nombre del evento
        TextField nombreEventoField = new TextField();
        nombreEventoField.setPromptText("Nombre del Evento");
        eventosContent.getChildren().add(new Label("Nombre del Evento:"));
        eventosContent.getChildren().add(nombreEventoField);
        // ... más campos y un botón para "Guardar Evento"
        Button guardarEventoBtn = new Button("Guardar Evento");
        eventosContent.getChildren().add(guardarEventoBtn);

        // Ejemplo de acción para el botón (conectando con EventoService)
        guardarEventoBtn.setOnAction(e -> {
            // Esto es solo un ejemplo muy simplificado
            // En una aplicación real, recolectarías todos los datos del formulario
            // y crearías el tipo de Evento correspondiente.
            String nombreEvento = nombreEventoField.getText();
            if (!nombreEvento.isEmpty()) {
                // Aquí deberías tener una lógica más completa para crear un Evento
                // Por simplicidad, solo mostraremos un mensaje.
                System.out.println("Intento de crear evento: " + nombreEvento);
                // Aquí llamarías a eventoService.agregarEvento(nuevoEvento);
                // y manejarías la respuesta (éxito/fracaso)
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Evento '" + nombreEvento + "' creado (simulado).");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "El nombre del evento no puede estar vacío.");
                alert.showAndWait();
            }
        });

        eventosTab.setContent(eventosContent);


        // --- Pestaña: Gestionar Organizadores/Personas ---
        Tab personasTab = new Tab("Gestionar Personas");
        GridPane personasContent = new GridPane();
        personasContent.setPadding(new Insets(10));
        personasContent.setHgap(10);
        personasContent.setVgap(10);

        Label tipoPersonaLabel = new Label("Tipo de Persona:");
        ComboBox<String> tipoPersonaComboBox = new ComboBox<>();
        // Puedes poblar este ComboBox dinámicamente si tienes una lista de tipos
        tipoPersonaComboBox.getItems().addAll("Organizador", "Artista", "Curador", "Instructor", "Participante");
        tipoPersonaComboBox.setPromptText("Seleccione un tipo");

        personasContent.add(tipoPersonaLabel, 0, 0);
        personasContent.add(tipoPersonaComboBox, 1, 0);

        // Ejemplo: Mostrar una lista de organizadores (o cualquier tipo de persona)
        Label listaOrgLabel = new Label("Organizadores Registrados:");
        ListView<String> listaOrganizadoresView = new ListView<>();
        // Esto es solo un ejemplo, en una aplicación real, tendrías una lista de objetos Organizador
        // Y posiblemente un ObservableList para ListView.
        listaOrganizadoresView.getItems().addAll(
            "Juan Pérez (Organizador)",
            "Maria García (Organizador)",
            "Ana Gómez (Curador)",
            "Los Rockeros (Artista)",
            "Carlos Díaz (Instructor)",
            "Laura Fernández (Participante)"
        );
        personasContent.add(listaOrgLabel, 0, 2);
        personasContent.add(listaOrganizadoresView, 1, 2);


        personasTab.setContent(personasContent);

        // --- Pestaña: Inscribir Participantes ---
        Tab participantesTab = new Tab("Inscribir Participantes");
        VBox participantesContent = new VBox(10);
        participantesContent.setPadding(new Insets(10));
        participantesContent.getChildren().add(new Label("Aquí se gestionarán las inscripciones."));
        // Aquí podríamos tener ComboBoxes para seleccionar el Evento y el Participante,
        // y un botón para registrar la inscripción, usando eventoService.registrarParticipanteAEvento().
        participantesTab.setContent(participantesContent);

        // --- Pestaña: Listar Eventos (para ver los datos del EventoService) ---
        Tab listarEventosTab = new Tab("Listar Eventos");
        VBox listarEventosContent = new VBox(10);
        listarEventosContent.setPadding(new Insets(10));

        Button refrescarListaBtn = new Button("Refrescar Lista");
        listarEventosContent.getChildren().add(refrescarListaBtn);

        ListView<String> eventosListView = new ListView<>();
        listarEventosContent.getChildren().add(eventosListView);

        // Acción para refrescar la lista de eventos
        refrescarListaBtn.setOnAction(e -> {
            eventosListView.getItems().clear();
            for (Evento evento : eventoService.getTodosLosEventos()) {
                eventosListView.getItems().add(evento.toString()); // Asegúrate de que tus Eventos tengan un buen toString()
            }
            if (eventoService.getTodosLosEventos().isEmpty()) {
                eventosListView.getItems().add("No hay eventos registrados.");
            }
        });
        // Llama al refrescar al iniciar la pestaña
        refrescarListaBtn.fire(); // Dispara el evento para cargar la lista al inicio

        listarEventosTab.setContent(listarEventosContent);


        // Añadir todas las pestañas al TabPane
        tabPane.getTabs().addAll(eventosTab, personasTab, participantesTab, listarEventosTab); // Agregamos la nueva pestaña

        // Crear la escena y configurar la ventana principal (Stage)
        Scene scene = new Scene(tabPane, 900, 700); // Aumentamos un poco el tamaño
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Método para cargar algunos datos de prueba en el EventoService,
     * replicando parte de la lógica de tu MainApp.java.
     */
    private void cargarDatosDePrueba() {
        // Tu lógica de carga de datos de MainApp.java aquí
        Organizador org1 = new Organizador("Juan Pérez", "12345678", "1122334455", "juan.perez@municipio.com");
        Organizador org2 = new Organizador("Maria García", "87654321", "1199887766", "maria.garcia@municipio.com");

        Artista art1 = new Artista("Los Rockeros", "22334455", "1133445566", "rockeros@banda.com");

        Curador cur1 = new Curador("Ana Gómez", "44556677", "1155667788", "ana.gomez@museo.com");

        Instructor inst1 = new Instructor("Carlos Díaz", "55667788", "1111223344", "carlos.diaz@educacion.com");

        Participante part1 = new Participante("Laura Fernández", "98765432", "1144556677", "laura.f@email.com");
        Participante part2 = new Participante("Pedro Martínez", "11223344", "1177889900", "pedro.m@email.com");
        Participante part3 = new Participante("Sofía Ruíz", "66778899", "1100112233", "sofia.r@email.com");


        Feria feriaMunicipal = new Feria(
            "Feria de Artesanías 2025", LocalDate.of(2025, 7, 20), 480,
            false, 0, 50, com.municipio.eventos.models.enums.TipoUbicacionFeria.AIRE_LIBRE
        );
        feriaMunicipal.agregarResponsable(org1);

        Concierto conciertoRock = new Concierto(
            "Noche de Rock Local", LocalDate.of(2025, 8, 15), 180,
            true, 500, com.municipio.eventos.models.enums.TipoEntradaConcierto.PAGA
        );
        conciertoRock.agregarResponsable(org2);
        conciertoRock.agregarArtista(art1);

        Exposicion expoFotografia = new Exposicion(
            "Miradas Urbanas", LocalDate.of(2025, 9, 10), 360,
            true, 100,
            "Fotografía", cur1
        );
        expoFotografia.agregarResponsable(org1);

        Taller tallerProgramacion = new Taller(
            "Taller de Introducción a Java", LocalDate.of(2025, 10, 5), 240,
            true, 20,
            inst1, com.municipio.eventos.models.enums.ModalidadTaller.VIRTUAL
        );
        tallerProgramacion.agregarResponsable(org2);

        // Agregamos los eventos al servicio
        eventoService.agregarEvento(feriaMunicipal);
        eventoService.agregarEvento(conciertoRock);
        eventoService.agregarEvento(expoFotografia);
        eventoService.agregarEvento(tallerProgramacion);

        // Cambiamos algunos estados para que los ejemplos funcionen
        eventoService.cambiarEstadoEvento(conciertoRock, com.municipio.eventos.models.enums.EstadoEvento.CONFIRMADO);
        eventoService.cambiarEstadoEvento(expoFotografia, com.municipio.eventos.models.enums.EstadoEvento.CONFIRMADO);
        eventoService.cambiarEstadoEvento(tallerProgramacion, com.municipio.eventos.models.enums.EstadoEvento.CONFIRMADO);

        // Registramos participantes
        eventoService.registrarParticipanteAEvento(conciertoRock, part1);
        eventoService.registrarParticipanteAEvento(conciertoRock, part2);
        eventoService.registrarParticipanteAEvento(expoFotografia, part1);
        eventoService.registrarParticipanteAEvento(tallerProgramacion, part3);
    }
}