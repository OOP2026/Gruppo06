package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Calendar;
import java.awt.event.ActionListener;
import java.util.Date;

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

    public Dimissioni() {
        initComponents();
        setupStyles();
    }

    public void aggiornaTabella(Object[][] dati) {
        if (pazientiTable != null) {
            DefaultTableModel model = (DefaultTableModel) pazientiTable.getModel();
            model.setRowCount(0);

            // Nascondiamo la colonna dell'ID Ricovero
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

    // Metodi per aggiungere i listener ai pulsanti
    public void addArchiviaDimissioneListener(ActionListener listener) {
        archiviaDimissioneButton.addActionListener(listener);
    }

    public void addCercaListener(ActionListener listener) {
        cercaButton.addActionListener(listener);
    }

    public void addResetListener(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    public void addLetturaDimissioneListener(ActionListener listener) {
        letturaDimissioneButton.addActionListener(listener);
    }

    // Metodi per ottenere i valori dai campi di input
    public String getCodiceFiscale() {
        return codiceFiscaleField.getText();
    }

    public String getNomeCognome() {
        return nomeCognomeField.getText();
    }

    public String getRepartoSelezionato() {
        return repartoList.getSelectedValue();
    }

    public String getTipoDimissioneSelezionato() {
        return tipoDimissioneList.getSelectedValue();
    }

    public Date getDataSelezionata() {
        if (dataDimissioneSpinner != null && dataDimissioneSpinner.getEditor() instanceof JSpinner.DateEditor) {
            JSpinner.DateEditor editor = (JSpinner.DateEditor) dataDimissioneSpinner.getEditor();
            // Se il campo di testo è vuoto, restituisce null per ignorare il filtro della data
            if (editor.getTextField().getText().trim().isEmpty()) {
                return null;
            }
        }
        return (Date) dataDimissioneSpinner.getValue();
    }

    public String getCFPazienteSelezionato() {
        int selectedRow = pazientiTable.getSelectedRow();
        if (selectedRow != -1) {
            // La colonna 2 contiene il Codice Fiscale
            return (String) pazientiTable.getValueAt(selectedRow, 2);
        }
        return null;
    }

    public String getIdRicoveroSelezionato() {
        int selectedRow = pazientiTable.getSelectedRow();
        if (selectedRow != -1) return (String) pazientiTable.getValueAt(selectedRow, 0);
        return null;
    }

    /**
     *
     * @param cercaListener
     */
    public void resetCampiRicerca(ActionListener cercaListener) {
        if (codiceFiscaleField != null) codiceFiscaleField.setText("");
        if (nomeCognomeField != null) nomeCognomeField.setText("");
        if (repartoList != null) repartoList.clearSelection();
        if (tipoDimissioneList != null) tipoDimissioneList.clearSelection();

        // Svuota fisicamente la data dallo Spinner
        if (dataDimissioneSpinner != null) {
            dataDimissioneSpinner.setValue(new Date());
            if (dataDimissioneSpinner.getEditor() instanceof JSpinner.DateEditor) {
                JSpinner.DateEditor editor = (JSpinner.DateEditor) dataDimissioneSpinner.getEditor();
                editor.getTextField().setValue(null);
                editor.getTextField().setText(""); // Forza la pulizia testuale assoluta
            }
        }

        // Esegue la ricerca con i campi resettati per mostrare tutti i risultati
        if (cercaListener != null) {
            cercaListener.actionPerformed(null);
        }
    }

    private void initComponents() {
        DefaultTableModel model = new DefaultTableModel(new Object[0][0], COLONNE) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pazientiTable.setModel(model);

        pazientiTable.setAutoCreateRowSorter(true);

        //Reparti
        repartoList.setListData(REPARTI_LIST_DATA);

        //Dimissione
        tipoDimissioneList.setListData(TIPO_DIMISSIONE_LIST_DATA);

        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dataDimissioneSpinner.setModel(dateModel);
        
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dataDimissioneSpinner, "yyyy-MM-dd");
        dataDimissioneSpinner.setEditor(editor);
        // All'avvio della schermata, imposta il campo della data come vuoto
        editor.getTextField().setValue(null);
        editor.getTextField().setText(""); // Forza la pulizia testuale assoluta
    }
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