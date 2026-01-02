import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;

public class ProfileWindow {

    private JButton SaveButton;
    private JPanel panel1;
    private JTextArea Bio;
    private JTextField UsernameTxt;

    public ProfileWindow(String username, String role) {
        JFrame frame = new JFrame("Profile - " + username + " (" + role + ")");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);

        // prikaz user u txtfield
        UsernameTxt.setText(username);
        UsernameTxt.setEditable(false);

        // fetcha bio ako ima da prikaze u text area
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase db = mongoClient.getDatabase("PUJ2DB");
            MongoCollection<Document> users = db.getCollection("users");

            Document user = users.find(new Document("username", username)).first();
            if (user != null) {
                String bio = user.getString("bio");
                Bio.setText(bio != null ? bio : ""); // empty ako nema
            } else {
                Bio.setText(""); // omogucava unos
            }
            Bio.setEditable(true); // mora editable
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching user data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }


        SaveButton.addActionListener(e -> {
            String updatedBio = Bio.getText().trim();
            try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
                MongoDatabase db = mongoClient.getDatabase("PUJ2DB");
                MongoCollection<Document> users = db.getCollection("users");

                users.updateOne(new Document("username", username),
                        new Document("$set", new Document("bio", updatedBio)));

                JOptionPane.showMessageDialog(frame, "Bio updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving bio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }
}