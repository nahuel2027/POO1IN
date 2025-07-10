package com.municipio.eventos.ventanaprincipal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet; // Importar HashSet

import com.municipio.eventos.models.CicloDeCine;
import com.municipio.eventos.models.Concierto;
import com.municipio.eventos.models.Exposicion;
import com.municipio.eventos.models.Feria;
import com.municipio.eventos.models.Pelicula;
import com.municipio.eventos.models.Persona;
import com.municipio.eventos.models.Taller;
import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.enums.EstadoEvento;
import com.municipio.eventos.models.enums.ModalidadTaller;
import com.municipio.eventos.models.enums.TipoEntradaConcierto;
import com.municipio.eventos.models.enums.TipoUbicacionFeria;
import com.municipio.eventos.models.enums.TipoRol;
import com.municipio.eventos.services.EventoService;
import com.municipio.eventos.services.PersonaService;
import com.municipio.eventos.utils.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;

public class VentanaPrincipal extends Application {

    private EventoService eventoService;
    private PersonaService personaService;

    // Campos de la UI para Eventos (declarados como campos de clase)
    private Label tipoEventoLabel;
    private Label nombreEventoLabel;
    private Label fechaInicioLabel;
    private Label duracionMinutosLabel;
    private Label cupoMaximoLabel;
    private Label datosEspecificosLabel;

    private ComboBox<String> tipoEventoComboBox;
    private TextField nombreEventoField;
    private DatePicker fechaInicioPicker;
    private TextField duracionMinutosField;
    private CheckBox requiereInscripcionCheckBox;
    private TextField cupoMaximoField;
    private StackPane camposEspecificosPane;
    private ListView<Pelicula> listaPeliculasCicloView;
    private List<Pelicula> peliculasTemporales;
    private TextField tituloPeliculaField;
    private TextField ordenPeliculaField;
    private CheckBox tieneCharlasCheckBox;
    private ListView<Pelicula> listaPeliculasExistentesView;

    // Campos específicos de eventos (declarados como campos de clase)
    private TextField cantidadStandsField;
    private ComboBox<TipoUbicacionFeria> tipoUbicacionFeriaComboBox;
    
    // Para Concierto: ahora múltiples artistas
    private ComboBox<Persona> addArtistaConciertoComboBox; // Para añadir artistas uno por uno
    private ListView<Persona> artistasSeleccionadosConciertoListView; // Para mostrar artistas seleccionados
    private Set<Persona> artistasTemporalesConcierto; // Para almacenar temporalmente los artistas
    
    private ComboBox<TipoEntradaConcierto> tipoEntradaConciertoComboBox;
    private TextField tipoArteField;
    private ComboBox<Persona> curadorExposicionComboBox;
    private ComboBox<Persona> instructorTallerComboBox;
    private ComboBox<ModalidadTaller> modalidadTallerComboBox;


    // Variables de instancia para referenciar ListViews/ComboBoxes que necesitan refrescarse globalmente
    private ListView<Evento> eventosListView = new ListView<>();
    private ComboBox<Evento> eventoComboBoxInscripcion = new ComboBox<>();
    private ComboBox<Persona> participanteComboBoxInscripcion = new ComboBox<>();
    private ListView<Persona> listaPersonasView = new ListView<>();

    private ComboBox<String> filtroEstadoComboBox = new ComboBox<>(); 
    // Añadimos un Label para mostrar mensajes en la lista de eventos si está vacía
    private Label mensajeListaEventosLabel = new Label();


    private Persona personaSeleccionadaParaModificar = null;

    // Campos de la UI para Gestionar Personas (declarados como campos de clase)
    private Label lblTituloPersona;
    private ComboBox<TipoRol> tipoRolComboBox;
    private ListView<TipoRol> rolesSeleccionadosListView;
    private TextField nombrePersonaField;
    private TextField dniPersonaField;
    private TextField telefonoPersonaField;
    private TextField emailPersonaField;
    private Button guardarPersonaBtn;
    private Button limpiarFormPersonaBtn;
    private Button eliminarPersonaBtn;
    private Button addRolBtn;
    private Button removeRolBtn;
    private HBox rolButtons;
    private HBox personaFormButtons;


    // Campos de la UI para Inscribir Participantes (declarados como campos de clase)
    private Label lblTituloInscripcion;
    private Label eventoLabel;
    private Label participanteLabel;
    private Label inscripcionesEventoLabel;
    private Button inscribirBtn;
    private ListView<String> inscripcionesEventoListView;


