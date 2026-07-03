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
    private JLabel mansioneLabel;
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

    // Impostazione font e colori
    private static final Color AZZURRO_HOME = new Color(70, 132, 197);
    private static final Color SELECTION_BG = new Color(187, 222, 247);
    private static final Color ALT_ROW_BG = new Color(0xf5, 0xf8, 0xfc);

    private static final Font BASE_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 12);

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
        styleList(tipologiaList);
        styleList(repartoList);

        // Stile Tabella
        turniTable.setRowHeight(26);
        turniTable.setShowGrid(false);
        turniTable.setIntercellSpacing(new Dimension(0, 0));
        turniTable.setSelectionBackground(SELECTION_BG);
        turniTable.setSelectionForeground(Color.BLACK);
        turniTable.setFont(BASE_FONT);

        JTableHeader th = turniTable.getTableHeader();
        th.setBackground(AZZURRO_HOME);
        th.setForeground(Color.WHITE);
        th.setFont(HEADER_FONT);
        th.setPreferredSize(new Dimension(th.getWidth(), 30));
        th.setReorderingAllowed(false);

        // Render Righe Alternate
        turniTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        if(cercaButton != null) applicaStilePulsantiCentrali(cercaButton);
        if(resetButton != null) applicaStilePulsantiCentrali(resetButton);
        if(nuovoTurnoButton1 != null) applicaStilePulsantiCentrali(nuovoTurnoButton1);
        if(modificaTurno != null) applicaStilePulsantiCentrali(modificaTurno); // <-- Aggiunto qui
    }

    private void styleList(JList<String> list) {
        list.setSelectionBackground(AZZURRO_HOME);
        list.setSelectionForeground(Color.WHITE);
        list.setFont(BASE_FONT);
    }

    private void applicaStilePulsantiCentrali(JButton bottone) {
        Color coloreSfondoDefault = Color.WHITE;
        Color coloreTestoDefault = Color.BLACK;

        Color coloreSfondoHover = AZZURRO_HOME;
        Color coloreTestoHover = Color.WHITE;

        impostaColoriEdEffetti(bottone, coloreSfondoDefault, coloreTestoDefault, coloreSfondoHover, coloreTestoHover);
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
            Dimension strictSize = new Dimension(1000, 680);

            // Lock delle dimensioni direttamente sul panelHome
            frame.panelHome.setPreferredSize(strictSize);
            frame.panelHome.setMinimumSize(strictSize);
            frame.panelHome.setMaximumSize(strictSize);
            frame.setContentPane(frame.panelHome);

            frame.setTitle("Gestione Turni Lavorativi - Ospedale San Raffaele");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();

            // Lock delle dimensioni sul Frame
            frame.setSize(1000, 680);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);

            frame.setVisible(true);
        });
    }
}