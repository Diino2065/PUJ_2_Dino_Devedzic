package MongoDBConnection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBConnection {

    private static final String URI = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "PUJ2DB";
    private static final String COLLECTION_NAME = "users";

    public static void main(String[] args) {

        try (MongoClient client = MongoClients.create(URI)) {

            MongoDatabase database = client.getDatabase(DATABASE_NAME);
            MongoCollection<Document> users =
                    database.getCollection(COLLECTION_NAME);

            Document user = new Document("username", "dino")
                    .append("email", "dino@email.com")
                    .append("password", "123456789");

            users.insertOne(user);

            System.out.println("MongoDB connected.");
            System.out.println("Database  and collection created.");
        }
    }
    public static MongoDatabase getDatabase() {
        MongoClient client = MongoClients.create(URI);
        return client.getDatabase(DATABASE_NAME);
    }
}
