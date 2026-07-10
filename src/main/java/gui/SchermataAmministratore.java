package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

/**
 * La classe SchermataAmministratore gestisce l'interfaccia grafica principale
 * per l'amministratore. Estende JFrame e definisce i metodi per interagire 
 * con i vari sottomenù e per la visualizzazione dell'agenda.
 */
public class SchermataAmministratore extends JFrame {

    public JPanel mainPanel;
    private JButton pazientiButton;
    private JButton lettiButton;
    private JButton dimissioniButton;
    private JButton ricoveroButton;
    private JButton prestazioniButton;
    private JButton mediciButton;
    private JButton turniButton;
    private JLabel utenteLoggatoLabel;
    private JButton esciButton;

    private JPanel agendaPanel;
    private JTextField dataField;
    private JButton ricercaButton;
    private JTable agendaTable;
    private JButton settimanaleButton;
    private JButton newEventButton;

    /**
     * Costruisce una nuova istanza della schermata Amministratore, inizializzando
     * i componenti grafici, applicando gli stili e configurando l'etichetta dell'utente loggato.
     *
     * @param nomeUtente il nome dell'utente attualmente loggato
     */
    public SchermataAmministratore(String nomeUtente) {

        if (utenteLoggatoLabel != null) {
            utenteLoggatoLabel.setText(" " + nomeUtente);
            utenteLoggatoLabel.setForeground(Color.WHITE);
            utenteLoggatoLabel.setFont(new Font("Arial", Font.BOLD, 14));
            utenteLoggatoLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            utenteLoggatoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    utenteLoggatoLabel.setForeground(new Color(173, 216, 230));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    utenteLoggatoLabel.setForeground(Color.WHITE);
                }
            });
        }

        applicaStiliAiPulsanti();

        Login.setupAgendaTableStyle(agendaTable);
    }

    /**
     * Registra un listener per il pulsante di navigazione verso la gestione dei pazienti.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addPazientiListener(ActionListener listener) {
        this.pazientiButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di navigazione verso la gestione dei letti.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addLettiListener(ActionListener listener) {
        this.lettiButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di navigazione verso la gestione delle prestazioni.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addPrestazioniListener(ActionListener listener) {
        this.prestazioniButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di uscita dall'applicazione.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addEsciListener(ActionListener listener) {
        this.esciButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di navigazione verso la gestione dei medici.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addMediciListener(ActionListener listener) {
        this.mediciButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di navigazione verso la gestione delle dimissioni.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addDimissioniListener(ActionListener listener) {
        this.dimissioniButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di navigazione verso la gestione dei ricoveri.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addRicoveroListener(ActionListener listener) {
        this.ricoveroButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di navigazione verso la gestione dei turni.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addTurniListener(ActionListener listener) {
        this.turniButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di ricerca all'interno dell'agenda.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addRicercaAgendaListener(ActionListener listener) {
        this.ricercaButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di aggiunta di un nuovo evento nell'agenda.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addNewEventListener(ActionListener listener) {
        this.newEventButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di visualizzazione settimanale dell'agenda.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addSettimanaleListener(ActionListener listener) {
        this.settimanaleButton.addActionListener(listener);
    }

    /**
     * Registra un listener per l'interazione con l'etichetta del profilo utente.
     *
     * @param listener il comportamento da eseguire al click o interazione del mouse
     */
    public void addProfiloListener(MouseAdapter listener) {
        if (utenteLoggatoLabel != null) utenteLoggatoLabel.addMouseListener(listener);
    }

    /**
     * Aggiorna il testo dell'etichetta che mostra l'utente attualmente loggato.
     *
     * @param nomeUtente il nuovo nome utente da visualizzare
     */
    public void updateUtenteLoggatoLabel(String nomeUtente) {
        if (utenteLoggatoLabel != null) {
            utenteLoggatoLabel.setText(" " + nomeUtente);
        }
    }

    /**
     * Aggiorna il contenuto della tabella dell'agenda con i nuovi dati forniti.
     *
     * @param dati matrice di oggetti contenente i record dell'agenda prelevati dal database
     */
    public void aggiornaAgenda(Object[][] dati) {
        if (agendaTable != null && dati != null) {
            DefaultTableModel model = (DefaultTableModel) agendaTable.getModel();
            model.setRowCount(0);
            for (Object[] riga : dati) {
                model.addRow(riga);
            }
            model.fireTableDataChanged();
        }
    }

    /**
     * Restituisce il testo inserito nel campo di ricerca dell'agenda.
     *
     * @return la stringa inserita per la ricerca
     */
    public String getTestoRicercaAgenda() {
        return (dataField != null) ? dataField.getText().trim() : "";
    }

    /**
     * Applica gli stili visivi personalizzati ai pulsanti del menù laterale 
     * e ai pulsanti della sezione centrale dell'interfaccia.
     */
    private void applicaStiliAiPulsanti() {
        JButton[] menuLateraleButtons = {prestazioniButton, mediciButton, turniButton, esciButton};
        for (JButton button : menuLateraleButtons) {
            Login.applicaStileMenuLaterale(button);
        }

        JButton[] pulsantiCentraliButtons = {ricercaButton, newEventButton, settimanaleButton, pazientiButton, lettiButton, dimissioniButton, ricoveroButton};
        for (JButton button : pulsantiCentraliButtons) {
            Login.applicaStilePulsantiCentrali(button);
        }
    }
}