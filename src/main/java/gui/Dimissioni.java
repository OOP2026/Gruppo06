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

    public void resetCampiRicerca(ActionListener cercaListener) {
        codiceFiscaleField.setText("");
        nomeCognomeField.setText("");
        repartoList.clearSelection();
        tipoDimissioneList.clearSelection();

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
        dataDimissioneSpinner.setEditor(new JSpinner.DateEditor(dataDimissioneSpinner, "yyyy-MM-dd"));
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