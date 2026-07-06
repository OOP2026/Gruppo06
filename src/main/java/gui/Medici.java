package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;

public class Medici extends JFrame {
    //Dichiarazione componenti GUI
    public JPanel mainPanel;
    private JTextField nomeField;
    private JTextField codiceField;
    private JSpinner dataSpinner;
    private JButton resetButton;
    private JButton cercaButton;
    private JTable mediciTable;
    private JButton refreshList;
    private JButton newMedico;

    private JList<String> specializzazioneList;
    private JList<String> repartoList;
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
            "Reparto Assegnato", "Stato", "Note/Contatto"
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
        if (newMedico != null) newMedico.addActionListener(listener);
    }
    //Inserimento parametri list e definizione modelli Table
    private void initComponents() {
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dataSpinner.setModel(dateModel);
        dataSpinner.setEditor(new JSpinner.DateEditor(dataSpinner, "dd/MM/yyyy"));

        specializzazioneList.setListData(new String[]{
                "Chirurgia Generale", "Cardiologia", "Neurologia",
                "Anestesia", "Chirurgia Toracica", "Ematologia", "Otorinolaringoiatria"
        });

        repartoList.setListData(new String[]{
                "Blocco Operatorio", "Terapia Intensiva", "Neuroradiologia",
                "Chirurgia Toracica", "Laboratorio Analisi", "Pronto Soccorso"
        });

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
        styleList(specializzazioneList);
        styleList(repartoList);

        mediciTable.setRowHeight(26);
        mediciTable.setShowGrid(false);
        mediciTable.setIntercellSpacing(new Dimension(0, 0));
        mediciTable.setSelectionBackground(SELECTION_BG);
        mediciTable.setSelectionForeground(Color.BLACK);
        mediciTable.setFont(BASE_FONT);

        JTableHeader th = mediciTable.getTableHeader();
        th.setBackground(AZZURRO_HOME);
        th.setForeground(Color.WHITE);
        th.setFont(HEADER_FONT);
        th.setPreferredSize(new Dimension(th.getWidth(), 30));
        th.setReorderingAllowed(false);

        mediciTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        if(refreshList != null) applicaStilePulsantiCentrali(refreshList);
        if(newMedico != null) applicaStilePulsantiCentrali(newMedico);
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
            dataSpinner.setValue(new Date());

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
            Dimension strictSize = new Dimension(1000, 680);

            frame.mainPanel.setPreferredSize(strictSize);
            frame.mainPanel.setMinimumSize(strictSize);
            frame.mainPanel.setMaximumSize(strictSize);

            frame.setContentPane(frame.mainPanel);
            frame.setTitle("Gestione Medici ");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setSize(1000, 680);
            frame.setResizable(false);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}