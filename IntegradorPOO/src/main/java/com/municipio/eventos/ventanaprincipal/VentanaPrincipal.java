package com.municipio.eventos.ventanaprincipal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.municipio.eventos.models.Artista;
import com.municipio.eventos.models.CicloDeCine;
import com.municipio.eventos.models.Concierto;
import com.municipio.eventos.models.Curador;
import com.municipio.eventos.models.Exposicion;
import com.municipio.eventos.models.Feria;
import com.municipio.eventos.models.Instructor;
import com.municipio.eventos.models.Organizador;
import com.municipio.eventos.models.Participante;
import com.municipio.eventos.models.Pelicula;
import com.municipio.eventos.models.Persona;
import com.municipio.eventos.models.Taller;
import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.enums.EstadoEvento;
import com.municipio.eventos.models.enums.ModalidadTaller;
import com.municipio.eventos.models.enums.TipoEntradaConcierto;
import com.municipio.eventos.models.enums.TipoUbicacionFeria;
import com.municipio.eventos.services.EventoService;
import com.municipio.eventos.services.PersonaService;

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

public class VentanaPrincipal extends Application {

    private EventoService eventoService;
    private PersonaService personaService;

    // Variables de instancia para referenciar ListViews/ComboBoxes que necesitan refrescarse globalmente
    private ListView<Evento> eventosListView = new ListView<>();
    private ComboBox<Evento> eventoComboBoxInscripcion = new ComboBox<>();
    private ComboBox<Participante> participanteComboBoxInscripcion = new ComboBox<>();
    private ListView<Persona> listaPersonasView = new ListView<>();

    private Persona personaSeleccionadaParaModificar = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestión de Eventos del Municipio");

        eventoService = new EventoService();
        personaService = new PersonaService();
        
        cargarDatosDePrueba();

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // --- Pestaña: Crear Eventos ---
        Tab eventosTab = new Tab("Crear Eventos");
        VBox eventosContent = new VBox(15);
        eventosContent.setPadding(new Insets(20));

        Label tipoEventoLabel = new Label("Seleccione el Tipo de Evento:");
        ComboBox<String> tipoEventoComboBox = new ComboBox<>();
        tipoEventoComboBox.getItems().addAll("Feria", "Concierto", "Exposición", "Taller", "Ciclo de Cine");
        tipoEventoComboBox.setPromptText("Seleccionar Tipo");
        tipoEventoComboBox.setMaxWidth(Double.MAX_VALUE);

        TextField nombreEventoField = new TextField();
        nombreEventoField.setPromptText("Nombre del Evento");

        DatePicker fechaInicioPicker = new DatePicker(LocalDate.now());
        fechaInicioPicker.setPromptText("Fecha de Inicio");

