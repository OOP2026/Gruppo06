package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Pazienti extends JFrame {

    public JPanel panelPrincipale;
    private JTextField nomeField;
    private JTextField codiceField;
    private JLabel RepartoLabel;
    private JList<String> tipologiaList;
    private JButton resetButton;
    private JButton cercaButton;
    private JTable PazientiTable;
    private JButton nuovoPazienteButton;
    private JButton storicoPazienteButton;
    private JButton assegnaLettoButton;
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
            "Sesso", "Residenza", "Stato Ricovero"
    };

    private TableRowSorter<DefaultTableModel> sorter;

    public Pazienti() {
        initComponents();
        setupStyles();
    }

    private void initComponents() {
        DefaultTableModel model = new DefaultTableModel(new Object[0][0], COLONNE) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        PazientiTable.setModel(model);

        sorter = new TableRowSorter<>(model);
        PazientiTable.setRowSorter(sorter);

        if (cercaButton != null) {
            cercaButton.addActionListener(e -> eseguiRicerca());
        }
        if (resetButton != null) {
            resetButton.addActionListener(e -> resettaRicerca());
        }

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

        if (tipologiaList != null) {
            tipologiaList.setSelectionBackground(AZZURRO_HOME);
            tipologiaList.setSelectionForeground(Color.WHITE);
            tipologiaList.setFont(BASE_FONT);
        }

        if(cercaButton != null) applicaStilePulsantiCentrali(cercaButton);
        if(resetButton != null) applicaStilePulsantiCentrali(resetButton);
        if(nuovoPazienteButton != null) applicaStilePulsantiCentrali(nuovoPazienteButton);
        if(storicoPazienteButton != null) applicaStilePulsantiCentrali(storicoPazienteButton);
        if(assegnaLettoButton != null) applicaStilePulsantiCentrali(assegnaLettoButton);
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

    // Metodo per permettere al sistema di ascoltare il bottone
    // senza importare o conoscere il Controller nella GUI
    public void addNuovoPazienteListener(java.awt.event.ActionListener listener) {
        if (nuovoPazienteButton != null) {
            nuovoPazienteButton.addActionListener(listener);
        }
    }

    public void addAssegnaLettoListener(java.awt.event.ActionListener listener) {
        if (assegnaLettoButton != null) {
            assegnaLettoButton.addActionListener(listener);
        }
    }

    public String getCfPazienteSelezionato() {
        int rigaSelezionata = PazientiTable.getSelectedRow();
        if (rigaSelezionata == -1) {
            JOptionPane.showMessageDialog(this, "Per favore, seleziona un paziente dalla tabella.", "Nessun Paziente Selezionato", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        // La colonna 0 contiene il CF (usato come ID Paziente)
        return (String) PazientiTable.getValueAt(rigaSelezionata, 0);
    }

    // Metodo per aggiornare la tabella con i dati reali dal database
    public void aggiornaTabella(List<ArrayList<String>> datiPazienti) {
        DefaultTableModel model = (DefaultTableModel) PazientiTable.getModel();
        model.setRowCount(0); // Svuota i vecchi dati finti/obsoleti

        for (java.util.ArrayList<String> p : datiPazienti) {
            String cf = p.get(0);
            String nomeCognome = p.get(1) + " " + p.get(2);
            String sesso = p.get(4);
            String residenza = p.get(5);

            // Indice 7: Stato ricovero (es. ID del letto). Se non presente o vuoto, il paziente non è ricoverato.
            String statoRicovero = (p.size() > 7 && p.get(7) != null && !p.get(7).isEmpty()) ? "Ricoverato - Letto " + p.get(7) : "Non ricoverato";

            model.addRow(new Object[]{cf, nomeCognome, cf, sesso, residenza, statoRicovero});
        }
    }

    private void eseguiRicerca() {
        if (sorter == null) return;
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (nomeField != null && !nomeField.getText().trim().isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + nomeField.getText().trim(), 1)); // Colonna Nome
        }
        if (codiceField != null && !codiceField.getText().trim().isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + codiceField.getText().trim(), 2)); // Colonna CF
        }
        if (residenzaField != null && !residenzaField.getText().trim().isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + residenzaField.getText().trim(), 4)); // Colonna Residenza
        }
        if (maschioRadioButton != null && maschioRadioButton.isSelected()) {
            filters.add(RowFilter.regexFilter("(?i)^M$", 3)); // Colonna Sesso
        } else if (femminaRadioButton != null && femminaRadioButton.isSelected()) {
            filters.add(RowFilter.regexFilter("(?i)^F$", 3)); // Colonna Sesso
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void resettaRicerca() {
        if (nomeField != null) nomeField.setText("");
        if (codiceField != null) codiceField.setText("");
        if (residenzaField != null) residenzaField.setText("");
        if (maschioRadioButton != null) maschioRadioButton.setSelected(false);
        if (femminaRadioButton != null) femminaRadioButton.setSelected(false);
        if (sorter != null) sorter.setRowFilter(null);
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