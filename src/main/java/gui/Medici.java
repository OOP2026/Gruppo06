package gui;

import javax.swing.*;
import javax.swing.table.*;

public class Medici extends JFrame {
    public JPanel mainPanel;
    private JTextField nomeField;
    private JTextField codiceField;
    private JButton resetButton;
    private JButton cercaButton;
    private JTable mediciTable;
    private JButton newmedicoButton;

    private JList<String> specializzazioneList;
    private JList<String> repartoList;
    private JButton assenzaButton;
    private JButton modificamedicoButton;
    private JRadioButton attivoRadioButton;
    private JRadioButton assenteRadioButton;
    private JRadioButton occupatoRadioButton;
    private JRadioButton tuttiRadioButton;

    private static final String[] COLONNE = {
            "Matricola", "Cognome e Nome", "Specializzazione",
            "Reparto Assegnato", "Stato"
    };

    private static final String[] SPECIALIZZAZIONI_DATA = {
            "Chirurgia Generale", "Cardiologia", "Neurologia",
            "Anestesia", "Chirurgia Toracica", "Ematologia", "Otorinolaringoiatria"
    };

    private static final String[] REPARTI_DATA = {
            "Blocco Operatorio", "Terapia Intensiva", "Neuroradiologia",
            "Chirurgia Toracica", "Laboratorio Analisi", "Pronto Soccorso"
    };

    // RISOLTO: Aggiunto 'transient' per evitare problemi di serializzazione
    private transient Object[][] datiMedici = new Object[0][0];

    public Medici() {
        initComponents();
        setupStyles();
        inizializzaComponentiDati();
        setupListeners();

        if (mediciTable != null) {
            loadTableData(null, null, null, null);
        }
    }

    public void aggiornaTabella(Object[][] dati) {
        this.datiMedici = dati != null ? dati : new Object[0][0];
        if (mediciTable != null) {
            loadTableData(null, null, null, null);
        }
    }

    public void addNuovoMedicoListener(java.awt.event.ActionListener listener) {
        if (newmedicoButton != null) newmedicoButton.addActionListener(listener);
    }

    public void addModificaMedicoListener(java.awt.event.ActionListener listener) {
        if (modificamedicoButton != null) modificamedicoButton.addActionListener(listener);
    }

    public void addAssenzaListener(java.awt.event.ActionListener listener) {
        if (assenzaButton != null) assenzaButton.addActionListener(listener);
    }

    public String getMatricolaMedicoSelezionato() {
        if (mediciTable == null) return null;

        int selectedRow = mediciTable.getSelectedRow();
        return (selectedRow == -1) ? null : (String) mediciTable.getValueAt(selectedRow, 0);
    }

    private void initComponents() {
        // Metodo per compatibilità con GUI Designer
    }

    private void setupStyles() {
        if (specializzazioneList != null) Login.styleList(specializzazioneList);
        if (repartoList != null) Login.styleList(repartoList);
        if (mediciTable != null) Login.setupTableStyle(mediciTable);
        if (cercaButton != null) Login.applicaStilePulsantiCentrali(cercaButton);
        if (resetButton != null) Login.applicaStilePulsantiCentrali(resetButton);
        if (modificamedicoButton != null) Login.applicaStilePulsantiCentrali(modificamedicoButton);
        if (newmedicoButton != null) Login.applicaStilePulsantiCentrali(newmedicoButton);
        if (assenzaButton != null) Login.applicaStilePulsantiCentrali(assenzaButton);
    }


    private void inizializzaComponentiDati() {
        if (specializzazioneList != null) specializzazioneList.setListData(SPECIALIZZAZIONI_DATA);
        if (repartoList != null) repartoList.setListData(REPARTI_DATA);
        inizializzaRadioButtons();
        inizializzaTabella();
    }

    private void inizializzaRadioButtons() {
        if (tuttiRadioButton == null || attivoRadioButton == null || assenteRadioButton == null || occupatoRadioButton == null) return;

        ButtonGroup statoGroup = new ButtonGroup();
        statoGroup.add(tuttiRadioButton);
        statoGroup.add(attivoRadioButton);
        statoGroup.add(assenteRadioButton);
        statoGroup.add(occupatoRadioButton);
        tuttiRadioButton.setSelected(true);
    }

    private void inizializzaTabella() {
        if (mediciTable == null) return;

        DefaultTableModel model = new DefaultTableModel(COLONNE, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        mediciTable.setModel(model);
    }

    // --- REFACTORING: Metodi estratti per abbattere la complessità di setupListeners ---

    private void setupListeners() {
        if (cercaButton != null) cercaButton.addActionListener(e -> eseguiRicerca());
        if (resetButton != null) resetButton.addActionListener(e -> eseguiReset());
    }

    private void eseguiRicerca() {
        String nome = (nomeField != null) ? nomeField.getText().toLowerCase().trim() : "";
        String matricola = (codiceField != null) ? codiceField.getText().toLowerCase().trim() : "";
        String specializzazione = (specializzazioneList != null) ? specializzazioneList.getSelectedValue() : null;
        String reparto = (repartoList != null) ? repartoList.getSelectedValue() : null;

        loadTableData(nome, matricola, specializzazione, reparto);
    }

    private void eseguiReset() {
        if (nomeField != null) nomeField.setText("");
        if (codiceField != null) codiceField.setText("");
        if (specializzazioneList != null) specializzazioneList.clearSelection();
        if (repartoList != null) repartoList.clearSelection();
        if (tuttiRadioButton != null) tuttiRadioButton.setSelected(true);

        loadTableData(null, null, null, null);
    }


    private void loadTableData(String fNome, String fMatricola, String fSpec, String fReparto) {
        if (mediciTable == null || mediciTable.getModel() == null) return;

        DefaultTableModel m = (DefaultTableModel) mediciTable.getModel();
        m.setRowCount(0);

        String filtroStato = determinaFiltroStatoSelezionato();

        for (Object[] row : datiMedici) {
            if (rigaCorrispondeAiFiltri(row, fNome, fMatricola, fSpec, fReparto, filtroStato)) {
                m.addRow(row);
            }
        }
    }

    private String determinaFiltroStatoSelezionato() {
        if (attivoRadioButton != null && attivoRadioButton.isSelected()) return "attivo";
        if (assenteRadioButton != null && assenteRadioButton.isSelected()) return "assente";
        if (occupatoRadioButton != null && occupatoRadioButton.isSelected()) return "occupato";
        return null;
    }

    private boolean rigaCorrispondeAiFiltri(Object[] row, String fNome, String fMatricola, String fSpec, String fReparto, String fStato) {
        if (row == null || row.length < 5) return false;

        String rMatricola = row[0] != null ? ((String) row[0]).toLowerCase() : "";
        String rNome = row[1] != null ? ((String) row[1]).toLowerCase() : "";
        String rSpec = (String) row[2];
        String rReparto = (String) row[3];
        String rStato = row[4] != null ? ((String) row[4]).toLowerCase() : "";

        if (fNome != null && !fNome.isEmpty() && !rNome.contains(fNome)) return false;
        if (fMatricola != null && !fMatricola.isEmpty() && !rMatricola.contains(fMatricola)) return false;
        if (fSpec != null && !fSpec.equals(rSpec)) return false;
        if (fReparto != null && !fReparto.equals(rReparto)) return false;
        if (fStato != null && !fStato.equals(rStato)) return false;

        return true;
    }
}