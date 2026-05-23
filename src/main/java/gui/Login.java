package gui;

import javax.swing.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login {
    private JPasswordField passwordField;
    private JButton accediButton;
    public JPanel mainPanel;
    private JTextField usernameField;
    private JLabel RegistratiLabel;

    public Login() {
        applicaStilePulsantiCentrali(accediButton);
        applicaStileLabelLink(RegistratiLabel);

        accediButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                effettuaLogin();
            }
        });

        RegistratiLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                vaiAllaRegistrazione();
            }
        });
    }

    private void effettuaLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Se non c'è un campo matricola nella schermata, passiamo stringa vuota
        String matricola = "";

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Inserisci Username e Password per accedere.",
                    "Campi vuoti", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Chiamata al Controller condiviso per verificare l'utente
        boolean accessoRiuscito = Registrazione.getController().whoIsAsking(username, password, matricola);

        if (accessoRiuscito) {
            JOptionPane.showMessageDialog(null,
                    "Accesso eseguito con successo!",
                    "Benvenuto", JOptionPane.INFORMATION_MESSAGE);

            // 1. Chiude la finestra di login attuale
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
            if (currentFrame != null) {
                currentFrame.dispose();
            }

            // 2. Apre la schermata successiva passando l'username
            Schermata_Amministratore schermataAmministratoreFrame = new Schermata_Amministratore(username);
            schermataAmministratoreFrame.setVisible(true);

        } else {
            JOptionPane.showMessageDialog(null,
                    "Credenziali errate. Utente non trovato o password sbagliata.",
                    "Errore di accesso", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void vaiAllaRegistrazione() {
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        Registrazione.main(null); // Richiama il main della classe Registrazione
    }

    private void applicaStilePulsantiCentrali(JButton bottone) {
        Color coloreSfondoDefault = Color.WHITE;
        Color coloreTestoDefault = Color.BLACK;

        Color coloreSfondoHover = new Color(70, 132, 197);
        Color coloreTestoHover = Color.WHITE;

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

        label.setForeground(new Color(70, 132, 197));
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