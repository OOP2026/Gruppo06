package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Letti extends JFrame {

    public JPanel LettiPanel;
    private JList<String> repartoList;
    private JLabel tipoLabel;
    private JList<String> tipologiaList;
    private JButton resetButton;
    private JButton cercaButton;
    private JTable prestazioniTable;
    private JButton assegnaPazienteButton;
    private JButton storicoLettiButton;
    private JRadioButton tuttiRadioButton;
    private JRadioButton disponibileRadioButton;
    private JRadioButton occupatoRadioButton;

    private JTextField stanzaPianoField;
    private JTextField pazienteField;

    private ButtonGroup statoButtonGroup;

    private static final Color AZZURRO_HOME = new Color(70, 132, 197);
    private static final Color SELECTION_BG = new Color(187, 222, 247);
    private static final Color ALT_ROW_BG = new Color(0xf5, 0xf8, 0xfc);

    private static final Font BASE_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 12);

    private static final String[] COLONNE = {
            "ID Letto", "Stanza", "Piano", "Reparto", "Tipo Letto", "Stato", "Paziente", "ID Paziente", "Data Amm."
    };

    private static final Object[][] DATI = {
            {"L001", "R101", "1", "Medicina Interna", "Standard", "Disponibile", "-", "-", "-"},
            {"L002", "R102", "1", "Medicina Interna", "Standard", "Occupato", "Rossi Mario", "12345", "12/10/2023"},
            {"L003", "R103", "1", "Chirurgia Generale", "Standard", "Occupato", "Bianchi Luigi", "23456", "14/10/2023"},
            {"L004", "R201", "2", "Bariatria", "Monitorato", "Disponibile", "-", "-", "-"},
            {"L005", "R202", "2", "Bariatria", "Monitorato", "Occupato", "Verdi Luca", "34567", "15/10/2023"},
            {"L006", "R301", "3", "Terapia Intensiva", "ICU", "Occupato", "Neri Anna", "45678", "16/10/2023"},
    };

    public Letti() {

        initComponents();
        setupStyles();
        setupListeners();
        loadTableData(null, null, "Tutti", "", "");
    }

    private void initComponents() {
        repartoList.setListData(new String[]{
                "Chirurgia Generale", "Bariatria", "Medicina Interna",
                "Pediatria", "Terapia Intensiva", "Oncologia", "Pronto Soccorso"
        });

        tipologiaList.setListData(new String[]{
                "Standard", "Monitorato", "ICU", "Pediatrico"
        });

        statoButtonGroup = new ButtonGroup();
        statoButtonGroup.add(tuttiRadioButton);
        statoButtonGroup.add(disponibileRadioButton);
        statoButtonGroup.add(occupatoRadioButton);
        tuttiRadioButton.setSelected(true);

        DefaultTableModel model = new DefaultTableModel(COLONNE, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        prestazioniTable.setModel(model);
    }

    private void setupStyles() {
        styleList(tipologiaList);
        styleList(repartoList);

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

        prestazioniTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            Icon greenDot = new CircleIcon(new Color(40, 167, 69));
            Icon redDot = new CircleIcon(new Color(220, 53, 69));

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : ALT_ROW_BG);
                    setForeground(Color.BLACK);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

                if (value != null) {
                    String stato = value.toString();
                    if ("Disponibile".equals(stato)) {
                        setIcon(greenDot);
                    } else if ("Occupato".equals(stato)) {
                        setIcon(redDot);
                    } else {
                        setIcon(null);
                    }
                }
                return this;
            }
        });

        if(cercaButton != null) applicaStilePulsantiCentrali(cercaButton);
        if(resetButton != null) applicaStilePulsantiCentrali(resetButton);
        if(assegnaPazienteButton != null) applicaStilePulsantiCentrali(assegnaPazienteButton);
        if(storicoLettiButton != null) applicaStilePulsantiCentrali(storicoLettiButton);
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
        cercaButton.addActionListener(e -> {
            String reparto = repartoList.getSelectedValue();
            String tipologia = tipologiaList.getSelectedValue();

            String stato = "Tutti";
            if (disponibileRadioButton != null && disponibileRadioButton.isSelected()) stato = "Disponibile";
            else if (occupatoRadioButton != null && occupatoRadioButton.isSelected()) stato = "Occupato";

            String stanzaPiano = stanzaPianoField != null ? stanzaPianoField.getText().trim().toLowerCase() : "";
            String paziente = pazienteField != null ? pazienteField.getText().trim().toLowerCase() : "";

            loadTableData(reparto, tipologia, stato, stanzaPiano, paziente);
        });

        resetButton.addActionListener(e -> {
            repartoList.clearSelection();
            tipologiaList.clearSelection();
            if (tuttiRadioButton != null) tuttiRadioButton.setSelected(true);
            if (stanzaPianoField != null) stanzaPianoField.setText("");
            if (pazienteField != null) pazienteField.setText("");

            loadTableData(null, null, "Tutti", "", "");
        });
    }

    private void loadTableData(String fReparto, String fTipo, String fStato, String fStanza, String fPaziente) {
        DefaultTableModel m = (DefaultTableModel) prestazioniTable.getModel();
        m.setRowCount(0);

        for (Object[] row : DATI) {
            String rStanza = ((String) row[1]).toLowerCase();
            String rPiano = ((String) row[2]).toLowerCase();
            String rReparto = (String) row[3];
            String rTipo = (String) row[4];
            String rStato = (String) row[5];
            String rPazNome = ((String) row[6]).toLowerCase();
            String rPazID = ((String) row[7]).toLowerCase();

            boolean matchStato = fStato.equals("Tutti") || rStato.equals(fStato);
            boolean matchReparto = (fReparto == null || rReparto.equals(fReparto));
            boolean matchTipo = (fTipo == null || rTipo.equals(fTipo));

            boolean matchStanza = fStanza.isEmpty() || rStanza.contains(fStanza) || rPiano.contains(fStanza);
            boolean matchPaziente = fPaziente.isEmpty() || rPazNome.contains(fPaziente) || rPazID.contains(fPaziente);

            if (matchStato && matchReparto && matchTipo && matchStanza && matchPaziente) {
                m.addRow(row);
            }
        }
    }

    private static class CircleIcon implements Icon {
        private final Color color;
        private final int size = 12;

        public CircleIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(x, y + 1, size, size);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size + 4;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Letti frame = new Letti();
            Dimension strictSize = new Dimension(1100, 680);

            if (frame.LettiPanel != null) {
                frame.LettiPanel.setPreferredSize(strictSize);
                frame.setContentPane(frame.LettiPanel);
            }

            frame.setTitle("Gestione Letti e Ricoveri");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}