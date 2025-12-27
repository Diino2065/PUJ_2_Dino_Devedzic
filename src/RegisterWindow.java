import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;




public class RegisterWindow extends Component  {
    private JButton RegisterButton;
 public JPanel RegisterWindow; // public da bi ga prepoznao i loginWindow da moze da ga pozove
    private JPasswordField passwordField;
    private JTextField usernameField;
    private JComboBox roleComboBox;

    public RegisterWindow()  {

        RegisterButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = roleComboBox.getSelectedItem().toString();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Username and password are required.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
                MongoDatabase db = mongoClient.getDatabase("PUJ2DB");
                MongoCollection<Document> users = db.getCollection("users");


                Document existingUser = users.find(
                        new Document("username", username)
                ).first();

                if (existingUser != null) {
                    JOptionPane.showMessageDialog(this,
                            "Username already exists.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }


                Document newUser = new Document("username", username)
                        .append("password", password)
                        .append("role", role)
                        .append("active", false);

                users.insertOne(newUser);

                JOptionPane.showMessageDialog(this,
                        "Registration successful!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);



                //  dispose(); nece raditi  na ovaj nacin
               // SwingUtilities.getWindowAncestor(this).dispose(); // mora ipak citav frame nece samo panel


                // Login je parent pa mora se uvesti da bi se zatvorio taj prozor jer
                // sam dispose vraca null a ne moze se klasa extendati sa JFrame jer
                // je vec extendana Component i vraca null ako nije extendano JFrame
                 Window parentWindow = SwingUtilities.getWindowAncestor(this);
                if (parentWindow != null) {
                    parentWindow.dispose();
                }
                JFrame loginFrame = new JFrame("Login");
                loginFrame.setContentPane(new LoginWindow().LoginWindow);
                loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                loginFrame.setSize(450, 400);
                loginFrame.setVisible(true);


            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Registration failed: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }




}
