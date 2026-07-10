package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Calendar;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * La classe Dimissioni gestisce l'interfaccia grafica per la ricerca, visualizzazione
 * e archiviazione delle dimissioni dei pazienti all'interno della struttura ospedaliera.
 * Estende JFrame e definisce i metodi per interagire con il controller.
 */
public class Dimissioni extends JFrame {

    public JPanel mainPanel;
    private JTextField codiceFiscaleField;
    private JList<String> repartoList;
    private JList<String> tipoDimissioneList;
    private JSpinner dataDimissioneSpinner;
    private JButton cercaButton;
    private JButton resetButton;
    private JTable pazientiTable;
    private JButton letturaDimissioneButton;
    private JButton archiviaDimissioneButton;
    private JTextField nomeCognomeField;

    private static final String[] COLONNE = {
            "ID Ricovero", "Paziente", "Codice Fiscale",
            "Reparto Dimissione", "Tipo Dimissione", "Data Dimissione"
    };

    private static final String[] REPARTI_LIST_DATA = {
            "Chirurgia generale", "Ortopedia", "Cardiologia"
    };

    private static final String[] TIPO_DIMISSIONE_LIST_DATA = {
            "Ordinaria", "Trasferimento", "Volontaria", "Decesso"
    };

    /**
     * Costruisce una nuova istanza della schermata Dimissioni,
     * inizializzando i componenti grafici e applicando gli stili visivi.
     */
    public Dimissioni() {
        initComponents();
        setupStyles();
    }

    /**
     * Aggiorna il contenuto della tabella delle dimissioni con i nuovi dati forniti.
     * La colonna relativa all'ID Ricovero viene nascosta all'utente per una visualizzazione più pulita.
     *
     * @param dati matrice di oggetti contenente i record delle dimissioni prelevati dal database
     */
    public void aggiornaTabella(Object[][] dati) {
        if (pazientiTable != null) {
            DefaultTableModel model = (DefaultTableModel) pazientiTable.getModel();
            model.setRowCount(0);

            pazientiTable.getColumnModel().getColumn(0).setMinWidth(0);
            pazientiTable.getColumnModel().getColumn(0).setMaxWidth(0);
            pazientiTable.getColumnModel().getColumn(0).setWidth(0);

            if (dati != null) {
                for (Object[] riga : dati) {
                    model.addRow(riga);
                }
            }
        }
    }

    /**
     * Registra un listener per il pulsante di archiviazione di una dimissione.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addArchiviaDimissioneListener(ActionListener listener) {
        archiviaDimissioneButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di ricerca delle dimissioni.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addCercaListener(ActionListener listener) {
        cercaButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di reset dei campi di ricerca.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addResetListener(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di visualizzazione dei dettagli di una dimissione.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addLetturaDimissioneListener(ActionListener listener) {
        letturaDimissioneButton.addActionListener(listener);
    }

    /**
     * Restituisce il codice fiscale inserito nel relativo campo di testo.
     *
     * @return la stringa contenente il codice fiscale
     */
    public String getCodiceFiscale() {
        return codiceFiscaleField.getText();
    }

    /**
     * Restituisce il nome e cognome inseriti nel relativo campo di testo.
     *
     * @return la stringa contenente il nome e cognome del paziente
     */
    public String getNomeCognome() {
        return nomeCognomeField.getText();
    }

    /**
     * Restituisce il reparto correntemente selezionato dalla lista di ricerca.
     *
     * @return il nome del reparto selezionato, oppure null se non è stata effettuata alcuna selezione
     */
    public String getRepartoSelezionato() {
        return repartoList.getSelectedValue();
    }

    /**
     * Restituisce il tipo di dimissione correntemente selezionato dalla lista di ricerca.
     *
     * @return il tipo di dimissione selezionato, oppure null se non è stata effettuata alcuna selezione
     */
    public String getTipoDimissioneSelezionato() {
        return tipoDimissioneList.getSelectedValue();
    }

