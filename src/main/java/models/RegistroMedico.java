package models;

import org.bson.Document;

public class RegistroMedico {

    private String fecha;
    private String diagnostico;
    private String tratamiento;

    public RegistroMedico() {
    }


    public RegistroMedico(String fecha, String diagnostico, String tratamiento) {
        this.fecha = fecha;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public Document toDocument() {
        return new Document("fecha", this.fecha)
                .append("diagnostico", this.diagnostico)
                .append("tratamiento", this.tratamiento);
    }

    public static RegistroMedico fromDocument(Document doc) {
        if (doc == null) return null;
        return new RegistroMedico(
                doc.getString("fecha"),
                doc.getString("diagnostico"),
                doc.getString("tratamiento")
        );
    }
}
