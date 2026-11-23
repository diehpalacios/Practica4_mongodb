package models;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Animal {
    private String idAnimal;
    private String nombre;
    private String especie;
    private String raza;
    private int edad;
    private String sexo;
    private String descripcion;
    private String fechaIngreso;
    private String estado; // disponible, reservado, adoptado, en tratamiento
    private boolean activo;
    private List<RegistroMedico> historialMedico;

    public Animal() {
        this.historialMedico = new ArrayList<>();
        this.activo = true;
    }

    public Animal(String idAnimal, String nombre, String especie, String raza, int edad,
                  String sexo, String descripcion, String fechaIngreso, String estado,
                  boolean activo, List<RegistroMedico> historialMedico) {
        this.idAnimal = idAnimal;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.sexo = sexo;
        this.descripcion = descripcion;
        this.fechaIngreso = fechaIngreso;
        this.estado = estado;
        this.activo = activo;
        this.historialMedico = historialMedico != null ? historialMedico : new ArrayList<>();
    }

    // Getters & Setters
    public String getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(String idAnimal) {
        this.idAnimal = idAnimal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<RegistroMedico> getHistorialMedico() {
        return historialMedico;
    }

    public void setHistorialMedico(List<RegistroMedico> historialMedico) {
        this.historialMedico = historialMedico;
    }

    public Document toDocument() {
        List<Document> docsHistorial = new ArrayList<>();
        if (this.historialMedico != null) {
            for (RegistroMedico rm : this.historialMedico) {
                docsHistorial.add(rm.toDocument());
            }
        }

        return new Document("idAnimal", this.idAnimal)
                .append("nombre", this.nombre)
                .append("especie", this.especie)
                .append("raza", this.raza)
                .append("edad", this.edad)
                .append("sexo", this.sexo)
                .append("descripcion", this.descripcion)
                .append("fechaIngreso", this.fechaIngreso)
                .append("estado", this.estado)
                .append("activo", this.activo)
                .append("historialMedico", docsHistorial);
    }

    public static Animal fromDocument(Document doc) {
        if (doc == null) return null;

        List<RegistroMedico> historial = new ArrayList<>();
        List<Document> docsHistorial = doc.getList("historialMedico", Document.class);
        if (docsHistorial != null) {
            for (Document d : docsHistorial) {
                historial.add(RegistroMedico.fromDocument(d));
            }
        }

        return new Animal(
                doc.getString("idAnimal"),
                doc.getString("nombre"),
                doc.getString("especie"),
                doc.getString("raza"),
                doc.getInteger("edad", 0),
                doc.getString("sexo"),
                doc.getString("descripcion"),
                doc.getString("fechaIngreso"),
                doc.getString("estado"),
                doc.getBoolean("activo", true),
                historial
        );
    }

}
