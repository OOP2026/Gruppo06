package gui;

import model.*;
import controller.Controller;
import javax.swing.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Registrazione {
    private JPanel registerPanel;

    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox amministratoreCheck;
    private JTextField pinField;
    private JTextField matricolaField; // assicurati di averla nel designer

    private JButton registratiButton;
    private JLabel accediLabel;

    // ── Controller condiviso ──────────────────────
    private static Controller controller = new Controller();

    public Registrazione() {
        applicaStilePulsantiCentrali(registratiButton);
        applicaStileLabelLink(accediLabel);

        registratiButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                effettuaRegistrazione();
            }
        });

        accediLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tornaAlLogin();
            }
        });
    }

    private void effettuaRegistrazione() {
        // ── Leggi i campi ─────────────────────────
        String nome     = nomeField.getText().trim();
        String cognome  = cognomeField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        boolean isAdmin = amministratoreCheck.isSelected();
        String pin      = pinField.getText().trim();
        String matricola = matricolaField != null ? matricolaField.getText().trim() : "";

        // ── Validazione campi obbligatori ─────────
        if (nome.isEmpty() || cognome.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Compila tutti i campi obbligatori (Nome, Cognome, Username, Password).",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isAdmin && pin.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Inserisci il PIN per registrarti come Amministratore.",
                    "Errore PIN", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ── Chiama il controller ──────────────────
        boolean successo = controller.registrazione(
                username, password, nome, cognome, pin, isAdmin, matricola
        );

        if (successo) {
            JOptionPane.showMessageDialog(null,
                    "Registrazione completata con successo!\nBenvenuto " + nome + " " + cognome,
                    "Successo", JOptionPane.INFORMATION_MESSAGE);
            tornaAlLogin();
        } else {
            // ── Distingui il tipo di errore ───────
            if (isAdmin && !pin.equals("1234")) {
                JOptionPane.showMessageDialog(null,
                        "PIN amministratore non valido!",
                        "Errore PIN", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Username '" + username + "' già esistente.\nScegli un username diverso.",
                        "Username duplicato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void tornaAlLogin() {
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(registerPanel);
        if (currentFrame != null) currentFrame.dispose();
        Login.main(null);
    }

    // ── Metodo per ottenere il controller (usato da altre classi) ──
    public static Controller getController() {
        return controller;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Registrazione");
            Registrazione r = new Registrazione();
            frame.setContentPane(r.registerPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 680);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}