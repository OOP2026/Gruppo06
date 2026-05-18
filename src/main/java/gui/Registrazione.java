package gui;

import javax.swing.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Registrazione {
    private JPanel RegisterPanel;

    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox AmministratoreCheck;
    private JTextField pinField;

    private JButton registratiButton;
    private JLabel AccediLabel;

    public Registrazione() {
        applicaStilePulsantiCentrali(registratiButton);
        applicaStileLabelLink(AccediLabel);

        registratiButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                effettuaRegistrazione();
            }
        });

        AccediLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tornaAlLogin();
            }
        });
    }

    private void effettuaRegistrazione() {
        String nome = nomeField.getText();
        String cognome = cognomeField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        boolean isAmministratore = AmministratoreCheck.isSelected();
        String pin = pinField.getText();

        if (nome.trim().isEmpty() || cognome.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Compila tutti i campi obbligatori (Nome, Cognome, Username, Password).", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isAmministratore && pin.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Inserisci il PIN per registrarti come Amministratore.", "Errore PIN", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(null, "Registrazione completata con successo!\nBenvenuto " + nome + " " + cognome);
        tornaAlLogin();
    }

    private void tornaAlLogin() {
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(RegisterPanel);
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        Login.main(null);
    }

    private void applicaStilePulsantiCentrali(JButton bottone) {
        Color coloreSfondoDefault = Color.WHITE; // Bianco di base
        Color coloreTestoDefault = Color.BLACK;  // Testo nero

        Color coloreSfondoHover = new Color(70, 132, 197); // Azzurro al passaggio del mouse
        Color coloreTestoHover = Color.WHITE;              // Testo bianco sull'azzurro

        impostaColoriEdEffetti(bottone, coloreSfondoDefault, coloreTestoDefault, coloreSfondoHover, coloreTestoHover);
    }

    private void impostaColoriEdEffetti(JButton bottone, Color sfondoDefault, Color testoDefault, Color sfondoHover, Color testoHover) {
        if (bottone == null) return;

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

        label.setForeground(new Color(70, 132, 197)); // Colore azzurro tipico dei link
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
            JFrame frame = new JFrame("Registrazione");
            Registrazione registrazioneLogic = new Registrazione();
            frame.setContentPane(registrazioneLogic.RegisterPanel);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 680);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}