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

    // --- Colori coordinati con la Schermata Home ---
    private static final Color AZZURRO_HOME = new Color(70, 132, 197); // Azzurro del menu
    private static final Color SELECTION_BG = new Color(187, 222, 247);
    private static final Color ALT_ROW_BG = new Color(0xf5, 0xf8, 0xfc);

    private static final Font BASE_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 12);

    // --- Dati ---
    private static final String[] COLONNE = {
            "ID Prestaz.", "Codice", "Nome Prestazione",
            "Tipo", "Reparto Erog.", "Note/Dettagli"
    };

    private static final Object[][] DATI = {
            {"ID: P001", "D001", "Intervento Chirurgia Bariatrica", "Chirurgia Gen.", "Blocco Operatorio", "Team spec."},
            {"ID: P002", "R005", "RM Cardiaca con Contrasto", "Diagnostica Av.", "Neuroradiologia", "Cardiologo pres."},
            {"ID: P003", "E101", "Colonscopia Robotica", "Procedure Endo.", "Blocco Operatorio", "Sedazione"},
            {"ID: P004", "R201", "TC Cranio Alta Risoluzione", "Radiologia Inter.", "Blocco Operatorio", "Urgenze priorità"},
            {"ID: P005", "D005", "TC Cranio Alta Risoluzione", "Radiologia Inter.", "Neuroradiologia", "Esame base"},
            {"ID: P006", "D006", "TC Cranio (base)", "Diagnostica Av.", "Laboratorio Analisi", "Esame base"},
            {"ID: P007", "D007", "Intervento Chirurgia Bariatrica", "Diagnostica Av.", "Neuroradiologia", "Team spec."},
            {"ID: P008", "D008", "Intervento Chirurgia Assistita", "Procedure Endo.", "Neuroradiologia", "Sedazione"},
            {"ID: P009", "D009", "Intervento Chirurgia Bariatrica", "Procedure Endo.", "Neuroradiologia", "Cardiologo pres."},
    };

    public Prestazioni() {
        initComponents();
        setupStyles();
        setupListeners();
        loadTableData(null, null, null, null);
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
        // Applica i colori alle liste
        styleList(tipologiaList);
        styleList(repartoList);

        // Stile Tabella
        prestazioniTable.setRowHeight(26);
        prestazioniTable.setShowGrid(false);
        prestazioniTable.setIntercellSpacing(new Dimension(0, 0));
        prestazioniTable.setSelectionBackground(SELECTION_BG);
        prestazioniTable.setSelectionForeground(Color.BLACK);
        prestazioniTable.setFont(BASE_FONT);

        // Header Tabella coordinato con l'azzurro della Home
        JTableHeader th = prestazioniTable.getTableHeader();
        th.setBackground(AZZURRO_HOME);
        th.setForeground(Color.WHITE);
        th.setFont(HEADER_FONT);
        th.setPreferredSize(new Dimension(th.getWidth(), 30));
        th.setReorderingAllowed(false);

        prestazioniTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : ALT_ROW_BG);
                    setForeground(Color.BLACK);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return this;
            }
        });

        // Applica lo stile della Home ai bottoni centrali della schermata Prestazioni
        if(cercaButton != null) applicaStilePulsantiCentrali(cercaButton);
        if(resetButton != null) applicaStilePulsantiCentrali(resetButton);
        if(storprestazioneButton != null) applicaStilePulsantiCentrali(storprestazioneButton);
        if(newprestazioneButton != null) applicaStilePulsantiCentrali(newprestazioneButton);
    }

    private void styleList(JList<String> list) {
        list.setSelectionBackground(AZZURRO_HOME);
        list.setSelectionForeground(Color.WHITE);
        list.setFont(BASE_FONT);
    }

    // --- METODI DI STILE COPIATI DALLA HOME ---
    private void applicaStilePulsantiCentrali(JButton bottone) {
        Color coloreSfondoDefault = Color.WHITE;
        Color coloreTestoDefault = Color.BLACK;

        Color coloreSfondoHover = AZZURRO_HOME;
        Color coloreTestoHover = Color.WHITE;

        impostaColoriEdEffetti(bottone, coloreSfondoDefault, coloreTestoDefault, coloreSfondoHover, coloreTestoHover);
        // Aggiungiamo un bordo sottile azzurro per definire i bottoni sul pannello bianco
        bottone.setBorder(BorderFactory.createLineBorder(AZZURRO_HOME, 1));
        bottone.setBorderPainted(true);
    }

    private void impostaColoriEdEffetti(JButton bottone, Color sfondoDefault, Color testoDefault, Color sfondoHover, Color testoHover) {
        bottone.setBackground(sfondoDefault);
        bottone.setForeground(testoDefault);
        bottone.setFocusPainted(false);
        bottone.setContentAreaFilled(true);
        bottone.setOpaque(true);
        bottone.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottone.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bottone.setBackground(sfondoHover);
                bottone.setForeground(testoHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bottone.setBackground(sfondoDefault);
                bottone.setForeground(testoDefault);
            }
        });
    }
    // ------------------------------------------

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

        for (Object[] row : DATI) {
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
            Dimension strictSize = new Dimension(1000, 680);

            frame.mainPanel.setPreferredSize(strictSize);
            frame.setContentPane(frame.mainPanel);
            frame.setTitle("Ricerca Prestazioni Mediche");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}