import javax.swing.*;
import java.awt.*;

public class MainMenuWindow extends JFrame {

    private JButton button1;
    private JPanel mainPanel;

    public MainMenuWindow(String username, String role) {
        setTitle("Main Dashboard : " + role);
        setSize(900, 600);
        //setLocationRelativeTo(null); ne treba mi na sredini
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel navbar = createNavbar(username, role);
        add(navbar, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 40));

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

        JButton MealPlanner= new JButton("Meal Planner");
        MealPlanner.setPreferredSize(new Dimension(200, 60));
        buttonPanel.add(MealPlanner);

        toDo.addActionListener(e -> new ToDoListWindow(username));

        add(buttonPanel, BorderLayout.CENTER);
        setVisible(true);

        profile.addActionListener(e -> new ProfileWindow(username, role));

        addRemoveUsers.addActionListener(e -> {
            if (!role.equals("Admin")) {
                JOptionPane.showMessageDialog(this, "Access Denied: This is an admin function", "Access Denied", JOptionPane.ERROR_MESSAGE);
            } else {
                new UserManagementWindow();
            }
        });

        financeTracker.addActionListener(e -> new FinanceTrackerWindow(username));

        viewUsers.addActionListener(e -> {
            if (!role.equals("Admin")) {
                JOptionPane.showMessageDialog(this, "Access Denied: This is an admin function", "Access Denied", JOptionPane.ERROR_MESSAGE);
            } else {
                new Users();
            }
        });
        MealPlanner.addActionListener(e -> new MealPlannerWindow(username));

        sleepSchedule.addActionListener(e -> new SleepScheduleWindow(username));
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
            dispose();
            new LoginWindow();
        });

        JButton bgColorButton = new JButton("Background Color");
        bgColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Background Color", getBackground());
            if (newColor != null) {
                updateComponentColors(this.getContentPane(), newColor, navbar);
            }
        });

        JButton navbarColorButton = new JButton("Navbar Color");
        navbarColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Navbar Color", navbar.getBackground());
            if (newColor != null) {
                navbar.setBackground(newColor);
                for (Component component : navbar.getComponents()) {
                    if (component instanceof JButton) {
                        component.setBackground(newColor);
                    }
                }
            }
        });

        JButton colors = new JButton("Colors");
        colors.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Background Color", getBackground());
            if (newColor != null) {
                updateComponentColors(this.getContentPane(), newColor, navbar);
            }
        });

        styleNavButton(homeBtn);
        styleNavButton(usersBtn);
        styleNavButton(colors);
        styleNavButton(navbarColorButton);
        styleNavButton(logoutBtn);

        navbar.add(greetingLabel);
        navbar.add(homeBtn);
        navbar.add(usersBtn);
        navbar.add(colors);
        navbar.add(navbarColorButton);
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

    private void updateComponentColors(Container container, Color color, Component exclude) {
        if (container != exclude) {
            container.setBackground(color);
            for (Component component : container.getComponents()) {
                if (component instanceof Container) {
                    updateComponentColors((Container) component, color, exclude);
                }
                if (component != exclude) {
                    component.setBackground(color);
                }
            }
        }
    }
}