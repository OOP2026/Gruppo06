package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.awt.event.ActionListener;
import java.util.Date;

public class Dimissioni extends JFrame {

    public JPanel mainPanel;
    private JTextField codiceficaleField;
    private JTextField idPazienteField;
    private JList<String> repartoList;
    private JList<String> tipoDimissioneList; // Questo è il reparto di dimissione
    private JSpinner spinner1;
    private JButton cercaButton;
    private JButton resetButton;
    private JTable pazientiTable;
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
        if (pazientiTable != null) {
            DefaultTableModel model = (DefaultTableModel) pazientiTable.getModel();
            model.setRowCount(0);
            if (dati != null) {
                for (Object[] riga : dati) {
                    model.addRow(riga);
                }
            }
        }
    }

    // Metodi per aggiungere i listener ai pulsanti
    public void addArchiviaDimissioneListener(ActionListener listener) {
        archiviaDimissioneButton.addActionListener(listener);
    }

    public void addCercaListener(ActionListener listener) {
        cercaButton.addActionListener(listener);
    }

    public void addResetListener(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    public void addLetturaDimissioneListener(ActionListener listener) {
        letturaDimissioneButton.addActionListener(listener);
    }

    // Metodi per ottenere i valori dai campi di input
    public String getCodiceFiscale() {
        return codiceficaleField.getText();
    }

    public String getNomeCognome() {
        return nomeCognomeField.getText();
    }

    public String getRepartoSelezionato() {
        return repartoList.getSelectedValue();
    }

    public String getTipoDimissioneSelezionato() {
        return tipoDimissioneList.getSelectedValue();
    }

    public Date getDataSelezionata() {
        return (Date) spinner1.getValue();
    }

    public String getCFPazienteSelezionato() {
        int selectedRow = pazientiTable.getSelectedRow();
        if (selectedRow != -1) {
            // La colonna 2 contiene il Codice Fiscale
            return (String) pazientiTable.getValueAt(selectedRow, 2);
        }
        return null;
    }

    public void resetCampiRicerca() {
        codiceficaleField.setText("");
        idPazienteField.setText("");
        nomeCognomeField.setText("");
        repartoList.clearSelection();
        tipoDimissioneList.clearSelection();
        spinner1.setValue(new Date()); // Resetta alla data odierna
    }

    private void initComponents() {
        DefaultTableModel model = new DefaultTableModel(new Object[0][0], COLONNE) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Rende la tabella non editabile
                return false;
            }
        };
        pazientiTable.setModel(model);

        // Popola la lista dei reparti
        repartoList.setListData(new String[]{
                "Chirurgia Generale", "Bariatria", "Radiologia Interventistica",
                "Cardiologia", "Terapia Intensiva"
        });

        // Popola la lista dei tipi di dimissione
        tipoDimissioneList.setListData(new String[]{
                "Ordinaria",
                "Trasferimento",
                "Volontaria",
                "Decesso"
        });

        // Imposta il modello per lo spinner della data
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        spinner1.setModel(dateModel);
        spinner1.setEditor(new JSpinner.DateEditor(spinner1, "dd/MM/yyyy"));
    }

    private void setupStyles() {
        // Stile Tabella
        pazientiTable.setRowHeight(26);
        pazientiTable.setShowGrid(false);
        pazientiTable.setIntercellSpacing(new Dimension(0, 0));
        pazientiTable.setSelectionBackground(SELECTION_BG);
        pazientiTable.setSelectionForeground(Color.BLACK);
        pazientiTable.setFont(BASE_FONT);

        JTableHeader th = pazientiTable.getTableHeader();
        th.setBackground(AZZURRO_HOME);
        th.setForeground(Color.WHITE);
        th.setFont(HEADER_FONT);
        th.setPreferredSize(new Dimension(th.getWidth(), 30));
        th.setReorderingAllowed(false);

        pazientiTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        // Stile Liste
        repartoList.setSelectionBackground(AZZURRO_HOME);
        repartoList.setSelectionForeground(Color.WHITE);
        repartoList.setFont(BASE_FONT);

        tipoDimissioneList.setSelectionBackground(AZZURRO_HOME);
        tipoDimissioneList.setSelectionForeground(Color.WHITE);
        tipoDimissioneList.setFont(BASE_FONT);

        // Stile Pulsanti
        applicaStilePulsantiCentrali(cercaButton);
        applicaStilePulsantiCentrali(resetButton);
        applicaStilePulsantiCentrali(letturaDimissioneButton);
        applicaStilePulsantiCentrali(archiviaDimissioneButton);
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
            if (frame.mainPanel != null) {
                frame.mainPanel.setPreferredSize(strictSize);
                frame.setContentPane(frame.mainPanel);
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