import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ToDoListWindow {

    private final ToDoManager manager;
    private final DefaultListModel<String> listModel;

    public ToDoListWindow(String username) {
        manager = new ToDoManager(username);
        listModel = new DefaultListModel<>();

        JFrame frame = new JFrame("To-Do List: " + username);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 600);

        JList<String> toDoList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(toDoList);

        JTextField taskField = new JTextField();
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton exportButtontxt = new JButton("Export as txt");
        JButton exportButtonPDF = new JButton("Export as PDF");

        JPanel panel = new JPanel(new GridLayout(1, 4));
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(exportButtontxt);
        panel.add(exportButtonPDF);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(taskField, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.SOUTH);

        //ucitavanje tasks iz baze
        loadTasks();


        addButton.addActionListener(e -> {
            String task = taskField.getText().trim();
            if (!task.isEmpty()) {
                manager.addToDo(task);
                listModel.addElement(task);
                taskField.setText("");
            }
        });


        updateButton.addActionListener(e -> {
            String selectedTask = toDoList.getSelectedValue();
            String newTask = taskField.getText().trim();
            if (selectedTask != null && !newTask.isEmpty()) {
                manager.updateToDo(selectedTask, newTask);
                listModel.setElementAt(newTask, toDoList.getSelectedIndex());
                taskField.setText("");
            }
        });


        deleteButton.addActionListener(e -> {
            String selectedTask = toDoList.getSelectedValue();
            if (selectedTask != null) {
                manager.deleteToDo(selectedTask);
                listModel.removeElement(selectedTask);
            }
        });

        exportButtontxt.addActionListener(e -> {
            try (FileWriter writer = new FileWriter(username + "_todos.txt")) {
                for (int i = 0; i < listModel.size(); i++) {
                    writer.write(listModel.getElementAt(i) + "\n");
                }
                JOptionPane.showMessageDialog(frame, "To-Do lista exportovana");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error prilikom exportovanja " + ex.getMessage());
            }
        });

        exportButtonPDF.addActionListener(e -> {
            try {
                Document pdfDocument = new Document();
                PdfWriter.getInstance(pdfDocument, new FileOutputStream(username + "_todos.pdf"));
                pdfDocument.open();

                PdfPTable pdfTable = new PdfPTable(1);
                for (int i = 0; i < listModel.size(); i++) {
                    pdfTable.addCell(listModel.getElementAt(i));
                }

                pdfDocument.add(pdfTable);
                pdfDocument.close();

                JOptionPane.showMessageDialog(frame, "To-Do lista exporotvana");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error prilikom exporta " + ex.getMessage());
            }
        });



        frame.setVisible(true);
    }




    private void loadTasks() {
        ArrayList<String> todos = manager.getToDos();
        for (String task : todos) {
            listModel.addElement(task);
        }
    }
}