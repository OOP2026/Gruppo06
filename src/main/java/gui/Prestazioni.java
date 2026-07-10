package gui;

import javax.swing.*;
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

/**
 * La classe Prestazioni gestisce l'interfaccia grafica per la visualizzazione,
 * la ricerca e la gestione delle prestazioni mediche erogate o programmate nella struttura ospedaliera.
 * Estende JFrame e fornisce i metodi necessari per interagire con il controller.
 */
public class Prestazioni {
    public JPanel mainPanel;
    private JTable prestazioniTable;
    private JButton gestisciPrestazioneButton;
    private JButton newprestazioneButton;
    private JTextField nomeField;
    private JTextField codiceField;
    private JSpinner dataSpinner;
    private JList<String> repartoList;
    private JList<String> tipologiaList;
    private JButton resetButton;
    private JButton cercaButton;

    private static final String[] ALL_COLUMNS = {
            "Paziente", "CF Paziente", "Tipo Prestazione", "Esito", "Data", "Reparto Erogante", "ID_Prestazione"
    };

    /**
     * Costruisce una nuova istanza della schermata Prestazioni,
     * inizializzando i componenti, i modelli di dati e applicando gli stili visivi.
     */
    public Prestazioni() {
        initComponents();
        setupStyles();
        setupComponents();
    }

    /**
     * Inizializza i componenti grafici gestiti dal GUI Designer.
     */
    private void initComponents() {
    }

    /**
     * Configura i modelli di dati per le liste, la tabella e il selettore della data.
     * Imposta inoltre la visibilità delle colonne nascoste per la gestione degli ID interni.
     */
    private void setupComponents() {
        if (dataSpinner != null) {
            dataSpinner.setModel(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dataSpinner, "yyyy-MM-dd");
            dataSpinner.setEditor(dateEditor);
            
            dateEditor.getTextField().setValue(null);
        }

        if (prestazioniTable != null) {
            DefaultTableModel model = new DefaultTableModel(ALL_COLUMNS, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            prestazioniTable.setModel(model);

            javax.swing.table.TableColumn idColumn = prestazioniTable.getColumnModel().getColumn(6);
            idColumn.setMinWidth(0);
            idColumn.setMaxWidth(0);
            idColumn.setWidth(0);
        }

        if (tipologiaList != null) {
            DefaultListModel<String> tipologiaModel = new DefaultListModel<>();
            tipologiaModel.addElement("Visita Oculistica");
            tipologiaModel.addElement("Visita Anestesiologica");
            tipologiaModel.addElement("Visita Cardiologica");
            tipologiaModel.addElement("Risonanza Magnetica");
            tipologiaModel.addElement("Tomografia Computerizzata (TAC)");
            tipologiaModel.addElement("Ecografia");
            tipologiaModel.addElement("Elettrocardiogramma (ECG)");
            tipologiaModel.addElement("Endoscopia");
            tipologiaModel.addElement("Radiografia");
            tipologiaList.setModel(tipologiaModel);
        }
    }

    /**
     * Applica gli stili visivi personalizzati a tabelle, liste e pulsanti dell'interfaccia.
     */
    private void setupStyles() {
        Login.setupTableStyle(prestazioniTable);
        if (gestisciPrestazioneButton != null) Login.applicaStilePulsantiCentrali(gestisciPrestazioneButton);
        if (newprestazioneButton != null) Login.applicaStilePulsantiCentrali(newprestazioneButton);
        if (resetButton != null) Login.applicaStilePulsantiCentrali(resetButton);
        if (cercaButton != null) Login.applicaStilePulsantiCentrali(cercaButton);
        Login.styleList(repartoList);
        Login.styleList(tipologiaList);
    }

    /**
     * Registra un listener per il pulsante di inserimento di una nuova prestazione.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addNuovaPrestazioneListener(ActionListener listener) {
        if (newprestazioneButton != null) newprestazioneButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di gestione o modifica di una prestazione esistente.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addGestisciPrestazioneListener(ActionListener listener) {
        if (gestisciPrestazioneButton != null) gestisciPrestazioneButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di ricerca delle prestazioni.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addCercaListener(ActionListener listener) {
        if (cercaButton != null) cercaButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di reset dei filtri di ricerca.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addResetListener(ActionListener listener) {
        if (resetButton != null) resetButton.addActionListener(listener);
    }

    /**
     * Aggiorna il contenuto della tabella delle prestazioni con i nuovi dati forniti.
     *
     * @param dati matrice di oggetti contenente i record delle prestazioni prelevati dal database
     */
    public void aggiornaTabella(Object[][] dati) {
        DefaultTableModel model = (DefaultTableModel) prestazioniTable.getModel();
        model.setRowCount(0);
        if (dati != null) {
            for (Object[] row : dati) {
                model.addRow(row);
            }
        }
    }

    /**
     * Identifica e restituisce l'ID della prestazione attualmente selezionata nella tabella.
     *
     * @return l'ID della prestazione come stringa, oppure null se non è stata selezionata alcuna riga
     */
    public String getIdPrestazioneSelezionata() {
        int selectedRow = prestazioniTable.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }
        return prestazioniTable.getModel().getValueAt(selectedRow, 6).toString();
    }

