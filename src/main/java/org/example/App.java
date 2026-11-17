package org.example;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class App {
    public static void main(String[] args) {

        MongoClient mongo = new MongoClient("localhost", 27017);

        MongoDatabase db = mongo.getDatabase("Concesionario");

        MongoCollection<Document> coches = db.getCollection("Coches");

        Document coche = new Document("id", 1)
                .append("marca", "Toyota")
                .append("modelo", "Corolla")
                .append("anio", 2022)
                .append("precio", 24500.50)
                .append("color", "Blanco")
                .append("electrico", false);

        coches.insertOne(coche);

        mongo.close();
    }
}