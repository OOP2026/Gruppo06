package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.util.Calendar;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe Ricovero gestisce l'interfaccia grafica per la visualizzazione,
 * la ricerca e la gestione dei ricoveri ospedalieri.
 * Estende JFrame e fornisce i metodi necessari per interagire con il controller.
 */
public class Ricovero extends JFrame {

    public JPanel mainPanel;
    private JTextField nomeField;
    private JTextField codiceField;
    private JTextField stanzaField;
    private JList<String> repartoList;
    private JSpinner dataSpinner;

    private JButton cercaButton;
    private JButton resetButton;
    private JTable ricoveriTable;

    private JButton nuovoRicoveroButton;
    private JButton gestisciRicoveroButton;
    private JButton gestisciDimissioneButton;

    private List<String> idRicoveriNascosti = new ArrayList<>();

    private static final String[] COLONNE = {
            "Codice Fiscale", "Paziente", "Stanza", 
            "Motivazione Ricovero", "Reparto di Ricovero", "Data e Ora Ingresso"
    };

    /**
     * Costruisce una nuova istanza della schermata Ricovero, inizializzando
     * i componenti grafici, i modelli di dati e applicando gli stili visivi.
     */
    public Ricovero() {
        initComponents();
        setupStyles();
		if (dataSpinner != null) {
			SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
			dataSpinner.setModel(dateModel);
			JSpinner.DateEditor editor = new JSpinner.DateEditor(dataSpinner, "yyyy-MM-dd");
			dataSpinner.setEditor(editor);
			editor.getTextField().setValue(null);
			editor.getTextField().setText("");
		}

		if (repartoList != null) {
			repartoList.setListData(new String[0]);
		}

		if (ricoveriTable != null) {
			DefaultTableModel model = new DefaultTableModel(new Object[0][0], COLONNE) {
				@Override public boolean isCellEditable(int row, int column) { return false; }
			};
			ricoveriTable.setModel(model);
		}
    }

    /**
     * Aggiorna il contenuto della tabella dei ricoveri con i nuovi dati forniti.
     * Nasconde l'ID reale del ricovero salvandolo in una lista interna per mantenere l'interfaccia pulita.
     *
     * @param dati matrice di oggetti contenente i record dei ricoveri prelevati dal database
     */
    public void aggiornaTabella(Object[][] dati) {
        idRicoveriNascosti.clear();
        DefaultTableModel model = (DefaultTableModel) ricoveriTable.getModel();
        model.setRowCount(0);
        if (dati != null) {
            for (Object[] riga : dati) {
                if (riga != null && riga.length >= 7) {
                    idRicoveriNascosti.add((String) riga[0]);
                    
                    Object[] rigaVisibile = new Object[6];
                    rigaVisibile[0] = riga[3];
                    rigaVisibile[1] = riga[1];
                    rigaVisibile[2] = riga[2];
                    rigaVisibile[3] = riga[4];
                    rigaVisibile[4] = riga[5];
                    rigaVisibile[5] = riga[6];
                    
                    model.addRow(rigaVisibile);
                }
            }
        }
    }

    /**
     * Registra un listener per il pulsante di inserimento di un nuovo ricovero.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addNuovoRicoveroListener(ActionListener listener) {
        nuovoRicoveroButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di gestione delle dimissioni di un paziente ricoverato.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addGestisciDimissioneListener(ActionListener listener) {
        gestisciDimissioneButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di modifica o gestione di un ricovero esistente.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addGestisciRicoveroListener(ActionListener listener) {
        gestisciRicoveroButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di ricerca dei ricoveri.
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
     * Imposta i dati per la lista dei reparti.
     *
     * @param reparti una lista di stringhe contenente i nomi dei reparti
     */
    public void setRepartiListData(List<String> reparti) {
        if (repartoList != null && reparti != null) {
            repartoList.setListData(reparti.toArray(new String[0]));
        }
    }

