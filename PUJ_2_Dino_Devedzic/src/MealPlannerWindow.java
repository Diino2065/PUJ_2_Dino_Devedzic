import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;

public class MealPlannerWindow {
    private JButton ButtonAdd;
    private JTextField danTxt;
    private JTextField hranaTtx;
    private JButton ExportButton;
    private JLabel UsernameLabel;
    private JTable tabelaHrana;
    private JPanel mainPanel;
    private JButton kalkulisiButton;
    private JButton IzbrisiButton;

    private MongoClient mongoClient;
    private MongoCollection<Document> mealPlannerCollection;

    public MealPlannerWindow(String username) {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("PUJ2DB");
        mealPlannerCollection = database.getCollection("mealplanner");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }));

        JFrame frame = new JFrame("Meal Planner: " + username);
        frame.setContentPane(mainPanel); // panel u formi definisam
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setVisible(true);

        UsernameLabel.setText(username);

        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Day", "Meal", "Calories"}, 0);
        tabelaHrana.setModel(tableModel);

        mealPlannerCollection.find(new Document("username", username)).forEach(document -> {
            String day = document.getString("day");
            String meal = document.getString("meal");
            int calories = document.getInteger("calories", 0);
            tableModel.addRow(new Object[]{day, meal, calories});
        });

        ButtonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String day = danTxt.getText().trim();
                String meal = hranaTtx.getText().trim();

                if (day.isEmpty() || meal.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter both day and meal.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String caloriesInput = JOptionPane.showInputDialog(frame, "Enter calories for the meal:");
                if (caloriesInput == null || caloriesInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Calories are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int calories = Integer.parseInt(caloriesInput.trim());

                tableModel.addRow(new Object[]{day, meal, calories});
                danTxt.setText("");
                hranaTtx.setText("");

                Document mealPlan = new Document("username", username)
                        .append("day", day)
                        .append("meal", meal)
                        .append("calories", calories);
                mealPlannerCollection.insertOne(mealPlan);
            }
        });

        IzbrisiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tabelaHrana.getSelectedRow();
                if (selectedRow != -1) {
                    String day = tableModel.getValueAt(selectedRow, 0).toString();
                    String meal = tableModel.getValueAt(selectedRow, 1).toString();

                    tableModel.removeRow(selectedRow);

                    mealPlannerCollection.deleteOne(new Document("username", username)
                            .append("day", day)
                            .append("meal", meal));
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a row to delete.", "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        ExportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Save PDF");
                    int userSelection = fileChooser.showSaveDialog(frame);

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                        if (!filePath.endsWith(".pdf")) {
                            filePath += ".pdf";
                        }

                        com.itextpdf.text.Document pdfDocument = new com.itextpdf.text.Document();
                        PdfWriter.getInstance(pdfDocument, new FileOutputStream(filePath));
                        pdfDocument.open();

                        PdfPTable pdfTable = new PdfPTable(3);
                        pdfTable.addCell("Day");
                        pdfTable.addCell("Meal");
                        pdfTable.addCell("Calories");

                        for (int i = 0; i < tabelaHrana.getRowCount(); i++) {
                            pdfTable.addCell(tabelaHrana.getValueAt(i, 0).toString());
                            pdfTable.addCell(tabelaHrana.getValueAt(i, 1).toString());
                            pdfTable.addCell(tabelaHrana.getValueAt(i, 2).toString());
                        }

                        pdfDocument.add(pdfTable);
                        pdfDocument.close();

                        JOptionPane.showMessageDialog(frame, "PDF exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error exporting to PDF: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        kalkulisiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalCalories = 0;
                for (int i = 0; i < tabelaHrana.getRowCount(); i++) {
                    totalCalories += Integer.parseInt(tabelaHrana.getValueAt(i, 2).toString());
                }
                JOptionPane.showMessageDialog(frame, "Total Calories: " + totalCalories, "Calories Calculation", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MealPlannerWindow("User"));
    }
}