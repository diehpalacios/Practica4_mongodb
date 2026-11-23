package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import util.MongoConnection;

import java.util.ArrayList;
import java.util.List;

public class MetadatosDao {

    public Document obtenerMetadatosCompletos() throws Exception {
        MongoDatabase db = MongoConnection.getDatabase();
        if (db == null) {
            throw new Exception("No hay conexión activa con la base de datos MongoDB.");
        }

        Document metadatos = new Document("baseDeDatos", db.getName());
        List<Document> listaColecciones = new ArrayList<>();

        for (String nombreColeccion : db.listCollectionNames()) {
            MongoCollection<Document> col = db.getCollection(nombreColeccion);
            long totalDocumentos = col.countDocuments();

            List<Document> listaIndices = new ArrayList<>();
            for (Document idx : col.listIndexes()) {
                Document infoIndice = new Document()
                        .append("nombre", idx.getString("name"))
                        .append("campos", idx.get("key", Document.class).toJson())
                        .append("unico", idx.getBoolean("unique", false));
                listaIndices.add(infoIndice);
            }

            Document infoColeccion = new Document()
                    .append("nombreColeccion", nombreColeccion)
                    .append("numeroDocumentos", totalDocumentos)
                    .append("indices", listaIndices);

            listaColecciones.add(infoColeccion);
        }

        metadatos.append("colecciones", listaColecciones);
        return metadatos;
    }
}