        TextField duracionMinutosField = new TextField();
        duracionMinutosField.setPromptText("Duración estimada (horas)");
        duracionMinutosField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*([\\.]\\d*)?")) {
                duracionMinutosField.setText(oldValue);
            }
        });

        CheckBox requiereInscripcionCheckBox = new CheckBox("Requiere Inscripción Previa");

        TextField cupoMaximoField = new TextField();
        cupoMaximoField.setPromptText("Cupo Máximo (0 si ilimitado)");
        cupoMaximoField.disableProperty().bind(requiereInscripcionCheckBox.selectedProperty().not());
        cupoMaximoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                cupoMaximoField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        StackPane camposEspecificosPane = new StackPane();

        // --- Formularios específicos para cada tipo de evento ---
        GridPane feriaForm = new GridPane();
        feriaForm.setVgap(10); feriaForm.setHgap(10); feriaForm.setPadding(new Insets(10, 0, 0, 0));
        TextField cantidadStandsField = new TextField(); cantidadStandsField.setPromptText("Cantidad de Stands");
        cantidadStandsField.textProperty().addListener((observable, oldValue, newValue) -> { if (!newValue.matches("\\d*")) { cantidadStandsField.setText(newValue.replaceAll("[^\\d]", "")); } });
        ComboBox<TipoUbicacionFeria> tipoUbicacionFeriaComboBox = new ComboBox<>();
        tipoUbicacionFeriaComboBox.getItems().addAll(TipoUbicacionFeria.values()); tipoUbicacionFeriaComboBox.setPromptText("Tipo de Ubicación");
        feriaForm.addRow(0, new Label("Cantidad de Stands:"), cantidadStandsField);
        feriaForm.addRow(1, new Label("Tipo de Ubicación:"), tipoUbicacionFeriaComboBox);

        GridPane conciertoForm = new GridPane();
        conciertoForm.setVgap(10); conciertoForm.setHgap(10); conciertoForm.setPadding(new Insets(10, 0, 0, 0));
        ComboBox<Artista> artistasConciertoComboBox = new ComboBox<>();
        artistasConciertoComboBox.setCellFactory(lv -> new ListCell<Artista>() { @Override protected void updateItem(Artista item, boolean empty) { super.updateItem(item, empty); setText(empty ? "" : item.getNombreCompleto()); } });
        artistasConciertoComboBox.setButtonCell(new ListCell<Artista>() { @Override protected void updateItem(Artista item, boolean empty) { super.updateItem(item, empty); setText(empty ? "Seleccionar Artista" : item.getNombreCompleto()); } });
        ComboBox<TipoEntradaConcierto> tipoEntradaConciertoComboBox = new ComboBox<>();
        tipoEntradaConciertoComboBox.getItems().addAll(TipoEntradaConcierto.values()); tipoEntradaConciertoComboBox.setPromptText("Tipo de Entrada");
        conciertoForm.addRow(0, new Label("Artista Principal:"), artistasConciertoComboBox);
        conciertoForm.addRow(1, new Label("Tipo de Entrada:"), tipoEntradaConciertoComboBox);

        GridPane exposicionForm = new GridPane();
        exposicionForm.setVgap(10); exposicionForm.setHgap(10); exposicionForm.setPadding(new Insets(10, 0, 0, 0));
        TextField tipoArteField = new TextField(); tipoArteField.setPromptText("Tipo de Arte (Ej: Fotografía, Pintura)");
        ComboBox<Curador> curadorExposicionComboBox = new ComboBox<>();
        curadorExposicionComboBox.setCellFactory(lv -> new ListCell<Curador>() { @Override protected void updateItem(Curador item, boolean empty) { super.updateItem(item, empty); setText(empty ? "" : item.getNombreCompleto()); } });
        curadorExposicionComboBox.setButtonCell(new ListCell<Curador>() { @Override protected void updateItem(Curador item, boolean empty) { super.updateItem(item, empty); setText(empty ? "Seleccionar Curador" : item.getNombreCompleto()); } });
        exposicionForm.addRow(0, new Label("Tipo de Arte:"), tipoArteField);
        exposicionForm.addRow(1, new Label("Curador:"), curadorExposicionComboBox);

        GridPane tallerForm = new GridPane();
        tallerForm.setVgap(10); tallerForm.setHgap(10); tallerForm.setPadding(new Insets(10, 0, 0, 0));
        ComboBox<Instructor> instructorTallerComboBox = new ComboBox<>();
        instructorTallerComboBox.setCellFactory(lv -> new ListCell<Instructor>() { @Override protected void updateItem(Instructor item, boolean empty) { super.updateItem(item, empty); setText(empty ? "" : item.getNombreCompleto()); } });
        instructorTallerComboBox.setButtonCell(new ListCell<Instructor>() { @Override protected void updateItem(Instructor item, boolean empty) { super.updateItem(item, empty); setText(empty ? "Seleccionar Instructor" : item.getNombreCompleto()); } });
        ComboBox<ModalidadTaller> modalidadTallerComboBox = new ComboBox<>();
        modalidadTallerComboBox.getItems().addAll(ModalidadTaller.values()); modalidadTallerComboBox.setPromptText("Modalidad");
        tallerForm.addRow(0, new Label("Instructor:"), instructorTallerComboBox);
        tallerForm.addRow(1, new Label("Modalidad:"), modalidadTallerComboBox);

        GridPane cicloCineForm = new GridPane();
        cicloCineForm.setVgap(10); cicloCineForm.setHgap(10); cicloCineForm.setPadding(new Insets(10, 0, 0, 0));
        TextField tituloPeliculaField = new TextField(); tituloPeliculaField.setPromptText("Título de la Película");
        TextField ordenPeliculaField = new TextField(); ordenPeliculaField.setPromptText("Orden de Proyección");
        ordenPeliculaField.textProperty().addListener((observable, oldValue, newValue) -> { if (!newValue.matches("\\d*")) { ordenPeliculaField.setText(newValue.replaceAll("[^\\d]", "")); } });
        Button agregarPeliculaBtn = new Button("Agregar Película");
        ListView<Pelicula> listaPeliculasCicloView = new ListView<>(); listaPeliculasCicloView.setPrefHeight(100);
        listaPeliculasCicloView.setCellFactory(lv -> new ListCell<Pelicula>() { @Override protected void updateItem(Pelicula item, boolean empty) { super.updateItem(item, empty); setText(empty ? "" : item.getTitulo() + " (Orden: " + item.getOrdenProyeccion() + ")"); } });
        List<Pelicula> peliculasTemporales = new ArrayList<>();
        agregarPeliculaBtn.setOnAction(e -> {
            String titulo = tituloPeliculaField.getText();
            int orden;
            try { orden = Integer.parseInt(ordenPeliculaField.getText()); } catch (NumberFormatException ex) { new Alert(Alert.AlertType.ERROR, "El orden de la película debe ser un número.").showAndWait(); return; }
            if (!titulo.isEmpty()) {
                Pelicula nuevaPelicula = new Pelicula(titulo, orden);
                if (peliculasTemporales.stream().noneMatch(p -> p.getTitulo().equalsIgnoreCase(titulo))) {
                    peliculasTemporales.add(nuevaPelicula); listaPeliculasCicloView.getItems().add(nuevaPelicula);
                    tituloPeliculaField.clear(); ordenPeliculaField.clear();
                    listaPeliculasCicloView.getItems().sort((p1, p2) -> Integer.compare(p1.getOrdenProyeccion(), p2.getOrdenProyeccion()));
                } else { new Alert(Alert.AlertType.WARNING, "La película '" + titulo + "' ya ha sido añadida a este ciclo.").showAndWait(); }
            } else { new Alert(Alert.AlertType.WARNING, "El título de la película no puede estar vacío.").showAndWait(); }
        });
        CheckBox tieneCharlasCheckBox = new CheckBox("Tiene Charlas Posteriores");
        cicloCineForm.addRow(0, new Label("Título Película:"), tituloPeliculaField);
        cicloCineForm.addRow(1, new Label("Orden Proyección:"), ordenPeliculaField, agregarPeliculaBtn);
        cicloCineForm.add(listaPeliculasCicloView, 0, 2, 3, 1);
        cicloCineForm.addRow(3, new Label("Charlas Posteriores:"), tieneCharlasCheckBox);

        tipoEventoComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            camposEspecificosPane.getChildren().clear();
            if (newVal != null) {
                switch (newVal) {
                    case "Feria": camposEspecificosPane.getChildren().add(feriaForm); break;
                    case "Concierto": camposEspecificosPane.getChildren().add(conciertoForm); cargarPersonasPorTipo(artistasConciertoComboBox, Artista.class); break;
                    case "Exposición": camposEspecificosPane.getChildren().add(exposicionForm); cargarPersonasPorTipo(curadorExposicionComboBox, Curador.class); break;
                    case "Taller": camposEspecificosPane.getChildren().add(tallerForm); cargarPersonasPorTipo(instructorTallerComboBox, Instructor.class); break;
                    case "Ciclo de Cine": camposEspecificosPane.getChildren().add(cicloCineForm); peliculasTemporales.clear(); listaPeliculasCicloView.getItems().clear(); break;
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
            int duracionEstimada = (int) Math.round(duracionHoras * 60); // Convertir horas a minutos para los modelos existentes

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

            Evento nuevoEvento = null;
            switch (tipoSeleccionado) {
                case "Feria":
                    if (cantidadStandsField.getText().isEmpty() || tipoUbicacionFeriaComboBox.getValue() == null) { new Alert(Alert.AlertType.ERROR, "Complete los campos específicos de Feria.").showAndWait(); return; }
                    int cantidadStands = Integer.parseInt(cantidadStandsField.getText()); TipoUbicacionFeria tipoUbicacion = tipoUbicacionFeriaComboBox.getValue();
                    nuevoEvento = new Feria(nombre, fechaInicio, duracionEstimada, requiereInscripcion, cupoMaximo, cantidadStands, tipoUbicacion);
                    break;
                case "Concierto":
                    if (artistasConciertoComboBox.getValue() == null || tipoEntradaConciertoComboBox.getValue() == null) { new Alert(Alert.AlertType.ERROR, "Complete los campos específicos de Concierto.").showAndWait(); return; }
                    Artista artistaSeleccionado = artistasConciertoComboBox.getValue(); TipoEntradaConcierto tipoEntrada = tipoEntradaConciertoComboBox.getValue();
                    Concierto nuevoConcierto = new Concierto(nombre, fechaInicio, duracionEstimada, requiereInscripcion, cupoMaximo, tipoEntrada); nuevoConcierto.agregarArtista(artistaSeleccionado);
                    nuevoEvento = nuevoConcierto;
                    break;
                case "Exposición":
                    if (tipoArteField.getText().isEmpty() || curadorExposicionComboBox.getValue() == null) { new Alert(Alert.AlertType.ERROR, "Complete los campos específicos de Exposición.").showAndWait(); return; }
                    String tipoArte = tipoArteField.getText(); Curador curadorSeleccionado = curadorExposicionComboBox.getValue();
                    nuevoEvento = new Exposicion(nombre, fechaInicio, duracionEstimada, requiereInscripcion, cupoMaximo, tipoArte, curadorSeleccionado);
                    break;
                case "Taller":
                    if (instructorTallerComboBox.getValue() == null || modalidadTallerComboBox.getValue() == null) { new Alert(Alert.AlertType.ERROR, "Complete los campos específicos de Taller.").showAndWait(); return; }
                    Instructor instructorSeleccionado = instructorTallerComboBox.getValue(); ModalidadTaller modalidad = modalidadTallerComboBox.getValue();
                    nuevoEvento = new Taller(nombre, fechaInicio, duracionEstimada, requiereInscripcion, cupoMaximo, instructorSeleccionado, modalidad);
                    break;
                case "Ciclo de Cine":
                    if (peliculasTemporales.isEmpty()) { 
                         new Alert(Alert.AlertType.ERROR, "Debe agregar al menos una película para el Ciclo de Cine.").showAndWait();
                         return;
                    }
                    boolean tieneCharlas = tieneCharlasCheckBox.isSelected();
                    CicloDeCine nuevoCiclo = new CicloDeCine(nombre, fechaInicio, duracionEstimada, requiereInscripcion, cupoMaximo, tieneCharlas); peliculasTemporales.forEach(nuevoCiclo::agregarPelicula);
                    nuevoEvento = nuevoCiclo;
                    break;
            }

            if (nuevoEvento != null) {
                if (eventoService.agregarEvento(nuevoEvento)) {
                    new Alert(Alert.AlertType.INFORMATION, "Evento '" + nuevoEvento.getNombre() + "' creado con éxito.").showAndWait();
                    nombreEventoField.clear(); fechaInicioPicker.setValue(LocalDate.now()); duracionMinutosField.clear();
                    requiereInscripcionCheckBox.setSelected(false); cupoMaximoField.clear(); tipoEventoComboBox.getSelectionModel().clearSelection(); camposEspecificosPane.getChildren().clear();
                    actualizarTodasLasListas();
                } else { new Alert(Alert.AlertType.ERROR, "No se pudo crear el evento. Verifique la consola para más detalles.").showAndWait(); }
            }
        });

        eventosContent.getChildren().addAll(new Label("Datos Comunes del Evento:"), tipoEventoLabel, tipoEventoComboBox,
            new Label("Nombre:"), nombreEventoField, new Label("Fecha de Inicio:"), fechaInicioPicker,
            new Label("Duración (horas):"), duracionMinutosField, requiereInscripcionCheckBox,
            new Label("Cupo Máximo:"), cupoMaximoField, new Separator(), new Label("Datos Específicos del Evento:"),
            camposEspecificosPane, crearEventoBtn);
        eventosTab.setContent(eventosContent);


        // --- Pestaña: Gestionar Personas ---
        Tab personasTab = new Tab("Gestionar Personas");
        VBox personasContent = new VBox(15);
        personasContent.setPadding(new Insets(20));

        GridPane personaFormGrid = new GridPane();
        personaFormGrid.setVgap(10); personaFormGrid.setHgap(10); personaFormGrid.setPadding(new Insets(10));
        Label lblTituloPersona = new Label("Alta/Modificación de Persona");
        lblTituloPersona.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ComboBox<String> tipoPersonaComboBox = new ComboBox<>();
        tipoPersonaComboBox.getItems().addAll("Organizador", "Artista", "Curador", "Instructor", "Participante");
        tipoPersonaComboBox.setPromptText("Seleccione Tipo de Persona"); tipoPersonaComboBox.setMaxWidth(Double.MAX_VALUE);
        TextField nombrePersonaField = new TextField(); nombrePersonaField.setPromptText("Nombre Completo");
        TextField dniPersonaField = new TextField(); dniPersonaField.setPromptText("DNI");
        dniPersonaField.textProperty().addListener((observable, oldValue, newValue) -> { if (!newValue.matches("\\d*")) { dniPersonaField.setText(newValue.replaceAll("[^\\d]", "")); } });
        TextField telefonoPersonaField = new TextField(); telefonoPersonaField.setPromptText("Teléfono");
        TextField emailPersonaField = new TextField(); emailPersonaField.setPromptText("Correo Electrónico");

        Button guardarPersonaBtn = new Button("Guardar Persona");
        Button limpiarFormPersonaBtn = new Button("Limpiar Formulario");
        Button eliminarPersonaBtn = new Button("Eliminar Seleccionado");
        HBox personaFormButtons = new HBox(10, guardarPersonaBtn, limpiarFormPersonaBtn);

        listaPersonasView.setPrefHeight(200);
        listaPersonasView.setCellFactory(lv -> new ListCell<Persona>() {
            @Override protected void updateItem(Persona item, boolean empty) { super.updateItem(item, empty); setText(empty ? "" : item.getNombreCompleto() + " (" + item.getClass().getSimpleName() + ")"); }
        });

        listaPersonasView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                personaSeleccionadaParaModificar = newVal;
                tipoPersonaComboBox.setValue(newVal.getClass().getSimpleName());
                nombrePersonaField.setText(newVal.getNombreCompleto());
                dniPersonaField.setText(newVal.getDni());
                telefonoPersonaField.setText(newVal.getTelefono());
                emailPersonaField.setText(newVal.getEmail());
                guardarPersonaBtn.setText("Modificar Persona");
                dniPersonaField.setDisable(true); 
            } else { 
                limpiarFormPersonaBtn.fire(); 
                dniPersonaField.setDisable(false);
            }
        });

        guardarPersonaBtn.setOnAction(e -> {
            String tipo = tipoPersonaComboBox.getValue();
            String nombre = nombrePersonaField.getText();
            String dni = dniPersonaField.getText();
            String telefono = telefonoPersonaField.getText();
            String email = emailPersonaField.getText();

            if (tipo == null || nombre.isEmpty() || dni.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Todos los campos de persona son obligatorios.").showAndWait();
                return;
            }

            // Validación de email con arroba
            if (!email.contains("@")) {
                new Alert(Alert.AlertType.ERROR, "El correo electrónico debe contener un '@'.").showAndWait();
                return;
            }

            Persona personaAGuardar = null;
            if (personaSeleccionadaParaModificar != null) {
                personaAGuardar = personaSeleccionadaParaModificar;
                personaAGuardar.setNombreCompleto(nombre);
                if (!personaAGuardar.getDni().equals(dni)) {
                    new Alert(Alert.AlertType.ERROR, "No se puede cambiar el DNI de una persona existente. Cree una nueva si necesita cambiar el DNI.").showAndWait();
                    return;
                }
                personaAGuardar.setTelefono(telefono);
                personaAGuardar.setEmail(email);
                if (!personaAGuardar.getClass().getSimpleName().equals(tipo)) {
                    new Alert(Alert.AlertType.WARNING, "Para cambiar el tipo de persona, elimine la persona actual y cree una nueva con el tipo deseado.").showAndWait();
                    return;
                }

                if(personaService.modificarPersona(personaAGuardar)) {
                    new Alert(Alert.AlertType.INFORMATION, "Persona modificada con éxito.").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Error al modificar persona. Verifique la consola.").showAndWait();
                }

            } else {
                switch (tipo) {
                    case "Organizador": personaAGuardar = new Organizador(nombre, dni, telefono, email); break;
                    case "Artista": personaAGuardar = new Artista(nombre, dni, telefono, email); break;
                    case "Curador": personaAGuardar = new Curador(nombre, dni, telefono, email); break;
                    case "Instructor": personaAGuardar = new Instructor(nombre, dni, telefono, email); break;
                    case "Participante": personaAGuardar = new Participante(nombre, dni, telefono, email); break;
                }
                if (personaAGuardar != null) {
                    if(personaService.altaPersona(personaAGuardar)) {
                        new Alert(Alert.AlertType.INFORMATION, "Persona creada con éxito.").showAndWait();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Error al dar de alta persona. DNI duplicado o error en DB.").showAndWait();
                    }
                }
            }
            actualizarTodasLasListas();
            limpiarFormPersonaBtn.fire();
        });

        limpiarFormPersonaBtn.setOnAction(e -> {
            tipoPersonaComboBox.getSelectionModel().clearSelection(); nombrePersonaField.clear(); dniPersonaField.clear();
            telefonoPersonaField.clear(); emailPersonaField.clear(); guardarPersonaBtn.setText("Guardar Persona");
            personaSeleccionadaParaModificar = null; listaPersonasView.getSelectionModel().clearSelection();
            dniPersonaField.setDisable(false);
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
                    limpiarFormPersonaBtn.fire();
                }
            } else { new Alert(Alert.AlertType.WARNING, "Seleccione una persona de la lista para eliminar.").showAndWait(); }
        });

        personaFormGrid.add(lblTituloPersona, 0, 0, 2, 1);
        personaFormGrid.addRow(1, new Label("Tipo:"), tipoPersonaComboBox);
        personaFormGrid.addRow(2, new Label("Nombre Completo:"), nombrePersonaField);
        personaFormGrid.addRow(3, new Label("DNI:"), dniPersonaField);
        personaFormGrid.addRow(4, new Label("Teléfono:"), telefonoPersonaField);
        personaFormGrid.addRow(5, new Label("Email:"), emailPersonaField);
        personaFormGrid.add(personaFormButtons, 0, 6, 2, 1);

        personasContent.getChildren().addAll(personaFormGrid, new Separator(), new Label("Personas Registradas:"), listaPersonasView, eliminarPersonaBtn);
        personasTab.setContent(personasContent);

        // --- Pestaña: Inscribir Participantes ---
        Tab participantesTab = new Tab("Inscribir Participantes");
        VBox participantesContent = new VBox(15);
        participantesContent.setPadding(new Insets(20));

        Label lblTituloInscripcion = new Label("Registrar Inscripción a Evento");
        lblTituloInscripcion.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label eventoLabel = new Label("Seleccione el Evento:");
        eventoComboBoxInscripcion.setPromptText("Seleccionar Evento"); eventoComboBoxInscripcion.setMaxWidth(Double.MAX_VALUE);
        eventoComboBoxInscripcion.setCellFactory(lv -> new ListCell<Evento>() {
            @Override protected void updateItem(Evento item, boolean empty) { super.updateItem(item, empty); setText(empty ? "" : item.getNombre() + " (" + item.getFechaInicio() + ")"); }
        });
        eventoComboBoxInscripcion.setButtonCell(new ListCell<Evento>() {
            // ERROR SOLUCIONADO AQUÍ: SE REMOVIÓ EL SEGUNDO "void" INNECESARIO
            @Override protected void updateItem(Evento item, boolean empty) { super.updateItem(item, empty); setText(empty ? "Seleccionar Evento" : item.getNombre() + " (" + item.getFechaInicio() + ")"); }
        });


        Label participanteLabel = new Label("Seleccione el Participante:");
        participanteComboBoxInscripcion.setPromptText("Seleccionar Participante"); participanteComboBoxInscripcion.setMaxWidth(Double.MAX_VALUE);
        participanteComboBoxInscripcion.setCellFactory(lv -> new ListCell<Participante>() {
            @Override protected void updateItem(Participante item, boolean empty) { super.updateItem(item, empty); setText(empty ? "" : item.getNombreCompleto() + " (DNI: " + item.getDni() + ")"); }
        });
        participanteComboBoxInscripcion.setButtonCell(new ListCell<Participante>() {
            @Override protected void updateItem(Participante item, boolean empty) { super.updateItem(item, empty); setText(empty ? "Seleccionar Participante" : item.getNombreCompleto() + " (DNI: " + item.getDni() + ")"); }
        });


        Button inscribirBtn = new Button("Inscribir Participante"); inscribirBtn.setMaxWidth(Double.MAX_VALUE);

        Label inscripcionesEventoLabel = new Label("Participantes Inscritos en el Evento Seleccionado:");
        ListView<String> inscripcionesEventoListView = new ListView<>(); inscripcionesEventoListView.setPrefHeight(150);


        // MODIFICACIÓN CLAVE AQUÍ: Refrescar la lista de inscripciones cuando se selecciona un evento
        eventoComboBoxInscripcion.valueProperty().addListener((obs, oldVal, newVal) -> {
            inscripcionesEventoListView.getItems().clear();
            if (newVal != null) {
                System.out.println("Evento seleccionado: " + newVal.getNombre() + " (ID: " + newVal.getId() + ")");
                Optional<Evento> freshEventoOpt = eventoService.buscarEvento(newVal.getId());
                if (freshEventoOpt.isPresent()) {
                    Evento freshEvento = freshEventoOpt.get();
                    List<Participante> participantesInscritos = eventoService.getParticipantesPorEvento(freshEvento);
                    System.out.println("Participantes encontrados: " + participantesInscritos.size());
                    if (participantesInscritos != null && !participantesInscritos.isEmpty()) {
                        participantesInscritos.forEach(p -> inscripcionesEventoListView.getItems().add(p.getNombreCompleto() + " (DNI: " + p.getDni() + ")"));
                    } else {
                        inscripcionesEventoListView.getItems().add("No hay participantes inscritos para este evento.");
                    }
                } else {
                    inscripcionesEventoListView.getItems().add("Evento no encontrado o ya no existe.");
                }
            }
        });

        inscribirBtn.setOnAction(e -> {
            Evento eventoSeleccionado = eventoComboBoxInscripcion.getValue();
            Participante participanteSeleccionado = participanteComboBoxInscripcion.getValue();

            if (eventoSeleccionado == null || participanteSeleccionado == null) { new Alert(Alert.AlertType.ERROR, "Debe seleccionar un Evento y un Participante para inscribir.").showAndWait(); return; }

            // Volver a obtener las instancias gestionadas del evento y participante
            // para asegurar que están en el contexto de persistencia antes de la operación de inscripción.
            Optional<Evento> managedEventoOpt = eventoService.buscarEvento(eventoSeleccionado.getId());
            Optional<Persona> managedParticipanteOpt = personaService.buscarPersonaPorDni(participanteSeleccionado.getDni());

            if (managedEventoOpt.isPresent() && managedParticipanteOpt.isPresent() && managedParticipanteOpt.get() instanceof Participante) {
                Evento managedEvento = managedEventoOpt.get();
                Participante managedParticipante = (Participante) managedParticipanteOpt.get();

                if (eventoService.registrarParticipanteAEvento(managedEvento, managedParticipante)) {
                    new Alert(Alert.AlertType.INFORMATION, "Participante '" + managedParticipante.getNombreCompleto() + "' inscrito con éxito en '" + managedEvento.getNombre() + "'.").showAndWait();
                    // Después de una inscripción exitosa, forzar la actualización del ListView de inscripciones
                    // seleccionando el mismo evento nuevamente en el ComboBox.
                    eventoComboBoxInscripcion.getSelectionModel().select(managedEvento);
                    // Actualizar otras listas si es necesario (e.g., lista de eventos si cambia el estado)
                    actualizarTodasLasListas(); 
                } else { new Alert(Alert.AlertType.WARNING, "No se pudo inscribir al participante. Verifique los requisitos del evento (estado, cupo, si ya está inscrito).").showAndWait(); }
            } else {
                new Alert(Alert.AlertType.ERROR, "Error: El evento o el participante seleccionado no pudieron ser recuperados de la base de datos.").showAndWait();
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
        ComboBox<String> filtroEstadoComboBox = new ComboBox<>();
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
                setText(empty ? "" : item.getNombre() + " (Fecha: " + item.getFechaInicio() + ") - Estado: " + item.getEstado());
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
            eventosListView
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
        if (eventoService != null) {
            EventoService.closeEntityManagerFactory();
        }
        if (personaService != null) {
            PersonaService.closeEntityManagerFactory();
        }
        System.out.println("Aplicación detenida. EntityManagerFactorys cerrados.");
    }


    private void cargarDatosDePrueba() {
        System.out.println("Cargando datos de prueba...");

        // Instancias temporales para la carga de datos
        Organizador org1Temp = new Organizador("Juan Pérez", "12345678", "1122334455", "juan.perez@municipio.com");
        Organizador org2Temp = new Organizador("Maria García", "87654321", "1199887766", "maria.garcia@municipio.com");
        Artista art1Temp = new Artista("Los Rockeros", "22334455", "1133445566", "rockeros@banda.com");
        Curador cur1Temp = new Curador("Ana Gómez", "44556677", "1155667788", "ana.gomez@museo.com");
        Instructor inst1Temp = new Instructor("Carlos Díaz", "55667788", "1111223344", "carlos.diaz@educacion.com");
        Participante part1Temp = new Participante("Laura Fernández", "98765432", "1144556677", "laura.f@email.com");
        Participante part2Temp = new Participante("Pedro Martínez", "11223344", "1177889900", "pedro.m@email.com");
        Participante part3Temp = new Participante("Sofía Ruíz", "66778899", "1100112233", "sofia.r@email.com");

        // Guardar personas solo si no existen y obtener las instancias gestionadas
        // IMPORTANTE: El cast se realiza de forma segura con el tipo esperado en el .orElseGet
        Organizador managedOrg1 = personaService.buscarPersonaPorDni(org1Temp.getDni())
                                    .map(Organizador.class::cast) // Convertir Optional<Persona> a Optional<Organizador> si es posible
                                    .orElseGet(() -> { personaService.altaPersona(org1Temp); return org1Temp; });
        Organizador managedOrg2 = personaService.buscarPersonaPorDni(org2Temp.getDni())
                                    .map(Organizador.class::cast)
                                    .orElseGet(() -> { personaService.altaPersona(org2Temp); return org2Temp; });
        Artista managedArt1 = personaService.buscarPersonaPorDni(art1Temp.getDni())
                                    .map(Artista.class::cast)
                                    .orElseGet(() -> { personaService.altaPersona(art1Temp); return art1Temp; });
        Curador managedCur1 = personaService.buscarPersonaPorDni(cur1Temp.getDni())
                                    .map(Curador.class::cast)
                                    .orElseGet(() -> { personaService.altaPersona(cur1Temp); return cur1Temp; });
        Instructor managedInst1 = personaService.buscarPersonaPorDni(inst1Temp.getDni())
                                    .map(Instructor.class::cast)
                                    .orElseGet(() -> { personaService.altaPersona(inst1Temp); return inst1Temp; });
        Participante managedPart1 = personaService.buscarPersonaPorDni(part1Temp.getDni())
                                    .map(Participante.class::cast)
                                    .orElseGet(() -> { personaService.altaPersona(part1Temp); return part1Temp; });
        Participante managedPart2 = personaService.buscarPersonaPorDni(part2Temp.getDni())
                                    .map(Participante.class::cast)
                                    .orElseGet(() -> { personaService.altaPersona(part2Temp); return part2Temp; });
        Participante managedPart3 = personaService.buscarPersonaPorDni(part3Temp.getDni())
                                    .map(Participante.class::cast)
                                    .orElseGet(() -> { personaService.altaPersona(part3Temp); return part3Temp; });


        // --- Feria ---
        String nombreFeria = "Feria de Artesanías 2025";
        LocalDate fechaFeria = LocalDate.of(2025, 7, 20);
        if (eventoService.buscarEvento(nombreFeria, fechaFeria).isEmpty()) {
            Feria feriaMunicipal = new Feria(nombreFeria, fechaFeria, 480, false, 0, 50, TipoUbicacionFeria.AIRE_LIBRE);
            feriaMunicipal.agregarResponsable(managedOrg1); 
            eventoService.agregarEvento(feriaMunicipal);
        }

        // --- Concierto ---
        String nombreConcierto = "Noche de Rock Local";
        LocalDate fechaConcierto = LocalDate.of(2025, 8, 15);
        if (eventoService.buscarEvento(nombreConcierto, fechaConcierto).isEmpty()) {
            Concierto conciertoRock = new Concierto(nombreConcierto, fechaConcierto, 180, true, 500, TipoEntradaConcierto.PAGA);
            conciertoRock.agregarResponsable(managedOrg2); 
            conciertoRock.agregarArtista(managedArt1);    
            eventoService.agregarEvento(conciertoRock);
            
            // Recargar el concierto para cambiar su estado y registrar participantes
            eventoService.buscarEvento(conciertoRock.getNombre(), conciertoRock.getFechaInicio()).ifPresent(managedConcierto -> {
                eventoService.cambiarEstadoEvento(managedConcierto, EstadoEvento.CONFIRMADO);
                eventoService.registrarParticipanteAEvento(managedConcierto, managedPart1);
                eventoService.registrarParticipanteAEvento(managedConcierto, managedPart2);
            });
        }

        // --- Exposición ---
        String nombreExposicion = "Miradas Urbanas";
        LocalDate fechaExposicion = LocalDate.of(2025, 9, 10);
        if (eventoService.buscarEvento(nombreExposicion, fechaExposicion).isEmpty()) {
            Exposicion expoFotografia = new Exposicion(nombreExposicion, fechaExposicion, 360, true, 100, "Fotografía", managedCur1); 
            expoFotografia.agregarResponsable(managedOrg1); 
            eventoService.agregarEvento(expoFotografia);

            eventoService.buscarEvento(expoFotografia.getNombre(), expoFotografia.getFechaInicio()).ifPresent(managedExpo -> {
                eventoService.cambiarEstadoEvento(managedExpo, EstadoEvento.CONFIRMADO);
                eventoService.registrarParticipanteAEvento(managedExpo, managedPart1);
            });
        }

        // --- Taller ---
        String nombreTaller = "Taller de Introducción a Java";
        LocalDate fechaTaller = LocalDate.of(2025, 10, 5);
        if (eventoService.buscarEvento(nombreTaller, fechaTaller).isEmpty()) {
            Taller tallerProgramacion = new Taller(nombreTaller, fechaTaller, 240, true, 20, managedInst1, ModalidadTaller.VIRTUAL); 
            tallerProgramacion.agregarResponsable(managedOrg2); 
            eventoService.agregarEvento(tallerProgramacion);

            eventoService.buscarEvento(tallerProgramacion.getNombre(), tallerProgramacion.getFechaInicio()).ifPresent(managedTaller -> {
                eventoService.cambiarEstadoEvento(managedTaller, EstadoEvento.CONFIRMADO);
                eventoService.registrarParticipanteAEvento(managedTaller, managedPart3);
            });
        }

        // --- Ciclo de Cine ---
        String nombreCicloCine = "Ciclo de Cine Clásico";
        LocalDate fechaCicloCine = LocalDate.of(2025, 11, 1);
        if (eventoService.buscarEvento(nombreCicloCine, fechaCicloCine).isEmpty()) {
            CicloDeCine cicloClasicos = new CicloDeCine(nombreCicloCine, fechaCicloCine, 300, false, 0, true);
            cicloClasicos.agregarResponsable(managedOrg1); 
            cicloClasicos.agregarPelicula(new Pelicula("Casablanca", 1));
            cicloClasicos.agregarPelicula(new Pelicula("El Padrino", 2));
            eventoService.agregarEvento(cicloClasicos);
            
            eventoService.buscarEvento(cicloClasicos.getNombre(), cicloClasicos.getFechaInicio()).ifPresent(managedCiclo -> {
                 eventoService.cambiarEstadoEvento(managedCiclo, EstadoEvento.CONFIRMADO);
            });
        }

        System.out.println("Datos de prueba cargados.");
    }


    /**
     * Carga las personas desde el PersonaService y las muestra en la ListView.
     * @param listView La ListView de Persona a actualizar.
     */
    private void cargarListaPersonas(ListView<Persona> listView) {
        listView.getItems().clear();
        List<Persona> personas = personaService.listarPersonas();
        if (personas != null && !personas.isEmpty()) {
            listView.getItems().addAll(personas);
        } else {
            listView.getItems().add(new Participante("No hay personas registradas", "00000000", "", ""));
        }
    }

    /**
     * Carga las personas de un tipo específico desde el PersonaService y las muestra en el ComboBox.
     * @param <T> El tipo de Persona (Artista, Curador, Instructor).
     * @param comboBox El ComboBox a poblar.
     * @param type La clase del tipo de persona a filtrar.
     */
    private <T extends Persona> void cargarPersonasPorTipo(ComboBox<T> comboBox, Class<T> type) {
        comboBox.getItems().clear();
        List<Persona> todasLasPersonas = personaService.listarPersonas();
        List<T> personasFiltradas = todasLasPersonas.stream()
            .filter(type::isInstance)
            .map(type::cast)
            .collect(Collectors.toList());
        comboBox.getItems().addAll(personasFiltradas);
    }

    /**
     * Carga los eventos y participantes en los ComboBox de la pestaña de inscripción.
     * @param eventoComboBox El ComboBox para eventos.
     * @param participanteComboBox El ComboBox para participantes.
     */
    private void cargarComboBoxInscripcion(ComboBox<Evento> eventoComboBox, ComboBox<Participante> participanteComboBox) {
        eventoComboBox.getItems().clear();
        participanteComboBox.getItems().clear();

        List<Evento> eventosDisponibles = eventoService.getTodosLosEventos();
        for (Evento evento : eventosDisponibles) {
            if (evento.isRequiereInscripcion() && evento.getEstado() == EstadoEvento.CONFIRMADO) {
                eventoComboBox.getItems().add(evento);
            }
        }

        List<Persona> todasLasPersonas = personaService.listarPersonas();
        for (Persona persona : todasLasPersonas) {
            if (persona instanceof Participante) {
                participanteComboBox.getItems().add((Participante) persona);
            }
        }
    }

    /**
     * Refresca la lista de eventos en la pestaña "Listar Eventos" con un filtro de estado opcional.
     * @param listView La ListView de eventos a actualizar.
     * @param filtroEstado El estado por el cual filtrar (String, "Todos" para no filtrar).
     */
    private void refrescarListaEventos(ListView<Evento> listView, String filtroEstado) {
        listView.getItems().clear();
        List<Evento> eventos = eventoService.getTodosLosEventos();

        if (eventos != null && !eventos.isEmpty()) {
            List<Evento> eventosFiltrados = eventos.stream()
                .filter(e -> "Todos".equals(filtroEstado) || e.getEstado().toString().equalsIgnoreCase(filtroEstado))
                .collect(Collectors.toList());

            if (!eventosFiltrados.isEmpty()) {
                eventosFiltrados.sort((e1, e2) -> e1.getFechaInicio().compareTo(e2.getFechaInicio()));
                listView.getItems().addAll(eventosFiltrados);
            } else {
                listView.getItems().add(new Feria("No hay eventos que coincidan con el filtro.", LocalDate.MIN, 0, false, 0, 0, TipoUbicacionFeria.AIRE_LIBRE) {
                    @Override public String toString() { return "No hay eventos que coincidan con el filtro."; }
                });
            }
        } else {
            listView.getItems().add(new Feria("No hay eventos registrados en el sistema.", LocalDate.MIN, 0, false, 0, 0, TipoUbicacionFeria.AIRE_LIBRE) {
                @Override public String toString() { return "No hay eventos registrados en el sistema."; }
            });
        }
    }

    /**
     * Método para actualizar todas las ListViews y ComboBoxes en la interfaz.
     * Esto asegura que los datos mostrados estén sincronizados después de cualquier operación CRUD.
     */
    public void actualizarTodasLasListas() {
        cargarListaPersonas(listaPersonasView);
        cargarComboBoxInscripcion(eventoComboBoxInscripcion, participanteComboBoxInscripcion);
        refrescarListaEventos(eventosListView, "Todos");
    }
}
