import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;

import static com.mongodb.client.model.Filters.eq;

public class UserManagementWindow {
    private JTable userTable;
    private JButton addUserButton;
    private JButton removeUserButton;
    private JPanel UserManagmentPanel;

    public UserManagementWindow() {
        JFrame frame = new JFrame("User Management");
        frame.setContentPane(this.UserManagmentPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);

        //inicijalizacija tabele
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Username");
        tableModel.addColumn("Password");
        tableModel.addColumn("Role");
        tableModel.addColumn("Bio");


        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase db = mongoClient.getDatabase("PUJ2DB");
            MongoCollection<Document> users = db.getCollection("users");

            List<Document> userList = users.find().into(new Vector<>());
            for (Document user : userList) {
                tableModel.addRow(new Object[]{
                        user.getString("username"),
                        user.getString("password"),
                        user.getString("role"),
                        user.getString("bio")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        userTable.setModel(tableModel);

       // dodavanje action listener
        addUserButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(frame, "Enter username:");
            String password = JOptionPane.showInputDialog(frame, "Enter password:");
            String[] roles = {"Admin", "Korisnik"};
            String role = (String) JOptionPane.showInputDialog(frame, "Select role:", "Role Selection",
                    JOptionPane.QUESTION_MESSAGE, null, roles, roles[0]);
            String bio = JOptionPane.showInputDialog(frame, "Enter bio:");

            if (username != null && password != null && role != null) {
                try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
                    MongoDatabase db = mongoClient.getDatabase("PUJ2DB");
                    MongoCollection<Document> users = db.getCollection("users");

                    Document newUser = new Document("username", username)
                            .append("password", password)
                            .append("role", role)
                            .append("bio", bio != null ? bio : "");

                    users.insertOne(newUser);
                    tableModel.addRow(new Object[]{username, password, role, bio});
                    JOptionPane.showMessageDialog(frame, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error adding user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Username, password, and role are required.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        //brisanje usera
        removeUserButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow != -1) {
                String username = (String) tableModel.getValueAt(selectedRow, 0);

                try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
                    MongoDatabase db = mongoClient.getDatabase("PUJ2DB");
                    MongoCollection<Document> users = db.getCollection("users");

                    users.deleteOne(eq("username", username));
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(frame, "User removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error removing user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a user to remove.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }
}