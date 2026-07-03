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
            // Il Controller ora gestisce l'avvio dell'applicazione e le logiche!
            controller.Controller controller = new controller.Controller();
            controller.avvia();
        });
    }
}