package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;

public class Dimissioni extends JFrame {

    public JPanel JpanelPrincipale;
    private JTextField nomeCognomeField1;
    private JTextField codiceField;
    private JTextField idPazienteField;
    private JList<String> tipologiaList;
    private JList<String> tipoDimissioneList;
    private JSpinner spinner1;
    private JButton cercaButton;
    private JButton resetButton;
    private JTable PazientiTable;
    private JButton letturaDimissioneButton;
    private JButton archiviaDimissioneButton;
    private JTextField nomeCognomeField;

    private static final Color AZZURRO_HOME = new Color(70, 132, 197);
    private static final Color SELECTION_BG = new Color(187, 222, 247);
    private static final Color ALT_ROW_BG = new Color(0xf5, 0xf8, 0xfc);
    private static final Font BASE_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 12);

    private static final String[] COLONNE = {
            "ID Paziente", "Paziente", "Codice Fiscale",
            "Reparto Dimissione", "Tipo Dimissione", "Data Dimissione"
    };

    public Dimissioni() {
        initComponents();
        setupStyles();
    }

    public void aggiornaTabella(Object[][] dati) {
        if (PazientiTable != null) {
            DefaultTableModel model = (DefaultTableModel) PazientiTable.getModel();
            model.setRowCount(0);
            if (dati != null) {
                for (Object[] riga : dati) {
                    model.addRow(riga);
                }
            }
        }
    }

    public void addArchiviaDimissioneListener(java.awt.event.ActionListener listener) {
        if (archiviaDimissioneButton != null) archiviaDimissioneButton.addActionListener(listener);
    }

    private void initComponents() {
        DefaultTableModel model = new DefaultTableModel(new Object[0][0], COLONNE) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (PazientiTable != null) {
            PazientiTable.setModel(model);
        }

        if (tipologiaList != null) {
            tipologiaList.setListData(new String[]{
                    "Chirurgia Generale", "Bariatria", "Radiologia Interventistica",
                    "Cardiologia", "Terapia Intensiva"
            });
        }

        if (tipoDimissioneList != null) {
            tipoDimissioneList.setListData(new String[]{
                    "Ordinaria",
                    "Trasferimento",
                    "Volontaria",
                    "Decesso"
            });
        }

        if (spinner1 != null) {
            SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
            spinner1.setModel(dateModel);
            spinner1.setEditor(new JSpinner.DateEditor(spinner1, "dd/MM/yyyy"));
        }
    }

    private void setupStyles() {
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

        if (tipologiaList != null) {
            tipologiaList.setSelectionBackground(AZZURRO_HOME);
            tipologiaList.setSelectionForeground(Color.WHITE);
            tipologiaList.setFont(BASE_FONT);
        }

        if (tipoDimissioneList != null) {
            tipoDimissioneList.setSelectionBackground(AZZURRO_HOME);
            tipoDimissioneList.setSelectionForeground(Color.WHITE);
            tipoDimissioneList.setFont(BASE_FONT);
        }

        if(cercaButton != null) applicaStilePulsantiCentrali(cercaButton);
        if(resetButton != null) applicaStilePulsantiCentrali(resetButton);
        if(letturaDimissioneButton != null) applicaStilePulsantiCentrali(letturaDimissioneButton);
        if(archiviaDimissioneButton != null) applicaStilePulsantiCentrali(archiviaDimissioneButton);
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
            Dimissioni frame = new Dimissioni();

            Dimension strictSize = new Dimension(1000, 680);
            if (frame.JpanelPrincipale != null) {
                frame.JpanelPrincipale.setPreferredSize(strictSize);
                frame.setContentPane(frame.JpanelPrincipale);
            }

            frame.setTitle("Ricerca Dimissioni");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}