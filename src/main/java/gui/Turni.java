package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Calendar;
import java.util.Date;

public class Turni extends JFrame {

    public JPanel panelHome;
    private JTextField nomeField;
    private JSpinner dataSpinner;
    private JList<String> tipologiaList;
    private JList<String> repartoList;
    private JButton cercaButton;
    private JButton resetButton;
    private JTable turniTable;
    private JButton nuovoTurnoButton1;
    private JButton modificaTurno;
    private JTextField idTurnoField;

    private static final String[] COLONNE = {
            "ID Turno", "Data", "Matricola", "Dipendente", "Ruolo", "Reparto", "Orario Effettivo" // Aggiornato a 7 colonne
    };

    private static final String[] TIPOLOGIA_DATA = {
            "Medico", "Amministratore"
    };

    private static final String[] REPARTI_DATA = {
            "Chirurgia Generale", "Cardiologia", "Ortopedia",
            "Pediatria", "Terapia Intensiva", "Pronto Soccorso",
            "Bariatria", "Radiologia Interventistica", "Nessuno"
    };
    
    private transient Object[][] datiTurni = new Object[0][0];
    
    public Turni() {
        initComponents();
        setupStyles();
        setupListeners();
        loadTableData(null, null, null, null, null, null); // Chiamata con 6 parametri
    }

    public void aggiornaTabella(Object[][] dati) {
        this.datiTurni = dati != null ? dati : new Object[0][0];
        loadTableData(null, null, null, null, null, null); // Chiamata con 6 parametri
    }

    public void addNuovoTurnoListener(java.awt.event.ActionListener listener) {
        nuovoTurnoButton1.addActionListener(listener);
    }

    public void addModificaTurnoListener(java.awt.event.ActionListener listener) {
        modificaTurno.addActionListener(listener);
    }

    public String[] getDatiTurnoSelezionato() {
        int selectedRow = turniTable.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) turniTable.getModel();
            String data = (String) model.getValueAt(selectedRow, 1); // Data è ora la colonna 1
            String matricola = (String) model.getValueAt(selectedRow, 2); // Matricola è ora la colonna 2
            String orarioEffettivo = (String) model.getValueAt(selectedRow, 6); // Orario Effettivo è ora la colonna 6
            return new String[]{data, matricola, orarioEffettivo};
        }
        return new String[0];
    }

    private void initComponents() {
        // Setup Spinner Data
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dataSpinner.setModel(dateModel);
        dataSpinner.setEditor(new JSpinner.DateEditor(dataSpinner, "yyyy-MM-dd")); // Formato data YYYY-MM-DD

        //Dichiarazione campi JList
        tipologiaList.setListData(TIPOLOGIA_DATA);
        //Dichiarazione campi JList
        repartoList.setListData(REPARTI_DATA);
        
        DefaultTableModel model = new DefaultTableModel(COLONNE, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        turniTable.setModel(model);
    }
    //Set di stili visivi per le componenti GUI
    private void setupStyles() {
        Login.styleList(tipologiaList);
        Login.styleList(repartoList);
        Login.setupTableStyle(turniTable);
        if(cercaButton != null) Login.applicaStilePulsantiCentrali(cercaButton);
        if(resetButton != null) Login.applicaStilePulsantiCentrali(resetButton);
        if(nuovoTurnoButton1 != null) Login.applicaStilePulsantiCentrali(nuovoTurnoButton1);
        if(modificaTurno != null) Login.applicaStilePulsantiCentrali(modificaTurno);
    }

    private void setupListeners() {
        cercaButton.addActionListener(e -> {
            String idTurnoInput = idTurnoField.getText().trim(); // Ottieni ID Turno
            String nomeInput = nomeField.getText().toLowerCase().trim();
            String ruoloSelezionato = tipologiaList.getSelectedValue();
            String repartoSelezionato = repartoList.getSelectedValue();
            String dataInput = new JSpinner.DateEditor(dataSpinner, "yyyy-MM-dd").getFormat().format(dataSpinner.getValue()); // Ottieni data formattata

            // Se l'utente cerca per ID Turno specifico, ignoriamo il filtro della data (che di default punta a oggi)
            if (!idTurnoInput.isEmpty()) {
                dataInput = "";
            }

            loadTableData(idTurnoInput, nomeInput, null, ruoloSelezionato, repartoSelezionato, dataInput);
        });

        resetButton.addActionListener(e -> {
            idTurnoField.setText(""); // Reset campo ID Turno
            nomeField.setText("");
            tipologiaList.clearSelection();
            repartoList.clearSelection();
            dataSpinner.setValue(new Date());

            loadTableData(null, null, null, null, null, null); // Reset filtri
        });
    }

    private boolean isTurnoCorrispondente(Object[] row, String filtroIdTurno, String filtroNome, String filtroMatricola, String filtroRuolo, String filtroReparto, String filtroData) {
        String rIdTurno = row[0] != null ? ((String) row[0]).toLowerCase() : "";
        String rData = row[1] != null ? ((String) row[1]).toLowerCase() : "";
        String rMatricola = row[2] != null ? ((String) row[2]).toLowerCase() : "";
        String rNome = row[3] != null ? ((String) row[3]).toLowerCase() : ""; // Dipendente
        String rRuolo = row[4] != null ? (String) row[4] : "";
        String rReparto = row[5] != null ? ((String) row[5]).toLowerCase() : "";

        boolean matchIdTurno = (filtroIdTurno == null || filtroIdTurno.isEmpty() || rIdTurno.equals(filtroIdTurno.toLowerCase()));
        boolean matchData = (filtroData == null || filtroData.isEmpty() || rData.equals(filtroData.toLowerCase()));
        boolean matchNome = (filtroNome == null || filtroNome.isEmpty() || rNome.contains(filtroNome));
        boolean matchMatricola = (filtroMatricola == null || filtroMatricola.isEmpty() || rMatricola.contains(filtroMatricola));
        boolean matchRuolo = (filtroRuolo == null || rRuolo.equalsIgnoreCase(filtroRuolo));
        boolean matchReparto = (filtroReparto == null || rReparto.equalsIgnoreCase(filtroReparto));

        return matchIdTurno && matchData && matchNome && matchMatricola && matchRuolo && matchReparto;
    }

    private void loadTableData(String filtroIdTurno, String filtroNome, String filtroMatricola, String filtroRuolo, String filtroReparto, String filtroData) {
        DefaultTableModel m = (DefaultTableModel) turniTable.getModel();
        m.setRowCount(0);

        for (Object[] row : datiTurni) {
            if (isTurnoCorrispondente(row, filtroIdTurno, filtroNome, filtroMatricola, filtroRuolo, filtroReparto, filtroData)) {
                m.addRow(row);
            }
        }
    }

}