package gui;

import javax.swing.*;

/**
 * La classe Registrazione gestisce l'interfaccia grafica per la creazione
 * di un nuovo account utente (Medico o Amministratore).
 * Fornisce i metodi per recuperare i dati inseriti e per agganciare i listener.
 */
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

    /**
     * Costruisce una nuova schermata di Registrazione, applicando
     * gli stili visivi ai componenti principali come bottoni e link.
     */
    public Registrazione() {
        Login.applicaStilePulsantiCentrali(registratiButton);
        Login.applicaStileLabelLink(accediLabel);
    }

    /**
     * Restituisce il nome inserito nel campo di testo.
     *
     * @return la stringa contenente il nome
     */
    public String getNome() {
        return nomeField.getText().trim();
    }

    /**
     * Restituisce il cognome inserito nel campo di testo.
     *
     * @return la stringa contenente il cognome
     */
    public String getCognome() {
        return cognomeField.getText().trim();
    }

    /**
     * Restituisce lo username scelto dall'utente.
     *
     * @return la stringa contenente lo username
     */
    public String getUsername() {
        return usernameField.getText().trim();
    }

    /**
     * Restituisce la password inserita dall'utente.
     *
     * @return la stringa contenente la password
     */
    public String getPassword() {
        return new String(passwordField.getPassword()).trim();
    }

    /**
     * Controlla se l'utente ha richiesto la registrazione con privilegi di amministratore.
     *
     * @return true se la checkbox "Amministratore" è selezionata, false altrimenti
     */
    public boolean isAdmin() {
        return amministratoreCheck.isSelected();
    }

    /**
     * Restituisce il PIN di sicurezza inserito per l'autorizzazione.
     *
     * @return la stringa contenente il PIN
     */
    public String getPin() {
        return pinField.getText().trim();
    }

    /**
     * Registra un listener per il pulsante di registrazione.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addRegisterListener(java.awt.event.ActionListener listener) {
        if (registratiButton != null) {
            registratiButton.addActionListener(listener);
        }
    }

    /**
     * Registra un listener per l'etichetta (link) di reindirizzamento al login.
     *
     * @param listener il comportamento da eseguire al click o interazione del mouse
     */
    public void addLoginListener(java.awt.event.MouseListener listener) {
        accediLabel.addMouseListener(listener);
    }

    /**
     * Mostra una finestra di dialogo (pop-up) con un messaggio per l'utente.
     *
     * @param title       il titolo della finestra di dialogo
     * @param message     il testo del messaggio da mostrare
     * @param messageType il tipo di messaggio (es. JOptionPane.ERROR_MESSAGE)
     */
    public void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(mainPanel, message, title, messageType);
    }
}