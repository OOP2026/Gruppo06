package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionListener;


/**
 * La classe Letti gestisce l'interfaccia grafica per la visualizzazione,
 * la ricerca e l'assegnazione dei letti all'interno della struttura ospedaliera.
 * Estende JFrame e fornisce i metodi necessari per interagire con il controller.
 */
public class Letti extends JFrame {
    public JPanel mainPanel;
    private JRadioButton tuttiRadioButton;
    private JRadioButton disponibileRadioButton;
    private JRadioButton occupatoRadioButton;
    private JList<String> repartoList;
    private JButton cercaButton;
    private JButton resetButton;
    private JTable lettiTable;
    private JButton assegnaPazienteButton;
    private JButton storicoLettiButton;
    private JTextField stanzaField;
    private JTextField pazienteField;

    private static final String[] COLONNE = {
            "Numero Letto", "Stanza", "Nome Paziente", "Codice Fiscale", "Reparto", "Stato"
    };

    /**
     * Costruisce una nuova istanza della schermata Letti,
     * inizializzando i componenti grafici e applicando gli stili visivi.
     */
    public Letti() {
        initComponents();
        setupStyles();
    }

    /**
     * Inizializza i componenti principali dell'interfaccia, configurando in particolare
     * il modello della tabella dei letti e raggruppando i filtri di selezione dello stato.
     */
    private void initComponents() {
        DefaultTableModel model = new DefaultTableModel(new Object[0][0], COLONNE) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        lettiTable.setModel(model);

        ButtonGroup statoLettoGroup = new ButtonGroup();
        statoLettoGroup.add(tuttiRadioButton);
        statoLettoGroup.add(disponibileRadioButton);
        statoLettoGroup.add(occupatoRadioButton);
        tuttiRadioButton.setSelected(true);
    }

    /**
     * Applica gli stili personalizzati ai componenti dell'interfaccia utente,
     * inclusi i colori dei pulsanti, delle liste e l'allineamento del testo in tabella.
     */
    private void setupStyles() {
        Login.styleList(repartoList);
        Login.setupTableStyle(lettiTable);
        Login.applicaStilePulsantiCentrali(cercaButton);
        Login.applicaStilePulsantiCentrali(resetButton);
        Login.applicaStilePulsantiCentrali(assegnaPazienteButton);
        Login.applicaStilePulsantiCentrali(storicoLettiButton);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < lettiTable.getColumnCount(); i++) {
            lettiTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    /**
     * Registra un listener per il pulsante di assegnazione di un paziente a un letto.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addAssegnaPazienteListener(ActionListener listener) {
        assegnaPazienteButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di ricerca dei letti.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addCercaListener(ActionListener listener) {
        cercaButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di reset dei filtri.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addResetListener(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di visualizzazione dello storico.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addStoricoLettiListener(ActionListener listener) {
        storicoLettiButton.addActionListener(listener);
    }

    /**
     * Restituisce lo stato del letto correntemente selezionato tramite radio button.
     *
     * @return una stringa rappresentante lo stato ("Libero", "Occupato" o "Tutti")
     */
    public String getSelectedStato() {
        if (disponibileRadioButton.isSelected()) return "Libero";
        if (occupatoRadioButton.isSelected()) return "Occupato";
        return "Tutti";
    }

    /**
     * Restituisce il reparto correntemente selezionato dalla lista di filtraggio.
     *
     * @return il nome del reparto selezionato, oppure null se non è stata effettuata alcuna selezione
     */
    public String getSelectedReparto() {
        return repartoList.getSelectedValue();
    }

    /**
     * Restituisce la stanza inserita nel campo di ricerca.
     *
     * @return la stringa inserita nel campo di testo dedicato alla stanza
     */
    public String getStanza() {
        return stanzaField.getText();
    }

    /**
     * Restituisce il nome del paziente inserito nel campo di ricerca.
     *
     * @return la stringa inserita nel campo di testo dedicato al paziente
     */
    public String getPaziente() {
        return pazienteField.getText();
    }

    /**
     * Resetta tutti i campi di ricerca e i filtri ai loro valori di default.
     */
    public void resetCampiRicerca() {
        tuttiRadioButton.setSelected(true);
        repartoList.clearSelection();
        stanzaField.setText("");
        pazienteField.setText("");
    }

    /**
     * Identifica e restituisce l'ID del letto selezionato nella tabella.
     *
     * @return l'ID del letto come stringa, oppure null se non è stata selezionata alcuna riga
     */
    public String getIdLettoSelezionato() {
        int rigaSelezionata = lettiTable.getSelectedRow();
        if (rigaSelezionata == -1) {
            return null;
        }
        return (String) lettiTable.getValueAt(rigaSelezionata, 0);
    }

    /**
     * Identifica e restituisce il reparto associato al letto selezionato nella tabella.
     *
     * @return il nome del reparto come stringa, oppure null se non è stata selezionata alcuna riga
     */
    public String getRepartoLettoSelezionato() {
        int rigaSelezionata = lettiTable.getSelectedRow();
        if (rigaSelezionata == -1) {
            return null;
        }
        return (String) lettiTable.getValueAt(rigaSelezionata, 4);
    }

    /**
     * Aggiorna il contenuto della tabella dei letti con i nuovi dati forniti.
     *
     * @param dati matrice di oggetti contenente i record dei letti prelevati dal database
     */
    public void aggiornaTabella(Object[][] dati) {
        DefaultTableModel model = (DefaultTableModel) lettiTable.getModel();
        model.setRowCount(0);
        if (dati != null) {
            for (Object[] riga : dati) {
                model.addRow(riga);
            }
        }
    }

    /**
     * Popola la lista dei reparti con i dati forniti dinamicamente.
     *
     * @param reparti lista di stringhe rappresentanti i nomi dei reparti.
     */
    public void setRepartiListData(java.util.List<String> reparti) {
        if (repartoList != null) {
            DefaultListModel<String> model = new DefaultListModel<>();
            for (String reparto : reparti) {
                model.addElement(reparto);
            }
            repartoList.setModel(model);
        }
    }
}