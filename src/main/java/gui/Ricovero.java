package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;

public class Ricovero extends JFrame {

    public JPanel JPanelPrincipale;
    private JTextField nomeField;
    private JTextField codiceField;
    private JList<String> repartoList;
    private JSpinner dataSpinner;
    private JSpinner oraSpinner;

    private JButton cercaButton;
    private JButton resetButton;
    private JTable PazientiTable;

    private JButton nuovoRicoveroButton;
    private JButton gestisciRicoveroButton;
    private JSpinner spinner1;
    private JTextField textField1;

    private static final Color AZZURRO_HOME = new Color(70, 132, 197);
    private static final Color SELECTION_BG = new Color(187, 222, 247);
    private static final Color ALT_ROW_BG = new Color(0xf5, 0xf8, 0xfc);

    private static final Font BASE_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 12);

    private static final String[] COLONNE = {
            "ID Paziente", "Paziente", "Codice Fiscale",
            "Reparto di Ricovero", "Data Ingresso, Ora Ingresso"
    };

    private static final Object[][] DATI = {
            {"1", "Mario Rossi", "RSSMRA80A01H501U", "Cardiologia", "10/05/2026", "08:30"},
            {"2", "Luigi Bianchi", "BNCLGU85M21F839O", "Cardiologia", "10/05/2026", "09:15"},
            {"3", "Giulia Verdi", "VRDGLI90C45A509Y", "Chirurgia Generale", "21/05/2026", "11:00"},
            {"4", "Elena Neri", "NRELNE75P44L219J", "Cardiologia", "21/05/2026", "14:45"},
            {"5", "Antonio Russo", "RSSNTN92B15F839V", "Cardiologia", "21/05/2026", "16:20"},
            {"6", "Sara Esposito", "SRAESP95M41F839X", "Terapia Intensiva", "22/05/2026", "02:10"},
            {"7", "Gennaro Savastano", "GNRSVS70B12H501Z", "Ortopedia", "15/05/2026", "10:05"}
    };

    public Ricovero() {
        initComponents();
        setupStyles();
        setupListeners();
        loadTableData(null, null, null);
    }

    private void initComponents() {

            // 1. Configurazione Spinner per la DATA
            if (dataSpinner != null) {
                SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
                dataSpinner.setModel(dateModel);
                dataSpinner.setEditor(new JSpinner.DateEditor(dataSpinner, "dd/MM/yyyy"));
            }

            // 2. Configurazione Spinner per l'ORA
            if (oraSpinner != null) {
                SpinnerDateModel timeModel = new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY);
                oraSpinner.setModel(timeModel);
                oraSpinner.setEditor(new JSpinner.DateEditor(oraSpinner, "HH:mm"));
            }

            // ... [qui sotto lascia il resto del codice per le liste e la tabella] ...

        if (repartoList != null) {
            repartoList.setListData(new String[]{
                    "Cardiologia", "Chirurgia Generale", "Ortopedia",
                    "Terapia Intensiva", "Pediatria", "Neurologia"
            });
        }


        if (PazientiTable != null) {
            DefaultTableModel model = new DefaultTableModel(COLONNE, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            PazientiTable.setModel(model);
        }
    }

    private void setupStyles() {
        if (repartoList != null) styleList(repartoList);

        if (PazientiTable != null) {
            PazientiTable.setRowHeight(26);
            PazientiTable.setShowGrid(false);
            PazientiTable.setIntercellSpacing(new Dimension(0, 0));
            PazientiTable.setSelectionBackground(SELECTION_BG);
            PazientiTable.setSelectionForeground(Color.BLACK);
            PazientiTable.setFont(BASE_FONT);

            JTableHeader th = PazientiTable.getTableHeader();
            th.setBackground(AZZURRO_HOME);
            th.setForeground(Color.WHITE);
            th.setFont(HEADER_FONT);
            th.setPreferredSize(new Dimension(th.getWidth(), 30));
            th.setReorderingAllowed(false);

            PazientiTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        }

        if(cercaButton != null) applicaStilePulsantiCentrali(cercaButton);
        if(resetButton != null) applicaStilePulsantiCentrali(resetButton);
        if(nuovoRicoveroButton != null) applicaStilePulsantiCentrali(nuovoRicoveroButton);
        if(gestisciRicoveroButton != null) applicaStilePulsantiCentrali(gestisciRicoveroButton);
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

        bottone.setBackground(coloreSfondoDefault);
        bottone.setForeground(coloreTestoDefault);
        bottone.setFocusPainted(false);
        bottone.setContentAreaFilled(true);
        bottone.setOpaque(true);
        bottone.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bottone.setBorder(BorderFactory.createLineBorder(AZZURRO_HOME, 1));
        bottone.setBorderPainted(true);

        bottone.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bottone.setBackground(coloreSfondoHover);
                bottone.setForeground(coloreTestoHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bottone.setBackground(coloreSfondoDefault);
                bottone.setForeground(coloreTestoDefault);
            }
        });
    }

    private void setupListeners() {
        if (cercaButton != null) {
            cercaButton.addActionListener(e -> {
                String nome = (nomeField != null) ? nomeField.getText().toLowerCase().trim() : "";
                String codice = (codiceField != null) ? codiceField.getText().toLowerCase().trim() : "";
                String reparto = (repartoList != null) ? repartoList.getSelectedValue() : null;

                loadTableData(nome, codice, reparto);
            });
        }

        if (resetButton != null) {
            resetButton.addActionListener(e -> {
                if (nomeField != null) nomeField.setText("");
                if (codiceField != null) codiceField.setText("");
                if (repartoList != null) repartoList.clearSelection();
                if (dataSpinner != null) dataSpinner.setValue(new Date());
                if (oraSpinner != null) oraSpinner.setValue(new Date());

                loadTableData(null, null, null);
            });
        }
    }

    private void loadTableData(String filtroNome, String filtroCodice, String filtroReparto) {
        if (PazientiTable == null) return;

        DefaultTableModel m = (DefaultTableModel) PazientiTable.getModel();
        m.setRowCount(0);

        for (Object[] row : DATI) {
            String rNome = ((String) row[1]).toLowerCase();
            String rCodice = ((String) row[2]).toLowerCase();
            String rReparto = (String) row[3];


            boolean matchNome = (filtroNome == null || filtroNome.isEmpty() || rNome.contains(filtroNome));
            boolean matchCodice = (filtroCodice == null || filtroCodice.isEmpty() || rCodice.contains(filtroCodice));
            boolean matchReparto = (filtroReparto == null || rReparto.equals(filtroReparto));


            if (matchNome && matchCodice && matchReparto) {
                m.addRow(row);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ricovero frame = new Ricovero();

            Dimension strictSize = new Dimension(1000, 680);

            if (frame.JPanelPrincipale != null) {
                frame.JPanelPrincipale.setPreferredSize(strictSize);
                frame.setContentPane(frame.JPanelPrincipale);
            }

            frame.setTitle("Ricerca Ricovero");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}