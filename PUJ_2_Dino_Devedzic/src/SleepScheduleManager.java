import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import MongoDBConnection.MongoDBConnection;

import java.util.ArrayList;
import java.util.List;

public class SleepScheduleManager {

    private final MongoCollection<Document> collection;
    private final String username;

    public SleepScheduleManager(String username) {
        this.username = username;
        MongoDatabase db = MongoDBConnection.getDatabase();
        collection = db.getCollection("sleepschedule");
    }

    public void addSleepData(String date, double hours) {
        Document doc = new Document("username", username)
                .append("date", date)
                .append("hours", hours);
        collection.insertOne(doc);
    }

    public List<Document> getSleepData() {
        List<Document> sleepData = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find(new Document("username", username)).iterator();
        while (cursor.hasNext()) {
            sleepData.add(cursor.next());
        }
        return sleepData;
    }

    public double getAverageSleep() {
        List<Document> sleepData = getSleepData();
        if (sleepData.isEmpty()) {
            return 0;
        }
        double totalHours = sleepData.stream()
                .mapToDouble(doc -> doc.getDouble("hours"))
                .sum();
        return totalHours / sleepData.size();
    }

    public String getDayWithMostSleep() {
        List<Document> sleepData = getSleepData();
        return sleepData.stream()
                .max((doc1, doc2) -> Double.compare(doc1.getDouble("hours"), doc2.getDouble("hours")))
                .map(doc -> doc.getString("date"))
                .orElse("No data");
    }

    public String getDayWithLeastSleep() {
        List<Document> sleepData = getSleepData();
        return sleepData.stream()
                .min((doc1, doc2) -> Double.compare(doc1.getDouble("hours"), doc2.getDouble("hours")))
                .map(doc -> doc.getString("date"))
                .orElse("No data");
    }
}