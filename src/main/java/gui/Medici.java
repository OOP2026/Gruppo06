package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class Medici extends JFrame {
    //Dichiarazione componenti GUI
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
    private JRadioButton tuttiRadioButton;
    private JRadioButton attivoRadioButton;
    private JRadioButton assenteRadioButton;

    //Selezione colori GUI
    private static final Color AZZURRO_HOME = new Color(70, 132, 197);
    private static final Color SELECTION_BG = new Color(187, 222, 247);
    private static final Color ALT_ROW_BG = new Color(0xf5, 0xf8, 0xfc);
    //Selezione font GUI
    private static final Font BASE_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 12);
    //Assegnazione campi presso: JTable mediciTable
    private static final String[] COLONNE = {
            "Matricola", "Cognome e Nome", "Specializzazione",
            "Reparto Assegnato", "Stato"
    };
    
    private Object[][] datiMedici = new Object[0][0];
    
    //Dichiarazione Costruttore
    public Medici() {
        initComponents();
        setupStyles();
        setupListeners();
        loadTableData(null, null, null, null);
    }

    public void aggiornaTabella(Object[][] dati) {
        this.datiMedici = dati != null ? dati : new Object[0][0];
        loadTableData(null, null, null, null);
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

    public String[] getDatiMedicoSelezionato() {
        int selectedRow = mediciTable.getSelectedRow();
        if (selectedRow == -1) {
            return null; // Nessuna riga selezionata
        }
        // La matricola è nella prima colonna (indice 0)
        return new String[]{(String) mediciTable.getValueAt(selectedRow, 0)};
    }
    //Inserimento parametri list e definizione modelli Table
    private void initComponents() {
        specializzazioneList.setListData(new String[]{
                "Chirurgia Generale", "Cardiologia", "Neurologia",
                "Anestesia", "Chirurgia Toracica", "Ematologia", "Otorinolaringoiatria"
        });

        repartoList.setListData(new String[]{
                "Blocco Operatorio", "Terapia Intensiva", "Neuroradiologia",
                "Chirurgia Toracica", "Laboratorio Analisi", "Pronto Soccorso"
        });

        // Raggruppa i radio button per consentire una sola selezione
        ButtonGroup statoGroup = new ButtonGroup();
        statoGroup.add(tuttiRadioButton);
        statoGroup.add(attivoRadioButton);
        statoGroup.add(assenteRadioButton);
        tuttiRadioButton.setSelected(true); // Imposta "Tutti" come predefinito

        DefaultTableModel model = new DefaultTableModel(COLONNE, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        mediciTable.setModel(model);
    }
    //Setup degli stili visivi per le componenti GUI
    private void setupStyles() {
        Login.styleList(specializzazioneList);
        Login.styleList(repartoList);
        Login.setupTableStyle(mediciTable);
        if(cercaButton != null) Login.applicaStilePulsantiCentrali(cercaButton);
        if(resetButton != null) Login.applicaStilePulsantiCentrali(resetButton);
        if(modificamedicoButton != null) Login.applicaStilePulsantiCentrali(modificamedicoButton);
        if(newmedicoButton != null) Login.applicaStilePulsantiCentrali(newmedicoButton);
        if(assenzaButton != null) Login.applicaStilePulsantiCentrali(assenzaButton);
    }

    private void setupListeners() { //Setup Listener e operazioni di filtraggio su Matricola
        cercaButton.addActionListener(e -> {
            String nome = nomeField.getText().toLowerCase().trim();
            String matricola = codiceField.getText().toLowerCase().trim();//codiceField viene usato per il filtraggio
            String specializzazione = specializzazioneList.getSelectedValue();
            String reparto = repartoList.getSelectedValue();

            loadTableData(nome, matricola, specializzazione, reparto);
        });

        resetButton.addActionListener(e -> {
            nomeField.setText("");
            codiceField.setText("");
            specializzazioneList.clearSelection();
            repartoList.clearSelection();
            tuttiRadioButton.setSelected(true);

            loadTableData(null, null, null, null);
        });
    }
    //Popolazione JTable mediciTable e filtraggio
    private void loadTableData(String filtroNome, String filtroMatricola, String filtroSpec, String filtroReparto) {
        DefaultTableModel m = (DefaultTableModel) mediciTable.getModel();
        m.setRowCount(0);

        for (Object[] row : datiMedici) {
            String rMatricola = ((String) row[0]).toLowerCase();
            String rNome = ((String) row[1]).toLowerCase();
            String rSpec = (String) row[2];
            String rReparto = (String) row[3];

            boolean matchNome = (filtroNome == null || filtroNome.isEmpty() || rNome.contains(filtroNome));
            boolean matchMatricola = (filtroMatricola == null || filtroMatricola.isEmpty() || rMatricola.contains(filtroMatricola));

            boolean matchSpec = (filtroSpec == null || rSpec.equals(filtroSpec));
            boolean matchReparto = (filtroReparto == null || rReparto.equals(filtroReparto));

            if (matchNome && matchMatricola && matchSpec && matchReparto) {
                m.addRow(row);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Medici frame = new Medici();
            controller.Controller.impostaSchermata(frame, frame.mainPanel, "Gestione Medici ", JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}