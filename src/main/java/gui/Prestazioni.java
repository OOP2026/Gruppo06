package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;
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
            "ID Prestazione", "Tipo Prestazione", "Esito", "Data Prestazione", "CF Paziente", "Reparto Erogante"
    };

    private Object[][] datiPrestazioni = new Object[0][0];

    public Prestazioni() {
        initComponents();
        setupStyles();
        setupListeners();
        loadTableData();
    }

    public void aggiornaTabella(Object[][] dati) {
        this.datiPrestazioni = dati != null ? dati : new Object[0][0];
        loadTableData();
    }

    public String getCodPrestazione() {
        return codiceField.getText();
    }

    public String getNomeCognome() {
        return nomeField.getText();
    }

    public String getData() {
        Object value = dataSpinner.getValue();
        if (value instanceof Date) {
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
            return formatter.format((Date) value);
        }
        return "";
    }

    public void addCercaListener(ActionListener listener) {
        if (cercaButton != null) cercaButton.addActionListener(listener);
    }

    public void addResetListener(ActionListener listener) {
        if (resetButton != null) resetButton.addActionListener(listener);
    }

    public void resetCampiRicerca() {
        nomeField.setText("");
        codiceField.setText("");
        dataSpinner.setValue(new Date());
        // Anche se non usati dal controller, è buona norma resettare tutti i filtri
        tipologiaList.clearSelection();
        repartoList.clearSelection();
    }

    public void addNuovaPrestazioneListener(java.awt.event.ActionListener listener) {
        if (newprestazioneButton != null) newprestazioneButton.addActionListener(listener);
    }

    private void initComponents() {
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dataSpinner.setModel(dateModel);
        dataSpinner.setEditor(new JSpinner.DateEditor(dataSpinner, "yyyy-MM-dd"));

        tipologiaList.setListData(new String[]{
                "Risonanza Magnetica", "Tomografia Computerizzata (TAC)", "Ecografia",
                "Elettrocardiogramma (ECG)", "Endoscopia", "Radiografia"
        });

        repartoList.setListData(new String[]{
                "Cardiologia", "Ortopedia", "Chirurgia Generale"
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
        // I listener per cerca e reset sono ora gestiti dal Controller
    }

    private void loadTableData() {
        DefaultTableModel m = (DefaultTableModel) prestazioniTable.getModel();
        m.setRowCount(0);
        for (Object[] row : datiPrestazioni) {
            m.addRow(row);
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