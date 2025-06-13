package main.java.com.municipio.eventos.models.abstractas;

public abstract class Persona {
    protected String nombreCompleto;
    protected String dni;
    protected String telefono;
    protected String email;

    // Constructor
    public Persona(String nombreCompleto, String dni, String telefono, String email) {
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
    }

    // Getters y Setters
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Nombre: " + nombreCompleto + ", DNI: " + dni + ", Teléfono: " + telefono + ", Email: " + email;
    }
}
