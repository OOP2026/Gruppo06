package gui;

import javax.swing.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Registrazione {
    public JPanel registerPanel;

    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox amministratoreCheck;
    private JTextField pinField;

    private JButton registratiButton;
    private JLabel accediLabel;

    public Registrazione() {
        applicaStilePulsantiCentrali(registratiButton);
        applicaStileLabelLink(accediLabel);
    }

    public String getNome() {
        return nomeField.getText().trim();
    }

    public String getCognome() {
        return cognomeField.getText().trim();
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword()).trim();
    }

    public boolean isAdmin() {
        return amministratoreCheck.isSelected();
    }

    public String getPin() {
        return pinField.getText().trim();
    }

    public void addRegisterListener(java.awt.event.ActionListener listener) {
        registratiButton.addActionListener(listener);
    }

    public void addLoginListener(java.awt.event.MouseListener listener) {
        accediLabel.addMouseListener(listener);
    }

    public void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(registerPanel, message, title, messageType);
    }

    private void applicaStilePulsantiCentrali(JButton bottone) {
        Color sfondoDefault = Color.WHITE;
        Color testoDefault  = Color.BLACK;
        Color sfondoHover   = new Color(70, 132, 197);
        Color testoHover    = Color.WHITE;
        impostaColoriEdEffetti(bottone, sfondoDefault, testoDefault, sfondoHover, testoHover);
    }

    private void impostaColoriEdEffetti(JButton bottone, Color sfondoDefault, Color testoDefault,
                                        Color sfondoHover, Color testoHover) {
        if (bottone == null) return;
        bottone.setBackground(sfondoDefault);
        bottone.setForeground(testoDefault);
        bottone.setFocusPainted(false);
        bottone.setBorderPainted(false);
        bottone.setContentAreaFilled(true);
        bottone.setOpaque(true);
        bottone.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottone.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                bottone.setBackground(sfondoHover);
                bottone.setForeground(testoHover);
            }
            @Override public void mouseExited(MouseEvent e) {
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
            @Override public void mouseEntered(MouseEvent e) {
                String testo = label.getText().replace("<html><u>","").replace("</u></html>","");
                label.setText("<html><u>" + testo + "</u></html>");
            }
            @Override public void mouseExited(MouseEvent e) {
                String testo = label.getText().replace("<html><u>","").replace("</u></html>","");
                label.setText(testo);
            }
        });
    }
}