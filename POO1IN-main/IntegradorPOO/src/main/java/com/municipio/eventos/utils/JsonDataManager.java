// src/main/java/com/municipio/eventos/utils/JsonDataManager.java
package com.municipio.eventos.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import com.municipio.eventos.models.abstractas.Evento;
import com.municipio.eventos.models.CicloDeCine;
import com.municipio.eventos.models.Concierto;
import com.municipio.eventos.models.Exposicion;
import com.municipio.eventos.models.Feria;
import com.municipio.eventos.models.Taller;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonDataManager {

    private Gson gson;
    private static final String FILE_PATH = "eventos.json"; // Nombre del archivo JSON

    public JsonDataManager() {
        // Configurar Gson para manejar la herencia (polimorfismo)
        // Esto es crucial para que Gson sepa cómo serializar/deserializar Evento a sus subtipos
        RuntimeTypeAdapterFactory<Evento> eventAdapter = RuntimeTypeAdapterFactory
            .of(Evento.class, "tipoEvento") // "tipoEvento" será un campo adicional en el JSON para indicar el tipo
            .registerSubtype(Feria.class, "Feria")
            .registerSubtype(Concierto.class, "Concierto")
            .registerSubtype(Exposicion.class, "Exposicion")
            .registerSubtype(Taller.class, "Taller")
            .registerSubtype(CicloDeCine.class, "CicloDeCine");

        this.gson = new GsonBuilder()
            .setPrettyPrinting() // Para que el JSON se vea formateado y legible
            .registerTypeAdapterFactory(eventAdapter)
            .create();
    }

    /**
     * Guarda una lista de eventos en un archivo JSON.
     * @param eventos La lista de eventos a guardar.
     */
    public void guardarEventos(List<Evento> eventos) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(eventos, writer);
            System.out.println("Eventos guardados en " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error al guardar eventos en JSON: " + e.getMessage());
        }
    }

    /**
     * Carga una lista de eventos desde un archivo JSON.
     * @return Una lista de eventos cargada desde el archivo, o una lista vacía si el archivo no existe o hay un error.
     */
    public List<Evento> cargarEventos() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Type eventListType = new TypeToken<ArrayList<Evento>>() {}.getType();
            List<Evento> eventosCargados = gson.fromJson(reader, eventListType);
            System.out.println("Eventos cargados desde " + FILE_PATH);
            return eventosCargados != null ? eventosCargados : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Archivo de eventos no encontrado o error al cargar: " + e.getMessage());
            return new ArrayList<>(); // Retorna una lista vacía si el archivo no existe o hay un error
        }
    }
}
