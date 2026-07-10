package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

/**
 * Classe che rappresenta la schermata principale per un utente di tipo Medico.
 * Fornisce l'accesso a funzionalità come la gestione dei pazienti, dei letti,
 * delle prestazioni, dei turni e dell'agenda personale.
 */
public class SchermataMedico extends JFrame {
    public JPanel mainPanel;
    private JButton prestazioniButton;
    private JButton turniButton;
    private JLabel utenteLoggatoLabel;
    private JButton esciButton;
    private JButton pazientiButton;
    private JButton lettiButton;
    private JButton dimissioniButton;
    private JButton ricoveroButton;

    // Attributi per l'Agenda
    private JPanel agendaPanel;
    private JTextField ricercaField;
    private JButton ricercaButton;
    private  JTable agendaTable ;
    private JButton newEventButton;
    private JButton settimanaleButton;

    /**
     * Costruisce una nuova istanza della schermata del medico.
     *
     * @param nomeUtente Il nome dell'utente da visualizzare nell'etichetta del profilo.
     */
    public SchermataMedico(String nomeUtente) {
        if (utenteLoggatoLabel != null) {
            utenteLoggatoLabel.setText(" " + nomeUtente);
            utenteLoggatoLabel.setForeground(Color.WHITE);
            utenteLoggatoLabel.setFont(new Font("Arial", Font.BOLD, 14));
            utenteLoggatoLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            utenteLoggatoLabel.setToolTipText("Clicca per visualizzare e modificare il tuo profilo");

            utenteLoggatoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    utenteLoggatoLabel.setForeground(new Color(173, 216, 230)); // Azzurro chiaro
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    utenteLoggatoLabel.setForeground(Color.WHITE);
                }
            });
        }

        if (agendaPanel != null && agendaPanel.getBorder() instanceof javax.swing.border.TitledBorder) {
            ((javax.swing.border.TitledBorder) agendaPanel.getBorder()).setTitleColor(Color.WHITE);
        }

        applicaStiliAiPulsanti();

        Login.setupAgendaTableStyle(agendaTable);
    }

    /**
     * Aggiunge un listener al pulsante "Pazienti".
     * @param listener l'ActionListener da aggiungere.
     */
    public void addPazientiListener(ActionListener listener) {
        if (pazientiButton != null) pazientiButton.addActionListener(listener);
    }

    /**
     * Aggiunge un listener al pulsante "Letti".
     * @param listener l'ActionListener da aggiungere.
     */
    public void addLettiListener(ActionListener listener) {
        if (lettiButton != null) lettiButton.addActionListener(listener);
    }

    /**
     * Aggiunge un listener al pulsante "Prestazioni".
     * @param listener l'ActionListener da aggiungere.
     */
    public void addPrestazioniListener(ActionListener listener) {
        if (prestazioniButton != null) prestazioniButton.addActionListener(listener);
    }

    /**
     * Aggiunge un listener al pulsante "Dimissioni".
     * @param listener l'ActionListener da aggiungere.
     */
    public void addDimissioniListener(ActionListener listener) {
        if (dimissioniButton != null) dimissioniButton.addActionListener(listener);
    }

    /**
     * Aggiunge un listener al pulsante "Ricovero".
     * @param listener l'ActionListener da aggiungere.
     */
    public void addRicoveroListener(ActionListener listener) {
        if (ricoveroButton != null) ricoveroButton.addActionListener(listener);
    }

    /**
     * Aggiunge un listener al pulsante "Turni".
     * @param listener l'ActionListener da aggiungere.
     */
    public void addTurniListener(ActionListener listener) {
        if (turniButton != null) turniButton.addActionListener(listener);
    }

    /**
     * Aggiunge un listener al pulsante "Esci".
     * @param listener l'ActionListener da aggiungere.
     */
    public void addEsciListener(ActionListener listener) {
        if (esciButton != null) esciButton.addActionListener(listener);
    }

    /**
     * Aggiunge un listener al pulsante di ricerca dell'agenda.
     * @param listener l'ActionListener da aggiungere.
     */
    public void addRicercaAgendaListener(ActionListener listener) {
        if (ricercaButton != null) ricercaButton.addActionListener(listener);
    }

    /**
     * Aggiunge un listener al pulsante "Nuovo Evento".
     * @param listener l'ActionListener da aggiungere.
     */
    public void addNewEventListener(ActionListener listener) {
        if (newEventButton != null) newEventButton.addActionListener(listener);
    }

    /**
     * Aggiunge un listener al pulsante "Settimanale".
     * @param listener l'ActionListener da aggiungere.
     */
    public void addSettimanaleListener(ActionListener listener) {
        if (settimanaleButton != null) settimanaleButton.addActionListener(listener);
    }

    /**
     * Aggiunge un listener per il click sull'etichetta del profilo utente.
     * @param listener il MouseAdapter da aggiungere.
     */
    public void addProfiloListener(java.awt.event.MouseAdapter listener) {
        if (utenteLoggatoLabel != null) utenteLoggatoLabel.addMouseListener(listener);
    }

    public void updateUtenteLoggatoLabel(String nomeUtente) {
        if (utenteLoggatoLabel != null) {
            utenteLoggatoLabel.setText(" " + nomeUtente);
        }
    }

    /**
     * Aggiorna la tabella dell'agenda con i dati forniti.
     * @param dati una matrice di oggetti contenente i dati degli eventi da visualizzare.
     */
    public void aggiornaAgenda(Object[][] dati) {
        if (agendaTable != null) {
            DefaultTableModel model = (DefaultTableModel) agendaTable.getModel();
            model.setRowCount(0); // Svuota la tabella dai vecchi dati
            for (Object[] riga : dati) {
                model.addRow(riga);
            }
        }
    }

    /**
     * Restituisce il testo inserito nel campo di ricerca per passarlo al Controller
     * @return il testo di ricerca inserito dall'utente.
     */
    public String getTestoRicercaAgenda() {
        return (ricercaField != null) ? ricercaField.getText().trim() : "";
    }

    /**
     * Metodo privato per raggruppare e applicare gli stili ai pulsanti della UI.
     */
    private void applicaStiliAiPulsanti() {
        JButton[] menuLateraleButtons = {prestazioniButton, turniButton, esciButton};
        for (JButton button : menuLateraleButtons) {
            Login.applicaStileMenuLaterale(button);
        }

        JButton[] pulsantiCentraliButtons = {ricercaButton, newEventButton, settimanaleButton, pazientiButton, lettiButton, dimissioniButton, ricoveroButton};
        for (JButton button : pulsantiCentraliButtons) {
            Login.applicaStilePulsantiCentrali(button);
        }
    }

    /**
     * Metodo helper per aggiungere un ActionListener a un pulsante,
     * controllando che il pulsante non sia nullo.
     * @param button il pulsante a cui aggiungere il listener.
     * @param listener l'ActionListener da aggiungere.
     */
    private void aggiungiListener(JButton button, ActionListener listener) {
        if (button != null) {
            button.addActionListener(listener);
        }
    }
}