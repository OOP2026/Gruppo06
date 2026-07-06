package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;

public class Turni extends JFrame {

    // Dichiarazione componenti GUI
    public JPanel panelHome;
    private JTextField nomeField;
    private JTextField codiceField;
    private JSpinner dataSpinner;
    private JList<String> tipologiaList;
    private JList<String> repartoList;
    private JButton cercaButton;
    private JButton resetButton;
    private JTable turniTable;
    private JButton nuovoTurnoButton1;
    private JButton modificaTurno;

    // Struttura campi per JTable turniTable
    private static final String[] COLONNE = {
            "Data", "Matricola", "Dipendente", "Ruolo", "Reparto", "Orario Effettivo"
    };
    
    private Object[][] datiTurni = new Object[0][0];
    
    //Dichiarazione Costruttore
    public Turni() {
        initComponents();
        setupStyles();
        setupListeners();
        loadTableData(null, null, null, null);
    }

    public void aggiornaTabella(Object[][] dati) {
        this.datiTurni = dati != null ? dati : new Object[0][0];
        loadTableData(null, null, null, null);
    }

    public void addNuovoTurnoListener(java.awt.event.ActionListener listener) {
        if (nuovoTurnoButton1 != null) nuovoTurnoButton1.addActionListener(listener);
    }
    //Inizializzazione Componenti
    private void initComponents() {
        // Setup Spinner Data
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dataSpinner.setModel(dateModel);
        dataSpinner.setEditor(new JSpinner.DateEditor(dataSpinner, "dd/MM/yyyy"));

        //Dichiarazione campi JList
        tipologiaList.setListData(new String[]{
                "Medico", "Infermiere", "OSS", "Tecnico"
        });
        //Dichiarazione campi JList
        repartoList.setListData(new String[]{
                "Blocco Op.", "Terapia Intensiva", "Neuroradiologia",
                "Chirurgia Toracica", "Laboratorio Analisi", "Pronto Soccorso"
        });


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
            String nomeInput = nomeField.getText().toLowerCase().trim();
            String matricolaInput = codiceField.getText().toLowerCase().trim();
            String ruoloSelezionato = tipologiaList.getSelectedValue();
            String repartoSelezionato = repartoList.getSelectedValue();

            loadTableData(nomeInput, matricolaInput, ruoloSelezionato, repartoSelezionato);
        });

        resetButton.addActionListener(e -> {
            nomeField.setText("");
            codiceField.setText("");
            tipologiaList.clearSelection();
            repartoList.clearSelection();
            dataSpinner.setValue(new Date());

            loadTableData(null, null, null, null);
        });
    }

    private void loadTableData(String filtroNome, String filtroMatricola, String filtroRuolo, String filtroReparto) {
        DefaultTableModel m = (DefaultTableModel) turniTable.getModel();
        m.setRowCount(0);

        for (Object[] row : datiTurni) {
            String rMatricola = ((String) row[1]).toLowerCase();
            String rNome = ((String) row[2]).toLowerCase();
            String rRuolo = (String) row[3];
            String rReparto = (String) row[4];

            boolean matchNome = (filtroNome == null || filtroNome.isEmpty() || rNome.contains(filtroNome));
            boolean matchMatricola = (filtroMatricola == null || filtroMatricola.isEmpty() || rMatricola.contains(filtroMatricola));

            boolean matchRuolo = (filtroRuolo == null || rRuolo.equals(filtroRuolo));
            boolean matchReparto = (filtroReparto == null || rReparto.equals(filtroReparto));

            if (matchNome && matchMatricola && matchRuolo && matchReparto) {
                m.addRow(row);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Turni frame = new Turni();
           controller.Controller.impostaSchermata(frame, frame.panelHome, "Gestione turni lavorativi", JFrame.EXIT_ON_CLOSE);

            frame.setVisible(true);
        });
    }
}