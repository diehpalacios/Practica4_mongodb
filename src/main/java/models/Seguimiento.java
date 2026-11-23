package models;

import org.bson.Document;

public class Seguimiento {
    private String fecha;
    private String notas;

    public Seguimiento() {
    }

    public Seguimiento(String fecha, String notas) {
        this.fecha = fecha;
        this.notas = notas;
    }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public Document toDocument() {
        return new Document("fecha", this.fecha)
                .append("notas", this.notas);
    }

    public static Seguimiento fromDocument(Document doc) {
        if (doc == null) return null;
        return new Seguimiento(
                doc.getString("fecha"),
                doc.getString("notas")
        );
    }
}