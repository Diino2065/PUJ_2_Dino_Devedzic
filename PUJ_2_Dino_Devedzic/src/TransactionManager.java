import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import MongoDBConnection.MongoDBConnection;

import java.util.ArrayList;

public class TransactionManager {

    private final MongoCollection<Document> collection;
    private final String userId;
    private final String username;

    public TransactionManager(String userId, String username) {
        this.userId = userId;
        this.username = username;
        MongoDatabase db = MongoDBConnection.getDatabase();
        collection = db.getCollection("users");
    }

    public void addTransaction(Transaction t) {
        Document transactionDoc = t.toDocument();
        transactionDoc.append("userId", userId);
        transactionDoc.append("username", username);
        collection.insertOne(transactionDoc);
    }

    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> list = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find(new Document("userId", userId)).iterator();
        while (cursor.hasNext()) {
            Document d = cursor.next();
            list.add(new Transaction(
                    d.getObjectId("_id"),
                    d.getString("type"),
                    d.getDouble("amount") != null ? d.getDouble("amount") : 0.0,
                    d.getString("description"),
                    d.getString("category"),
                    d.getString("username")
            ));
        }
        return list;
    }

    public double getTotalIncome() {
        double total = 0;
        for (Transaction t : getAllTransactions()) {
            if ("Prihod".equalsIgnoreCase(t.getType())) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public double getTotalExpense() {
        double total = 0;
        for (Transaction t : getAllTransactions()) {
            if ("Rashod".equalsIgnoreCase(t.getType())) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public void updateTransaction(String selectedTransactionId, String type, double amount, String description, String category) {
        ObjectId objectId = new ObjectId(selectedTransactionId);
        Document updated = new Document("type", type)
                .append("amount", amount)
                .append("description", description)
                .append("category", category);

        collection.updateOne(
                new Document("_id", objectId).append("userId", userId),
                new Document("$set", updated)
        );
    }

    public void deleteTransaction(String transactionId) {
        ObjectId objectId = new ObjectId(transactionId);
        collection.deleteOne(new Document("_id", objectId).append("userId", userId));
    }
}