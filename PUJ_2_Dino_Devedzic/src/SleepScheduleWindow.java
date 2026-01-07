import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.util.List;
import java.awt.event.ActionEvent;

public class SleepScheduleWindow {

    private final SleepScheduleManager manager;

    public SleepScheduleWindow(String username) {
        manager = new SleepScheduleManager(username);

        JFrame frame = new JFrame("Sleep Schedule");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 600);

        JLabel dateLabel = new JLabel("Datum (DD-MM-YYYY):");
        JTextField dateField = new JTextField();
        dateField.setHorizontalAlignment(JTextField.LEFT);

        JLabel hoursLabel = new JLabel("Sati spavanja:");
        JTextField hoursField = new JTextField();
        hoursField.setHorizontalAlignment(JTextField.LEFT);

        JButton addButton = new JButton("Dodaj sleep data");
        JButton calculateButton = new JButton("Kalkulisi prosjek");
        JButton exportButton = new JButton("Export to PDF");
        exportButton.setBackground(Color.RED);
        exportButton.setForeground(Color.WHITE);

        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(dateLabel);
        inputPanel.add(dateField);
        inputPanel.add(hoursLabel);
        inputPanel.add(hoursField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        addButton.setPreferredSize(new Dimension(150, 30));
        calculateButton.setPreferredSize(new Dimension(150, 30));
        exportButton.setPreferredSize(new Dimension(150, 30));
        buttonPanel.add(addButton);
        buttonPanel.add(calculateButton);
        buttonPanel.add(exportButton);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(580, 300));

        frame.setLayout(new BorderLayout());
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);

        // fetch odnosno load
        loadSleepData(resultArea);

        addButton.addActionListener(e -> {
            String date = dateField.getText().trim();
            String hoursText = hoursField.getText().trim();
            try {
                double hours = Double.parseDouble(hoursText);
                manager.addSleepData(date, hours);
                resultArea.append("Dodano: " + date + " - " + hours + " sati\n");
                dateField.setText("");
                hoursField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Unesite validan broj sati.");
            }
        });

        calculateButton.addActionListener(e -> {
            double average = manager.getAverageSleep();
            String mostSleep = manager.getDayWithMostSleep();
            String leastSleep = manager.getDayWithLeastSleep();

            resultArea.append("\n Analiza spavanja \n");
            resultArea.append("Prosjek : " + average + " sati \n");
            resultArea.append("Dan sa najvise sna: " + mostSleep + "\n");
            resultArea.append("Dan sa najmanje sna: " + leastSleep + "\n");
        });

        exportButton.addActionListener(e -> {
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(username + "_sleep_schedule.pdf"));
                document.open();

                document.add(new Paragraph("Sleep schedule korisnika : " + username));
                document.add(new Paragraph(" "));

                PdfPTable table = new PdfPTable(2);
                table.addCell("Date");
                table.addCell("Hours Slept");

                List<org.bson.Document> sleepData = manager.getSleepData();
                for (org.bson.Document doc : sleepData) {
                    String date = doc.getString("date");
                    double hours = doc.getDouble("hours");
                    table.addCell(date);
                    table.addCell(String.valueOf(hours));
                }

                document.add(table);
                document.close();

                JOptionPane.showMessageDialog(frame, "Sleep schedule exported to PDF.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Greska pri eksportu PDF-a.");
            }
        });

        frame.setVisible(true);
    }

    private void loadSleepData(JTextArea resultArea) {
        List<org.bson.Document> sleepData = manager.getSleepData();
        if (sleepData.isEmpty()) {
            resultArea.append("Nema podataka : dodaj podatke \n");
        } else {
            resultArea.append("Postojeci podaci: \n");
            for (org.bson.Document doc : sleepData) {
                String date = doc.getString("date");
                double hours = doc.getDouble("hours");
                resultArea.append(date + " - " + hours + " sati\n");
            }
        }
    }
}