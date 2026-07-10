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

/**
 * La classe Calendario gestisce l'interfaccia grafica per la visualizzazione settimanale
 * degli eventi e dei turni. Estende JFrame e implementa una griglia interattiva.
 */
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

    /**
     * Costruisce una nuova istanza del Calendario, inizializzando la data di partenza,
     * le strutture dati, i componenti grafici e applicando i listener.
     */
    public Calendario() {
        this.lunediCorrente = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        this.eventiMappa = new HashMap<>();
        this.tuttiGliEventi = new ArrayList<>();

        initComponents();
        setupStyles();
        setupListeners();

        aggiornaVistaCalendario();
    }
    
    /**
     * Inizializza i componenti principali dell'interfaccia, in particolare il modello
     * della tabella che forma la griglia degli orari e dei giorni della settimana.
     */
    private void initComponents() {
        String[] colonne = {"Orario", "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};
        tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        settimanaTable.setModel(tableModel);

        for (int i = 0; i < 24; i++) {
            tableModel.addRow(new Object[]{String.format("%02d:00", i)});
        }
    }

    /**
     * Configura l'aspetto visivo dei vari elementi dell'interfaccia utente,
     * applicando colori, font, dimensioni e logiche di selezione alla griglia.
     */
    private void setupStyles() {
        mainPanel.setBackground(Color.WHITE);
        meseLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        meseLabel.setForeground(Login.AZZURRO_HOME);

        Login.applicaStilePulsantiCentrali(avantiButton);
        Login.applicaStilePulsantiCentrali(dietroButton);
        Login.applicaStilePulsantiCentrali(aggiungiEventoButton);
        Login.applicaStilePulsantiCentrali(modificaEventoButton);
        avantiButton.setText("Settimana Successiva >>");
        dietroButton.setText("<< Settimana Precedente");

        Login.setupTableStyle(settimanaTable);
        settimanaTable.setRowHeight(40);

        settimanaTable.setCellSelectionEnabled(true);
        settimanaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        settimanaTable.setSelectionBackground(new Color(173, 216, 230));
        settimanaTable.setSelectionForeground(Color.BLACK);
        settimanaTable.setShowGrid(true);
        settimanaTable.setGridColor(Color.LIGHT_GRAY);

        settimanaTable.setIntercellSpacing(new Dimension(1, 1));

        settimanaTable.getColumnModel().getColumn(0).setCellRenderer(new OrarioCellRenderer());
        settimanaTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        settimanaTable.getColumnModel().getColumn(0).setMaxWidth(70);
    }

    /**
     * Associa i listener per i pulsanti di navigazione tra le settimane.
     */
    private void setupListeners() {
        dietroButton.addActionListener(e -> cambiaSettimana(-7));
        avantiButton.addActionListener(e -> cambiaSettimana(7));
    }

    /**
     * Registra un listener per il pulsante di aggiunta di un nuovo evento.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addAggiungiEventoListener(java.awt.event.ActionListener listener) {
        aggiungiEventoButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di modifica di un evento esistente.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addModificaEventoListener(java.awt.event.ActionListener listener) {
        modificaEventoButton.addActionListener(listener);
    }

    /**
     * Modifica la data di riferimento della vista corrente spostandola avanti o indietro.
     *
     * @param giorniDaAggiungere il numero di giorni da sommare o sottrarre
     */
    private void cambiaSettimana(int giorniDaAggiungere) {
        lunediCorrente = lunediCorrente.plusDays(giorniDaAggiungere);
        aggiornaVistaCalendario();
    }

    /**
     * Coordina l'aggiornamento visivo dell'intero calendario rigenerando intestazioni,
     * pulendo la griglia e riposizionando gli eventi estratti.
     */
    private void aggiornaVistaCalendario() {
        aggiornaIntestazioni();
        svuotaCelleImpegni();
        disponiEventiNellaGriglia();
    }

    /**
     * Calcola e aggiorna le etichette delle colonne inserendo i nomi dei giorni e 
     * le rispettive date per la settimana in fase di visualizzazione.
     */
    private void aggiornaIntestazioni() {
        DateTimeFormatter formatterGiorno = DateTimeFormatter.ofPattern("dd/MM");
        DateTimeFormatter formatterMese = DateTimeFormatter.ofPattern("MMMM yyyy");

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

    /**
     * Cancella il contenuto di tutte le celle della griglia rimuovendo gli eventi renderizzati.
     */
    private void svuotaCelleImpegni() {
        if (tableModel == null) return;
        eventiMappa.clear();
        for (int riga = 0; riga < tableModel.getRowCount(); riga++) {
            for (int colonna = 1; colonna < tableModel.getColumnCount(); colonna++) {
                tableModel.setValueAt("", riga, colonna);
            }
        }
    }

    /**
     * Imposta il dataset contenente tutti gli eventi da mostrare e aggiorna la vista.
     *
     * @param eventi lista contenente i dati grezzi degli eventi prelevati dal database
     */
    public void setEventi(java.util.List<ArrayList<String>> eventi) {
        this.tuttiGliEventi = eventi;
        aggiornaVistaCalendario();
    }

    /**
     * Estrae le date e gli orari dalla lista degli eventi e inserisce i titoli
     * nelle celle corrispondenti all'interno della griglia della settimana corrente.
     */
    private void disponiEventiNellaGriglia() {
        if (tuttiGliEventi == null) return;

        LocalDate fineSettimana = lunediCorrente.plusDays(7);

        for (ArrayList<String> evento : tuttiGliEventi) {
            try {
                Timestamp tsInizio = Timestamp.valueOf(evento.get(4));
                LocalDateTime ldtInizio = tsInizio.toLocalDateTime();
                LocalDate dataEvento = ldtInizio.toLocalDate();

                if (!dataEvento.isBefore(lunediCorrente) && dataEvento.isBefore(fineSettimana)) {
                    int riga = ldtInizio.getHour();
                    int colonna = ldtInizio.getDayOfWeek().getValue();

                    if (riga < tableModel.getRowCount() && colonna > 0 && colonna <= 7) {
                        String titolo = evento.get(1);
                        tableModel.setValueAt("<html><center>" + titolo + "</center></html>", riga, colonna);
                        eventiMappa.put(new Point(colonna, riga), evento);
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * Recupera le informazioni dell'evento cliccato all'interno della griglia.
     *
     * @return i dettagli dell'evento selezionato, oppure null se la cella è vuota o non valida
     */
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
            return null;
        }

        LocalDate dataSelezionata = lunediCorrente.plusDays(colonna - 1L);

        return LocalDateTime.of(dataSelezionata, java.time.LocalTime.of(riga, 0));
    }

    /**
     * Classe interna dedicata alla formattazione estetica della colonna degli orari,
     * differenziandola dalle celle standard assegnate agli eventi.
     */
    private static class OrarioCellRenderer extends DefaultTableCellRenderer {
        private final Color nonSelectedBackground = new Color(240, 240, 240);

        public OrarioCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("SansSerif", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                setBackground(nonSelectedBackground);
            }
            return this;
        }
    }
}
