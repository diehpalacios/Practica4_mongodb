package models;

import org.bson.Document;

public class Refugio {
    private String id;
    private String nombre;
    private String cif;
    private String fechaApertura;
    private String direccion;
    private String contacto;

    public Refugio() {
    }

    public Refugio(String id, String nombre, String cif, String fechaApertura, String direccion, String contacto) {
        this.id = id;
        this.nombre = nombre;
        this.cif = cif;
        this.fechaApertura = fechaApertura;
        this.direccion = direccion;
        this.contacto = contacto;
    }

    // Getter & Setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(String fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    // Conversion de Obj a BSON
    public Document toDocument() {
        return new Document("_id", this.id)
                .append("nombre", this.nombre)
                .append("cif", this.cif)
                .append("fechaApertura", this.fechaApertura)
                .append("direccion", this.direccion)
                .append("contacto", this.contacto);
    }

    // Conversion de Documento BSON a Obj
    public static Refugio fromDocument(Document doc) {
        if (doc == null) return null;
        return new Refugio(
                doc.getString("_id"),
                doc.getString("nombre"),
                doc.getString("cif"),
                doc.getString("fechaApertura"),
                doc.getString("direccion"),
                doc.getString("contacto")
        );
    }
}
