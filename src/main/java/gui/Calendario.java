package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.time.temporal.TemporalAdjusters;

public class Calendario extends JFrame {

    public JPanel mainPanel;
    private JTable settimanaTable;
    private JButton avantiButton;
    private JButton dietroButton;
    private JLabel meseLabel;
    private JScrollPane scrollPane;
    private JButton aggiungiEventoButton;
    private JButton modificaEventoButton;

    private LocalDate lunediCorrente;
    private DefaultTableModel tableModel;

    public Calendario() {
        // 1. GESTIONE DELLE DATE
        this.lunediCorrente = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 2. INIZIALIZZAZIONE COMPONENTI E STILI
        initComponents();
        setupStyles();
        setupListeners();

        // 3. AGGIORNAMENTO INIZIALE VISTA
        aggiornaVistaCalendario();
    }
    
    private void initComponents() {
        String[] colonne = {"Orario", "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};
        tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rende la tabella non modificabile
            }
        };
        settimanaTable.setModel(tableModel);

        // Popola le righe delle ore
        for (int i = 0; i < 24; i++) {
            tableModel.addRow(new Object[]{String.format("%02d:00", i)});
        }
    }

    private void setupStyles() {
        // Stile generale
        mainPanel.setBackground(Color.WHITE);
        meseLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        meseLabel.setForeground(Login.AZZURRO_HOME);

        // Stile pulsanti
        Login.applicaStilePulsantiCentrali(avantiButton);
        Login.applicaStilePulsantiCentrali(dietroButton);
        Login.applicaStilePulsantiCentrali(aggiungiEventoButton);
        Login.applicaStilePulsantiCentrali(modificaEventoButton);
        avantiButton.setText("Settimana Successiva >>");
        dietroButton.setText("<< Settimana Precedente");

        // Stile tabella
        Login.setupTableStyle(settimanaTable);
        settimanaTable.setRowHeight(40);

        // Stile specifico per la colonna degli orari
        settimanaTable.getColumnModel().getColumn(0).setCellRenderer(new OrarioCellRenderer());
        settimanaTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        settimanaTable.getColumnModel().getColumn(0).setMaxWidth(70);
    }

    private void setupListeners() {
        dietroButton.addActionListener(e -> cambiaSettimana(-7));
        avantiButton.addActionListener(e -> cambiaSettimana(7));
    }

    public void addAggiungiEventoListener(java.awt.event.ActionListener listener) {
        aggiungiEventoButton.addActionListener(listener);
    }

    public void addModificaEventoListener(java.awt.event.ActionListener listener) {
        modificaEventoButton.addActionListener(listener);
    }

    private void cambiaSettimana(int giorniDaAggiungere) {
        lunediCorrente = lunediCorrente.plusDays(giorniDaAggiungere);
        aggiornaVistaCalendario();
    }

    private void aggiornaVistaCalendario() {
        aggiornaIntestazioni();
        svuotaCelleImpegni();
        // Qui andrà la logica per caricare gli eventi per la nuova settimana
    }

    private void aggiornaIntestazioni() {
        DateTimeFormatter formatterGiorno = DateTimeFormatter.ofPattern("dd/MM");
        DateTimeFormatter formatterMese = DateTimeFormatter.ofPattern("MMMM yyyy");

        String meseAnno = lunediCorrente.format(formatterMese);
        meseLabel.setText(meseAnno.substring(0, 1).toUpperCase() + meseAnno.substring(1));

        for (int i = 0; i < 7; i++) {
            LocalDate giorno = lunediCorrente.plusDays(i);
            String nomeGiorno = giorno.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
            nomeGiorno = nomeGiorno.substring(0, 1).toUpperCase() + nomeGiorno.substring(1);
            String header = String.format("<html><center>%s<br>%s</center></html>", nomeGiorno, giorno.format(formatterGiorno));
            settimanaTable.getColumnModel().getColumn(i + 1).setHeaderValue(header);
        }
        settimanaTable.getTableHeader().repaint();
    }

    private void svuotaCelleImpegni() {
        for (int riga = 0; riga < tableModel.getRowCount(); riga++) {
            for (int colonna = 1; colonna < tableModel.getColumnCount(); colonna++) {
                tableModel.setValueAt("", riga, colonna);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Calendario frame = new Calendario();
            controller.Controller.impostaSchermata(frame, frame.mainPanel, "Calendario Settimanale", JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    // Classe interna per personalizzare il rendering della colonna degli orari
    private static class OrarioCellRenderer extends DefaultTableCellRenderer {
        public OrarioCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setBackground(new Color(240, 240, 240));
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Chiama il metodo della superclasse per ottenere il componente di default
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return this; // Restituisce il renderer (this) con gli stili personalizzati
        }
    }
}
