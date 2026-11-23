package models;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Adoptante {
    private String dni;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String email;
    private String fechaRegistro;
    private boolean activo;
    private List<String> preferencias;

    public Adoptante() {
        this.preferencias = new ArrayList<>();
        this.activo = true;
    }

    public Adoptante(String dni, String nombre, String apellidos, String telefono,
                     String direccion, String email, String fechaRegistro,
                     boolean activo, List<String> preferencias) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.direccion = direccion;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
        this.preferencias = preferencias != null ? preferencias : new ArrayList<>();
    }

    // Getters y Setters
    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<String> getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(List<String> preferencias) {
        this.preferencias = preferencias;
    }

    public Document toDocument() {
        return new Document("dni", this.dni)
                .append("nombre", this.nombre)
                .append("apellidos", this.apellidos)
                .append("telefono", this.telefono)
                .append("direccion", this.direccion)
                .append("email", this.email)
                .append("fechaRegistro", this.fechaRegistro)
                .append("activo", this.activo)
                .append("preferencias", this.preferencias);
    }

    public static Adoptante fromDocument(Document doc) {
        if (doc == null) return null;
        List<String> prefs = doc.getList("preferencias", String.class);
        return new Adoptante(
                doc.getString("dni"),
                doc.getString("nombre"),
                doc.getString("apellidos"),
                doc.getString("telefono"),
                doc.getString("direccion"),
                doc.getString("email"),
                doc.getString("fechaRegistro"),
                doc.getBoolean("activo", true),
                prefs != null ? prefs : new ArrayList<>()
        );
    }
}
