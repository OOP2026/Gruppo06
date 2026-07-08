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

public class Dimissioni extends JFrame {

    public JPanel mainPanel;
    private JTextField codiceficaleField;
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
            "Paziente", "Codice Fiscale",
            "Reparto Dimissione", "Tipo Dimissione", "Data Dimissione"
    };

    private TableRowSorter<DefaultTableModel> sorter;
    private boolean isDataModificata = false;
    private List<String> idRicoveriNascosti = new ArrayList<>();

    public Dimissioni() {
        initComponents();
        setupStyles();
    }

    public void aggiornaTabella(Object[][] dati) {
        idRicoveriNascosti.clear();
        if (pazientiTable != null) {
            DefaultTableModel model = (DefaultTableModel) pazientiTable.getModel();
            model.setRowCount(0);
            if (dati != null) {
                for (Object[] riga : dati) {
                    if (riga != null && riga.length > 0) {
                        idRicoveriNascosti.add((String) riga[0]); // Salva l'ID Ricovero
                        Object[] rigaSenzaId = new Object[riga.length - 1];
                        System.arraycopy(riga, 1, rigaSenzaId, 0, riga.length - 1);
                        model.addRow(rigaSenzaId);
                    }
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
            // La colonna 1 (nuova) contiene il Codice Fiscale
            return (String) pazientiTable.getValueAt(selectedRow, 1);
        }
        return null;
    }

    public String getIdRicoveroSelezionato() {
        int selectedRow = pazientiTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = pazientiTable.convertRowIndexToModel(selectedRow);
            if (modelRow >= 0 && modelRow < idRicoveriNascosti.size()) {
                return idRicoveriNascosti.get(modelRow);
            }
        }
        return null;
    }

    public void resetCampiRicerca() {
        codiceficaleField.setText("");
        nomeCognomeField.setText("");
        repartoList.clearSelection();
        tipoDimissioneList.clearSelection();
        spinner1.setValue(new Date()); // Resetta alla data odierna
        
        // Riporta il cursore sulla porzione del giorno anche dopo un Reset
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner1.getEditor();
        SwingUtilities.invokeLater(() -> {
            editor.getTextField().setCaretPosition(editor.getTextField().getText().length());
        });

        isDataModificata = false;
        if (sorter != null) sorter.setRowFilter(null);
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

        sorter = new TableRowSorter<>(model);
        pazientiTable.setRowSorter(sorter);

        // Popola la lista dei reparti
        repartoList.setListData(new String[]{
                "Chirurgia generale", "Ortopedia", "Cardiologia"
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
        spinner1.setEditor(new JSpinner.DateEditor(spinner1, "yyyy-MM-dd"));

        spinner1.addChangeListener(e -> isDataModificata = true);
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner1.getEditor();
        
        // Sposta il cursore alla fine in modo che le freccette modifichino di default il giorno (l'ultima parte della data)
        SwingUtilities.invokeLater(() -> {
            editor.getTextField().setCaretPosition(editor.getTextField().getText().length());
        });

        // Allinea visivamente il testo a sinistra e assicura che il cursore 
        // vada in fondo (sul giorno) ogni volta che si clicca o si seleziona il campo
        editor.getTextField().setHorizontalAlignment(JTextField.LEFT);
        editor.getTextField().addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    editor.getTextField().setCaretPosition(editor.getTextField().getText().length());
                });
            }
        });
        editor.getTextField().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    editor.getTextField().setCaretPosition(editor.getTextField().getText().length());
                });
            }
        });

        editor.getTextField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { isDataModificata = true; }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { isDataModificata = true; }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { isDataModificata = true; }
        });

        if (cercaButton != null) {
            cercaButton.addActionListener(e -> eseguiRicerca());
        }
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

    private void eseguiRicerca() {
        if (sorter == null) return;
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (codiceficaleField != null && !codiceficaleField.getText().trim().isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + codiceficaleField.getText().trim(), 1)); // Colonna CF
        }
        if (nomeCognomeField != null && !nomeCognomeField.getText().trim().isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + nomeCognomeField.getText().trim(), 0)); // Colonna Paziente
        }
        if (repartoList != null && !repartoList.isSelectionEmpty()) {
            List<String> repartiScelti = repartoList.getSelectedValuesList();
            if (!repartiScelti.isEmpty()) {
                String regex = "(?i)(" + String.join("|", repartiScelti) + ")";
                filters.add(RowFilter.regexFilter(regex, 2)); // Colonna Reparto Dimissione
            }
        }
        if (tipoDimissioneList != null && !tipoDimissioneList.isSelectionEmpty()) {
            List<String> tipiScelti = tipoDimissioneList.getSelectedValuesList();
            if (!tipiScelti.isEmpty()) {
                String regex = "(?i)(" + String.join("|", tipiScelti) + ")";
                filters.add(RowFilter.regexFilter(regex, 3)); // Colonna Tipo Dimissione
            }
        }

        if (isDataModificata && spinner1 != null && spinner1.getValue() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String dataStr = sdf.format((Date) spinner1.getValue());
            filters.add(RowFilter.regexFilter("^" + dataStr, 4)); // Colonna Data Dimissione
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dimissioni frame = new Dimissioni();
            controller.Controller.impostaSchermata(frame, frame.mainPanel, "Ricerca Dimissioni", JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}