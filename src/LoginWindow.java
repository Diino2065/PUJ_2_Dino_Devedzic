
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;



public class LoginWindow extends Component {
    private JTextField TxtUsername;
    private JPasswordField txtPassword;
    private JButton loginButton;
    private JButton registerButton;
    JPanel LoginWindow;
    private JComboBox roleComboBox;

    public LoginWindow() {


        loginButton.addActionListener(e -> {
            String username = TxtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String selectedRole = roleComboBox.getSelectedItem().toString();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter username and password.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
                MongoDatabase db = mongoClient.getDatabase("PUJ2DB");
                MongoCollection<Document> users = db.getCollection("users");

                Document user = users.find(new Document("username", username)
                        .append("password", password)).first();

                if (user == null) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid username or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String role = user.getString("role");

                JOptionPane.showMessageDialog(this,
                        "Welcome " + username,
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE);



               //checka top frame da zatvori login prozor
                new MainMenuWindow(username, role);
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (topFrame != null) {
                    topFrame.dispose();
                }



            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Login error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });


        registerButton.addActionListener(e -> {
            JFrame registerFrame = new JFrame("Register");
            RegisterWindow registerWindow = new RegisterWindow();
            registerFrame.setContentPane(registerWindow.RegisterWindow);
            registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            registerFrame.setSize(450, 400);
            registerFrame.setVisible(true);
        });
    }
}
