import javax.swing.*;

public class Main {
    public static void main(String[] args) {


       // LoginWindow loginWindow = new LoginWindow();


        JFrame frame = new JFrame("Login");
        frame.setContentPane(new LoginWindow().LoginWindow);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(450, 400);
        frame.setVisible(true);

    }
}