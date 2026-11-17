package util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import javax.swing.*;
import java.io.InputStream;
import java.util.Properties;

public class MongoConnection {
    private static MongoClient mongoClient = null;
    private static MongoDatabase database = null;

    private MongoConnection() {
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            try (InputStream input = MongoConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
                Properties prop = new Properties();
                if (input == null) {
                    throw new RuntimeException("config.properties resource not found");
                }

                prop.load(input);

                String user = prop.getProperty("mongo.user");
                String password = prop.getProperty("mongo.password");
                String host = prop.getProperty("mongo.host", "localhost");
                String port = prop.getProperty("mongo.port", "27017");
                String authDb = prop.getProperty("mongo.authDb", "admin");
                String dbName = prop.getProperty("mongo.database", "AD_refugio");

                String uri = String.format("mongodb://%s:%s@%s:%s/?authSource=%s",
                        user, password, host, port, authDb);
                mongoClient = MongoClients.create(uri);
                database = mongoClient.getDatabase(dbName);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error al conectar con MongoDB: " + e.getMessage(),
                        "Error de Conexión",
                        JOptionPane.ERROR_MESSAGE);
            }

        }
        return database;

    }
    public static void closeConnection(){
        if(mongoClient!=null){
            mongoClient.close();
            mongoClient = null;
            database = null;
        }
    }

}
