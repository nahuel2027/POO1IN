# IntegradorPOO - Gestión de Eventos del Municipio

## Descripción

**IntegradorPOO** es una aplicación de escritorio desarrollada en Java que permite gestionar eventos culturales y sociales organizados por un municipio. Incluye funcionalidades para crear y administrar eventos (Ferias, Conciertos, Exposiciones, Talleres, Ciclos de Cine), gestionar personas (Organizadores, Artistas, Curadores, Instructores, Participantes) e inscribir participantes a eventos. Utiliza JavaFX para la interfaz gráfica y JPA/Hibernate para la persistencia de datos en una base de datos PostgreSQL.

---

## Características principales

- **Gestión de Eventos:** Alta, listado, filtrado y cambio de estado de eventos.
- **Gestión de Personas:** Alta, modificación y baja de personas de distintos roles.
- **Inscripción de Participantes:** Inscripción de participantes a eventos con control de cupo y estado.
- **Datos de Prueba:** Carga automática de datos de ejemplo al iniciar la aplicación.
- **Persistencia:** Uso de JPA/Hibernate con PostgreSQL.
- **Interfaz Gráfica:** Desarrollada con JavaFX, intuitiva y fácil de usar.

---

## Estructura del Proyecto

```
IntegradorPOO/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/municipio/eventos/
│   │   │       ├── main/                # MainApp.java (CLI)
│   │   │       ├── models/              # Entidades JPA (Evento, Persona, etc.)
│   │   │       ├── dao/                 # DAOs para acceso a datos
│   │   │       ├── services/            # Lógica de negocio
│   │   │       └── ventanaprincipal/    # VentanaPrincipal.java (JavaFX)
│   │   └── resources/
│   │       └── META-INF/
│   │           └── persistence.xml      # Configuración JPA
│   └── test/
│       └── java/                        # Pruebas unitarias (si aplica)
└── target/                              # Archivos compilados por Maven
```

---

## Requisitos

- **Java 17** o superior (probado con Java 24)
- **Maven 3.8+**
- **PostgreSQL** (configurado y corriendo)
- **IDE recomendado:** Visual Studio Code o IntelliJ IDEA

---

## Configuración de la Base de Datos

1. Crea una base de datos llamada `eventos_db` en PostgreSQL.
2. Crea un usuario y otórgale permisos, por ejemplo:
   ```sql
   CREATE USER eventos_user WITH PASSWORD 'tu_password';
   CREATE DATABASE eventos_db OWNER eventos_user;
   GRANT ALL PRIVILEGES ON DATABASE eventos_db TO eventos_user;
   ```
3. Edita el archivo [`src/main/resources/META-INF/persistence.xml`](src/main/resources/META-INF/persistence.xml) para que coincidan el usuario y la contraseña:
   ```xml
   <property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/eventos_db" />
   <property name="jakarta.persistence.jdbc.user" value="eventos_user" />
   <property name="jakarta.persistence.jdbc.password" value="tu_password" />
   ```

---

## Compilación y Ejecución

### 1. Compilar el proyecto

Desde la raíz del proyecto, ejecuta:

```sh
mvn clean install
```

### 2. Ejecutar la aplicación JavaFX

Puedes ejecutar la aplicación gráfica desde tu IDE o desde terminal:

```sh
mvn javafx:run
```
O ejecuta el JAR generado (asegúrate de tener JavaFX en el classpath si usas el JAR):

```sh
java -jar target/IntegradorPOO-1.0-SNAPSHOT.jar
```

### 3. Ejecutar la versión CLI (opcional)

El archivo `MainApp.java` permite ejecutar una versión por consola para pruebas rápidas:

```sh
mvn exec:java -Dexec.mainClass="com.municipio.eventos.main.MainApp"
```

---

## Uso de la Aplicación

### Pestañas principales

- **Crear Eventos:** Completa los datos comunes y específicos según el tipo de evento. Haz clic en "Crear Evento".
- **Gestionar Personas:** Alta, modificación y baja de personas. Selecciona una persona para editar o eliminar.
- **Inscribir Participantes:** Selecciona un evento y un participante para inscribir. Visualiza los inscriptos.
- **Listar Eventos:** Filtra eventos por estado y cambia el estado de los mismos.

### Datos de Prueba

Al iniciar la aplicación se cargan automáticamente ejemplos de eventos y personas para facilitar las pruebas.

---

## Notas y recomendaciones de uso

### Sobre la gestión de películas en Ciclos de Cine

- Al crear un **Ciclo de Cine**, puedes agregar películas nuevas o seleccionar películas existentes de otros ciclos.
- **IMPORTANTE:** Si agregas una película existente (doble clic en la lista de películas existentes), la aplicación crea una **nueva instancia** de esa película para evitar problemas de persistencia y asegurar que el evento se guarde correctamente.
- Si notas que un ciclo de cine no aparece en el listado tras crearlo, asegúrate de que las películas agregadas sean nuevas instancias (esto ya está resuelto en la versión actual).
- Si modificas la lógica de persistencia, revisa el mapeo JPA entre `CicloDeCine` y `Pelicula` para evitar conflictos de entidades gestionadas.

### Troubleshooting

- Si un evento no aparece en el listado tras crearlo, revisa la consola por posibles errores de persistencia.
- Verifica que la base de datos esté corriendo y que los datos de conexión sean correctos.
- Si tienes problemas con JavaFX y el JAR, asegúrate de tener las librerías de JavaFX en el classpath.

---

## Tecnologías utilizadas

- **Java 17+**
- **JavaFX** (interfaz gráfica)
- **JPA/Hibernate** (persistencia)
- **PostgreSQL** (base de datos)
- **Maven** (gestión de dependencias y build)

---

## Personalización

- Puedes modificar o ampliar los tipos de eventos y personas editando las clases en `com.municipio.eventos.models`.
- Para cambiar la configuración de la base de datos, edita `persistence.xml`.
- Para agregar más datos de prueba, modifica el método `cargarDatosDePrueba()` en [`VentanaPrincipal.java`](src/main/java/com/municipio/eventos/ventanaprincipal/VentanaPrincipal.java).

---

## Créditos

Desarrollado por Grupo n°1 para la materia Programación Orientada a Objetos I (UNaM).

---

## Licencia

Este proyecto es de uso académico.