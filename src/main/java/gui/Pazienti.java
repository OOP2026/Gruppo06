package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Pazienti extends JFrame {

    public JPanel mainPanel;
    private JTextField nomeField;
    private JTextField codiceField;
    private JList<String> tipologiaList;
    private JButton resetButton;
    private JButton cercaButton;
    private JTable pazientiTable;
    private JButton nuovoPazienteButton;
    private JButton storicoPazienteButton;
    private JButton assegnaLettoButton;
    private JTextField residenzaField;
    private JTextField dataField;
    private JTextField ricercaprognosiField;
    private JRadioButton femminaRadioButton;
    private JRadioButton maschioRadioButton;

    private static final String[] COLONNE = {
            "ID Paziente", "Nome e Cognome", "Codice Fiscale", "Data Nascita", "Diagnosi",
            "Sesso", "Residenza", "Stato Ricovero", "Reparto"
    };

    private TableRowSorter<DefaultTableModel> sorter;

    public Pazienti() {
        initComponents();
        setupStyles();
    }

    private void initComponents() {
        DefaultTableModel model = new DefaultTableModel(new Object[0][0], COLONNE) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pazientiTable.setModel(model);

        sorter = new TableRowSorter<>(model);
        pazientiTable.setRowSorter(sorter);

        if (cercaButton != null) {
            cercaButton.addActionListener(e -> eseguiRicerca());
        }
        if (resetButton != null) {
            resetButton.addActionListener(e -> resettaRicerca());
        }

        if (tipologiaList != null) {
            tipologiaList.setListData(new String[]{
                    "Chirurgia Generale", "Cardiologia", "Ortopedia",
                    "Pediatria", "Terapia Intensiva", "Pronto Soccorso"
            });
        }
    }

    private void setupStyles() {
        Login.setupTableStyle(pazientiTable);
        Login.styleList(tipologiaList);
        if(cercaButton != null) Login.applicaStilePulsantiCentrali(cercaButton);
        if(resetButton != null) Login.applicaStilePulsantiCentrali(resetButton);
        if(nuovoPazienteButton != null) Login.applicaStilePulsantiCentrali(nuovoPazienteButton);
        if(storicoPazienteButton != null) Login.applicaStilePulsantiCentrali(storicoPazienteButton);
        if(assegnaLettoButton != null) Login.applicaStilePulsantiCentrali(assegnaLettoButton);
    }

    // Metodo per permettere al sistema di ascoltare il bottone
    // senza importare o conoscere il Controller nella GUI
    public void addNuovoPazienteListener(java.awt.event.ActionListener listener) {
        if (nuovoPazienteButton != null) {
            nuovoPazienteButton.addActionListener(listener);
        }
    }

    public void addAssegnaLettoListener(java.awt.event.ActionListener listener) {
        if (assegnaLettoButton != null) {
            assegnaLettoButton.addActionListener(listener);
        }
    }

    public void addStoricoPazienteListener(java.awt.event.ActionListener listener) {
        if (storicoPazienteButton != null) {
            storicoPazienteButton.addActionListener(listener);
        }
    }

    public String getCfPazienteSelezionato() {
        int rigaSelezionata = pazientiTable.getSelectedRow();
        if (rigaSelezionata == -1) {
            JOptionPane.showMessageDialog(this, "Per favore, seleziona un paziente dalla tabella.", "Nessun Paziente Selezionato", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        // La colonna 0 contiene il CF (usato come ID Paziente)
        return (String) pazientiTable.getValueAt(rigaSelezionata, 0);
    }

    // Metodo per aggiornare la tabella con i dati reali dal database
    public void aggiornaTabella(List<ArrayList<String>> datiPazienti) {
        DefaultTableModel model = (DefaultTableModel) pazientiTable.getModel();
        model.setRowCount(0); // Svuota i vecchi dati finti/obsoleti

        for (java.util.ArrayList<String> p : datiPazienti) {
            String cf = p.get(0);
            String nomeCognome = p.get(1) + " " + p.get(2);
            String dataNascita = p.size() > 3 ? p.get(3) : "";
            String sesso = p.size() > 4 ? p.get(4) : "";
            String residenza = p.size() > 5 ? p.get(5) : "";
            String diagnosi = p.size() > 6 ? p.get(6) : "";

            // Indice 7: Stato ricovero (es. ID del letto). Se non presente o vuoto, il paziente non è ricoverato.
            String statoRicovero = (p.size() > 7 && p.get(7) != null && !p.get(7).isEmpty()) ? "Ricoverato - Letto " + p.get(7) : "Non ricoverato";
            String reparto = (p.size() > 8 && p.get(8) != null && !p.get(8).isEmpty()) ? p.get(8) : "Nessuno";

            model.addRow(new Object[]{cf, nomeCognome, cf, dataNascita, diagnosi, sesso, residenza, statoRicovero, reparto});
        }
    }

    private void eseguiRicerca() {
        if (sorter == null) return;
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (nomeField != null && !nomeField.getText().trim().isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + nomeField.getText().trim(), 1)); // Colonna Nome
        }
        if (codiceField != null && !codiceField.getText().trim().isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + codiceField.getText().trim(), 2)); // Colonna CF
        }
        if (dataField != null && !dataField.getText().trim().isEmpty()) {
            String text = dataField.getText().trim();
            // Applica un filtro che matcha l'inizio della stringa (es. "2024", "2024-05", "2024-05-15")
            filters.add(RowFilter.regexFilter("^" + text, 3)); // Colonna Data Nascita
        }
        if (ricercaprognosiField != null && !ricercaprognosiField.getText().trim().isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + ricercaprognosiField.getText().trim(), 4)); // Colonna Diagnosi
        }
        if (residenzaField != null && !residenzaField.getText().trim().isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + residenzaField.getText().trim(), 6)); // Colonna Residenza
        }
        if (maschioRadioButton != null && maschioRadioButton.isSelected()) {
            filters.add(RowFilter.regexFilter("(?i)^M$", 5)); // Colonna Sesso
        } else if (femminaRadioButton != null && femminaRadioButton.isSelected()) {
            filters.add(RowFilter.regexFilter("(?i)^F$", 5)); // Colonna Sesso
        }
        if (tipologiaList != null && !tipologiaList.isSelectionEmpty()) {
            List<String> repartiScelti = tipologiaList.getSelectedValuesList();
            if (!repartiScelti.isEmpty()) {
                StringBuilder regex = new StringBuilder("(?i)(");
                for (int i = 0; i < repartiScelti.size(); i++) {
                    regex.append(repartiScelti.get(i));
                    if (i < repartiScelti.size() - 1) regex.append("|");
                }
                regex.append(")");
                filters.add(RowFilter.regexFilter(regex.toString(), 8)); // Colonna Reparto
            }
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void resettaRicerca() {
        if (nomeField != null) nomeField.setText("");
        if (codiceField != null) codiceField.setText("");
        if (residenzaField != null) residenzaField.setText("");
        if (ricercaprognosiField != null) ricercaprognosiField.setText("");
        if (maschioRadioButton != null) maschioRadioButton.setSelected(false);
        if (femminaRadioButton != null) femminaRadioButton.setSelected(false);
        if (tipologiaList != null) tipologiaList.clearSelection();
        if (dataField != null) dataField.setText("");
        if (sorter != null) sorter.setRowFilter(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Pazienti frame = new Pazienti();
            controller.Controller.impostaSchermata(frame, frame.mainPanel, "Ricerca Pazienti", JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}