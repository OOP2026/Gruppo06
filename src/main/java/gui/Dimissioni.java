package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.awt.event.ActionListener;
import java.util.Date;

public class Dimissioni extends JFrame {

    public JPanel mainPanel;
    private JTextField codiceficaleField;
    private JTextField idPazienteField;
    private JList<String> repartoList;
    private JList<String> tipoDimissioneList; // Questo è il reparto di dimissione
    private JSpinner spinner1;
    private JButton cercaButton;
    private JButton resetButton;
    private JTable pazientiTable;
    private JButton letturaDimissioneButton;
    private JButton archiviaDimissioneButton;
    private JTextField nomeCognomeField;

    private static final String[] COLONNE = {
            "ID Paziente", "Paziente", "Codice Fiscale",
            "Reparto Dimissione", "Tipo Dimissione", "Data Dimissione"
    };

    public Dimissioni() {
        initComponents();
        setupStyles();
    }

    public void aggiornaTabella(Object[][] dati) {
        if (pazientiTable != null) {
            DefaultTableModel model = (DefaultTableModel) pazientiTable.getModel();
            model.setRowCount(0);
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
        return codiceficaleField.getText();
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
        return (Date) spinner1.getValue();
    }

    public String getCFPazienteSelezionato() {
        int selectedRow = pazientiTable.getSelectedRow();
        if (selectedRow != -1) {
            // La colonna 2 contiene il Codice Fiscale
            return (String) pazientiTable.getValueAt(selectedRow, 2);
        }
        return null;
    }

    public void resetCampiRicerca() {
        codiceficaleField.setText("");
        idPazienteField.setText("");
        nomeCognomeField.setText("");
        repartoList.clearSelection();
        tipoDimissioneList.clearSelection();
        spinner1.setValue(new Date()); // Resetta alla data odierna
    }

    private void initComponents() {
        DefaultTableModel model = new DefaultTableModel(new Object[0][0], COLONNE) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Rende la tabella non editabile
                return false;
            }
        };
        pazientiTable.setModel(model);

        // Popola la lista dei reparti
        repartoList.setListData(new String[]{
                "Chirurgia Generale", "Bariatria", "Radiologia Interventistica",
                "Cardiologia", "Terapia Intensiva"
        });

        // Popola la lista dei tipi di dimissione
        tipoDimissioneList.setListData(new String[]{
                "Ordinaria",
                "Trasferimento",
                "Volontaria",
                "Decesso"
        });

        // Imposta il modello per lo spinner della data
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        spinner1.setModel(dateModel);
        spinner1.setEditor(new JSpinner.DateEditor(spinner1, "dd/MM/yyyy"));
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dimissioni frame = new Dimissioni();
            controller.Controller.impostaSchermata(frame, frame.mainPanel, "Ricerca Dimissioni", JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}