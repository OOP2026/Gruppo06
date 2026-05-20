package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;

public class Pazienti extends JFrame {

    private JPanel panelPrincipale;
    private JTextField nomeField;
    private JTextField codiceField;
    private JLabel tipoLabel;
    private JList<String> tipologiaList;
    private JButton resetButton;
    private JButton cercaButton;
    private JTable prestazioniTable;
    private JButton nuovoPazienteButton;
    private JButton storicoPazienteButton;
    private JTextField idField;
    private JTextField residenzaField;
    private JSpinner dataSpinner;
    private JTextField ricercaprognosiField;
    private JRadioButton femminaRadioButton;
    private JRadioButton maschioRadioButton;

    private static final Color AZZURRO_HOME = new Color(70, 132, 197);
    private static final Color SELECTION_BG = new Color(187, 222, 247);
    private static final Color ALT_ROW_BG = new Color(0xf5, 0xf8, 0xfc);

    private static final Font BASE_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 12);

    private static final String[] COLONNE = {
            "ID Paziente", "Nome e Cognome", "Codice Fiscale",
            "Sesso", "Residenza", "Stato"
    };

    private static final Object[][] DATI = {
            {"ID: 12345", "Mario Rossi", "RSSMRA80A01H501U", "Maschio", "Napoli", "Ricoverato"},
            {"ID: 12346", "Luigi Bianchi", "BNCLGU85M21F839O", "Maschio", "Roma", "Dimesso"},
            {"ID: 12347", "Giulia Verdi", "VRDGLI90C45A509Y", "Femmina", "Milano", "Ricoverato"},
            {"ID: 12348", "Elena Neri", "NRELNE75P44L219J", "Femmina", "Torino", "In Trasferimento"},
            {"ID: 12349", "Antonio Russo", "RSSNTN92B15F839V", "Maschio", "Palermo", "Dimesso"}
    };

    public Pazienti() {
        initComponents();
        setupStyles();
    }

    private void initComponents() {
        DefaultTableModel model = new DefaultTableModel(DATI, COLONNE) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        prestazioniTable.setModel(model);

        if (tipologiaList != null) {
            tipologiaList.setListData(new String[]{
                    "Chirurgia Generale", "Cardiologia", "Ortopedia",
                    "Pediatria", "Terapia Intensiva", "Pronto Soccorso"
            });
        }

        if (dataSpinner != null) {
            SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
            dataSpinner.setModel(dateModel);
            dataSpinner.setEditor(new JSpinner.DateEditor(dataSpinner, "dd/MM/yyyy"));
        }
    }

    private void setupStyles() {
        prestazioniTable.setRowHeight(26);
        prestazioniTable.setShowGrid(false);
        prestazioniTable.setIntercellSpacing(new Dimension(0, 0));
        prestazioniTable.setSelectionBackground(SELECTION_BG);
        prestazioniTable.setSelectionForeground(Color.BLACK);
        prestazioniTable.setFont(BASE_FONT);

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

        if (tipologiaList != null) {
            tipologiaList.setSelectionBackground(AZZURRO_HOME);
            tipologiaList.setSelectionForeground(Color.WHITE);
            tipologiaList.setFont(BASE_FONT);
        }

        if(cercaButton != null) applicaStilePulsantiCentrali(cercaButton);
        if(resetButton != null) applicaStilePulsantiCentrali(resetButton);
        if(nuovoPazienteButton != null) applicaStilePulsantiCentrali(nuovoPazienteButton);
        if(storicoPazienteButton != null) applicaStilePulsantiCentrali(storicoPazienteButton);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Pazienti frame = new Pazienti();

            Dimension strictSize = new Dimension(1000, 680);
            frame.panelPrincipale.setPreferredSize(strictSize);

            frame.setContentPane(frame.panelPrincipale);
            frame.setTitle("Ricerca Pazienti");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}