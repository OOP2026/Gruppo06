package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;

public class Prestazioni extends JFrame {

    public JPanel mainPanel;
    private JButton cercaButton;
    private JButton resetButton;
    private JSpinner dataSpinner;
    private JTable prestazioniTable;
    private JList<String> repartoList;
    private JList<String> tipologiaList;
    private JLabel tipoLabel;
    private JButton storprestazioneButton;
    private JButton newprestazioneButton;
    private JTextField nomeField;
    private JTextField codiceField;

    private static final String[] COLONNE = {
            "ID Prestaz.", "Codice", "Nome Prestazione",
            "Tipo", "Reparto Erog.", "Note/Dettagli"
    };

    private Object[][] datiPrestazioni = new Object[0][0];

    public Prestazioni() {
        initComponents();
        setupStyles();
        setupListeners();
        loadTableData(null, null, null, null);
    }

    public void aggiornaTabella(Object[][] dati) {
        this.datiPrestazioni = dati != null ? dati : new Object[0][0];
        loadTableData(null, null, null, null);
    }

    public void addNuovaPrestazioneListener(java.awt.event.ActionListener listener) {
        if (newprestazioneButton != null) newprestazioneButton.addActionListener(listener);
    }

    private void initComponents() {
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dataSpinner.setModel(dateModel);
        dataSpinner.setEditor(new JSpinner.DateEditor(dataSpinner, "dd/MM/yyyy"));

        tipologiaList.setListData(new String[]{
                "Chirurgia Generale", "Radiologia Interventistica", "Diagnostica Avanzata",
                "Chirurgia Robotica", "Procedure Endoscopiche", "Radioterapia", "Cardiologia", "Oncologia"
        });

        repartoList.setListData(new String[]{
                "Chirurgia Robotica", "Neuroradiologia", "Blocco Operatorio",
                "Chirurgia Toracica", "Anatomia Patologica", "Laboratorio Analisi", "Radiologia Interventistica"
        });

        DefaultTableModel model = new DefaultTableModel(COLONNE, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        prestazioniTable.setModel(model);
    }

    private void setupStyles() {
        Login.styleList(tipologiaList);
        Login.styleList(repartoList);
        Login.setupTableStyle(prestazioniTable);
        if(cercaButton != null) Login.applicaStilePulsantiCentrali(cercaButton);
        if(resetButton != null) Login.applicaStilePulsantiCentrali(resetButton);
        if(storprestazioneButton != null) Login.applicaStilePulsantiCentrali(storprestazioneButton);
        if(newprestazioneButton != null) Login.applicaStilePulsantiCentrali(newprestazioneButton);
    }

    private void setupListeners() {
        cercaButton.addActionListener(e -> {
            String nome = nomeField.getText().toLowerCase().trim();
            String codice = codiceField.getText().toLowerCase().trim();
            String tipologia = tipologiaList.getSelectedValue();
            String reparto = repartoList.getSelectedValue();

            loadTableData(nome, codice, tipologia, reparto);
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

    private void loadTableData(String filtroNome, String filtroCodice, String filtroTipo, String filtroReparto) {
        DefaultTableModel m = (DefaultTableModel) prestazioniTable.getModel();
        m.setRowCount(0);

        for (Object[] row : datiPrestazioni) {
            String rCodice = ((String) row[1]).toLowerCase();
            String rNome = ((String) row[2]).toLowerCase();
            String rTipo = (String) row[3];
            String rReparto = (String) row[4];

            boolean matchNome = (filtroNome == null || filtroNome.isEmpty() || rNome.contains(filtroNome));
            boolean matchCodice = (filtroCodice == null || filtroCodice.isEmpty() || rCodice.contains(filtroCodice));

            boolean matchTipo = (filtroTipo == null || rTipo.equals(filtroTipo));
            boolean matchReparto = (filtroReparto == null || rReparto.equals(filtroReparto));

            if (matchNome && matchCodice && matchTipo && matchReparto) {
                m.addRow(row);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Prestazioni frame = new Prestazioni();
            controller.Controller.impostaSchermata(frame, frame.mainPanel, "Ricerca Prestazioni Mediche", JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}