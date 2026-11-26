package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import idao.ICrudDao;
import models.Animal;
import models.RegistroMedico;
import org.bson.Document;
import org.bson.conversions.Bson;
import util.MongoConnection;

import java.util.ArrayList;
import java.util.List;

public class AnimalDao implements ICrudDao<Animal, String> {
    private MongoCollection<Document> getCollection() throws Exception {
        MongoDatabase db = MongoConnection.getDatabase();
        if (db == null) {
            throw new Exception("Error: MongoDatabase is null");
        }
        return db.getCollection("animales");
    }

    @Override
    public void create(Animal animal) throws Exception {
        if (findById(animal.getIdAnimal()) != null) {
            throw new Exception("Ya existe un animal registrado con el ID: " + animal.getIdAnimal());
        }
        getCollection().insertOne(animal.toDocument());
    }

    @Override
    public Animal findById(String idAnimal) throws Exception {
        Document doc = getCollection().find(Filters.eq("idAnimal", idAnimal)).first();
        return Animal.fromDocument(doc);
    }

    @Override
    public List<Animal> findAll(boolean incluirInactivos) throws Exception {
        List<Animal> lista = new ArrayList<>();
        Bson filtro = incluirInactivos ? new Document() : Filters.eq("activo", true);

        for (Document doc : getCollection().find(filtro)) {
            lista.add(Animal.fromDocument(doc));
        }
        return lista;
    }

    @Override
    public void update(Animal animal) throws Exception {
        if (findById(animal.getIdAnimal()) != null) {
            throw new Exception("No se encontro el animal con ID: " + animal.getIdAnimal());
        }
        getCollection().replaceOne(Filters.eq("idAnimal", animal.getIdAnimal()), animal.toDocument());
    }

    @Override
    public void deleteLogico(String idAnimal) throws Exception {
        if (findById(idAnimal) == null) {
            throw new Exception("No existe el animal con ID: " + idAnimal);
        }
        if (tieneAdopcionesAsociadas(idAnimal)) {
            throw new Exception("No se puede dar de baja el animal porque está asociado a una o más adopciones. Elimine primero la adopción.");
        }
        getCollection().updateOne(Filters.eq("idAnimal", idAnimal), Updates.set("activo", false));
    }

    @Override
    public void deleteFisico(String idAnimal) throws Exception {
        if (findById(idAnimal) == null) {
            throw new Exception("No existe el animal con ID: " + idAnimal);
        }
        if (tieneAdopcionesAsociadas(idAnimal)) {
            throw new Exception("No se puede eliminar físicamente el animal porque está asociado a una o más adopciones. Elimine primero la adopción.");
        }
        getCollection().deleteOne(Filters.eq("idAnimal", idAnimal));
    }

    public boolean tieneAdopcionesAsociadas(String idAnimal) throws Exception {
        MongoCollection<Document> colAdopciones = MongoConnection.getDatabase().getCollection("adopciones");
        long count = colAdopciones.countDocuments(Filters.eq("idAnimal", idAnimal));
        return count > 0;
    }

    public void agregarRegistroMedico(String idAnimal, RegistroMedico registro) throws Exception {
        if (findById(idAnimal) == null) {
            throw new Exception("No se encontró el animal con ID: " + idAnimal);
        }
        getCollection().updateOne(
                Filters.eq("idAnimal", idAnimal),
                Updates.push("historialMedico", registro.toDocument())
        );
    }

    public void actualizarEstado(String idAnimal, String nuevoEstado) throws Exception {
        getCollection().updateOne(
                Filters.eq("idAnimal", idAnimal),
                Updates.set("estado", nuevoEstado)
        );
    }
}