    /**
     * Estrae e restituisce la data di dimissione selezionata dal selettore.
     * Se il campo di testo associato è vuoto, la data viene ignorata.
     *
     * @return la data selezionata, oppure null se il campo è vuoto o non valido
     */
    public Date getDataSelezionata() {
        if (dataDimissioneSpinner != null && dataDimissioneSpinner.getEditor() instanceof JSpinner.DateEditor) {
            JSpinner.DateEditor editor = (JSpinner.DateEditor) dataDimissioneSpinner.getEditor();
            if (editor.getTextField().getText().trim().isEmpty()) {
                return null;
            }
        }
        return (Date) dataDimissioneSpinner.getValue();
    }

    /**
     * Identifica e restituisce il codice fiscale del paziente selezionato nella tabella.
     *
     * @return il codice fiscale del paziente, oppure null se non è stata selezionata alcuna riga
     */
    public String getCFPazienteSelezionato() {
        int selectedRow = pazientiTable.getSelectedRow();
        if (selectedRow != -1) {
            return (String) pazientiTable.getValueAt(selectedRow, 2);
        }
        return null;
    }

    /**
     * Identifica e restituisce l'ID del ricovero associato alla dimissione selezionata nella tabella.
     *
     * @return l'ID del ricovero come stringa, oppure null se non è stata selezionata alcuna riga
     */
    public String getIdRicoveroSelezionato() {
        int selectedRow = pazientiTable.getSelectedRow();
        if (selectedRow != -1) return (String) pazientiTable.getValueAt(selectedRow, 0);
        return null;
    }

    /**
     * Resetta tutti i campi di ricerca della schermata ai loro valori predefiniti
     * e avvia automaticamente un aggiornamento per mostrare la totalità dei risultati.
     *
     * @param cercaListener il listener di ricerca da invocare dopo la pulizia dei campi
     */
    public void resetCampiRicerca(ActionListener cercaListener) {
        if (codiceFiscaleField != null) codiceFiscaleField.setText("");
        if (nomeCognomeField != null) nomeCognomeField.setText("");
        if (repartoList != null) repartoList.clearSelection();
        if (tipoDimissioneList != null) tipoDimissioneList.clearSelection();

        if (dataDimissioneSpinner != null) {
            dataDimissioneSpinner.setValue(new Date());
            if (dataDimissioneSpinner.getEditor() instanceof JSpinner.DateEditor) {
                JSpinner.DateEditor editor = (JSpinner.DateEditor) dataDimissioneSpinner.getEditor();
                editor.getTextField().setValue(null);
                editor.getTextField().setText("");
            }
        }

        if (cercaListener != null) {
            cercaListener.actionPerformed(null);
        }
    }

    /**
     * Inizializza i componenti chiave dell'interfaccia, in particolare il modello
     * della tabella per l'elenco delle dimissioni e i modelli per le liste di ricerca.
     */
    private void initComponents() {
        DefaultTableModel model = new DefaultTableModel(new Object[0][0], COLONNE) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pazientiTable.setModel(model);

        pazientiTable.setAutoCreateRowSorter(true);

        repartoList.setListData(REPARTI_LIST_DATA);

        tipoDimissioneList.setListData(TIPO_DIMISSIONE_LIST_DATA);

        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dataDimissioneSpinner.setModel(dateModel);
        
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dataDimissioneSpinner, "yyyy-MM-dd");
        dataDimissioneSpinner.setEditor(editor);
        editor.getTextField().setValue(null);
        editor.getTextField().setText("");
    }
    /**
     * Applica gli stili visivi personalizzati a tabelle, liste e bottoni dell'interfaccia.
     */
    private void setupStyles() {
        Login.setupTableStyle(pazientiTable);
        Login.styleList(repartoList);
        Login.styleList(tipoDimissioneList);
        Login.applicaStilePulsantiCentrali(cercaButton);
        Login.applicaStilePulsantiCentrali(resetButton);
        Login.applicaStilePulsantiCentrali(letturaDimissioneButton);
        Login.applicaStilePulsantiCentrali(archiviaDimissioneButton);
    }
}