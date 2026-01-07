import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FinanceTrackerWindow {
    private JTextField amountField;
    private JTextField descriptionField;
    private JComboBox typeCombo;
    private JButton addButton;
    private JPanel mainPanel;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel balanceLabel;
    private JComboBox categoryCombo;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton exportButton;
    private JTable transactionTable;
    private JLabel UserLabel;

    private String selectedTransactionId = null; // ID odabrane transakcije za update i delete null je jer nije nista odabrano
    private TransactionManager manager;


    public FinanceTrackerWindow(String username) {
        JFrame frame = new JFrame("Finance Tracker "+ username);
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
        exportButton.setBackground(Color.RED);
        exportButton.setForeground(Color.WHITE);

        UserLabel.setText("User: " + username);

        manager = new TransactionManager(username,username );


        typeCombo.addItem("Prihod");
        typeCombo.addItem("Rashod");


        categoryCombo.addItem("Plata");
        categoryCombo.addItem("Hrana");
        categoryCombo.addItem("Racuni");
        categoryCombo.addItem("Zabava");
        categoryCombo.addItem("Prijevoz");
        categoryCombo.addItem("Ostalo");

        loadDataIntoTable();
        updateSummary();


        addButton.addActionListener(e -> {
            try {
                String type = (String) typeCombo.getSelectedItem();
                String category = (String) categoryCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText();
                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Opis ne moze biti prazan.");
                    return;
                }

                Transaction t = new Transaction(type, amount, description, category);
                manager.addTransaction(t);
                loadDataIntoTable();
                updateSummary();
                amountField.setText("");
                descriptionField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Iznos mora biti broj");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error pri spremanju transakcije: " + ex.getMessage());
            }
        });


        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.getSelectionModel().addListSelectionListener((ListSelectionListener) e -> {
            int selectedRow = transactionTable.getSelectedRow();
            if (selectedRow >= 0) {
                Transaction selected = manager.getAllTransactions().get(selectedRow);
                selectedTransactionId = selected.getIdString();

                typeCombo.setSelectedItem(selected.getType());
                categoryCombo.setSelectedItem(selected.getCategory());
                amountField.setText(String.valueOf(selected.getAmount()));
                descriptionField.setText(selected.getDescription());
            }
        });


        updateButton.addActionListener(e -> {
            if (selectedTransactionId != null) {
                try {
                    String type = (String) typeCombo.getSelectedItem();
                    String category = (String) categoryCombo.getSelectedItem();
                    double amount = Double.parseDouble(amountField.getText());
                    String description = descriptionField.getText();
                    if (description.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Description cannot be empty.");
                        return;
                    }


                    manager.updateTransaction(selectedTransactionId, type, amount, description, category);

                    loadDataIntoTable();
                    updateSummary();

                    amountField.setText("");
                    descriptionField.setText("");
                    selectedTransactionId = null;

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Amount must be a number");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error updating transaction: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Odaberite transakciju za ažuriranje");
            }
        });


        deleteButton.addActionListener(e -> {
            int selectedRow = transactionTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(
                        mainPanel,
                        "Zelite izbrisati navedenu transakciju?",
                        "Potvrda brisanja",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    String transactionId = manager.getAllTransactions().get(selectedRow).getIdString();
                    manager.deleteTransaction(transactionId);

                    loadDataIntoTable();
                    updateSummary();

                    amountField.setText("");
                    descriptionField.setText("");
                    selectedTransactionId = null;
                }
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Odaberite transakciju za brisanje");
            }
        });


        exportButton.addActionListener(e -> {
            try {
                double totalIncome = manager.getTotalIncome();
                double totalExpense = manager.getTotalExpense();
                double balance = totalIncome - totalExpense;

                ArrayList<Transaction> transactions = manager.getAllTransactions();
                Map<String, Double> categorySums = new HashMap<>();
                for (Transaction t : transactions) {
                    String cat = t.getCategory() != null ? t.getCategory() : "Ostalo";
                    categorySums.put(cat, categorySums.getOrDefault(cat, 0.0) + t.getAmount());
                }

                StringBuilder sb = new StringBuilder();
                sb.append("Ukupni prihod: ").append(totalIncome).append("\n");
                sb.append("Ukupni rashod: ").append(totalExpense).append("\n");
                sb.append("Stanje: ").append(balance).append("\n");
                sb.append("Rashodi po kategorijama:\n");
                for (String cat : categorySums.keySet()) {
                    sb.append(cat).append(": ").append(categorySums.get(cat)).append("\n");
                }

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Spremi kao");
                int userSelection = fileChooser.showSaveDialog(mainPanel);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    try (FileWriter fw = new FileWriter(fileToSave)) {
                        fw.write(sb.toString());
                    }
                    JOptionPane.showMessageDialog(mainPanel, "Export izvrsen uspreno");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainPanel, "Greška pri exportu: " + ex.getMessage());
            }
        });
    }

    private void loadDataIntoTable() {
        ArrayList<Transaction> list = manager.getAllTransactions();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Type");
        model.addColumn("Amount");
        model.addColumn("Description");
        model.addColumn("Category");

        for (Transaction t : list) {
            model.addRow(new Object[]{
                    t.getType(),
                    t.getAmount(),
                    t.getDescription(),
                    t.getCategory()
            });
        }
        transactionTable.setModel(model);
    }

    private void updateSummary() {
        double income = manager.getTotalIncome();
        double expense = manager.getTotalExpense();
        double balance = income - expense;

        incomeLabel.setText("Income: " + income);
        expenseLabel.setText("Expense: " + expense);
        balanceLabel.setText("Balance: " + balance);
    }

    public JPanel getMainPanel() {

        return mainPanel;
    }

    }


