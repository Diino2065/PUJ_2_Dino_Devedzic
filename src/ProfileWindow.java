import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;

public class ProfileWindow {

    private JButton SaveButton;
    private JTextArea Bio;
    private JTextField UsernameTxt;
    private JTextField NewUsernameTxt;
    private JLabel ProfilePictureLabel;
    private JButton UploadPictureButton;
    private JPasswordField PasswordTxt;
    private JPanel panel1;

    public ProfileWindow(String username, String role) {


        JFrame frame = new JFrame("Profile : " + username + " (" + role + ")");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 600);

        UsernameTxt.setEditable(false);
        UsernameTxt.setText(username);

        // load odnosno fetch
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase db = mongoClient.getDatabase("PUJ2DB");
            MongoCollection<Document> users = db.getCollection("users");

            Document user = users.find(new Document("username", username)).first();
            if (user != null) {
                String bio = user.getString("bio");
                Bio.setText(bio != null ? bio : "");

                String profilePicturePath = user.getString("profilePicture");
                if (profilePicturePath != null) {
                    ImageIcon imageIcon = new ImageIcon(new ImageIcon(profilePicturePath).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                    ProfilePictureLabel.setIcon(imageIcon);
                    ProfilePictureLabel.setText("");
                }
            } else {
                Bio.setText("");
            }
            Bio.setEditable(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching user data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        UploadPictureButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "png", "jpeg", "gif"));
            int result = fileChooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                try {
                    ImageIcon imageIcon = new ImageIcon(new ImageIcon(file.getAbsolutePath()).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                    ProfilePictureLabel.setIcon(imageIcon);
                    ProfilePictureLabel.setText("");

                    // slika
                    try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
                        MongoDatabase db = mongoClient.getDatabase("PUJ2DB");
                        MongoCollection<Document> users = db.getCollection("users");

                        users.updateOne(new Document("username", username),
                                new Document("$set", new Document("profilePicture", file.getAbsolutePath())));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error loading image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        SaveButton.addActionListener(e -> {
            String updatedBio = Bio.getText().trim();
            try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
                MongoDatabase db = mongoClient.getDatabase("PUJ2DB");
                MongoCollection<Document> users = db.getCollection("users");

                Document updateFields = new Document("bio", updatedBio);

                String newUsername = NewUsernameTxt.getText().trim();
                if (!newUsername.isEmpty() && !newUsername.equals(username)) {
                    updateFields.append("username", newUsername);
                }

                String newPassword = new String(PasswordTxt.getPassword()).trim();
                if (!newPassword.isEmpty()) {
                    updateFields.append("password", newPassword);
                }

                users.updateOne(new Document("username", username), new Document("$set", updateFields));

                JOptionPane.showMessageDialog(frame, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving profile: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setContentPane(panel1);
        frame.setVisible(true);
    }


}