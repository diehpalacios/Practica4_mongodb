package util;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class ExportadorUtil {

    /**
     * Exporta una lista de documentos BSON (animales, adoptantes o adopciones) a un fichero JSON.
     */
    public static void exportarListaAJson(List<Document> documentos, String nombreColeccion, File archivoDestino) throws Exception {
        if (documentos == null) {
            throw new Exception("No hay datos disponibles para exportar.");
        }

        JSONArray jsonArray = new JSONArray();
        for (Document doc : documentos) {
            jsonArray.put(convertirAJsonObjectLimpio(doc));
        }

        JSONObject raiz = new JSONObject();
        raiz.put(nombreColeccion, jsonArray);

        // El parámetro 4 aplica sangría de 4 espacios para que el JSON sea legible
        Files.writeString(archivoDestino.toPath(), raiz.toString(4), StandardCharsets.UTF_8);
    }

    /**
     * Exporta una lista de documentos BSON a un fichero XML bien formado.
     */
    public static void exportarListaAXml(List<Document> documentos, String etiquetaRaiz, String etiquetaElemento, File archivoDestino) throws Exception {
        if (documentos == null) {
            throw new Exception("No hay datos disponibles para exportar.");
        }

        JSONArray jsonArray = new JSONArray();
        for (Document doc : documentos) {
            jsonArray.put(convertirAJsonObjectLimpio(doc));
        }

        JSONObject contenedor = new JSONObject();
        contenedor.put(etiquetaElemento, jsonArray);

        String cuerpoXml = XML.toString(contenedor, etiquetaRaiz);
        String xmlCompleto = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + formatXml(cuerpoXml);

        Files.writeString(archivoDestino.toPath(), xmlCompleto, StandardCharsets.UTF_8);
    }

    /**
     * Exporta un único documento (como el informe de metadatos) a JSON.
     */
    public static void exportarDocumentoAJson(Document documento, File archivoDestino) throws Exception {
        if (documento == null) {
            throw new Exception("El documento a exportar está vacío.");
        }
        JSONObject jsonObj = convertirAJsonObjectLimpio(documento);
        Files.writeString(archivoDestino.toPath(), jsonObj.toString(4), StandardCharsets.UTF_8);
    }

    /**
     * Exporta un único documento (como el informe de metadatos) a XML.
     */
    public static void exportarDocumentoAXml(Document documento, String etiquetaRaiz, File archivoDestino) throws Exception {
        if (documento == null) {
            throw new Exception("El documento a exportar está vacío.");
        }
        JSONObject jsonObj = convertirAJsonObjectLimpio(documento);
        String cuerpoXml = XML.toString(jsonObj, etiquetaRaiz);
        String xmlCompleto = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + formatXml(cuerpoXml);

        Files.writeString(archivoDestino.toPath(), xmlCompleto, StandardCharsets.UTF_8);
    }

    /**
     * Convierte el ObjectId interno de MongoDB a texto plano para evitar etiquetas XML inválidas como <$oid>.
     */
    private static JSONObject convertirAJsonObjectLimpio(Document doc) {
        Document copia = new Document(doc);
        Object id = copia.get("_id");
        if (id instanceof ObjectId) {
            copia.put("_id", ((ObjectId) id).toHexString());
        }
        return new JSONObject(copia.toJson());
    }

    /**
     * Añade saltos de línea básicos entre etiquetas XML para mejorar su lectura.
     */
    private static String formatXml(String xml) {
        return xml.replace("><", ">\n<");
    }
}