    public static void main(String[] args) {
        System.out.println("DEBUG: Iniciando aplicación JavaFX desde VentanaPrincipal.main()");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestión de Eventos del Municipio");

        eventoService = new EventoService();
        personaService = new PersonaService();
        
        cargarDatosDePrueba(); // Esta es la llamada que puede fallar con la DB

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // --- Pestaña: Crear Eventos ---
        Tab eventosTab = new Tab("Crear Eventos");
        VBox eventosContent = new VBox(15);
        eventosContent.setPadding(new Insets(20));

        // Inicialización de campos de eventos como campos de clase
        tipoEventoLabel = new Label("Seleccione el Tipo de Evento:");
        tipoEventoComboBox = new ComboBox<>();
        nombreEventoLabel = new Label("Nombre:");
        nombreEventoField = new TextField();
        fechaInicioLabel = new Label("Fecha de Inicio:");
        fechaInicioPicker = new DatePicker(LocalDate.now());
        duracionMinutosLabel = new Label("Duración (horas):");
        duracionMinutosField = new TextField();
        requiereInscripcionCheckBox = new CheckBox("Requiere Inscripción Previa");
        cupoMaximoLabel = new Label("Cupo Máximo:");
        cupoMaximoField = new TextField();
        datosEspecificosLabel = new Label("Datos Específicos del Evento:");
        camposEspecificosPane = new StackPane();
        peliculasTemporales = new ArrayList<>();
        listaPeliculasCicloView = new ListView<>();
        tituloPeliculaField = new TextField();
        ordenPeliculaField = new TextField();
        tieneCharlasCheckBox = new CheckBox("Tiene Charlas Posteriores");
        listaPeliculasExistentesView = new ListView<>();

        // Inicialización de campos específicos de eventos
        cantidadStandsField = new TextField();
        tipoUbicacionFeriaComboBox = new ComboBox<>();
        
        // Para Concierto: Múltiples artistas
        addArtistaConciertoComboBox = new ComboBox<>();
        artistasSeleccionadosConciertoListView = new ListView<>();
        artistasTemporalesConcierto = new HashSet<>(); // Usar HashSet para unicidad
        
        tipoEntradaConciertoComboBox = new ComboBox<>();
        tipoArteField = new TextField();
        curadorExposicionComboBox = new ComboBox<>();
        instructorTallerComboBox = new ComboBox<>();
        modalidadTallerComboBox = new ComboBox<>();


        tipoEventoComboBox.getItems().addAll("Feria", "Concierto", "Exposición", "Taller", "Ciclo de Cine");
        tipoEventoComboBox.setPromptText("Seleccionar Tipo");
        tipoEventoComboBox.setMaxWidth(Double.MAX_VALUE);

        nombreEventoField.setPromptText("Nombre del Evento");
        fechaInicioPicker.setPromptText("Fecha de Inicio");
        duracionMinutosField.setPromptText("Duración estimada (horas)");
        duracionMinutosField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*([\\.]\\d*)?")) {
                duracionMinutosField.setText(oldValue);
            }
        });

        cupoMaximoField.setPromptText("Cupo Máximo (0 si ilimitado)");
        cupoMaximoField.disableProperty().bind(requiereInscripcionCheckBox.selectedProperty().not());
        cupoMaximoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                cupoMaximoField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // --- Formularios específicos para cada tipo de evento ---
        GridPane feriaForm = new GridPane();
        feriaForm.setVgap(10); feriaForm.setHgap(10); feriaForm.setPadding(new Insets(10, 0, 0, 0));
        cantidadStandsField.setPromptText("Cantidad de Stands");
        cantidadStandsField.textProperty().addListener((observable, oldValue, newValue) -> { if (!newValue.matches("\\d*")) { cantidadStandsField.setText(newValue.replaceAll("[^\\d]", "")); } });
        tipoUbicacionFeriaComboBox.getItems().addAll(TipoUbicacionFeria.values()); tipoUbicacionFeriaComboBox.setPromptText("Tipo de Ubicación");
        feriaForm.addRow(0, new Label("Cantidad de Stands:"), cantidadStandsField);
        feriaForm.addRow(1, new Label("Tipo de Ubicación:"), tipoUbicacionFeriaComboBox);

        // Concierto Form - Ahora con múltiples artistas
        GridPane conciertoForm = new GridPane();
        conciertoForm.setVgap(10); conciertoForm.setHgap(10); conciertoForm.setPadding(new Insets(10, 0, 0, 0));
        
        // ComboBox para añadir artistas
        addArtistaConciertoComboBox.setCellFactory(lv -> new ListCell<Persona>() { 
            @Override protected void updateItem(Persona item, boolean empty) { 
                super.updateItem(item, empty); 
                setText(empty ? "" : item != null ? item.getNombreCompleto() : ""); 
            } 
        });
        addArtistaConciertoComboBox.setButtonCell(new ListCell<Persona>() { 
            @Override protected void updateItem(Persona item, boolean empty) { 
                super.updateItem(item, empty); 
                setText(empty ? "Seleccionar Artista" : item != null ? item.getNombreCompleto() : ""); 
            } 
        });

        // ListView para artistas seleccionados
        artistasSeleccionadosConciertoListView.setPrefHeight(80);
        artistasSeleccionadosConciertoListView.setCellFactory(lv -> new ListCell<Persona>() {
            @Override protected void updateItem(Persona item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item != null ? item.getNombreCompleto() : "");
            }
        });
        
        Button addArtistaBtn = new Button("Añadir Artista");
        addArtistaBtn.setOnAction(e -> {
            Persona selectedArtist = addArtistaConciertoComboBox.getValue();
            if (selectedArtist != null) {
                if (artistasTemporalesConcierto.add(selectedArtist)) { // Add to set to ensure uniqueness
                    artistasSeleccionadosConciertoListView.getItems().add(selectedArtist);
                    addArtistaConciertoComboBox.getSelectionModel().clearSelection();
                } else {
                    new Alert(Alert.AlertType.WARNING, "El artista '" + selectedArtist.getNombreCompleto() + "' ya ha sido añadido.").showAndWait();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Por favor, seleccione un artista para añadir.").showAndWait();
            }
        });

        Button removeArtistaBtn = new Button("Remover Artista");
        removeArtistaBtn.setOnAction(e -> {
            Persona selectedArtist = artistasSeleccionadosConciertoListView.getSelectionModel().getSelectedItem();
            if (selectedArtist != null) {
                artistasTemporalesConcierto.remove(selectedArtist);
                artistasSeleccionadosConciertoListView.getItems().remove(selectedArtist);
            } else {
                new Alert(Alert.AlertType.WARNING, "Seleccione un artista de la lista para remover.").showAndWait();
            }
        });

        HBox artistaButtons = new HBox(5, addArtistaBtn, removeArtistaBtn);

        tipoEntradaConciertoComboBox.getItems().addAll(TipoEntradaConcierto.values()); tipoEntradaConciertoComboBox.setPromptText("Tipo de Entrada");
        
        conciertoForm.addRow(0, new Label("Añadir Artista:"), addArtistaConciertoComboBox, artistaButtons);
        conciertoForm.add(new Label("Artistas Seleccionados:"), 0, 1);
        conciertoForm.add(artistasSeleccionadosConciertoListView, 1, 1, 2, 1);
        conciertoForm.addRow(2, new Label("Tipo de Entrada:"), tipoEntradaConciertoComboBox);

        GridPane exposicionForm = new GridPane();
        exposicionForm.setVgap(10); exposicionForm.setHgap(10); exposicionForm.setPadding(new Insets(10, 0, 0, 0));
        tipoArteField.setPromptText("Tipo de Arte (Ej: Fotografía, Pintura)");
        curadorExposicionComboBox.setCellFactory(lv -> new ListCell<Persona>() { @Override protected void updateItem(Persona item, boolean empty) { super.updateItem(item, empty); setText(empty ? "" : item != null ? item.getNombreCompleto() : ""); } });
        curadorExposicionComboBox.setButtonCell(new ListCell<Persona>() { @Override protected void updateItem(Persona item, boolean empty) { super.updateItem(item, empty); setText(empty ? "Seleccionar Curador" : item != null ? item.getNombreCompleto() : ""); } });
        exposicionForm.addRow(0, new Label("Tipo de Arte:"), tipoArteField);
        exposicionForm.addRow(1, new Label("Curador:"), curadorExposicionComboBox);

        GridPane tallerForm = new GridPane();
        tallerForm.setVgap(10); tallerForm.setHgap(10); tallerForm.setPadding(new Insets(10, 0, 0, 0));
        instructorTallerComboBox.setCellFactory(lv -> new ListCell<Persona>() { @Override protected void updateItem(Persona item, boolean empty) { super.updateItem(item, empty); setText(empty ? "" : item != null ? item.getNombreCompleto() : ""); } });
        instructorTallerComboBox.setButtonCell(new ListCell<Persona>() { @Override protected void updateItem(Persona item, boolean empty) { super.updateItem(item, empty); setText(empty ? "Seleccionar Instructor" : item != null ? item.getNombreCompleto() : ""); } });
        modalidadTallerComboBox.getItems().addAll(ModalidadTaller.values()); modalidadTallerComboBox.setPromptText("Modalidad");
        tallerForm.addRow(0, new Label("Instructor:"), instructorTallerComboBox);
        tallerForm.addRow(1, new Label("Modalidad:"), modalidadTallerComboBox);

        GridPane cicloCineForm = new GridPane();
        cicloCineForm.setVgap(10); cicloCineForm.setHgap(10); cicloCineForm.setPadding(new Insets(10, 0, 0, 0));
        tituloPeliculaField = new TextField();
        ordenPeliculaField = new TextField();
        
        tituloPeliculaField.setPromptText("Título de la Película");
        ordenPeliculaField.setPromptText("Orden de Proyección");
        ordenPeliculaField.textProperty().addListener((observable, oldValue, newValue) -> { if (!newValue.matches("\\d*")) { ordenPeliculaField.setText(newValue.replaceAll("[^\\d]", "")); } });
        Button agregarPeliculaBtn = new Button("Agregar Película");
        listaPeliculasExistentesView.setPrefHeight(100);
        listaPeliculasExistentesView.setCellFactory(lv -> new ListCell<Pelicula>() {
            @Override
            protected void updateItem(Pelicula item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item != null ? item.getTitulo() + " (Orden: " + item.getOrdenProyeccion() + ")" : "");
            }
        });

        listaPeliculasCicloView.setPrefHeight(100);
        listaPeliculasCicloView.setCellFactory(lv -> new ListCell<Pelicula>() {
            @Override
            protected void updateItem(Pelicula item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item != null ? item.getTitulo() + " (Orden: " + item.getOrdenProyeccion() + ")" : "");
            }
        });

        peliculasTemporales = new ArrayList<>();
        agregarPeliculaBtn.setOnAction(e -> {
            String titulo = tituloPeliculaField.getText();
            int orden;
            try { orden = Integer.parseInt(ordenPeliculaField.getText()); } catch (NumberFormatException ex) { new Alert(Alert.AlertType.ERROR, "El orden de la película debe ser un número.").showAndWait(); return; }
            if (!titulo.isEmpty()) {
                Pelicula nuevaPelicula = new Pelicula(titulo, orden);
                if (peliculasTemporales.stream().noneMatch(p -> p.getTitulo().equalsIgnoreCase(titulo))) {
                    peliculasTemporales.add(nuevaPelicula); listaPeliculasCicloView.getItems().add(nuevaPelicula);
                    
                    clearPeliculaInputFields();

                    listaPeliculasCicloView.getItems().sort((p1, p2) -> Integer.compare(p1.getOrdenProyeccion(), p2.getOrdenProyeccion()));
                } else { new Alert(Alert.AlertType.WARNING, "La película '" + titulo + "' ya ha sido añadida a este ciclo.").showAndWait(); }
            } else { new Alert(Alert.AlertType.WARNING, "El título de la película no puede estar vacío.").showAndWait(); }
        });
        tieneCharlasCheckBox = new CheckBox("Tiene Charlas Posteriores");
        cicloCineForm.addRow(0, new Label("Título Película:"), tituloPeliculaField);
        cicloCineForm.addRow(1, new Label("Orden Proyección:"), ordenPeliculaField, agregarPeliculaBtn);
        cicloCineForm.addRow(2, new Label("Películas existentes:"), listaPeliculasExistentesView);
        cicloCineForm.add(listaPeliculasCicloView, 0, 3, 3, 1);
        cicloCineForm.addRow(4, new Label("Charlas Posteriores:"), tieneCharlasCheckBox);

        listaPeliculasExistentesView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Pelicula seleccionada = listaPeliculasExistentesView.getSelectionModel().getSelectedItem();
                if (seleccionada != null && peliculasTemporales.stream().noneMatch(p -> p.getTitulo().equalsIgnoreCase(seleccionada.getTitulo()))) {
                    Pelicula copia = new Pelicula(seleccionada.getTitulo(), seleccionada.getOrdenProyeccion());
                    peliculasTemporales.add(copia);
                    listaPeliculasCicloView.getItems().add(copia);
                    listaPeliculasCicloView.getItems().sort((p1, p2) -> Integer.compare(p1.getOrdenProyeccion(), p2.getOrdenProyeccion()));
                }
            }
        });

        tipoEventoComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            camposEspecificosPane.getChildren().clear();
            if (newVal != null) {
                switch (newVal) {
                    case "Feria": camposEspecificosPane.getChildren().add(feriaForm); break;
                    case "Concierto": 
                        camposEspecificosPane.getChildren().add(conciertoForm); 
                        cargarPersonasPorTipo(addArtistaConciertoComboBox, TipoRol.ARTISTA); // Cargar artistas en el ComboBox de añadir
                        artistasTemporalesConcierto.clear(); // Limpiar artistas temporales
                        artistasSeleccionadosConciertoListView.getItems().clear(); // Limpiar la lista de artistas seleccionados
                        break;
                    case "Exposición": camposEspecificosPane.getChildren().add(exposicionForm); cargarPersonasPorTipo(curadorExposicionComboBox, TipoRol.CURADOR); break;
                    case "Taller": camposEspecificosPane.getChildren().add(tallerForm); cargarPersonasPorTipo(instructorTallerComboBox, TipoRol.INSTRUCTOR); break;
                    case "Ciclo de Cine":
                        camposEspecificosPane.getChildren().add(cicloCineForm);
                        peliculasTemporales.clear();
                        listaPeliculasCicloView.getItems().clear();
                        listaPeliculasExistentesView.getItems().clear();

                        List<Pelicula> todasLasPeliculas = eventoService.getTodasLasPeliculasDeCiclos();
                        if (todasLasPeliculas != null && !todasLasPeliculas.isEmpty()) {
                            listaPeliculasExistentesView.getItems().addAll(todasLasPeliculas);
                            listaPeliculasExistentesView.getItems().sort((p1, p2) -> Integer.compare(p1.getOrdenProyeccion(), p2.getOrdenProyeccion()));
                        }
                        break;
                }
            }
        });

        Button crearEventoBtn = new Button("Crear Evento");
        crearEventoBtn.setMaxWidth(Double.MAX_VALUE);
        crearEventoBtn.setOnAction(e -> {
            String nombre = nombreEventoField.getText();
            LocalDate fechaInicio = fechaInicioPicker.getValue();
            double duracionHoras;
            boolean requiereInscripcion = requiereInscripcionCheckBox.isSelected();
            int cupoMaximo = 0;
            String tipoSeleccionado = tipoEventoComboBox.getValue();
            if (nombre.isEmpty() || fechaInicio == null || duracionMinutosField.getText().isEmpty() || tipoSeleccionado == null) {
                new Alert(Alert.AlertType.ERROR, "Por favor, complete todos los campos comunes del evento.").showAndWait();
                return;
            }
            try {
                duracionHoras = Double.parseDouble(duracionMinutosField.getText());
                if (duracionHoras <= 0) {
                    new Alert(Alert.AlertType.ERROR, "La duración estimada debe ser un número positivo.").showAndWait();
                    return;
                }
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "La duración estimada debe ser un número.").showAndWait();
                return;
            }
            int duracionEstimada = (int) Math.round(duracionHoras * 60);

            if (requiereInscripcion) {
                try {
                    cupoMaximo = Integer.parseInt(cupoMaximoField.getText());
                    if (cupoMaximo < 0) {
                        new Alert(Alert.AlertType.ERROR, "El cupo máximo no puede ser negativo.").showAndWait();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.ERROR, "El cupo máximo debe ser un número entero.").showAndWait();
                    return;
                }
            }

            final Evento[] nuevoEvento = new Evento[1];
            nuevoEvento[0] = null;

            switch (tipoSeleccionado) {
                case "Feria":
                    if (cantidadStandsField.getText().isEmpty() || tipoUbicacionFeriaComboBox.getValue() == null) { new Alert(Alert.AlertType.ERROR, "Complete los campos específicos de Feria.").showAndWait(); return; }
                    int cantidadStands = Integer.parseInt(cantidadStandsField.getText()); TipoUbicacionFeria tipoUbicacion = tipoUbicacionFeriaComboBox.getValue();
                    nuevoEvento[0] = new Feria(nombre, fechaInicio, duracionEstimada, requiereInscripcion, cupoMaximo, cantidadStands, tipoUbicacion);
                    break;
                case "Concierto":
                    if (artistasTemporalesConcierto.isEmpty() || tipoEntradaConciertoComboBox.getValue() == null) { 
                        new Alert(Alert.AlertType.ERROR, "Debe seleccionar al menos un artista y el tipo de entrada para el Concierto.").showAndWait(); 
                        return; 
                    }
                    TipoEntradaConcierto tipoEntrada = tipoEntradaConciertoComboBox.getValue();
                    Concierto nuevoConcierto = new Concierto(nombre, fechaInicio, duracionEstimada, requiereInscripcion, cupoMaximo, tipoEntrada);
                    artistasTemporalesConcierto.forEach(nuevoConcierto::agregarArtista); // Añadir todos los artistas seleccionados
                    nuevoEvento[0] = nuevoConcierto;
                    break;
                case "Exposición":
                    if (tipoArteField.getText().isEmpty() || curadorExposicionComboBox.getValue() == null) { new Alert(Alert.AlertType.ERROR, "Complete los campos específicos de Exposición.").showAndWait(); return; }
                    String tipoArte = tipoArteField.getText();
                    Persona curadorSeleccionado = curadorExposicionComboBox.getValue();
                    if (curadorSeleccionado == null) {
                        new Alert(Alert.AlertType.ERROR, "Error: Seleccione un curador válido.").showAndWait();
                        return;
                    }
                    nuevoEvento[0] = new Exposicion(nombre, fechaInicio, duracionEstimada, requiereInscripcion, cupoMaximo, tipoArte, curadorSeleccionado);
                    break;
                case "Taller":
                    if (instructorTallerComboBox.getValue() == null || modalidadTallerComboBox.getValue() == null) { new Alert(Alert.AlertType.ERROR, "Complete los campos específicos de Taller.").showAndWait(); return; }
                    Persona instructorSeleccionado = instructorTallerComboBox.getValue();
                    if (instructorSeleccionado == null) {
                        new Alert(Alert.AlertType.ERROR, "Error: Seleccione un instructor válido.").showAndWait();
                        return;
                    }
                    ModalidadTaller modalidad = modalidadTallerComboBox.getValue();
                    nuevoEvento[0] = new Taller(nombre, fechaInicio, duracionEstimada, requiereInscripcion, cupoMaximo, instructorSeleccionado, modalidad);
                    break;
                case "Ciclo de Cine":
                    if (peliculasTemporales.isEmpty()) {
                        new Alert(Alert.AlertType.ERROR, "Debe agregar o seleccionar al menos una película para el Ciclo de Cine.").showAndWait();
                        return;
                    }
                    boolean tieneCharlas = tieneCharlasCheckBox.isSelected();
                    CicloDeCine nuevoCiclo = new CicloDeCine(nombre, fechaInicio, duracionEstimada, requiereInscripcion, cupoMaximo, tieneCharlas);
                    peliculasTemporales.forEach(nuevoCiclo::agregarPelicula);
                    nuevoEvento[0] = nuevoCiclo;
                    break;
            }

            if (nuevoEvento[0] != null) {
                Optional<Persona> org1Opt = personaService.buscarPersonaPorDni("12345678");
                Optional<Persona> org2Opt = personaService.buscarPersonaPorDni("87654321");

                org1Opt.ifPresent(o -> nuevoEvento[0].agregarResponsable(o));
                org2Opt.ifPresent(o -> nuevoEvento[0].agregarResponsable(o));


                if (eventoService.agregarEvento(nuevoEvento[0])) {
                    new Alert(Alert.AlertType.INFORMATION, "Evento '" + nuevoEvento[0].getNombre() + "' creado con éxito.").showAndWait();
                    limpiarFormularioEvento();
                    actualizarTodasLasListas();
                } else {
                    new Alert(Alert.AlertType.WARNING, "No se pudo crear el evento (posiblemente ya existe o hubo un error). Verifique la consola para más detalles.").showAndWait();
                }
            }
        });

        eventosContent.getChildren().addAll(
            tipoEventoLabel, tipoEventoComboBox,
            nombreEventoLabel, nombreEventoField,
            fechaInicioLabel, fechaInicioPicker,
            duracionMinutosLabel, duracionMinutosField,
            requiereInscripcionCheckBox,
            cupoMaximoLabel, cupoMaximoField,
            new Separator(),
            datosEspecificosLabel,
            camposEspecificosPane,
            crearEventoBtn
        );
        eventosTab.setContent(eventosContent);


        // --- Pestaña: Gestionar Personas ---
        Tab personasTab = new Tab("Gestionar Personas");
        VBox personasContent = new VBox(15);
        personasContent.setPadding(new Insets(20));

        GridPane personaFormGrid = new GridPane();
        personaFormGrid.setVgap(10); personaFormGrid.setHgap(10); personaFormGrid.setPadding(new Insets(10));
        
        // Inicialización de campos de personas como campos de clase
        lblTituloPersona = new Label("Alta/Modificación de Persona");
        lblTituloPersona.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        tipoRolComboBox = new ComboBox<>();
        tipoRolComboBox.getItems().addAll(TipoRol.values());
        tipoRolComboBox.setPromptText("Seleccione Rol"); tipoRolComboBox.setMaxWidth(Double.MAX_VALUE);
        
        rolesSeleccionadosListView = new ListView<>();
        rolesSeleccionadosListView.setPrefHeight(80);
        rolesSeleccionadosListView.setCellFactory(lv -> new ListCell<TipoRol>() {
            @Override protected void updateItem(TipoRol item, boolean empty) { super.updateItem(item, empty); setText(empty ? "" : item.name()); }
        });

        // Inicialización de botones para añadir/remover rol (ahora son campos de clase)
        addRolBtn = new Button("Añadir Rol");
        removeRolBtn = new Button("Remover Rol");
        rolButtons = new HBox(5, addRolBtn, removeRolBtn); // Inicialización única para el campo de clase

        addRolBtn.setOnAction(event -> {
            TipoRol selectedRol = tipoRolComboBox.getValue();
            if (selectedRol != null && !rolesSeleccionadosListView.getItems().contains(selectedRol)) {
                rolesSeleccionadosListView.getItems().add(selectedRol);
            } else if (selectedRol == null) {
                new Alert(Alert.AlertType.WARNING, "Por favor, seleccione un rol para añadir.").showAndWait();
            } else {
                new Alert(Alert.AlertType.WARNING, "El rol '" + selectedRol.name() + "' ya ha sido añadido.").showAndWait();
            }
        });

        removeRolBtn.setOnAction(event -> {
            TipoRol selectedRol = rolesSeleccionadosListView.getSelectionModel().getSelectedItem();
            if (selectedRol != null) {
                rolesSeleccionadosListView.getItems().remove(selectedRol);
            } else {
                new Alert(Alert.AlertType.WARNING, "Seleccione un rol de la lista para remover.").showAndWait();
            }
        });

        nombrePersonaField = new TextField(); nombrePersonaField.setPromptText("Nombre Completo");
        dniPersonaField = new TextField(); dniPersonaField.setPromptText("DNI");
        dniPersonaField.textProperty().addListener((observable, oldValue, newValue) -> { if (!newValue.matches("\\d*")) { dniPersonaField.setText(newValue.replaceAll("[^\\d]", "")); } });
        telefonoPersonaField = new TextField(); telefonoPersonaField.setPromptText("Teléfono");
        emailPersonaField = new TextField(); emailPersonaField.setPromptText("Correo Electrónico");

        guardarPersonaBtn = new Button("Guardar Persona");
        limpiarFormPersonaBtn = new Button("Limpiar Formulario");
        eliminarPersonaBtn = new Button("Eliminar Seleccionado");

        personaFormButtons = new HBox(10, guardarPersonaBtn, limpiarFormPersonaBtn); // Inicialización única para el campo de clase

        listaPersonasView.setPrefHeight(200);
        listaPersonasView.setCellFactory(lv -> new ListCell<Persona>() {
            @Override protected void updateItem(Persona item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    // Acceder a los roles para mostrarlos
                    String rolesStr = item.getRoles().stream()
                                          .map(r -> r.getTipoRol().name())
                                          .collect(Collectors.joining(", "));
                    // Mostrar solo el nombre completo de la persona
                    setText(item.getNombreCompleto() + " - Roles: [" + (rolesStr.isEmpty() ? "Ninguno" : rolesStr) + "]");
                }
            }
        });

        listaPersonasView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                System.out.println("DEBUG: Persona seleccionada en ListView: " + newVal.getNombreCompleto() + " (DNI: " + newVal.getDni() + ")");
                Optional<Persona> freshPersonaOpt = personaService.buscarPersonaPorDni(newVal.getDni());
                if (freshPersonaOpt.isPresent()) {
                    personaSeleccionadaParaModificar = freshPersonaOpt.get();
                    System.out.println("DEBUG: Persona recargada: " + personaSeleccionadaParaModificar.getNombreCompleto());
                } else {
                    System.err.println("ERROR: No se pudo recargar la persona seleccionada del DNI: " + newVal.getDni());
                    personaSeleccionadaParaModificar = newVal; // Usar la instancia no gestionada como fallback
                }

                nombrePersonaField.setText(personaSeleccionadaParaModificar.getNombreCompleto());
                dniPersonaField.setText(personaSeleccionadaParaModificar.getDni());
                telefonoPersonaField.setText(personaSeleccionadaParaModificar.getTelefono());
                emailPersonaField.setText(personaSeleccionadaParaModificar.getEmail());
                guardarPersonaBtn.setText("Modificar Persona");
                dniPersonaField.setDisable(true); 
                
                rolesSeleccionadosListView.getItems().clear();
                if (personaSeleccionadaParaModificar.getRoles() != null) {
                    System.out.println("DEBUG: Roles de la persona seleccionada (" + personaSeleccionadaParaModificar.getRoles().size() + "):");
                    personaSeleccionadaParaModificar.getRoles().forEach(rol -> {
                        System.out.println("DEBUG: - Añadiendo rol a ListView: " + rol.getTipoRol().name());
                        rolesSeleccionadosListView.getItems().add(rol.getTipoRol());
                    });
                } else {
                    System.out.println("DEBUG: La colección de roles es nula para la persona seleccionada.");
                }
                tipoRolComboBox.getSelectionModel().clearSelection();
            } else { 
                System.out.println("DEBUG: Deseleccionando persona. Limpiando formulario.");
                limpiarFormularioPersona();
            }
        });

        guardarPersonaBtn.setOnAction(e -> {
            String nombre = nombrePersonaField.getText();
            String dni = dniPersonaField.getText();
            String telefono = telefonoPersonaField.getText();
            String email = emailPersonaField.getText();
            List<TipoRol> rolesSeleccionadosEnUI = new ArrayList<>(rolesSeleccionadosListView.getItems());

            if (nombre.isEmpty() || dni.isEmpty() || telefono.isEmpty() || email.isEmpty() || rolesSeleccionadosEnUI.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Todos los campos de persona son obligatorios y debe seleccionar al menos un rol.").showAndWait();
                return;
            }

            if (!email.contains("@")) {
                new Alert(Alert.AlertType.ERROR, "El correo electrónico debe contener un '@'.").showAndWait();
                return;
            }

            if (personaSeleccionadaParaModificar != null) {
                Optional<Persona> managedPersonaOpt = personaService.buscarPersonaPorDni(personaSeleccionadaParaModificar.getDni());
                if (managedPersonaOpt.isPresent()) {
                    Persona managedPersona = managedPersonaOpt.get();
                    managedPersona.setNombreCompleto(nombre);
                    managedPersona.setTelefono(telefono);
                    managedPersona.setEmail(email);

                    Set<TipoRol> desiredRolesFromUI = rolesSeleccionadosEnUI.stream().collect(Collectors.toSet());
                    
                    List<TipoRol> rolesToRemove = managedPersona.getRoles().stream()
                                                                .map(r -> r.getTipoRol())
                                                                .filter(rolType -> !desiredRolesFromUI.contains(rolType))
                                                                .collect(Collectors.toList());
                    rolesToRemove.forEach(managedPersona::removeRol);

                    desiredRolesFromUI.stream()
                                    .filter(rolType -> !managedPersona.hasRol(rolType))
                                    .forEach(managedPersona::addRol);

                    if(personaService.modificarPersona(managedPersona)) {
                        new Alert(Alert.AlertType.INFORMATION, "Persona modificada con éxito.").showAndWait();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Error al modificar persona. Verifique la consola.").showAndWait();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "No se pudo encontrar la persona para modificar en la base de datos.").showAndWait();
                }

            } else {
                Persona nuevaPersona = new Persona(dni, nombre, telefono, email); 
                rolesSeleccionadosEnUI.forEach(nuevaPersona::addRol);

                if (personaService.altaPersona(nuevaPersona)) {
                    new Alert(Alert.AlertType.INFORMATION, "Persona creada con éxito.").showAndWait(); 
                } else {
                    new Alert(Alert.AlertType.ERROR, "Error al dar de alta persona. DNI duplicado o error en DB.").showAndWait();
                }
            }
            actualizarTodasLasListas();
            limpiarFormularioPersona();
        });

        limpiarFormPersonaBtn.setOnAction(e -> {
            limpiarFormularioPersona();
        });

        eliminarPersonaBtn.setOnAction(e -> {
            Persona personaAEliminar = listaPersonasView.getSelectionModel().getSelectedItem();
            if (personaAEliminar != null) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de que desea eliminar a " + personaAEliminar.getNombreCompleto() + "?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    personaService.bajaPersona(personaAEliminar.getDni());
                    new Alert(Alert.AlertType.INFORMATION, "Persona eliminada con éxito.").showAndWait();
                    actualizarTodasLasListas();
                    limpiarFormularioPersona();
                }
            } else { new Alert(Alert.AlertType.WARNING, "Seleccione una persona de la lista para eliminar.").showAndWait(); }
        });

        personaFormGrid.add(lblTituloPersona, 0, 0, 2, 1);
        personaFormGrid.addRow(1, new Label("Seleccionar Rol:"), tipoRolComboBox);
        personaFormGrid.add(rolButtons, 0, 2, 2, 1);
        personaFormGrid.add(new Label("Roles Asignados:"), 0, 3);
        personaFormGrid.add(rolesSeleccionadosListView, 1, 3);
        personaFormGrid.addRow(4, new Label("Nombre Completo:"), nombrePersonaField);
        personaFormGrid.addRow(5, new Label("DNI:"), dniPersonaField);
        personaFormGrid.addRow(6, new Label("Teléfono:"), telefonoPersonaField);
        personaFormGrid.addRow(7, new Label("Email:"), emailPersonaField);
        personaFormGrid.add(personaFormButtons, 0, 8, 2, 1);

        personasContent.getChildren().addAll(personaFormGrid, new Separator(), new Label("Personas Registradas:"), listaPersonasView, eliminarPersonaBtn);
        personasTab.setContent(personasContent);

        // --- Pestaña: Inscribir Participantes ---
        Tab participantesTab = new Tab("Inscribir Participantes");
        VBox participantesContent = new VBox(15);
        participantesContent.setPadding(new Insets(20));

        // Inicialización de campos de inscripción como campos de clase
        lblTituloInscripcion = new Label("Registrar Inscripción a Evento");
        lblTituloInscripcion.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        eventoLabel = new Label("Seleccione el Evento:");
        eventoComboBoxInscripcion.setPromptText("Seleccionar Evento"); eventoComboBoxInscripcion.setMaxWidth(Double.MAX_VALUE);
        eventoComboBoxInscripcion.setCellFactory(lv -> new ListCell<Evento>() {
            @Override protected void updateItem(Evento item, boolean empty) { super.updateItem(item, empty); setText(empty ? "" : item != null ? item.getNombre() + " (" + item.getFechaInicio() + ")" : ""); }
        });
        eventoComboBoxInscripcion.setButtonCell(new ListCell<Evento>() {
            @Override protected void updateItem(Evento item, boolean empty) { super.updateItem(item, empty); setText(empty ? "Seleccionar Evento" : item != null ? item.getNombre() + " (" + item.getFechaInicio() + ")" : ""); }
        });


        participanteLabel = new Label("Seleccione el Participante:");
        participanteComboBoxInscripcion.setPromptText("Seleccionar Participante"); participanteComboBoxInscripcion.setMaxWidth(Double.MAX_VALUE);
        participanteComboBoxInscripcion.setCellFactory(lv -> new ListCell<Persona>() {
            @Override protected void updateItem(Persona item, boolean empty) { super.updateItem(item, empty); setText(empty ? "" : item != null ? item.getNombreCompleto() : ""); } // Mostrar solo el nombre completo
        });
        participanteComboBoxInscripcion.setButtonCell(new ListCell<Persona>() {
            @Override protected void updateItem(Persona item, boolean empty) { super.updateItem(item, empty); setText(empty ? "Seleccionar Participante" : item != null ? item.getNombreCompleto() : ""); } // Mostrar solo el nombre completo
        });


        inscribirBtn = new Button("Inscribir Participante"); inscribirBtn.setMaxWidth(Double.MAX_VALUE);

        inscripcionesEventoListView = new ListView<>(); inscripcionesEventoListView.setPrefHeight(150);


        inscripcionesEventoLabel = new Label("Participantes Inscritos:");

        eventoComboBoxInscripcion.valueProperty().addListener((obs, oldVal, newVal) -> {
            inscripcionesEventoListView.getItems().clear();
            if (newVal != null) {
                System.out.println("DEBUG: Evento seleccionado en ComboBox de Inscripción: " + newVal.getNombre() + " (ID: " + newVal.getId() + ")");
                Optional<Evento> freshEventoOpt = eventoService.buscarEvento(newVal.getId());
                if (freshEventoOpt.isPresent()) {
                    Evento freshEvento = freshEventoOpt.get();
                    System.out.println("DEBUG: Evento recargado desde DB para inscripción: " + freshEvento.getNombre() + " (ID: " + freshEvento.getId() + ")");
                    List<Persona> participantesInscritos = eventoService.getParticipantesPorEvento(freshEvento);
                    System.out.println("DEBUG: Número de participantes recuperados para ListView de inscripciones: " + participantesInscritos.size());
                    if (participantesInscritos != null && !participantesInscritos.isEmpty()) {
                        participantesInscritos.forEach(p -> inscripcionesEventoListView.getItems().add(p.getNombreCompleto() + " (DNI: " + p.getDni() + ")"));
                    } else { inscripcionesEventoListView.getItems().add("No hay participantes inscritos para este evento."); }
                } else {
                     inscripcionesEventoListView.getItems().add("Evento no encontrado o ya no existe.");
                     System.err.println("ERROR: No se pudo recargar el evento con ID " + newVal.getId() + " desde la DB para inscripción.");
                }
            } else {
                inscripcionesEventoListView.getItems().add("Seleccione un evento para ver sus participantes.");
            }
        });

        inscribirBtn.setOnAction(e -> {
            Evento eventoSeleccionado = eventoComboBoxInscripcion.getValue();
            Persona participanteSeleccionado = participanteComboBoxInscripcion.getValue();

            if (eventoSeleccionado == null || participanteSeleccionado == null) { new Alert(Alert.AlertType.ERROR, "Debe seleccionar un Evento y un Participante para inscribir.").showAndWait(); return; }

            if (eventoService.registrarParticipanteAEvento(eventoSeleccionado, participanteSeleccionado)) {
                new Alert(Alert.AlertType.INFORMATION, "Participante '" + participanteSeleccionado.getNombreCompleto() + "' inscrito con éxito en '" + eventoSeleccionado.getNombre() + "'.").showAndWait();
                final Evento eventoParaMantener = eventoSeleccionado;
                actualizarTodasLasListas();
                Platform.runLater(() -> {
                    eventoComboBoxInscripcion.getSelectionModel().select(
                        eventoComboBoxInscripcion.getItems().stream()
                            .filter(ev -> ev.getId().equals(eventoParaMantener.getId()))
                            .findFirst().orElse(null)
                    );
                });
            } else {
                new Alert(Alert.AlertType.WARNING, "No se pudo inscribir al participante. Verifique los requisitos del evento (estado, cupo, si ya está inscrito, o si la persona tiene el rol de PARTICIPANTE).").showAndWait();
            }
        });

        participantesContent.getChildren().addAll(lblTituloInscripcion, eventoLabel, eventoComboBoxInscripcion,
            participanteLabel, participanteComboBoxInscripcion, inscribirBtn, new Separator(),
            inscripcionesEventoLabel, inscripcionesEventoListView);
        participantesTab.setContent(participantesContent);


        // --- Pestaña: Listar Eventos ---
        Tab listarEventosTab = new Tab("Listar Eventos");
        VBox listarEventosContent = new VBox(15);
        listarEventosContent.setPadding(new Insets(20));

        Label lblTituloListarEventos = new Label("Listado de Eventos y Gestión de Estado"); 
        lblTituloListarEventos.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"); 

        Label filtroEstadoLabel = new Label("Filtrar por Estado:");
        filtroEstadoComboBox.getItems().addAll("Todos",
            EstadoEvento.PLANIFICACION.toString(),
            EstadoEvento.CONFIRMADO.toString(),
            EstadoEvento.EN_EJECUCION.toString(),
            EstadoEvento.FINALIZADO.toString()
        );
        filtroEstadoComboBox.setValue("Todos");
        
        Label cambiarEstadoLabel = new Label("Cambiar estado a:");
        ComboBox<EstadoEvento> nuevoEstadoComboBox = new ComboBox<>();
        nuevoEstadoComboBox.getItems().addAll(EstadoEvento.values());
        nuevoEstadoComboBox.setPromptText("Seleccionar Nuevo Estado");

        Button cambiarEstadoBtn = new Button("Cambiar Estado");
        cambiarEstadoBtn.disableProperty().bind(eventosListView.getSelectionModel().selectedItemProperty().isNull());


        HBox controlesFiltro = new HBox(10, filtroEstadoLabel, filtroEstadoComboBox);
        HBox controlesCambioEstado = new HBox(10, cambiarEstadoLabel, nuevoEstadoComboBox, cambiarEstadoBtn);
        controlesCambioEstado.setPadding(new Insets(10, 0, 0, 0));


        eventosListView.setPrefHeight(300);
        eventosListView.setCellFactory(lv -> new ListCell<Evento>() {
            @Override
            protected void updateItem(Evento item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    System.out.println("DEBUG UI: Intentando mostrar Evento: " + item.getNombre() + 
                                       " (ID: " + item.getId() + ", Estado: " + item.getEstado() + 
                                       ", Fecha: " + item.getFechaInicio() + ")");
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append(item.getNombre())
                      .append(" (Fecha: ").append(item.getFechaInicio())
                      .append(") - Estado: ").append(item.getEstado());

                    if (item instanceof Feria) {
                        sb.append(" [Tipo: Feria]");
                    } else if (item instanceof Concierto) {
                        sb.append(" [Tipo: Concierto]");
                    } else if (item instanceof Exposicion) {
                        sb.append(" [Tipo: Exposición]");
                    } else if (item instanceof Taller) {
                        sb.append(" [Tipo: Taller]");
                    } else if (item instanceof CicloDeCine) {
                        sb.append(" [Tipo: Ciclo de Cine]");
                    }

                    if (item instanceof CicloDeCine) {
                        CicloDeCine ciclo = (CicloDeCine) item;
                        if (ciclo.getPeliculasAProyectar() != null && !ciclo.getPeliculasAProyectar().isEmpty()) {
                            String peliculas = ciclo.getPeliculasAProyectar().stream()
                                .map(p -> p.getTitulo() + " (Orden: " + p.getOrdenProyeccion() + ")")
                                .collect(Collectors.joining(", "));
                            sb.append("\n  Películas: ").append(peliculas);
                        } else {
                            sb.append("\n  Películas: Ninguna");
                        }
                        sb.append("\n  Tiene Charlas Posteriores: ").append(ciclo.isTieneCharlasPosteriores());
                    } else if (item instanceof Concierto) {
                        Concierto concierto = (Concierto) item;
                        sb.append("\n  Tipo de Entrada: ").append(concierto.getTipoEntrada());
                        if (concierto.getArtistas() != null && !concierto.getArtistas().isEmpty()) {
                            String artistas = concierto.getArtistas().stream()
                                .map(Persona::getNombreCompleto)
                                .collect(Collectors.joining(", "));
                            sb.append("\n  Artistas: ").append(artistas);
                        } else {
                            sb.append("\n  Artistas: Ninguno");
                        }
                    } else if (item instanceof Exposicion) {
                        Exposicion expo = (Exposicion) item;
                        sb.append("\n  Tipo de Arte: ").append(expo.getTipoArte());
                        if (expo.getCurador() != null) {
                            sb.append("\n  Curador: ").append(expo.getCurador().getNombreCompleto());
                        }
                    } else if (item instanceof Feria) {
                        Feria feria = (Feria) item;
                        sb.append("\n  Cantidad de Stands: ").append(feria.getCantidadStands());
                        sb.append("\n  Tipo de Ubicación: ").append(feria.getTipoUbicacion());
                    } else if (item instanceof Taller) {
                        Taller taller = (Taller) item;
                        sb.append("\n  Modalidad: ").append(taller.getModalidad());
                        if (taller.getInstructor() != null) {
                            sb.append("\n  Instructor: ").append(taller.getInstructor().getNombreCompleto());
                        }
                    }
                    
                    if (item.getResponsables() != null && !item.getResponsables().isEmpty()) {
                        String responsablesStr = item.getResponsables().stream()
                            .map(Persona::getNombreCompleto)
                            .collect(Collectors.joining(", "));
                        sb.append("\n  Responsables: ").append(responsablesStr);
                    } else {
                        sb.append("\n  Responsables: Ninguno");
                    }

                    setText(sb.toString());
                }
            }
        });

        filtroEstadoComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            refrescarListaEventos(eventosListView, newVal);
        });

        cambiarEstadoBtn.setOnAction(e -> {
            Evento eventoSeleccionado = eventosListView.getSelectionModel().getSelectedItem();
            EstadoEvento nuevoEstado = nuevoEstadoComboBox.getValue();

            if (eventoSeleccionado == null) { new Alert(Alert.AlertType.WARNING, "Por favor, seleccione un evento de la lista.").showAndWait(); return; }
            if (nuevoEstado == null) { new Alert(Alert.AlertType.WARNING, "Por favor, seleccione un nuevo estado.").showAndWait(); return; }

            if (eventoService.cambiarEstadoEvento(eventoSeleccionado, nuevoEstado)) {
                new Alert(Alert.AlertType.INFORMATION, "Estado del evento '" + eventoSeleccionado.getNombre() + "' cambiado a " + nuevoEstado + " con éxito.").showAndWait();
                actualizarTodasLasListas();
            } else { new Alert(Alert.AlertType.ERROR, "No se pudo cambiar el estado del evento. Verifique la consola.").showAndWait(); }
        });


        listarEventosContent.getChildren().addAll(
            lblTituloListarEventos, 
            controlesFiltro,
            new Separator(),
            controlesCambioEstado,
            new Separator(),
            eventosListView,
            mensajeListaEventosLabel
        );

        listarEventosTab.setContent(listarEventosContent);


        tabPane.getTabs().addAll(eventosTab, personasTab, participantesTab, listarEventosTab);

        Scene scene = new Scene(tabPane, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

        actualizarTodasLasListas();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        JPAUtil.closeEntityManagerFactory();
        System.out.println("DEBUG: Aplicación detenida. EntityManagerFactorys cerrados.");
    }


    private void cargarDatosDePrueba() {
        System.out.println("DEBUG: Cargando datos de prueba...");
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            System.out.println("DEBUG: Phase 1 - Persisting all test Personas within shared transaction...");
            List<Persona> initialPersonsData = List.of(
                new Persona("12345678", "Juan Pérez", "1122334455", "juan.perez@municipio.com").addRol(TipoRol.ORGANIZADOR),
                new Persona("87654321", "Maria García", "1199887766", "maria.garcia@municipio.com").addRol(TipoRol.ORGANIZADOR),
                new Persona("22334455", "Los Rockeros", "1133445566", "rockeros@banda.com").addRol(TipoRol.ARTISTA),
                new Persona("44556677", "Ana Gómez", "1155667788", "ana.gomez@museo.com").addRol(TipoRol.CURADOR),
                new Persona("55667788", "Carlos Díaz", "1111223344", "carlos.diaz@educacion.com").addRol(TipoRol.INSTRUCTOR),
                new Persona("98765432", "Laura Fernández", "1144556677", "laura.f@email.com").addRol(TipoRol.PARTICIPANTE),
                new Persona("11223344", "Pedro Martínez", "1177889900", "pedro.m@email.com").addRol(TipoRol.PARTICIPANTE),
                new Persona("66778899", "Sofía Ruíz", "1100112233", "sofia.r@email.com").addRol(TipoRol.PARTICIPANTE),
                new Persona("55666777", "BTS", "1155443322", "bts@kpop.com").addRol(TipoRol.ARTISTA),
                new Persona("99887766", "Queen", "1122113344", "queen@banda.com").addRol(TipoRol.ARTISTA), // Nuevo artista
                new Persona("11335577", "The Beatles", "1199775533", "beatles@banda.com").addRol(TipoRol.ARTISTA) // Nuevo artista
            );

            for (Persona p : initialPersonsData) {
                Optional<Persona> existing = personaService.buscarPersonaPorDni(p.getDni(), em);
                if (existing.isEmpty()) {
                    System.out.println("DEBUG: Persisting new persona: " + p.getNombreCompleto() + " (DNI: " + p.getDni() + ") within shared EM.");
                    if (!personaService.altaPersona(p, em)) {
                        System.err.println("ERROR: Failed to persist persona: " + p.getNombreCompleto() + " (DNI: " + p.getDni() + ") within shared EM.");
                    }
                } else {
                    System.out.println("DEBUG: Persona already exists: " + p.getNombreCompleto() + " (DNI: " + p.getDni() + ") within shared EM. Merging to ensure it's managed.");
                    em.merge(p);
                }
            }
            em.flush(); 
            System.out.println("DEBUG: Phase 1 - All test Personas processed and flushed.");

            System.out.println("DEBUG: Phase 2 - Retrieving managed Persona instances from shared EM...");
            Persona managedOrg1 = personaService.buscarPersonaPorDni("12345678", em).orElse(null);
            Persona managedOrg2 = personaService.buscarPersonaPorDni("87654321", em).orElse(null);
            Persona managedArt1 = personaService.buscarPersonaPorDni("22334455", em).orElse(null); // Los Rockeros
            Persona managedCur1 = personaService.buscarPersonaPorDni("44556677", em).orElse(null);
            Persona managedInst1 = personaService.buscarPersonaPorDni("55667788", em).orElse(null);
            Persona managedPart1 = personaService.buscarPersonaPorDni("98765432", em).orElse(null);
            Persona managedPart2 = personaService.buscarPersonaPorDni("11223344", em).orElse(null);
            Persona managedPart3 = personaService.buscarPersonaPorDni("66778899", em).orElse(null);
            Persona managedArtBTS = personaService.buscarPersonaPorDni("55666777", em).orElse(null);
            Persona managedArtQueen = personaService.buscarPersonaPorDni("99887766", em).orElse(null); // Nuevo artista
            Persona managedArtBeatles = personaService.buscarPersonaPorDni("11335577", em).orElse(null); // Nuevo artista


            if (managedOrg1 == null || managedOrg2 == null || managedArt1 == null || managedCur1 == null || managedInst1 == null || managedPart1 == null || managedPart2 == null || managedPart3 == null || managedArtBTS == null || managedArtQueen == null || managedArtBeatles == null) {
                System.err.println("CRITICAL ERROR: One or more managed Persona instances are null after retrieval. Cannot proceed with event creation.");
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.ERROR, "Error crítico: No se pudieron cargar todas las personas de prueba. Verifique la consola para más detalles.").showAndWait();
                });
                tx.rollback();
                return; 
            }
            System.out.println("DEBUG: Phase 2 - All managed Persona instances retrieved successfully.");

            System.out.println("DEBUG: Phase 3 - Creating and persisting Events within shared transaction...");

            // --- Feria ---
            String nombreFeria = "Feria de Artesanías 2025";
            LocalDate fechaFeria = LocalDate.of(2025, 7, 20);
            System.out.println("DEBUG: Intentando crear Feria: " + nombreFeria + " en fecha " + fechaFeria);
            Optional<Evento> existingFeria = eventoService.buscarEvento(nombreFeria, fechaFeria, em);
            if (existingFeria.isEmpty()) {
                System.out.println("DEBUG: Feria '" + nombreFeria + "' NO encontrada, procediendo a crearla.");
                Feria feriaMunicipal = new Feria(nombreFeria, fechaFeria, 480, false, 0, 50, TipoUbicacionFeria.AIRE_LIBRE);
                feriaMunicipal.agregarResponsable(managedOrg1); 
                boolean added = eventoService.agregarEvento(feriaMunicipal, em);
                if (added) {
                    System.out.println("DEBUG: Feria '" + nombreFeria + "' agregada con éxito: " + added);
                } else {
                    System.err.println("ERROR: Fallo al agregar Feria: " + nombreFeria + ". Resultado: " + added);
                }
            } else {
                System.out.println("DEBUG: Feria '" + nombreFeria + "' YA EXISTE. No se creará de nuevo.");
            }

            // --- Concierto 1 (con múltiples artistas) ---
            String nombreConcierto1 = "Noche de Rock Local";
            LocalDate fechaConcierto1 = LocalDate.of(2025, 8, 15);
            System.out.println("DEBUG: Intentando crear Concierto: " + nombreConcierto1 + " en fecha " + fechaConcierto1);
            Optional<Evento> existingConcierto1 = eventoService.buscarEvento(nombreConcierto1, fechaConcierto1, em);
            if (existingConcierto1.isEmpty()) {
                System.out.println("DEBUG: Concierto '" + nombreConcierto1 + "' NO encontrado, procediendo a crearla.");
                Concierto conciertoRock = new Concierto(nombreConcierto1, fechaConcierto1, 180, true, 500, TipoEntradaConcierto.PAGA);
                conciertoRock.agregarResponsable(managedOrg2); 
                conciertoRock.agregarArtista(managedArt1); // Los Rockeros
                conciertoRock.agregarArtista(managedArtQueen); // Queen
                conciertoRock.agregarArtista(managedArtBeatles); // The Beatles

                boolean added = eventoService.agregarEvento(conciertoRock, em);
                if (added) {
                    System.out.println("DEBUG: Concierto '" + nombreConcierto1 + "' agregado con éxito: " + added);
                    eventoService.cambiarEstadoEvento(conciertoRock, EstadoEvento.CONFIRMADO, em);
                    boolean inscrito1 = eventoService.registrarParticipanteAEvento(conciertoRock, managedPart1, em);
                    if (inscrito1) {
                        System.out.println("DEBUG: Participante " + managedPart1.getNombreCompleto() + " inscrito en " + conciertoRock.getNombre() + ": " + inscrito1);
                    } else {
                        System.err.println("ERROR: No se pudo inscribir a " + managedPart1.getNombreCompleto() + " en " + conciertoRock.getNombre() + ": " + inscrito1);
                    }
                    boolean inscrito2 = eventoService.registrarParticipanteAEvento(conciertoRock, managedPart2, em);
                    if (inscrito2) {
                        System.out.println("DEBUG: Participante " + managedPart2.getNombreCompleto() + " inscrito en " + conciertoRock.getNombre() + ": " + inscrito2);
                    } else {
                        System.err.println("ERROR: No se pudo inscribir a " + managedPart2.getNombreCompleto() + " en " + conciertoRock.getNombre() + ": " + inscrito2);
                    }
                } else {
                    System.err.println("ERROR: Fallo al agregar Concierto: " + nombreConcierto1 + ". Resultado: " + added);
                }
            } else {
                System.out.println("DEBUG: Concierto '" + nombreConcierto1 + "' YA EXISTE. No se creará de nuevo.");
            }
            
            // --- Concierto 2 (K-Pop) ---
            String nombreConciertoBTS = "Concierto K-Pop Mania";
            LocalDate fechaConciertoBTS = LocalDate.of(2025, 7, 20);
            System.out.println("DEBUG: Intentando crear Concierto: " + nombreConciertoBTS + " en fecha " + fechaConciertoBTS);
            Optional<Evento> existingConciertoBTS = eventoService.buscarEvento(nombreConciertoBTS, fechaConciertoBTS, em);
            if (existingConciertoBTS.isEmpty()) {
                System.out.println("DEBUG: Concierto '" + nombreConciertoBTS + "' NO encontrado, procediendo a crearla.");
                Concierto conciertoKpop = new Concierto(nombreConciertoBTS, fechaConciertoBTS, 200, true, 800, TipoEntradaConcierto.PAGA);
                conciertoKpop.agregarResponsable(managedOrg1);
                conciertoKpop.agregarArtista(managedArtBTS);
                
                boolean added = eventoService.agregarEvento(conciertoKpop, em);
                if (added) {
                    System.out.println("DEBUG: Concierto '" + nombreConciertoBTS + "' agregado con éxito: " + added);
                    eventoService.cambiarEstadoEvento(conciertoKpop, EstadoEvento.CONFIRMADO, em);
                } else {
                    System.err.println("ERROR: Fallo al agregar Concierto K-Pop: " + nombreConciertoBTS + ". Resultado: " + added);
                }
            } else {
                System.out.println("DEBUG: Concierto '" + nombreConciertoBTS + "' YA EXISTE. No se creará de nuevo.");
            }


            // --- Exposición ---
            String nombreExposicion = "Miradas Urbanas";
            LocalDate fechaExposicion = LocalDate.of(2025, 9, 10);
            System.out.println("DEBUG: Intentando crear Exposición: " + nombreExposicion + " en fecha " + fechaExposicion);
            Optional<Evento> existingExposicion = eventoService.buscarEvento(nombreExposicion, fechaExposicion, em);
            if (existingExposicion.isEmpty()) {
                System.out.println("DEBUG: Exposición '" + nombreExposicion + "' NO encontrada, procediendo a crearla.");
                Exposicion expoFotografia = new Exposicion(nombreExposicion, fechaExposicion, 360, true, 100, "Fotografía", managedCur1); 
                expoFotografia.agregarResponsable(managedOrg1); 
                boolean added = eventoService.agregarEvento(expoFotografia, em);
                if (added) {
                    System.out.println("DEBUG: Exposición '" + nombreExposicion + "' agregada con éxito: " + added);
                    eventoService.cambiarEstadoEvento(expoFotografia, EstadoEvento.CONFIRMADO, em);
                    boolean inscrito = eventoService.registrarParticipanteAEvento(expoFotografia, managedPart1, em);
                    if (inscrito) {
                        System.out.println("DEBUG: Participante " + managedPart1.getNombreCompleto() + " inscrito en " + expoFotografia.getNombre() + ": " + inscrito);
                    } else {
                        System.err.println("ERROR: No se pudo inscribir a " + managedPart1.getNombreCompleto() + " en " + expoFotografia.getNombre() + ": " + inscrito);
                    }
                } else {
                    System.err.println("ERROR: Fallo al agregar Exposición: " + nombreExposicion + ". Resultado: " + added);
                }
            } else {
                System.out.println("DEBUG: Exposición '" + nombreExposicion + "' YA EXISTE. No se creará de nuevo.");
            }

            String nombreTaller = "Taller de Introducción a Java";
            LocalDate fechaTaller = LocalDate.of(2025, 10, 5);
            System.out.println("DEBUG: Intentando crear Taller: " + nombreTaller + " en fecha " + fechaTaller);
            Optional<Evento> existingTaller = eventoService.buscarEvento(nombreTaller, fechaTaller, em);
            if (existingTaller.isEmpty()) {
                System.out.println("DEBUG: Taller '" + nombreTaller + "' NO encontrado, procediendo a crearla.");
                Taller tallerProgramacion = new Taller(nombreTaller, fechaTaller, 240, true, 20, managedInst1, ModalidadTaller.VIRTUAL); 
                tallerProgramacion.agregarResponsable(managedOrg2); 
                boolean added = eventoService.agregarEvento(tallerProgramacion, em);
                if (added) {
                    System.out.println("DEBUG: Taller '" + nombreTaller + "' agregado con éxito: " + added);
                    eventoService.cambiarEstadoEvento(tallerProgramacion, EstadoEvento.CONFIRMADO, em);
                    boolean inscrito = eventoService.registrarParticipanteAEvento(tallerProgramacion, managedPart3, em);
                    if (inscrito) {
                        System.out.println("DEBUG: Participante " + managedPart3.getNombreCompleto() + " inscrito en " + tallerProgramacion.getNombre() + ": " + inscrito);
                    } else {
                        System.err.println("ERROR: No se pudo inscribir a " + managedPart3.getNombreCompleto() + " en " + tallerProgramacion.getNombre() + ": " + inscrito);
                    }
                } else {
                    System.err.println("ERROR: Fallo al agregar Taller: " + nombreTaller + ". Resultado: " + added);
                }
            } else {
                System.out.println("DEBUG: Taller '" + nombreTaller + "' YA EXISTE. No se creará de nuevo.");
            }

            // --- Ciclo de Cine (con múltiples películas) ---
            String nombreCicloCine = "Ciclo de Cine Clásico";
            LocalDate fechaCicloCine = LocalDate.of(2025, 11, 1);
            System.out.println("DEBUG: Intentando crear Ciclo de Cine: " + nombreCicloCine + " en fecha " + fechaCicloCine);
            Optional<Evento> existingCiclo = eventoService.buscarEvento(nombreCicloCine, fechaCicloCine, em);
            if (existingCiclo.isEmpty()) {
                System.out.println("DEBUG: Ciclo de Cine '" + nombreCicloCine + "' NO encontrado, procediendo a crearla.");
                CicloDeCine cicloClasicos = new CicloDeCine(nombreCicloCine, fechaCicloCine, 300, false, 0, true);
                cicloClasicos.agregarResponsable(managedOrg1); 
                cicloClasicos.agregarPelicula(new Pelicula("Casablanca", 1));
                cicloClasicos.agregarPelicula(new Pelicula("El Padrino", 2));
                cicloClasicos.agregarPelicula(new Pelicula("Lo que el viento se llevó", 3)); // Nueva película
                cicloClasicos.agregarPelicula(new Pelicula("Ciudadano Kane", 4)); // Nueva película
                boolean added = eventoService.agregarEvento(cicloClasicos, em);
                if (added) {
                    System.out.println("DEBUG: Ciclo de Cine '" + nombreCicloCine + "' agregado con éxito: " + added);
                    eventoService.cambiarEstadoEvento(cicloClasicos, EstadoEvento.CONFIRMADO, em);
                } else {
                    System.err.println("ERROR: Fallo al agregar Ciclo de Cine: " + nombreCicloCine + ". Resultado: " + added);
                }
            } else {
                System.out.println("DEBUG: Ciclo de Cine '" + nombreCicloCine + "' YA EXISTE. No se creará de nuevo.");
            }

            tx.commit();
            System.out.println("DEBUG: Datos de prueba cargados y transacción confirmada.");

        } catch (jakarta.persistence.PersistenceException pe) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.err.println("CRITICAL ERROR: Fallo en la persistencia de datos (JPA/Hibernate) durante cargarDatosDePrueba.");
            System.err.println("Mensaje: " + pe.getMessage());
            pe.printStackTrace();
            Platform.runLater(() -> {
                new Alert(Alert.AlertType.ERROR, "Error crítico de base de datos (JPA/Hibernate) al cargar datos iniciales.\n" +
                                                  "Por favor, verifique:\n" +
                                                  "1. Que el servidor PostgreSQL esté corriendo.\n" +
                                                  "2. Que la base de datos 'eventos_db' exista.\n" +
                                                  "3. Que las credenciales en persistence.xml sean correctas.\n" +
                                                  "4. Que las tablas de la base de datos estén creadas y coincidan con tus entidades.\n\n" +
                                                  "Detalles: " + pe.getMessage()).showAndWait();
            });
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.err.println("CRITICAL UNEXPECTED ERROR during data loading: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> {
                new Alert(Alert.AlertType.ERROR, "Error inesperado al cargar datos iniciales: " + e.getMessage() + "\n" +
                                                  "Por favor, revise la consola para más detalles.").showAndWait();
            });
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }


    /**
     * Carga las personas desde el PersonaService y las muestra en la ListView.
     * @param listView La ListView de Persona a actualizar.
     */
    private void cargarListaPersonas(ListView<Persona> listView) {
        listView.getItems().clear();
        List<Persona> personas = personaService.listarPersonas();
        System.out.println("DEBUG: cargarListaPersonas - Personas obtenidas del servicio: " + (personas != null ? personas.size() : "null"));
        if (personas != null && !personas.isEmpty()) {
            listView.getItems().addAll(personas);
            System.out.println("DEBUG: Cargadas " + personas.size() + " personas en la lista de la UI.");
            personas.forEach(p -> {
                String rolesStr = p.getRoles().stream().map(r -> r.getTipoRol().name()).collect(Collectors.joining(", "));
                System.out.println("DEBUG: Persona en lista UI (Debug): Nombre: " + p.getNombreCompleto() + ", DNI: " + p.getDni() + ", Roles: [" + (rolesStr.isEmpty() ? "Ninguno" : rolesStr) + "]");
            });
        } else {
            System.out.println("DEBUG: No hay personas registradas en el sistema para cargar en la lista de la UI.");
        }
    }

    /**
     * Carga las personas de un rol específico desde el PersonaService y las muestra en el ComboBox.
     * @param comboBox El ComboBox a poblar.
     * @param tipoRol El TipoRol por el cual filtrar.
     */
    private void cargarPersonasPorTipo(ComboBox<Persona> comboBox, TipoRol tipoRol) {
        comboBox.getItems().clear();
        List<Persona> personasFiltradas = personaService.getPersonasByRol(tipoRol);
        System.out.println("DEBUG: cargarPersonasPorTipo - Personas con rol " + tipoRol.name() + " obtenidas: " + (personasFiltradas != null ? personasFiltradas.size() : "null"));
        comboBox.getItems().addAll(personasFiltradas);
        System.out.println("DEBUG: Cargadas " + personasFiltradas.size() + " personas con rol " + tipoRol.name() + " en ComboBox."); 
    }

    /**
     * Carga los eventos y participantes en los ComboBox de la pestaña de inscripción.
     * @param eventoComboBox El ComboBox para eventos.
     * @param participanteComboBox El ComboBox para participantes.
     */
    private void cargarComboBoxInscripcion(ComboBox<Evento> eventoComboBox, ComboBox<Persona> participanteComboBox) {
        eventoComboBox.getItems().clear();
        participanteComboBox.getItems().clear();
        
        List<Evento> eventosDisponibles = eventoService.getTodosLosEventos();
        System.out.println("DEBUG: cargarComboBoxInscripcion - Eventos obtenidos del servicio: " + (eventosDisponibles != null ? eventosDisponibles.size() : "null"));
        for (Evento evento : eventosDisponibles) {
            if (evento.isRequiereInscripcion() && evento.getEstado() == EstadoEvento.CONFIRMADO) {
                eventoComboBox.getItems().add(evento);
            }
        }
        System.out.println("DEBUG: Cargados " + eventoComboBox.getItems().size() + " eventos disponibles para inscripción en ComboBox.");

        List<Persona> participantesDisponibles = personaService.getPersonasByRol(TipoRol.PARTICIPANTE);
        System.out.println("DEBUG: cargarComboBoxInscripcion - Participantes obtenidos del servicio: " + (participantesDisponibles != null ? participantesDisponibles.size() : "null"));
        participanteComboBox.getItems().addAll(participantesDisponibles);
        System.out.println("DEBUG: Cargados " + participanteComboBox.getItems().size() + " participantes disponibles para inscripción en ComboBox.");
    }

    /**
     * Refresca la lista de eventos en la pestaña "Listar Eventos" con un filtro de estado opcional.
     * @param listView La ListView de eventos a actualizar.
     * @param filtroEstado El estado por el cual filtrar (String, "Todos" para no filtrar).
     */
    private void refrescarListaEventos(ListView<Evento> listView, String filtroEstado) {
        listView.getItems().clear();
        mensajeListaEventosLabel.setText("");

        System.out.println("DEBUG: refrescarListaEventos - Obteniendo todos los eventos del servicio...");
        List<Evento> eventos = eventoService.getTodosLosEventos();

        System.out.println("DEBUG: refrescarListaEventos - Eventos traídos de la BD antes de filtrar: " + (eventos != null ? eventos.size() : 0));
        if (eventos != null && !eventos.isEmpty()) {
            List<Evento> eventosFiltrados = eventos.stream() 
                .filter(e -> "Todos".equals(filtroEstado) || e.getEstado().toString().equalsIgnoreCase(filtroEstado))
                .collect(Collectors.toList());

            if (!eventosFiltrados.isEmpty()) {
                eventosFiltrados.sort((e1, e2) -> e1.getFechaInicio().compareTo(e2.getFechaInicio()));
                listView.getItems().addAll(eventosFiltrados);
                System.out.println("DEBUG: refrescarListaEventos - Cargados " + eventosFiltrados.size() + " eventos filtrados en la ListView.");
            } else {
                System.out.println("DEBUG: refrescarListaEventos - No hay eventos que coincidan con el filtro '" + filtroEstado + "'.");
                mensajeListaEventosLabel.setText("No hay eventos disponibles con el filtro seleccionado.");
            }
        } else {
            System.out.println("DEBUG: refrescarListaEventos - No hay eventos registrados en el sistema.");
            mensajeListaEventosLabel.setText("No hay eventos registrados en el sistema.");
        }
    }

    /**
     * Método para actualizar todas las ListViews y ComboBoxes en la interfaz.
     * Esto asegura que los datos mostrados estén sincronizados después de cualquier operación CRUD.
     */
    public void actualizarTodasLasListas() {
        System.out.println("DEBUG: Llamando a actualizarTodasLasListas()...");
        cargarListaPersonas(listaPersonasView);
        cargarComboBoxInscripcion(eventoComboBoxInscripcion, participanteComboBoxInscripcion);
        refrescarListaEventos(eventosListView, filtroEstadoComboBox.getValue());

        // AÑADIDO: Recargar ComboBoxes de roles específicos para la creación de eventos
        cargarPersonasPorTipo(addArtistaConciertoComboBox, TipoRol.ARTISTA);
        cargarPersonasPorTipo(curadorExposicionComboBox, TipoRol.CURADOR);
        cargarPersonasPorTipo(instructorTallerComboBox, TipoRol.INSTRUCTOR);
        
        System.out.println("DEBUG: Finalizada actualización de todas las listas.");
    }

    private void clearPeliculaInputFields() {
        Platform.runLater(() -> {
            if (tituloPeliculaField != null) {
                tituloPeliculaField.clear();
            }
            if (ordenPeliculaField != null) {
                ordenPeliculaField.clear();
            }
        });
    }

    private void limpiarFormularioEvento() {
        nombreEventoField.clear();
        fechaInicioPicker.setValue(LocalDate.now());
        duracionMinutosField.clear();
        requiereInscripcionCheckBox.setSelected(false);
        cupoMaximoField.clear();
        tipoEventoComboBox.getSelectionModel().clearSelection();
        camposEspecificosPane.getChildren().clear();
        if (cantidadStandsField != null) cantidadStandsField.clear();
        if (tipoUbicacionFeriaComboBox != null) tipoUbicacionFeriaComboBox.getSelectionModel().clearSelection();
        
        // Limpiar campos de Concierto
        if (addArtistaConciertoComboBox != null) addArtistaConciertoComboBox.getSelectionModel().clearSelection();
        if (artistasSeleccionadosConciertoListView != null) artistasSeleccionadosConciertoListView.getItems().clear();
        if (artistasTemporalesConcierto != null) artistasTemporalesConcierto.clear();

        if (tipoEntradaConciertoComboBox != null) tipoEntradaConciertoComboBox.getSelectionModel().clearSelection();
        if (tipoArteField != null) tipoArteField.clear();
        if (curadorExposicionComboBox != null) curadorExposicionComboBox.getSelectionModel().clearSelection();
        if (instructorTallerComboBox != null) instructorTallerComboBox.getSelectionModel().clearSelection();
        if (modalidadTallerComboBox != null) modalidadTallerComboBox.getSelectionModel().clearSelection();
        clearPeliculaInputFields(); 
        if (peliculasTemporales != null) peliculasTemporales.clear();
        if (listaPeliculasCicloView != null) listaPeliculasCicloView.getItems().clear();
        if (listaPeliculasExistentesView != null) listaPeliculasExistentesView.getItems().clear();
        if (tieneCharlasCheckBox != null) tieneCharlasCheckBox.setSelected(false);
    }

    private void limpiarFormularioPersona() {
        tipoRolComboBox.getSelectionModel().clearSelection();
        rolesSeleccionadosListView.getItems().clear();
        nombrePersonaField.clear();
        dniPersonaField.clear();
        telefonoPersonaField.clear();
        emailPersonaField.clear();
        guardarPersonaBtn.setText("Guardar Persona");
        personaSeleccionadaParaModificar = null;
        listaPersonasView.getSelectionModel().clearSelection();
        dniPersonaField.setDisable(false);
    }
}
