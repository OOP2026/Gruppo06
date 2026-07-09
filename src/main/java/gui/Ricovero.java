package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.util.Calendar;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

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

    private boolean dataModificata = false;

    private List<String> idRicoveriNascosti = new ArrayList<>();

    private static final String[] COLONNE = {
            "Codice Fiscale", "Paziente", "Stanza", 
            "Motivazione Ricovero", "Reparto di Ricovero", "Data e Ora Ingresso"
    };

    public Ricovero() {
        initComponents();
        setupStyles();
    }

    public void aggiornaTabella(Object[][] dati) {
        idRicoveriNascosti.clear();
        DefaultTableModel model = (DefaultTableModel) ricoveriTable.getModel();
        model.setRowCount(0); // Pulisce la tabella
        if (dati != null) {
            for (Object[] riga : dati) {
                if (riga != null && riga.length >= 7) {
                    idRicoveriNascosti.add((String) riga[0]); // Salva l'ID in memoria
                    
                    // Mappiamo l'array dal vecchio formato del DAO al nuovo ordine visibile:
                    Object[] rigaVisibile = new Object[6];
                    rigaVisibile[0] = riga[3]; // Codice Fiscale
                    rigaVisibile[1] = riga[1]; // Paziente
                    rigaVisibile[2] = riga[2]; // Stanza
                    rigaVisibile[3] = riga[4]; // Motivazione Ricovero
                    rigaVisibile[4] = riga[5]; // Reparto di Ricovero
                    rigaVisibile[5] = riga[6]; // Data e Ora Ingresso
                    
                    model.addRow(rigaVisibile);
                }
            }
        }
    }

    // Metodi per aggiungere i listener ai pulsanti
    public void addNuovoRicoveroListener(ActionListener listener) {
        nuovoRicoveroButton.addActionListener(listener);
    }

    public void addGestisciDimissioneListener(ActionListener listener) {
        gestisciDimissioneButton.addActionListener(listener);
    }

    public void addGestisciRicoveroListener(ActionListener listener) {
        gestisciRicoveroButton.addActionListener(listener);
    }

    public void addCercaListener(ActionListener listener) {
        cercaButton.addActionListener(listener);
    }

    public void addResetListener(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    // Metodi per ottenere i valori dai campi di input
    public String getNome() {
        return nomeField.getText();
    }

    public String getCodiceFiscale() {
        return codiceField.getText();
    }

    public String getStanza() {
        return stanzaField.getText();
    }

    public String getRepartoSelezionato() {
        return repartoList.getSelectedValue();
    }

    public String getDataStr() {
        try {
            dataSpinner.commitEdit();
        } catch (java.text.ParseException e) {
        }
        JSpinner.DateEditor editor = (JSpinner.DateEditor) dataSpinner.getEditor();
        String text = editor.getTextField().getText().trim();
        if (text.isEmpty() || !dataModificata) return "";
        return text;
    }

    public void resetCampiRicerca() {
        nomeField.setText("");
        codiceField.setText("");
        stanzaField.setText("");
        repartoList.clearSelection();
        dataSpinner.setValue(new Date());
        dataModificata = false;
    }

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

    private void initComponents() {
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dataSpinner.setModel(dateModel);
        dataSpinner.setEditor(new JSpinner.DateEditor(dataSpinner, "yyyy-MM-dd"));
        dataSpinner.addChangeListener(e -> dataModificata = true);
        ((JSpinner.DateEditor) dataSpinner.getEditor()).getTextField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { dataModificata = true; }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { dataModificata = true; }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { dataModificata = true; }
        });

        repartoList.setListData(new String[]{"Chirurgia generale", "Ortopedia", "Cardiologia"});

        DefaultTableModel model = new DefaultTableModel(new Object[0][0], COLONNE) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ricoveriTable.setModel(model);
    }

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