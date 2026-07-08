package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
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
    private transient java.util.List<ArrayList<String>> tuttiGliEventi;
    private transient Map<Point, ArrayList<String>> eventiMappa;

    public Calendario() {
        // 1. GESTIONE DELLE DATE
        this.lunediCorrente = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        this.eventiMappa = new HashMap<>();
        this.tuttiGliEventi = new ArrayList<>();

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

        // --- IMPOSTAZIONI PER GRIGLIA E SELEZIONE ---
        settimanaTable.setCellSelectionEnabled(true); // Permette di selezionare una singola cella
        settimanaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Imposta un colore di sfondo per la cella selezionata per renderla più visibile
        settimanaTable.setSelectionBackground(new Color(173, 216, 230));
        settimanaTable.setSelectionForeground(Color.BLACK);
        settimanaTable.setShowGrid(true); // Mostra le linee della griglia
        settimanaTable.setGridColor(Color.LIGHT_GRAY); // Imposta il colore della griglia

        // Aggiunge una spaziatura tra le celle per rendere la griglia più evidente e continua
        settimanaTable.setIntercellSpacing(new Dimension(1, 1));

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
        disponiEventiNellaGriglia();
    }

    private void aggiornaIntestazioni() {
        DateTimeFormatter formatterGiorno = DateTimeFormatter.ofPattern("dd/MM");
        DateTimeFormatter formatterMese = DateTimeFormatter.ofPattern("MMMM yyyy");

        // Controllo per evitare NullPointerException in fase di design
        if (lunediCorrente == null) {
            lunediCorrente = LocalDate.now();
        }
        if (meseLabel == null || settimanaTable == null) {
            return;
        }

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
        if (tableModel == null) return;
        eventiMappa.clear();
        for (int riga = 0; riga < tableModel.getRowCount(); riga++) {
            for (int colonna = 1; colonna < tableModel.getColumnCount(); colonna++) {
                tableModel.setValueAt("", riga, colonna);
            }
        }
    }

    public void setEventi(java.util.List<ArrayList<String>> eventi) {
        this.tuttiGliEventi = eventi;
        aggiornaVistaCalendario();
    }

    private void disponiEventiNellaGriglia() {
        if (tuttiGliEventi == null) return;

        LocalDate fineSettimana = lunediCorrente.plusDays(7);

        for (ArrayList<String> evento : tuttiGliEventi) {
            try {
                // DAO: 0:id, 1:titolo, 2:desc, 3:matricola, 4:inizio, 5:fine
                Timestamp tsInizio = Timestamp.valueOf(evento.get(4));
                LocalDateTime ldtInizio = tsInizio.toLocalDateTime();
                LocalDate dataEvento = ldtInizio.toLocalDate();

                // Controlla se l'evento appartiene alla settimana visualizzata
                if (!dataEvento.isBefore(lunediCorrente) && dataEvento.isBefore(fineSettimana)) {
                    int riga = ldtInizio.getHour();
                    int colonna = ldtInizio.getDayOfWeek().getValue(); // Lun=1, ..., Dom=7

                    if (riga < tableModel.getRowCount() && colonna > 0 && colonna <= 7) {
                        String titolo = evento.get(1);
                        tableModel.setValueAt("<html><center>" + titolo + "</center></html>", riga, colonna);
                        eventiMappa.put(new Point(colonna, riga), evento);
                    }
                }
            } catch (Exception e) {
                // Ignora eventi con formato data non valido
            }
        }
    }

    public ArrayList<String> getEventoSelezionato() {
        int riga = settimanaTable.getSelectedRow();
        int colonna = settimanaTable.getSelectedColumn();
        if (riga == -1 || colonna <= 0) return null;
        return eventiMappa.get(new Point(colonna, riga));
    }

    /**
     * Calcola e restituisce il timestamp (data e ora) corrispondente alla cella selezionata,
     * anche se la cella è vuota.
     * @return un LocalDateTime per la cella selezionata, o null se nessuna cella valida è selezionata.
     */
    public LocalDateTime getTimestampCellaSelezionata() {
        int riga = settimanaTable.getSelectedRow();
        int colonna = settimanaTable.getSelectedColumn();

        if (riga == -1 || colonna <= 0) {
            return null; // Nessuna cella valida selezionata
        }

        LocalDate dataSelezionata = lunediCorrente.plusDays(colonna - 1);
        return LocalDateTime.of(dataSelezionata, java.time.LocalTime.of(riga, 0));
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
        private final Color nonSelectedBackground = new Color(240, 240, 240);

        public OrarioCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("SansSerif", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Chiama il metodo della superclasse per impostare testo, colori di selezione, ecc.
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Se la cella non è selezionata, applica il nostro sfondo personalizzato.
            if (!isSelected) {
                setBackground(nonSelectedBackground);
            }
            return this;
        }
    }
}