    /**
     * Restituisce il nome e cognome inseriti nel campo di ricerca.
     *
     * @return la stringa inserita nel campo del nome
     */
    public String getNome() {
        return nomeField != null ? nomeField.getText().trim() : "";
    }

    /**
     * Restituisce il codice fiscale inserito nel campo di ricerca.
     *
     * @return la stringa inserita nel campo del codice fiscale
     */
    public String getCodiceFiscale() {
        return codiceField != null ? codiceField.getText().trim() : "";
    }

    /**
     * Restituisce il numero della stanza inserito nel campo di ricerca.
     *
     * @return la stringa inserita nel campo della stanza
     */
    public String getStanza() {
        return stanzaField != null ? stanzaField.getText().trim() : "";
    }

    /**
     * Restituisce il reparto correntemente selezionato dalla lista di filtraggio.
     *
     * @return il nome del reparto selezionato, oppure null se non c'è selezione
     */
    public String getRepartoSelezionato() {
        return repartoList.getSelectedValue();
    }

    /**
     * Estrae e restituisce la data formattata selezionata nello spinner.
     *
     * @return la data in formato "yyyy-MM-dd", oppure una stringa vuota se non avvalorata
     */
    public String getDataStr() {
        if (dataSpinner != null && dataSpinner.getEditor() instanceof JSpinner.DateEditor) {
            JSpinner.DateEditor editor = (JSpinner.DateEditor) dataSpinner.getEditor();
            if (editor.getTextField().getText().trim().isEmpty()) {
                return "";
            }
        }
        Date selectedDate = (Date) dataSpinner.getValue();
        if (selectedDate == null) return "";
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(selectedDate);
    }

    /**
     * Resetta tutti i campi di ricerca e svuota le selezioni applicate.
     */
    public void resetCampiRicerca() {
        if (nomeField != null) nomeField.setText("");
        if (codiceField != null) codiceField.setText("");
        if (stanzaField != null) stanzaField.setText("");
        if (repartoList != null) repartoList.clearSelection();
        if (dataSpinner != null) {
            dataSpinner.setValue(new Date());
            if (dataSpinner.getEditor() instanceof JSpinner.DateEditor) {
                JSpinner.DateEditor editor = (JSpinner.DateEditor) dataSpinner.getEditor();
                editor.getTextField().setValue(null);
                editor.getTextField().setText("");
            }
        }
    }

    /**
     * Recupera le informazioni identificative del ricovero attualmente selezionato nella tabella.
     *
     * @return un array di stringhe contenente l'ID reale del ricovero (indice 0)
     *         e il codice fiscale del paziente (indice 1), oppure un array vuoto se nessuna riga è selezionata
     */
    public String[] getRicoveroSelezionato() {
        int rigaSelezionata = ricoveriTable.getSelectedRow();
        if (rigaSelezionata == -1) {
            return new String[0];
        }
        int modelRow = ricoveriTable.convertRowIndexToModel(rigaSelezionata);
        String idRicovero = null;
        if (modelRow >= 0 && modelRow < idRicoveriNascosti.size()) {
            idRicovero = idRicoveriNascosti.get(modelRow);
        }
        String cf = (String) ricoveriTable.getValueAt(rigaSelezionata, 0);
        return new String[]{idRicovero, cf};
    }

    /**
     * Inizializza i componenti grafici generati dal GUI Designer.
     */
    private void initComponents() {
    }

    /**
     * Applica gli stili visivi personalizzati a tabelle, liste e pulsanti dell'interfaccia.
     */
    private void setupStyles() {
        Login.styleList(repartoList);
        Login.setupTableStyle(ricoveriTable);
        Login.applicaStilePulsantiCentrali(cercaButton);
        Login.applicaStilePulsantiCentrali(resetButton);
        Login.applicaStilePulsantiCentrali(nuovoRicoveroButton);
        Login.applicaStilePulsantiCentrali(gestisciRicoveroButton);
        Login.applicaStilePulsantiCentrali(gestisciDimissioneButton);
    }

}