    /**
     * Popola la lista dei reparti con i dati forniti.
     *
     * @param reparti lista di stringhe rappresentanti i nomi dei reparti disponibili
     */
    public void setRepartiListData(List<String> reparti) {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String reparto : reparti) {
            model.addElement(reparto);
        }
        repartoList.setModel(model);
    }

    /**
     * Restituisce il reparto correntemente selezionato dalla lista di filtraggio.
     *
     * @return il nome del reparto selezionato, oppure null se non è stata effettuata alcuna selezione
     */
    public String getRepartoSelezionato() {
        return repartoList.getSelectedValue();
    }

    /**
     * Restituisce la tipologia di prestazione correntemente selezionata dalla lista.
     *
     * @return il tipo di prestazione selezionato, oppure null se non è stata effettuata alcuna selezione
     */
    public String getTipologiaSelezionata() {
        return tipologiaList.getSelectedValue();
    }

    /**
     * Restituisce il codice inserito nel campo di testo dedicato.
     *
     * @return la stringa inserita nel campo di ricerca del codice, privata degli spazi iniziali e finali
     */
    public String getCodPrestazione() {
        if (codiceField == null) return "";
        return codiceField.getText().trim();
    }

    /**
     * Restituisce il nome e cognome inseriti nel campo di ricerca.
     *
     * @return la stringa inserita nel campo di ricerca del nome
     */
    public String getNomeCognome() {
        return nomeField.getText().trim();
    }

    /**
     * Estrae e restituisce in formato testuale la data selezionata nello spinner.
     *
     * @return la data formattata come "yyyy-MM-dd", oppure una stringa vuota se il campo non è avvalorato
     */
    public String getData() {
        if (dataSpinner != null && dataSpinner.getEditor() instanceof JSpinner.DateEditor) {
            JSpinner.DateEditor editor = (JSpinner.DateEditor) dataSpinner.getEditor();
            if (editor.getTextField().getText().trim().isEmpty()) {
                return "";
            }
        }
        Date selectedDate = (Date) dataSpinner.getValue();
        if (selectedDate == null) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(selectedDate);
    }

    /**
     * Resetta tutti i campi di ricerca e i filtri delle liste ai loro valori predefiniti o vuoti.
     */
    public void resetCampiRicerca() {
        if (codiceField != null) codiceField.setText("");
        if (nomeField != null) nomeField.setText("");
        if (dataSpinner != null) {
            dataSpinner.setValue(new Date());
            if (dataSpinner.getEditor() instanceof JSpinner.DateEditor) {
                ((JSpinner.DateEditor) dataSpinner.getEditor()).getTextField().setValue(null);
            }
        }
        if (repartoList != null) repartoList.clearSelection();
        if (tipologiaList != null) tipologiaList.clearSelection();
    }
}