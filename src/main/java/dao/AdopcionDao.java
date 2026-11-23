package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import idao.ICrudDao;
import models.Adopcion;
import models.Adoptante;
import models.Animal;
import models.Seguimiento;
import org.bson.Document;
import org.bson.conversions.Bson;
import util.MongoConnection;

import java.util.ArrayList;
import java.util.List;

public class AdopcionDao implements ICrudDao<Adopcion, String> {

    private final AnimalDao animalDao = new AnimalDao();
    private final AdoptanteDao adoptanteDao = new AdoptanteDao();

    private MongoCollection<Document> getCollection() throws Exception {
        MongoDatabase db = MongoConnection.getDatabase();
        if (db == null) {
            throw new Exception("No hay conexión activa con la base de datos MongoDB.");
        }
        return db.getCollection("adopciones");
    }

    @Override
    public void create(Adopcion adopcion) throws Exception {
        if (findById(adopcion.getIdAdopcion()) != null) {
            throw new Exception("Ya existe una adopción con el ID: " + adopcion.getIdAdopcion());
        }

        // Validar integridad referencial de animal y adoptante
        Animal animal = animalDao.findById(adopcion.getIdAnimal());
        if (animal == null || !animal.isActivo()) {
            throw new Exception("El animal indicado no existe o está dado de baja.");
        }
        Adoptante adoptante = adoptanteDao.findById(adopcion.getDniAdoptante());
        if (adoptante == null || !adoptante.isActivo()) {
            throw new Exception("El adoptante indicado no existe o está dado de baja.");
        }

        getCollection().insertOne(adopcion.toDocument());

        // Sincronizar estado del animal según el estado de la solicitud
        sincronizarEstadoAnimal(adopcion);
    }

    @Override
    public Adopcion findById(String idAdopcion) throws Exception {
        Document doc = getCollection().find(Filters.eq("idAdopcion", idAdopcion)).first();
        return Adopcion.fromDocument(doc);
    }

    @Override
    public List<Adopcion> findAll(boolean incluirInactivos) throws Exception {
        List<Adopcion> lista = new ArrayList<>();
        Bson filtro = incluirInactivos ? new Document() : Filters.eq("activo", true);

        for (Document doc : getCollection().find(filtro)) {
            lista.add(Adopcion.fromDocument(doc));
        }
        return lista;
    }

    public List<Adopcion> findByAdoptante(String dniAdoptante) throws Exception {
        List<Adopcion> lista = new ArrayList<>();
        Bson filtro = Filters.and(
                Filters.eq("dniAdoptante", dniAdoptante),
                Filters.eq("activo", true)
        );
        for (Document doc : getCollection().find(filtro)) {
            lista.add(Adopcion.fromDocument(doc));
        }
        return lista;
    }

    @Override
    public void update(Adopcion adopcion) throws Exception {
        if (findById(adopcion.getIdAdopcion()) == null) {
            throw new Exception("No se encontró la adopción con ID: " + adopcion.getIdAdopcion());
        }
        getCollection().replaceOne(Filters.eq("idAdopcion", adopcion.getIdAdopcion()), adopcion.toDocument());
        sincronizarEstadoAnimal(adopcion);
    }

    @Override
    public void deleteLogico(String idAdopcion) throws Exception {
        if (findById(idAdopcion) == null) {
            throw new Exception("No existe la adopción con ID: " + idAdopcion);
        }
        getCollection().updateOne(Filters.eq("idAdopcion", idAdopcion), Updates.set("activo", false));
    }

    @Override
    public void deleteFisico(String idAdopcion) throws Exception {
        getCollection().deleteOne(Filters.eq("idAdopcion", idAdopcion));
    }

    public void agregarSeguimiento(String idAdopcion, Seguimiento seguimiento) throws Exception {
        Adopcion adopcion = findById(idAdopcion);
        if (adopcion == null) {
            throw new Exception("No se encontró la adopción con ID: " + idAdopcion);
        }
        if (!"aprobada".equalsIgnoreCase(adopcion.getEstado())) {
            throw new Exception("Solo se puede registrar seguimiento en adopciones aprobadas.");
        }
        getCollection().updateOne(
                Filters.eq("idAdopcion", idAdopcion),
                Updates.push("seguimiento", seguimiento.toDocument())
        );
    }

    private void sincronizarEstadoAnimal(Adopcion adopcion) throws Exception {
        if ("aprobada".equalsIgnoreCase(adopcion.getEstado())) {
            animalDao.actualizarEstado(adopcion.getIdAnimal(), "adoptado");
        } else if ("pendiente".equalsIgnoreCase(adopcion.getEstado())) {
            animalDao.actualizarEstado(adopcion.getIdAnimal(), "reservado");
        }
    }
}