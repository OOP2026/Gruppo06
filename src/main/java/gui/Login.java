package gui;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login {
    private JPasswordField passwordField;
    private JTextField textField;
    private JButton accediButton;
    private JPanel mainPanel;

    private final String userMedico = "123";
    private final String passMedico = "123";
    private final String nomeCompletoMedico = "Dott. Mario Rossi";

    public Login() {
        accediButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String username = textField.getText();
                String password = new String(passwordField.getPassword());


                if (username.equals(userMedico) && password.equals(passMedico)) {
                    JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(accediButton);
                    currentFrame.dispose();

                    Home homeFrame = new Home(nomeCompletoMedico);
                    homeFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Credenziali errate. Riprova.");
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Login");
            Login loginLogic = new Login();
            frame.setContentPane(loginLogic.mainPanel);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 680);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
