# 🎭 Sistema de Gestión de Eventos Culturales para un Municipio

Este proyecto es una aplicación de escritorio desarrollada en **Java** para gestionar eventos culturales organizados por un municipio. Permite registrar distintos tipos de eventos, personas con distintos roles (organizadores, artistas, curadores, instructores), participantes, cupos, y manejar el estado de los eventos.

---

## 🛠️ Tecnologías Utilizadas

- **Java 17**
- **JavaFX** (interfaz gráfica)
- **JPA (Jakarta Persistence API)** con **Hibernate**
- **Base de datos relacional** (PostgreSQL o H2)
- **Maven** (gestión y empaquetado del proyecto)
- **UML** (para el modelado del dominio y relaciones)

---

## 📦 Funcionalidades Implementadas

### 🧱 Modelo de Dominio (paquete `modelo`)

- **Evento (abstracto)**: clase base para distintos tipos de eventos.
  - Atributos: `id`, `nombre`, `fechaInicio`, `fechaFin`, `cupos`, `estado`, etc.
  - Subclases:
    - `Concierto`
    - `Feria`
    - `ObraTeatral`
    - `Pelicula`
    - `Taller`
    - `Exposicion`

- **Persona (abstracta)**: representa una persona en el sistema.
  - Subclases:
    - `Organizador`
    - `Participante`
    - `Artista`
    - `Curador`
    - `Instructor`

- **Interfaces**:
  - `Inscribible`: implementada por eventos que permiten inscripción.
  - `Evaluable`: para eventos que pueden ser evaluados (ej. talleres).

- **Relaciones y Composición**:
  - Un `Evento` puede tener muchos `Participantes`.
  - `Taller` está compuesto por un `Instructor`.
  - `Exposicion` tiene uno o más `Artistas`.

---

### 🔁 Capa de Servicio (paquete `servicio`)

- Encapsula la lógica de negocio y acceso a datos.
- Proporciona métodos para:
  - Registrar y consultar eventos.
  - Inscribir participantes.
  - Validar fechas, cupos, duplicados, etc.
  - Consultas por tipo de evento, estado o fechas.

---

### 🖼️ Interfaz Gráfica (paquete `interfaz`)

- JavaFX para construir la interfaz de usuario.
- Ventanas y formularios para:
  - Registrar eventos y personas.
  - Inscribir participantes.
  - Visualizar y filtrar eventos.

---

### 🧩 Persistencia con JPA

- Uso de anotaciones como `@Entity`, `@Id`, `@GeneratedValue`, `@OneToMany`, `@ManyToMany`, `@Enumerated`.
- Las relaciones entre objetos están mapeadas como relaciones en la base de datos.
- Arquitectura basada en dominio rico (modelo de objetos conectado a la lógica).

---

### 📋 Gestión de Estados y Validaciones

- Estados posibles: `PLANEADO`, `EN_CURSO`, `FINALIZADO`.
- Se valida:
  - Fechas válidas (inicio < fin).
  - Cupos máximos de participantes.
  - Evitar inscripciones duplicadas.
  - Roles adecuados por tipo de evento.

---

## 📂 Estructura del Proyecto

   src/
└── main/
├── java/
│ └── com/municipio/eventos/
│ ├── modelo/ → Clases del dominio (Evento, Persona, etc.)
│ ├── servicio/ → Lógica de negocio y servicios
│ └── interfaz/ → Controladores y vistas JavaFX
└── resources/
└── application.properties (configuración de conexión)



---

## 📌 Requisitos

- **Java 17+**
- **Maven** (el proyecto está estructurado como un proyecto Maven)
- IDE como IntelliJ IDEA, Eclipse o NetBeans
- Base de datos compatible con JPA (PostgreSQL, H2, etc.)

---

## ✅ Estado del Proyecto

✔️ Modelo de clases con herencia, interfaces y relaciones implementado  
✔️ Persistencia y mapeo JPA funcionando  
✔️ Validaciones y lógica de inscripción implementadas en la capa de servicio  
⚙️ Interfaz JavaFX en construcción con integración modular  
📁 Proyecto reestructurado con paquetes `modelo`, `servicio` e `interfaz`  

---

## 📌 Tareas Pendientes

- [ ] Completar controladores y vistas JavaFX.
- [ ] Conectar todos los formularios a la capa de servicio.
- [ ] Añadir funcionalidades para reportes y estadísticas.
- [ ] Implementar pruebas unitarias.

---

## 🧠 Conceptos de POO Aplicados

- ✔️ **Herencia**: `Evento`, `Persona` como clases abstractas.
- ✔️ **Polimorfismo**: uso de interfaces `Inscribible`, `Evaluable`.
- ✔️ **Composición**: relación entre eventos y personas específicas.
- ✔️ **Abstracción**: separación de lógica en capas (modelo, servicio, interfaz).
- ✔️ **Encapsulamiento**: acceso controlado mediante getters y setters.

---

## 📜 Licencia

Este proyecto es de uso académico y libre para adaptación y estudio.
