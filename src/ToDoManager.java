import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import MongoDBConnection.MongoDBConnection;

import java.util.ArrayList;

public class ToDoManager {

    private final MongoCollection<Document> collection;
    private final String username;

    public ToDoManager(String username) {
        this.username = username;
        MongoDatabase db = MongoDBConnection.getDatabase();
        collection = db.getCollection("todos");
    }

    public void addToDo(String task) {
        Document doc = new Document("username", username)
                .append("task", task);
        collection.insertOne(doc);
    }

    public ArrayList<String> getToDos() {
        ArrayList<String> todos = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find(new Document("username", username)).iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            todos.add(doc.getString("task"));
        }
        return todos;
    }

    public void updateToDo(String oldTask, String newTask) {
        collection.updateOne(
                new Document("username", username).append("task", oldTask),
                new Document("$set", new Document("task", newTask))
        );
    }

    public void deleteToDo(String task) {
        collection.deleteOne(new Document("username", username).append("task", task));
    }
}