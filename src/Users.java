import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;



public class Users {
    private JTable usersTable;
    private JPanel UsersPanel;
    private JButton exportButton;

    public Users() {
        JFrame frame = new JFrame("Users");
        frame.setContentPane(this.UsersPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // incijalizacij tabele
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Username");
        tableModel.addColumn("Role");
        tableModel.addColumn("Bio");

        // fetch korisnika
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase db = mongoClient.getDatabase("PUJ2DB");
            MongoCollection<org.bson.Document> users = db.getCollection("users");

            List<org.bson.Document> userList = users.find().into(new ArrayList<>());
            for (org.bson.Document user : userList) {
                tableModel.addRow(new Object[]{
                        user.getString("username"),
                        user.getString("role"),
                        user.getString("bio")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        usersTable.setModel(tableModel);


        exportButton.setBackground(Color.RED);
        exportButton.setForeground(Color.WHITE);


        exportButton.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save as PDF");
                int userSelection = fileChooser.showSaveDialog(null);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    String filePath = fileToSave.getAbsolutePath();

                    if (!filePath.endsWith(".pdf")) {
                        filePath += ".pdf";
                    }


                    Document pdfDocument = new Document();
                    PdfWriter.getInstance(pdfDocument, new FileOutputStream(filePath));
                    pdfDocument.open();

                    PdfPTable pdfTable = new PdfPTable(usersTable.getColumnCount());
                    for (int i = 0; i < usersTable.getColumnCount(); i++) {
                        pdfTable.addCell(usersTable.getColumnName(i));
                    }

                    for (int i = 0; i < usersTable.getRowCount(); i++) {
                        for (int j = 0; j < usersTable.getColumnCount(); j++) {
                            pdfTable.addCell(usersTable.getValueAt(i, j).toString());
                        }
                    }

                    pdfDocument.add(pdfTable);
                    pdfDocument.close();

                    JOptionPane.showMessageDialog(null, "Users exported successfully to " + filePath, "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error exporting users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }
}