// src/main/java/com/municipio/eventos/utils/JsonDataManager.java
package com.municipio.eventos.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.municipio.eventos.models.CicloDeCine;
import com.municipio.eventos.models.Concierto;
import com.municipio.eventos.models.Exposicion;
import com.municipio.eventos.models.Feria;
import com.municipio.eventos.models.Taller;
import com.municipio.eventos.models.abstractas.Evento;

public class JsonDataManager {

    private Gson gson;
    // JsonDataManager ya no es estrictamente necesario con JPA, pero lo mantengo si quieres usarlo
    // para importar/exportar datos a JSON, aunque la aplicación ahora se basa en la DB.
    // Lo comento para dejar claro que no es parte del flujo principal de persistencia de JPA.
    // private static final String FILE_PATH = "eventos.json"; 

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
     * (Este método es más para importar/exportar datos a JSON, no es el mecanismo principal de persistencia)
     * @param eventos La lista de eventos a guardar.
     * @param filePath Ruta del archivo JSON.
     */
    public void guardarEventos(List<Evento> eventos, String filePath) {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(eventos, writer);
            System.out.println("Eventos guardados en " + filePath);
        } catch (IOException e) {
            System.err.println("Error al guardar eventos en JSON: " + e.getMessage());
        }
    }

    /**
     * Carga una lista de eventos desde un archivo JSON.
     * (Este método es más para importar/exportar datos a JSON, no es el mecanismo principal de persistencia)
     * @param filePath Ruta del archivo JSON.
     * @return Una lista de eventos cargada desde el archivo, o una lista vacía si el archivo no existe o hay un error.
     */
    public List<Evento> cargarEventos(String filePath) {
        try (Reader reader = new FileReader(filePath)) {
            Type eventListType = new TypeToken<ArrayList<Evento>>() {}.getType();
            List<Evento> eventosCargados = gson.fromJson(reader, eventListType);
            System.out.println("Eventos cargados desde " + filePath);
            return eventosCargados != null ? eventosCargados : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Archivo de eventos no encontrado o error al cargar: " + e.getMessage());
            return new ArrayList<>(); // Retorna una lista vacía si el archivo no existe o hay un error
        }
    }
}
