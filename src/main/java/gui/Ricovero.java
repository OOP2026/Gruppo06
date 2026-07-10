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

    private List<String> idRicoveriNascosti = new ArrayList<>();

    private static final String[] COLONNE = {
            "Codice Fiscale", "Paziente", "Stanza", 
            "Motivazione Ricovero", "Reparto di Ricovero", "Data e Ora Ingresso"
    };

    public Ricovero() {
        initComponents();
        setupStyles();
		if (dataSpinner != null) {
			SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
			dataSpinner.setModel(dateModel);
			JSpinner.DateEditor editor = new JSpinner.DateEditor(dataSpinner, "yyyy-MM-dd");
			dataSpinner.setEditor(editor);
			editor.getTextField().setValue(null);
			editor.getTextField().setText(""); // Forza la pulizia visiva e logica all'avvio
		}

		if (repartoList != null) {
			repartoList.setListData(new String[]{"Chirurgia generale", "Ortopedia", "Cardiologia"});
		}

		if (ricoveriTable != null) {
			DefaultTableModel model = new DefaultTableModel(new Object[0][0], COLONNE) {
				@Override public boolean isCellEditable(int row, int column) { return false; }
			};
			ricoveriTable.setModel(model);
		}
    }

    public void aggiornaTabella(Object[][] dati) {
        idRicoveriNascosti.clear();
        DefaultTableModel model = (DefaultTableModel) ricoveriTable.getModel();
        model.setRowCount(0); // Pulisce la tabella
        if (dati != null) {
            for (Object[] riga : dati) {
                if (riga != null && riga.length >= 7) {
                    idRicoveriNascosti.add((String) riga[0]); // Salva l'ID in memoria
                    
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

    public String getNome() {
        return nomeField != null ? nomeField.getText().trim() : "";
    }

    public String getCodiceFiscale() {
        return codiceField != null ? codiceField.getText().trim() : "";
    }

    public String getStanza() {
        return stanzaField != null ? stanzaField.getText().trim() : "";
    }

    public String getRepartoSelezionato() {
        return repartoList.getSelectedValue();
    }

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
                editor.getTextField().setText(""); // Forza la pulizia durante il reset
            }
        }
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
        // Questo metodo è mantenuto per compatibilità con il GUI Designer, ma la logica è stata spostata.
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