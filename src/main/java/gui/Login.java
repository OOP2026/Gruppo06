package gui;

import javax.swing.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login {
    // --- Costanti per lo stile ---
    private static final Color COLORE_PRIMARIO = new Color(70, 132, 197);
    private static final Color COLORE_TESTO_PULSANTE_DEFAULT = Color.BLACK;
    private static final Color COLORE_SFONDO_PULSANTE_DEFAULT = Color.WHITE;
    private static final Color COLORE_TESTO_PULSANTE_HOVER = Color.WHITE;
    // ---

    private JPasswordField passwordField;
    private JButton accediButton;
    public JPanel mainPanel;
    private JTextField usernameField;
    private JLabel registratiLabel;
    private JPasswordField pinField;



    public Login() {
        applicaStilePulsantiCentrali(accediButton);
        applicaStileLabelLink(registratiLabel);
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword()).trim();
    }

    public void addLoginListener(java.awt.event.ActionListener listener) {
        accediButton.addActionListener(listener);
    }

    public void addRegisterListener(java.awt.event.MouseListener listener) {
        registratiLabel.addMouseListener(listener);
    }

    public void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(mainPanel, message, title, messageType);
    }

    private void applicaStilePulsantiCentrali(JButton bottone) {
        impostaColoriEdEffetti(bottone, COLORE_SFONDO_PULSANTE_DEFAULT, COLORE_TESTO_PULSANTE_DEFAULT, COLORE_PRIMARIO, COLORE_TESTO_PULSANTE_HOVER);
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

        final String testoOriginale = label.getText();
        label.setForeground(COLORE_PRIMARIO);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Applica la sottolineatura usando HTML
                label.setText("<html><u>" + testoOriginale + "</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Ripristina il testo originale senza sottolineatura
                label.setText(testoOriginale);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Il Controller ora gestisce l'avvio dell'applicazione e le logiche!
            controller.Controller controller = new controller.Controller();
            controller.avvia();
        });
    }
}