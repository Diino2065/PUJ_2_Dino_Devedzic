import javax.swing.*;
import java.awt.*;

public class MainMenuWindow extends JFrame {

    private JButton button1;
    private JPanel mainPanel;

    public MainMenuWindow(String username, String role) {
        setTitle("Main Dashboard - " + role);
        setSize(900, 600);
       // setLocationRelativeTo(null); ne treba mi na sredini ekrana
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createNavbar(username, role), BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 40));

        JButton financeTracker = new JButton("Finance Tracker");
        financeTracker.setPreferredSize(new Dimension(200, 60));
        buttonPanel.add(financeTracker);

        JButton profile = new JButton("Profile");
        profile.setPreferredSize(new Dimension(200, 60));
        buttonPanel.add(profile);

        JButton sleepSchedule = new JButton("Sleep Schedule");
        sleepSchedule.setPreferredSize(new Dimension(200, 60));
        buttonPanel.add(sleepSchedule);

        JButton addRemoveUsers = new JButton("Add / Remove Users");
        addRemoveUsers.setPreferredSize(new Dimension(200, 60));
        buttonPanel.add(addRemoveUsers);

        JButton viewUsers = new JButton("View Users");
        viewUsers.setPreferredSize(new Dimension(200, 60));
        buttonPanel.add(viewUsers);

        JButton toDo = new JButton("To-Do List");
        toDo.setPreferredSize(new Dimension(200, 60));
        buttonPanel.add(toDo);




        add(buttonPanel, BorderLayout.CENTER);
        setVisible(true);

      profile.addActionListener(e -> {
            new ProfileWindow(username, role);

        });


        addRemoveUsers.addActionListener(e -> {
            if (!role.equals("Admin")) {
                JOptionPane.showMessageDialog(this, "Access Denied: This is an admin function", "Access Denied", JOptionPane.ERROR_MESSAGE);
            } else {
                new UserManagementWindow();
            }
        });

        financeTracker.addActionListener(e -> {
            new FinanceTrackerWindow(username);
        });


    }

    private JPanel createNavbar(String username, String role) {
        JPanel navbar = new JPanel();
        navbar.setPreferredSize(new Dimension(900, 60));
        navbar.setBackground(new Color(0, 0, 139));
        navbar.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 15));

        JLabel greetingLabel = new JLabel("Hello, " + username + " (" + role + ")");
        greetingLabel.setForeground(Color.WHITE);
        greetingLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton homeBtn = new JButton("Home");
        homeBtn.addActionListener(e -> {
            new MainMenuWindow(username, role);
            dispose();
        });
        JButton usersBtn = new JButton("Users");
        usersBtn.addActionListener(e -> {
            if (!role.equals("Admin")) {
                JOptionPane.showMessageDialog(this, "Access Denied: This is an admin function", "Access Denied", JOptionPane.ERROR_MESSAGE);
            } else {
                new Users();

            }
        });
        JButton logoutBtn = new JButton("Logout");

        logoutBtn.addActionListener(e -> {
            dispose(); // zatvara  prozor main menu
            new LoginWindow();
        });



        styleNavButton(homeBtn);
        styleNavButton(usersBtn);
        styleNavButton(logoutBtn);

        navbar.add(greetingLabel);
        navbar.add(homeBtn);
        navbar.add(usersBtn);
        navbar.add(logoutBtn);

        return navbar;
    }

    private void styleNavButton(JButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(26, 26, 180));
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }
}