package gui;

import javax.swing.*;
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

public class Prestazioni {
    public JPanel mainPanel;
    private JTable prestazioniTable;
    private JButton gestisciPrestazioneButton;
    private JButton newprestazioneButton;
    private JTextField nomeField;
    private JTextField codiceField;
    private JSpinner dataSpinner;
    private JList repartoList;
    private JList tipologiaList;
    private JButton resetButton;
    private JButton cercaButton;
    private JLabel tipoLabel;

    private static final String[] COLONNE = {
            "Paziente", "CF Paziente", "Tipo Prestazione", "Esito", "Data", "Reparto Erogante"
    };
    private static final String[] ALL_COLUMNS = {
            "Paziente", "CF Paziente", "Tipo Prestazione", "Esito", "Data", "Reparto Erogante", "ID_Prestazione"
    };

    public Prestazioni() {
        initComponents();
        setupStyles();
    }

    private void initComponents() {
        dataSpinner.setModel(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dataSpinner, "yyyy-MM-dd");
        dataSpinner.setEditor(dateEditor);

        DefaultTableModel model = new DefaultTableModel(ALL_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        prestazioniTable.setModel(model);

        // Nascondi la colonna dell'ID (indice 6) impostando la sua larghezza a 0
        javax.swing.table.TableColumn idColumn = prestazioniTable.getColumnModel().getColumn(6);
        idColumn.setMinWidth(0);
        idColumn.setMaxWidth(0);
        idColumn.setWidth(0);

        // Popola la lista delle tipologie di prestazione
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

    private void setupStyles() {
        Login.setupTableStyle(prestazioniTable);
        if (gestisciPrestazioneButton != null) Login.applicaStilePulsantiCentrali(gestisciPrestazioneButton);
        if (newprestazioneButton != null) Login.applicaStilePulsantiCentrali(newprestazioneButton);
        if (resetButton != null) Login.applicaStilePulsantiCentrali(resetButton);
        if (cercaButton != null) Login.applicaStilePulsantiCentrali(cercaButton);
        Login.styleList(repartoList);
        Login.styleList(tipologiaList);
    }

    public void addNuovaPrestazioneListener(ActionListener listener) {
        if (newprestazioneButton != null) newprestazioneButton.addActionListener(listener);
    }

    public void addGestisciPrestazioneListener(ActionListener listener) {
        if (gestisciPrestazioneButton != null) gestisciPrestazioneButton.addActionListener(listener);
    }

    public void addCercaListener(ActionListener listener) {
        if (cercaButton != null) cercaButton.addActionListener(listener);
    }

    public void addResetListener(ActionListener listener) {
        if (resetButton != null) resetButton.addActionListener(listener);
    }

    public void aggiornaTabella(Object[][] dati) {
        DefaultTableModel model = (DefaultTableModel) prestazioniTable.getModel();
        model.setRowCount(0);
        if (dati != null) {
            for (Object[] row : dati) {
                model.addRow(row);
            }
        }
    }

    public String getIdPrestazioneSelezionata() {
        int selectedRow = prestazioniTable.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }
        // L'ID Prestazione è ora in una colonna "nascosta" (la 7a, indice 6) nel modello dati per non appesantire la UI.
        return prestazioniTable.getModel().getValueAt(selectedRow, 6).toString();
    }

    public void setRepartiListData(List<String> reparti) {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String reparto : reparti) {
            model.addElement(reparto);
        }
        repartoList.setModel(model);
    }

    public String getRepartoSelezionato() {
        return (String) repartoList.getSelectedValue();
    }

    public String getTipologiaSelezionata() {
        return (String) tipologiaList.getSelectedValue();
    }

    public String getCodPrestazione() {
        // Aggiunto controllo per evitare NullPointerException se il campo non è inizializzato dal designer
        if (codiceField == null) return "";
        return codiceField.getText().trim();
    }


    public String getNomeCognome() {
        return nomeField.getText().trim();
    }

    public String getData() {
        Date selectedDate = (Date) dataSpinner.getValue();
        if (selectedDate == null) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(selectedDate);
    }

    public void resetCampiRicerca() {
        if (codiceField != null) codiceField.setText("");
        if (nomeField != null) nomeField.setText("");
        dataSpinner.setValue(new Date());
        repartoList.clearSelection();
        tipologiaList.clearSelection();
    }
}