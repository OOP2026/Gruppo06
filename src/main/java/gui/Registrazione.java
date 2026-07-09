package gui;

import javax.swing.*;

public class Registrazione {
    public JPanel mainPanel;

    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox amministratoreCheck;
    private JTextField pinField;

    private JButton registratiButton;
    private JLabel accediLabel;

    public Registrazione() {
        Login.applicaStilePulsantiCentrali(registratiButton);
        Login.applicaStileLabelLink(accediLabel);
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
        JOptionPane.showMessageDialog(mainPanel, message, title, messageType);
    }
}