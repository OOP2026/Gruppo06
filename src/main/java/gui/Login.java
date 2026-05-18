package gui;

import javax.swing.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login {
    private JPasswordField passwordField;
    private JButton accediButton;
    private JPanel mainPanel;
    private JTextField usernameField;
    private JLabel RegistratiLabel;


    private final String userMedico = "123";
    private final String passMedico = "123";
    private final String nomeCompletoMedico = "Dott. Mario Rossi";

    public Login() {
        applicaStilePulsantiCentrali(accediButton);
        applicaStileLabelLink(RegistratiLabel);

        accediButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());


                if (username.equals(userMedico) && password.equals(passMedico)) {
                    JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(accediButton);
                    currentFrame.dispose();

                    Schermata_Amministratore schermataAmministratoreFrame = new Schermata_Amministratore(nomeCompletoMedico);
                    schermataAmministratoreFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Credenziali errate. Riprova.");
                }
            }
        });

        RegistratiLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                vaiAllaRegistrazione();
            }
        });
    }

    private void vaiAllaRegistrazione() {
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        Registrazione.main(null); // Richiama il main della classe Registrazione
    }


    private void applicaStilePulsantiCentrali(JButton bottone) {
        Color coloreSfondoDefault = Color.WHITE; // Bianco di base
        Color coloreTestoDefault = Color.BLACK;  // Testo nero per essere leggibile sul bianco

        Color coloreSfondoHover = new Color(70, 132, 197); // Azzurro al passaggio del mouse
        Color coloreTestoHover = Color.WHITE;              // Testo bianco sull'azzurro

        impostaColoriEdEffetti(bottone, coloreSfondoDefault, coloreTestoDefault, coloreSfondoHover, coloreTestoHover);
    }

    private void impostaColoriEdEffetti(JButton bottone, Color sfondoDefault, Color testoDefault, Color sfondoHover, Color testoHover) {
        bottone.setBackground(sfondoDefault);
        bottone.setForeground(testoDefault);
        bottone.setFocusPainted(false);
        bottone.setBorderPainted(false);
        bottone.setContentAreaFilled(true);
        bottone.setOpaque(true);
        bottone.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottone.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bottone.setBackground(sfondoHover);
                bottone.setForeground(testoHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bottone.setBackground(sfondoDefault);
                bottone.setForeground(testoDefault);
            }
        });
    }

    private void applicaStileLabelLink(JLabel label) {
        if (label == null) return;

        label.setForeground(new Color(70, 132, 197)); // Azzurro
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                String testo = label.getText().replace("<html><u>", "").replace("</u></html>", "");
                label.setText("<html><u>" + testo + "</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                String testo = label.getText().replace("<html><u>", "").replace("</u></html>", "");
                label.setText(testo);
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
