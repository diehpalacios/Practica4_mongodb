package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import models.Refugio;
import org.bson.Document;
import util.MongoConnection;

public class RefugioDao {

    private MongoCollection<Document> getCollection() throws Exception {
        MongoDatabase db = MongoConnection.getDatabase();
        if (db == null) {
            throw new Exception("Mongo Database is null");
        }
        return db.getCollection("Refugio");
    }

    public Refugio obtenerRefugio() throws Exception {
        Document doc = getCollection().find().first();
        return Refugio.fromDocument(doc);
    }

    public void guardarOActualizar(Refugio refugio) throws Exception {
        if (refugio.getId() == null || refugio.getId().trim().isEmpty()) {
            refugio.setId("REF01");
        }
        ReplaceOptions options = new ReplaceOptions().upsert(true);
        getCollection().replaceOne(
                Filters.eq("_id", refugio.getId()),
                refugio.toDocument(),
                options
        );
    }

}
