package models;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Adopcion {
    private String idAdopcion;
    private String idAnimal;     // Referencia a colección 'animales'
    private String dniAdoptante; // Referencia a colección 'adoptantes'
    private String fechaSolicitud;
    private String fechaResolucion;
    private String estado;       // pendiente, aprobada, rechazada
    private String observaciones;
    private boolean activo;
    private List<Seguimiento> seguimiento;

    public Adopcion() {
        this.seguimiento = new ArrayList<>();
        this.activo = true;
    }

    public Adopcion(String idAdopcion, String idAnimal, String dniAdoptante,
                    String fechaSolicitud, String fechaResolucion, String estado,
                    String observaciones, boolean activo, List<Seguimiento> seguimiento) {
        this.idAdopcion = idAdopcion;
        this.idAnimal = idAnimal;
        this.dniAdoptante = dniAdoptante;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaResolucion = fechaResolucion;
        this.estado = estado;
        this.observaciones = observaciones;
        this.activo = activo;
        this.seguimiento = seguimiento != null ? seguimiento : new ArrayList<>();
    }

    // Getters y Setters
    public String getIdAdopcion() {
        return idAdopcion;
    }

    public void setIdAdopcion(String idAdopcion) {
        this.idAdopcion = idAdopcion;
    }

    public String getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(String idAnimal) {
        this.idAnimal = idAnimal;
    }

    public String getDniAdoptante() {
        return dniAdoptante;
    }

    public void setDniAdoptante(String dniAdoptante) {
        this.dniAdoptante = dniAdoptante;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(String fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<Seguimiento> getSeguimiento() {
        return seguimiento;
    }

    public void setSeguimiento(List<Seguimiento> seguimiento) {
        this.seguimiento = seguimiento;
    }

    public Document toDocument() {
        List<Document> docsSeguimiento = new ArrayList<>();
        if (this.seguimiento != null) {
            for (Seguimiento seg : this.seguimiento) {
                docsSeguimiento.add(seg.toDocument());
            }
        }

        return new Document("idAdopcion", this.idAdopcion)
                .append("idAnimal", this.idAnimal)
                .append("dniAdoptante", this.dniAdoptante)
                .append("fechaSolicitud", this.fechaSolicitud)
                .append("fechaResolucion", this.fechaResolucion)
                .append("estado", this.estado)
                .append("observaciones", this.observaciones)
                .append("activo", this.activo)
                .append("seguimiento", docsSeguimiento);
    }

    public static Adopcion fromDocument(Document doc) {
        if (doc == null) return null;

        List<Seguimiento> listaSeg = new ArrayList<>();
        List<Document> docsSeg = doc.getList("seguimiento", Document.class);
        if (docsSeg != null) {
            for (Document d : docsSeg) {
                listaSeg.add(Seguimiento.fromDocument(d));
            }
        }

        return new Adopcion(
                doc.getString("idAdopcion"),
                doc.getString("idAnimal"),
                doc.getString("dniAdoptante"),
                doc.getString("fechaSolicitud"),
                doc.getString("fechaResolucion"),
                doc.getString("estado"),
                doc.getString("observaciones"),
                doc.getBoolean("activo", true),
                listaSeg
        );
    }
}