package com.municipio.eventos.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    // ¡CORRECCIÓN AQUÍ! El nombre debe coincidir EXACTAMENTE con el de persistence.xml
    private static final String PERSISTENCE_UNIT_NAME = "EventosPU"; // Cambiado a "EventosPU" (con 'E' mayúscula)
    private static EntityManagerFactory entityManagerFactory;

    // Bloque estático para inicializar el EntityManagerFactory una sola vez
    static {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            System.out.println("DEBUG: EntityManagerFactory inicializado con éxito.");
        } catch (Exception e) {
            System.err.println("ERROR: Fallo al inicializar EntityManagerFactory.");
            e.printStackTrace();
            // Considera lanzar una RuntimeException o manejar el error de forma más robusta
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Obtiene una nueva instancia de EntityManager.
     * Es importante cerrar el EntityManager después de cada operación de base de datos.
     * @return Una instancia de EntityManager.
     */
    public static EntityManager getEntityManager() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            // Reintentar inicializar si por alguna razón se cerró o no se inicializó correctamente
            try {
                entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                System.out.println("DEBUG: Re-inicializando EntityManagerFactory.");
            } catch (Exception e) {
                System.err.println("ERROR: Fallo al re-inicializar EntityManagerFactory.");
                e.printStackTrace();
                throw new RuntimeException("No se pudo obtener EntityManager: " + e.getMessage(), e);
            }
        }
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Cierra el EntityManagerFactory. Debe llamarse al finalizar la aplicación
     * para liberar los recursos.
     */
    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            System.out.println("DEBUG: EntityManagerFactory cerrado.");
        }
    }
}
