package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Letti extends JFrame {
    public JPanel mainPanel;
    private JRadioButton tuttiRadioButton;
    private JRadioButton disponibileRadioButton;
    private JRadioButton occupatoRadioButton;
    private JList repartoList;
    private JList tipologiaList;
    private JButton cercaButton;
    private JButton resetButton;
    private JTable lettiTable; // Questa è la tabella che mostra i letti
    private JButton assegnaPazienteButton;
    private JButton storicoLettiButton;

    //Selezione colori GUI
    private static final Color AZZURRO_HOME = new Color(70, 132, 197);
    private static final Color SELECTION_BG = new Color(187, 222, 247);
    private static final Color ALT_ROW_BG = new Color(0xf5, 0xf8, 0xfc);
    //Selezione font GUI
    private static final Font BASE_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 12);

    private static final String[] COLONNE = {
            "ID Letto", "Tipologia Letto", "Reparto",
            "Stanza", "Numero Letto", "Stato"
    };

    public Letti() {
        this.setTitle("Gestione Letti");
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(1000, 680);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        initComponents();
        setupStyles();
    }

    private void initComponents() {
        // Inizializza subito il modello della tabella con le colonne per mostrare le intestazioni
        DefaultTableModel model = new DefaultTableModel(new Object[0][0], COLONNE) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rende la tabella non modificabile
            }
        };

        lettiTable.setModel(model);

        // Raggruppa i radio button per permettere una sola selezione
        ButtonGroup statoLettoGroup = new ButtonGroup();
        statoLettoGroup.add(tuttiRadioButton);
        statoLettoGroup.add(disponibileRadioButton);
        statoLettoGroup.add(occupatoRadioButton);
        tuttiRadioButton.setSelected(true); // Imposta "Tutti" come predefinito
    }

    private void setupStyles() {
        styleList(repartoList);
        styleList(tipologiaList);

        lettiTable.setRowHeight(26);
        lettiTable.setShowGrid(false);
        lettiTable.setIntercellSpacing(new Dimension(0, 0));
        lettiTable.setSelectionBackground(SELECTION_BG);
        lettiTable.setSelectionForeground(Color.BLACK);
        lettiTable.setFont(BASE_FONT);

        JTableHeader th = lettiTable.getTableHeader();
        th.setBackground(AZZURRO_HOME);
        th.setForeground(Color.WHITE);
        th.setFont(HEADER_FONT);
        th.setPreferredSize(new Dimension(th.getWidth(), 30));
        th.setReorderingAllowed(false);

        lettiTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        applicaStilePulsantiCentrali(assegnaPazienteButton);
        applicaStilePulsantiCentrali(storicoLettiButton);
    }

    private void styleList(JList list) {
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

    /**
     * Rende il pulsante "Assegna Paziente" accessibile a un controller esterno.
     * @param listener L'ActionListener che verrà eseguito al click del pulsante.
     */
    public void addAssegnaPazienteListener(ActionListener listener) {
        assegnaPazienteButton.addActionListener(listener);
    }

    /**
     * Recupera l'ID del letto attualmente selezionato nella tabella.
     * @return L'ID del letto come String, o null se non c'è nessuna selezione.
     */
    public String getIdLettoSelezionato() {
        int rigaSelezionata = lettiTable.getSelectedRow();
        if (rigaSelezionata == -1) {
            return null;
        }
        // Si assume che l'ID del letto sia nella prima colonna (indice 0)
        return (String) lettiTable.getValueAt(rigaSelezionata, 0);
    }

    /**
     * Popola la tabella dei letti con i dati forniti dal controller.
     * @param dati Una matrice di oggetti da visualizzare nella tabella.
     */
    public void aggiornaTabella(Object[][] dati) {
        DefaultTableModel model = (DefaultTableModel) lettiTable.getModel();
        model.setRowCount(0); // Pulisce la tabella
        if (dati != null) {
            for (Object[] riga : dati) {
                model.addRow(riga);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Letti frame = new Letti();
            frame.setVisible(true);
        });
    }
}