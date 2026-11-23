package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import idao.ICrudDao;
import models.Adoptante;
import org.bson.Document;
import org.bson.conversions.Bson;
import util.MongoConnection;

import java.util.ArrayList;
import java.util.List;

public class AdoptanteDao implements ICrudDao<Adoptante, String>{
    private MongoCollection<Document> getCollection() throws Exception {
        MongoDatabase db = MongoConnection.getDatabase();
        if (db == null) {
            throw new Exception("No hay conexión activa con la base de datos MongoDB.");
        }
        return db.getCollection("adoptantes");
    }
    @Override
    public void create(Adoptante adoptante) throws Exception {
        if (findById(adoptante.getDni()) != null) {
            throw new Exception("Ya existe un adoptante registrado con el DNI/NIE: " + adoptante.getDni());
        }
        getCollection().insertOne(adoptante.toDocument());
    }

    @Override
    public Adoptante findById(String dni) throws Exception {
        Document doc = getCollection().find(Filters.eq("dni", dni)).first();
        return Adoptante.fromDocument(doc);
    }

    @Override
    public List<Adoptante> findAll(boolean incluirInactivos) throws Exception {
        List<Adoptante> lista = new ArrayList<>();
        Bson filtro = incluirInactivos ? new Document() : Filters.eq("activo", true);

        for (Document doc : getCollection().find(filtro)) {
            lista.add(Adoptante.fromDocument(doc));
        }
        return lista;
    }

    @Override
    public void update(Adoptante adoptante) throws Exception {
        if (findById(adoptante.getDni()) == null) {
            throw new Exception("No se encontró el adoptante con DNI/NIE: " + adoptante.getDni());
        }
        getCollection().replaceOne(Filters.eq("dni", adoptante.getDni()), adoptante.toDocument());
    }

    @Override
    public void deleteLogico(String dni) throws Exception {
        if (findById(dni) == null) {
            throw new Exception("No existe el adoptante con DNI/NIE: " + dni);
        }
        getCollection().updateOne(Filters.eq("dni", dni), Updates.set("activo", false));
    }

    @Override
    public void deleteFisico(String dni) throws Exception {
        if (tieneAdopcionesAsociadas(dni)) {
            throw new Exception("No se puede eliminar físicamente: el adoptante tiene solicitudes o adopciones asociadas. Use la baja lógica.");
        }
        getCollection().deleteOne(Filters.eq("dni", dni));
    }

    public boolean tieneAdopcionesAsociadas(String dni) throws Exception {
        MongoCollection<Document> colAdopciones = MongoConnection.getDatabase().getCollection("adopciones");
        long count = colAdopciones.countDocuments(Filters.eq("dniAdoptante", dni));
        return count > 0;
    }
}
