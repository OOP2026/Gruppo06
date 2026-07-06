package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.awt.event.ActionListener;
import java.util.Date;

public class Ricovero extends JFrame {

    public JPanel mainPanel;
    private JTextField nomeField;
    private JTextField codiceField;
    private JTextField idField;
    private JList<String> repartoList;
    private JSpinner dataSpinner;
    private JSpinner oraSpinner;

    private JButton cercaButton;
    private JButton resetButton;
    private JTable ricoveriTable;

    private JButton nuovoRicoveroButton;
    private JButton gestisciRicoveroButton;


    private static final Color AZZURRO_HOME = new Color(70, 132, 197);
    private static final Color SELECTION_BG = new Color(187, 222, 247);
    private static final Color ALT_ROW_BG = new Color(0xf5, 0xf8, 0xfc);

    private static final Font BASE_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 12);

    private static final String[] COLONNE = {
            "ID Paziente", "Paziente", "Codice Fiscale",
            "Reparto di Ricovero", "Data Ingresso", "Ora Ingresso"
    };

    public Ricovero() {
        initComponents();
        setupStyles();
    }

    public void aggiornaTabella(Object[][] dati) {
        DefaultTableModel model = (DefaultTableModel) ricoveriTable.getModel();
        model.setRowCount(0); // Pulisce la tabella
        if (dati != null) {
            for (Object[] riga : dati) {
                model.addRow(riga);
            }
        }
    }

    // Metodi per aggiungere i listener ai pulsanti
    public void addNuovoRicoveroListener(ActionListener listener) {
        nuovoRicoveroButton.addActionListener(listener);
    }

    public void addGestisciRicoveroListener(ActionListener listener) {
        gestisciRicoveroButton.addActionListener(listener);
    }

    public void addCercaListener(ActionListener listener) {
        cercaButton.addActionListener(listener);
    }

    public void addResetListener(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    // Metodi per ottenere i valori dai campi di input
    public String getNome() {
        return nomeField.getText();
    }

    public String getCodiceFiscale() {
        return codiceField.getText();
    }

    public String getIdPaziente() {
        return idField.getText();
    }

    public String getRepartoSelezionato() {
        return repartoList.getSelectedValue();
    }

    public void resetCampiRicerca() {
        nomeField.setText("");
        codiceField.setText("");
        idField.setText("");
        repartoList.clearSelection();
        dataSpinner.setValue(new Date());
        oraSpinner.setValue(new Date());
    }

    private void initComponents() {
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dataSpinner.setModel(dateModel);
        dataSpinner.setEditor(new JSpinner.DateEditor(dataSpinner, "dd/MM/yyyy"));

        SpinnerDateModel timeModel = new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY);
        oraSpinner.setModel(timeModel);
        oraSpinner.setEditor(new JSpinner.DateEditor(oraSpinner, "HH:mm"));

        repartoList.setListData(new String[]{"Cardiologia", "Chirurgia Generale", "Ortopedia", "Terapia Intensiva", "Pediatria", "Neurologia"});

        DefaultTableModel model = new DefaultTableModel(new Object[0][0], COLONNE) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ricoveriTable.setModel(model);
    }

    private void setupStyles() {
        styleList(repartoList);

        ricoveriTable.setRowHeight(26);
        ricoveriTable.setShowGrid(false);
        ricoveriTable.setIntercellSpacing(new Dimension(0, 0));
        ricoveriTable.setSelectionBackground(SELECTION_BG);
        ricoveriTable.setSelectionForeground(Color.BLACK);
        ricoveriTable.setFont(BASE_FONT);

        JTableHeader th = ricoveriTable.getTableHeader();
        th.setBackground(AZZURRO_HOME);
        th.setForeground(Color.WHITE);
        th.setFont(HEADER_FONT);
        th.setPreferredSize(new Dimension(th.getWidth(), 30));
        th.setReorderingAllowed(false);

        ricoveriTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        applicaStilePulsantiCentrali(cercaButton);
        applicaStilePulsantiCentrali(resetButton);
        applicaStilePulsantiCentrali(nuovoRicoveroButton);
        applicaStilePulsantiCentrali(gestisciRicoveroButton);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ricovero frame = new Ricovero();

            Dimension strictSize = new Dimension(1000, 680);

            if (frame.mainPanel != null) {
                frame.mainPanel.setPreferredSize(strictSize);
                frame.setContentPane(frame.mainPanel);
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