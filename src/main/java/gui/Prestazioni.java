package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class Prestazioni extends JFrame {

    public JPanel panel1;
    private JButton cercaButton;
    private JButton resetButton;
    private JSpinner spinner1;
    private JTable table1;
    private JList list2;
    private JLabel Tipo;
    private JList list1;
    private JButton button1;
    private JButton button2;
    private JTextField textField1;
    private JTextField textField2;

    // Dati completi
    private final Object[][] DATI = {
            {"ID: P001","D001","Intervento Chirurgia Bariatrica","Chirurgia Gen.","Blocco Operatorio","Team spec."},
            {"ID: P002","R005","RM Cardiaca con Contrasto","Diagnostica Av.","Neuroradiologia","Cardiologo pres."},
            {"ID: P003","E101","Colonscopia Robotica","Procedure Endo.","Blocco Operatorio","Sedazione"},
            {"ID: P004","R201","TC Cranio Alta Risoluzione","Radiologia Inter.","Blocco Operatorio","Urgenze priorità"},
            {"ID: P005","D005","TC Cranio Alta Risoluzione","Radiologia Inter.","Neuroradiologia","Esame base"},
            {"ID: P006","D006","TC Cranio (base)","Diagnostica Av.","Laboratorio Analisi","Esame base"},
            {"ID: P007","D007","Intervento Chirurgia Bariatrica","Diagnostica Av.","Neuroradiologia","Team spec."},
            {"ID: P008","D008","Intervento Chirurgia Assistita","Procedure Endo.","Neuroradiologia","Sedazione"},
            {"ID: P009","D009","Intervento Chirurgia Bariatrica","Procedure Endo.","Neuroradiologia","Cardiologo pres."},
    };

    public Prestazioni() {

        // ── Spinner data ──────────────────────────────
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        spinner1.setModel(dateModel);
        spinner1.setEditor(new JSpinner.DateEditor(spinner1, "dd/MM/yyyy"));

        // ── Popola list1 (Tipologia) ──────────────────
        String[] tipologie = {
                "Chirurgia Generale", "Radiologia Interventistica",
                "Diagnostica Avanzata", "Chirurgia Robotica",
                "Procedure Endoscopiche", "Radioterapia",
                "Cardiologia", "Oncologia"
        };
        list1.setListData(tipologie);
        list1.setSelectionBackground(new Color(0x1a, 0x5f, 0xa0));
        list1.setSelectionForeground(Color.WHITE);
        list1.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // ── Popola list2 (Reparto) ────────────────────
        String[] reparti = {
                "Chirurgia Robotica", "Neuroradiologia",
                "Blocco Operatorio", "Chirurgia Toracica",
                "Anatomia Patologica", "Laboratorio Analisi",
                "Radiologia Interventistica"
        };
        list2.setListData(reparti);
        list2.setSelectionBackground(new Color(0x1a, 0x5f, 0xa0));
        list2.setSelectionForeground(Color.WHITE);
        list2.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // ── Tabella ───────────────────────────────────
        String[] colonne = {"ID Prestaz.", "Codice", "Nome Prestazione",
                "Tipo", "Reparto Erog.", "Note/Dettagli"};
        DefaultTableModel model = new DefaultTableModel(colonne, 0);
        for (Object[] row : DATI) model.addRow(row);
        table1.setModel(model);

        table1.setRowHeight(26);
        table1.setShowGrid(false);
        table1.setIntercellSpacing(new Dimension(0, 0));
        table1.setSelectionBackground(new Color(187, 222, 247));
        table1.setSelectionForeground(Color.BLACK);
        table1.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JTableHeader th = table1.getTableHeader();
        th.setBackground(new Color(0x1a, 0x3a, 0x5c));
        th.setForeground(Color.WHITE);
        th.setFont(new Font("SansSerif", Font.BOLD, 12));
        th.setPreferredSize(new Dimension(th.getWidth(), 30));
        th.setReorderingAllowed(false);

        // Righe alternate
        table1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xf5, 0xf8, 0xfc));
                    setForeground(Color.BLACK);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return this;
            }
        });

        // ── Bottone Cerca ─────────────────────────────
        cercaButton.addActionListener(e -> {
            String nome   = textField1.getText().toLowerCase().trim();
            String codice = textField2.getText().toLowerCase().trim();

            DefaultTableModel m = (DefaultTableModel) table1.getModel();
            m.setRowCount(0);

            for (Object[] row : DATI) {
                String rNome   = ((String) row[2]).toLowerCase();
                String rCodice = ((String) row[1]).toLowerCase();

                boolean okNome   = nome.isEmpty()   || rNome.contains(nome);
                boolean okCodice = codice.isEmpty() || rCodice.contains(codice);

                if (okNome && okCodice) m.addRow(row);
            }
        });

        // ── Bottone Reset ─────────────────────────────
        resetButton.addActionListener(e -> {
            textField1.setText("");
            textField2.setText("");
            list1.clearSelection();
            list2.clearSelection();
            spinner1.setValue(new Date());

            DefaultTableModel m = (DefaultTableModel) table1.getModel();
            m.setRowCount(0);
            for (Object[] row : DATI) m.addRow(row);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Prestazioni frame = new Prestazioni();

            Dimension strictSize = new Dimension(1000, 680);
            frame.panel1.setPreferredSize(strictSize);
            frame.panel1.setMinimumSize(strictSize);
            frame.panel1.setMaximumSize(strictSize);

            frame.setContentPane(frame.panel1);
            frame.setTitle("Ricerca Prestazioni Mediche");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 680);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}