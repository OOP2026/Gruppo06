package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private JSpinner oraSpinner;

    private JButton cercaButton;
    private JButton resetButton;
    private JTable ricoveriTable;

    private JButton nuovoRicoveroButton;
    private JButton gestisciRicoveroButton;
    private JButton gestisciDimissioneButton;

    private List<String> idRicoveriNascosti = new ArrayList<>();

    private static final String[] COLONNE = {
            "Paziente", "Stanza", "Codice Fiscale", 
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
                if (riga != null && riga.length > 0) {
                    idRicoveriNascosti.add((String) riga[0]); // Salva l'ID in memoria
                    Object[] rigaVisibile = new Object[riga.length - 1];
                    System.arraycopy(riga, 1, rigaVisibile, 0, riga.length - 1);
                    model.addRow(rigaVisibile);
                }
            }
        }
    }

    // Metodi per aggiungere i listener ai pulsanti
    public void addNuovoRicoveroListener(ActionListener listener) {
        nuovoRicoveroButton.addActionListener(listener);
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

    public void resetCampiRicerca() {
        nomeField.setText("");
        codiceField.setText("");
        stanzaField.setText("");
        repartoList.clearSelection();
        dataSpinner.setValue(new Date());
        oraSpinner.setValue(new Date());
    }

    public String[] getRicoveroSelezionato() {
        int rigaSelezionata = ricoveriTable.getSelectedRow();
        if (rigaSelezionata == -1) {
            return null;
        }
        int modelRow = ricoveriTable.convertRowIndexToModel(rigaSelezionata);
        String idRicovero = null;
        if (modelRow >= 0 && modelRow < idRicoveriNascosti.size()) {
            idRicovero = idRicoveriNascosti.get(modelRow);
        }
        String cf = (String) ricoveriTable.getValueAt(rigaSelezionata, 2);
        return new String[]{idRicovero, cf};
    }

    private void initComponents() {
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dataSpinner.setModel(dateModel);
        dataSpinner.setEditor(new JSpinner.DateEditor(dataSpinner, "dd/MM/yyyy"));

        SpinnerDateModel timeModel = new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY);
        oraSpinner.setModel(timeModel);
        oraSpinner.setEditor(new JSpinner.DateEditor(oraSpinner, "HH:mm"));

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ricovero frame = new Ricovero();
            controller.Controller.impostaSchermata(frame, frame.mainPanel, "Ricerca Ricovero", JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}