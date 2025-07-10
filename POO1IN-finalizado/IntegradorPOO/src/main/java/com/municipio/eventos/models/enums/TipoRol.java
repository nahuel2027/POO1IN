package com.municipio.eventos.models.enums;

/**
 * Enumeración que define los diferentes tipos de roles que una Persona puede tener
 * dentro del sistema de gestión de eventos.
 * Esto reemplaza la necesidad de subclases de Persona para cada rol.
 */
public enum TipoRol {
    ORGANIZADOR,
    ARTISTA,
    CURADOR,
    INSTRUCTOR,
    PARTICIPANTE
}
