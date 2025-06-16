# 🎭 Sistema de Gestión de Eventos Culturales para un Municipio

Este proyecto es una aplicación de escritorio desarrollada en **Java** para gestionar eventos culturales organizados por un municipio. Permite registrar distintos tipos de eventos, personas con distintos roles (organizadores, artistas, curadores, instructores), participantes, cupos, y manejar el estado de los eventos.

---

## 🛠️ Tecnologías Utilizadas

- **Java 17**
- **JavaFX** (interfaz gráfica, pendiente de implementación)
- **JPA (Jakarta Persistence API)** con **Hibernate**
- **Base de datos relacional** (PostgreSQL, H2, etc.)
- **Maven** (gestión y empaquetado del proyecto)

---

## 📦 Funcionalidades Implementadas

### 🧱 Modelo de Dominio

- **Evento (abstracto)**: clase base para distintos tipos de eventos.
  - Atributos: `nombre`, `fechaInicio`, `duracionEstimadaMinutos`, `estado`, `requiereInscripcion`, `cupoMaximo`, `responsables`, `inscripciones`, etc.
  - Subclases:
    - `Feria`: cantidad de stands, tipo de ubicación (al aire libre o techada).
    - `Concierto`: lista de artistas, tipo de entrada (gratuita o paga).
    - `Exposicion`: tipo de arte, curador.
    - `Taller`: cupo máximo, instructor, modalidad (presencial o virtual).
    - `CicloDeCine`: películas a proyectar, orden, charlas posteriores.

- **Persona**: nombre completo, DNI, teléfono, correo electrónico.
  - Subclases/roles: Organizador, Artista, Curador, Instructor, Participante.

- **Inscripcion**: relación entre participante y evento, con fecha de inscripción.

- **Enums**: 
  - `EstadoEvento`: PLANIFICACION, CONFIRMADO, EN_EJECUCION, FINALIZADO.
  - Otros según tipo de evento (ej: modalidad, tipo de entrada, tipo de ubicación).

---

### 🔁 Capa de Servicio

- Lógica de negocio y acceso a datos.
- Métodos para:
  - Alta, modificación y baja de eventos y personas.
  - Asociación de personas a eventos según su rol.
  - Registro y validación de inscripciones (incluyendo cupo y estado).
  - Cambio de estado de eventos.
  - Listado de participantes por evento.
  - Validación de cupo máximo y duplicados.

---

### 🖼️ Interfaz Gráfica

- **JavaFX** (pendiente de implementación)
- Formularios previstos para:
  - Registrar eventos y personas.
  - Inscribir participantes.
  - Visualizar y filtrar eventos.
  - Visualización de calendario de eventos (pendiente).

---

### 🧩 Persistencia con JPA

- Uso de anotaciones como `@Entity`, `@Id`, `@GeneratedValue`, `@OneToMany`, `@ManyToMany`, `@Enumerated`.
- Relaciones entre objetos mapeadas como relaciones en la base de datos.
- Persistencia polimórfica para eventos y personas.

---

### 📋 Gestión de Estados y Validaciones

- Estados posibles: `PLANIFICACION`, `CONFIRMADO`, `EN_EJECUCION`, `FINALIZADO`.
- Validaciones:
  - Fechas válidas (inicio < fin).
  - Cupos máximos de participantes.
  - Evitar inscripciones duplicadas.
  - Roles adecuados por tipo de evento.
  - No permitir inscripciones si el evento no está confirmado o está finalizado.

---

## 📂 Estructura del Proyecto

📦 IntegradorPOO  
└── 📁 src  
&emsp; └── 📁 main  
&emsp; &emsp; ├── 📁 java  
&emsp; &emsp; │   └── 📁 com  
&emsp; &emsp; │       └── 📁 municipio  
&emsp; &emsp; │           └── 📁 eventos  
&emsp; &emsp; │               ├── 📁 dao  
&emsp; &emsp; │               ├── 📁 main  
&emsp; &emsp; │               ├── 📁 models  
&emsp; &emsp; │               ├── 📁 services  
&emsp; &emsp; │               └── 📁 utils  
&emsp; &emsp; └── 📁 META-INF  
&emsp; &emsp;     └── persistence.xml  

---

## 📌 Requisitos

- **Java 17+**
- **Maven**
- IDE como IntelliJ IDEA, Eclipse o VS Code
- Base de datos compatible con JPA (PostgreSQL, H2, etc.)

---

## ✅ Estado del Proyecto

✔️ Modelo de clases con herencia, composición y relaciones implementado  
✔️ Persistencia y mapeo JPA funcionando  
✔️ Validaciones y lógica de inscripción implementadas en la capa de servicio  
✔️ Gestión de roles y asociaciones por tipo de evento  
⚙️ Interfaz JavaFX pendiente de implementación  
📁 Proyecto estructurado en paquetes `dao`, `main`, `models`, `services`, `utils`  

---

## 📌 Tareas Pendientes

- [ ] Completar controladores y vistas JavaFX.
- [ ] Conectar todos los formularios a la capa de servicio.
- [ ] Añadir funcionalidades para reportes y estadísticas.
- [ ] Implementar pruebas unitarias.
- [ ] Mejorar validaciones y lógica de negocio en entidades.
- [ ] Visualización de calendario de eventos.

---

## 🧠 Conceptos de POO Aplicados

- ✔️ **Herencia**: `Evento`, `Persona` como clases abstractas.
- ✔️ **Polimorfismo**: uso de subtipos de eventos/personas.
- ✔️ **Composición**: relación entre eventos y personas específicas.
- ✔️ **Abstracción**: separación de lógica en capas (modelo, servicio, interfaz).
- ✔️ **Encapsulamiento**: acceso controlado mediante getters y setters.

---

## 📜 Licencia

Este proyecto es de uso académico y libre para adaptación y estudio.