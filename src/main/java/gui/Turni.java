package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.Instant;

/**
 * Classe che rappresenta l'interfaccia grafica per la gestione dei turni di lavoro.
 * Permette di visualizzare, filtrare, creare e modificare i turni.
 */
public class Turni extends JFrame {

    /** Il pannello principale della finestra. */
    public JPanel panelHome;
    private JTextField nomeField;
    private JSpinner dataSpinner;
    private JList<String> tipologiaList;
    private JList<String> repartoList;
    private JButton cercaButton;
    private JButton resetButton;
    private JTable turniTable;
    private JButton nuovoTurnoButton1;
    private JButton modificaTurno;
    private JTextField idTurnoField;

    private static final String[] COLONNE = {
            "ID Turno", "Data", "Matricola", "Dipendente", "Ruolo", "Reparto", "Orario Effettivo"
    };

    private static final String[] TIPOLOGIA_DATA = {
            "Medico", "Amministratore"
    };

    private static final String[] REPARTI_DATA = {
            "Chirurgia Generale", "Cardiologia", "Ortopedia",
            "Pediatria", "Terapia Intensiva", "Pronto Soccorso",
            "Bariatria", "Radiologia Interventistica", "Nessuno"
    };

    private transient Object[][] datiTurni = new Object[0][0];

    /**
     * Costruisce una nuova finestra per la gestione dei turni.
     * Inizializza i componenti, imposta gli stili e i listener.
     */
    public Turni() {
        initComponents();
        setupStyles();

        if (dataSpinner != null) {
            SpinnerDateModel dateModel = new SpinnerDateModel(java.util.Date.from(Instant.now()), null, null, java.util.Calendar.DAY_OF_MONTH);
            dataSpinner.setModel(dateModel);
            dataSpinner.setEditor(new JSpinner.DateEditor(dataSpinner, "yyyy-MM-dd"));
        }

        if (tipologiaList != null) {
            tipologiaList.setListData(TIPOLOGIA_DATA);
        }
        if (repartoList != null) {
            repartoList.setListData(REPARTI_DATA);
        }

        if (turniTable != null) {
            turniTable.setModel(new DefaultTableModel(COLONNE, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } });
        }

        setupListeners();
        if (turniTable != null) {
            loadTableData(null, null, null, null, null, null);
        }
    }

    /**
     * Aggiorna i dati dei turni visualizzati nella tabella.
     *
     * @param dati una matrice di oggetti contenente i nuovi dati dei turni.
     */
    public void aggiornaTabella(Object[][] dati) {
        this.datiTurni = dati != null ? dati : new Object[0][0];
        loadTableData(null, null, null, null, null, null);
    }

    /**
     * Aggiunge un listener al pulsante "Nuovo Turno".
     *
     * @param listener l'ActionListener da aggiungere.
     */
    public void addNuovoTurnoListener(java.awt.event.ActionListener listener) {
        nuovoTurnoButton1.addActionListener(listener);
    }

    /**
     * Aggiunge un listener al pulsante "Modifica Turno".
     *
     * @param listener l'ActionListener da aggiungere.
     */
    public void addModificaTurnoListener(java.awt.event.ActionListener listener) {
        modificaTurno.addActionListener(listener);
    }

    /**
     * Restituisce i dati chiave del turno attualmente selezionato nella tabella.
     *
     * @return un array di stringhe contenente [data, matricola, orarioEffettivo] del turno selezionato,
     *         o un array vuoto se nessuna riga è selezionata.
     */
    public String[] getDatiTurnoSelezionato() {
        int selectedRow = turniTable.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) turniTable.getModel();
            String data = (String) model.getValueAt(selectedRow, 1);
            String matricola = (String) model.getValueAt(selectedRow, 2);
            String orarioEffettivo = (String) model.getValueAt(selectedRow, 6);
            return new String[]{data, matricola, orarioEffettivo};
        }
        return new String[0];
    }

    /**
     * Inizializza i componenti della GUI.
     * Questo metodo è mantenuto per compatibilità con il GUI Designer.
     */
    private void initComponents() {
        // Questo metodo è mantenuto per compatibilità con il GUI Designer, ma la logica è stata spostata.
    }

    /**
     * Applica gli stili personalizzati ai componenti della GUI.
     */
    private void setupStyles() {
        Login.styleList(tipologiaList);
        Login.styleList(repartoList);
        Login.setupTableStyle(turniTable);
        Login.applicaStilePulsantiCentrali(cercaButton);
        Login.applicaStilePulsantiCentrali(resetButton);
        Login.applicaStilePulsantiCentrali(nuovoTurnoButton1);
        Login.applicaStilePulsantiCentrali(modificaTurno);
    }

    /**
     * Imposta i listener per i pulsanti di ricerca e reset.
     */
    private void setupListeners() {
        if (cercaButton != null) {
            cercaButton.addActionListener(e -> {
                String idTurnoInput = idTurnoField.getText() != null ? idTurnoField.getText().trim() : "";
                String nomeText = nomeField.getText();
                String nomeInput = nomeText != null ? nomeText.toLowerCase().trim() : "";

                String ruoloSelezionato = tipologiaList.getSelectedValue();
                String repartoSelezionato = repartoList.getSelectedValue();

                String dataInput = "";
                if (dataSpinner.getValue() != null) {
                    dataInput = new JSpinner.DateEditor(dataSpinner, "yyyy-MM-dd").getFormat().format(dataSpinner.getValue());
                }

                if (!idTurnoInput.isEmpty()) {
                    dataInput = "";
                }

                loadTableData(idTurnoInput, nomeInput, null, ruoloSelezionato, repartoSelezionato, dataInput);
            });
        }

        if (resetButton != null) {
            resetButton.addActionListener(e -> {
                idTurnoField.setText("");
                nomeField.setText("");
                tipologiaList.clearSelection();
                repartoList.clearSelection();
                dataSpinner.setValue(java.util.Date.from(Instant.now()));

                loadTableData(null, null, null, null, null, null);
            });
        }
    }

    /**
     * Verifica se una riga di dati di un turno corrisponde ai filtri di ricerca specificati.
     *
     * @param row             l'array di oggetti che rappresenta la riga del turno.
     * @param filtroIdTurno   il filtro per l'ID del turno.
     * @param filtroNome      il filtro per il nome del dipendente.
     * @param filtroMatricola il filtro per la matricola.
     * @param filtroRuolo     il filtro per il ruolo.
     * @param filtroReparto   il filtro per il reparto.
     * @param filtroData      il filtro per la data.
     * @return {@code true} se il turno corrisponde ai filtri, altrimenti {@code false}.
     */
    private boolean isTurnoCorrispondente(Object[] row, String filtroIdTurno, String filtroNome, String filtroMatricola, String filtroRuolo, String filtroReparto, String filtroData) {
        String rIdTurno = row[0] != null ? ((String) row[0]).toLowerCase() : "";
        String rData = row[1] != null ? ((String) row[1]).toLowerCase() : "";
        String rMatricola = row[2] != null ? ((String) row[2]).toLowerCase() : "";
        String rNome = row[3] != null ? ((String) row[3]).toLowerCase() : "";
        String rRuolo = row[4] != null ? (String) row[4] : "";
        String rReparto = row[5] != null ? ((String) row[5]).toLowerCase() : "";

        boolean matchIdTurno = (filtroIdTurno == null || filtroIdTurno.isEmpty() || rIdTurno.equals(filtroIdTurno.toLowerCase()));
        boolean matchData = (filtroData == null || filtroData.isEmpty() || rData.equals(filtroData.toLowerCase()));
        boolean matchNome = (filtroNome == null || filtroNome.isEmpty() || rNome.contains(filtroNome));
        boolean matchMatricola = (filtroMatricola == null || filtroMatricola.isEmpty() || rMatricola.contains(filtroMatricola));
        boolean matchRuolo = (filtroRuolo == null || rRuolo.equalsIgnoreCase(filtroRuolo));
        boolean matchReparto = (filtroReparto == null || rReparto.equalsIgnoreCase(filtroReparto));

        return matchIdTurno && matchData && matchNome && matchMatricola && matchRuolo && matchReparto;
    }

    /**
     * Carica e filtra i dati nella tabella dei turni in base ai filtri forniti.
     *
     * @param filtroIdTurno   il filtro per l'ID del turno.
     * @param filtroNome      il filtro per il nome del dipendente.
     * @param filtroMatricola il filtro per la matricola.
     * @param filtroRuolo     il filtro per il ruolo.
     * @param filtroReparto   il filtro per il reparto.
     * @param filtroData      il filtro per la data.
     */
    private void loadTableData(String filtroIdTurno, String filtroNome, String filtroMatricola, String filtroRuolo, String filtroReparto, String filtroData) {
        DefaultTableModel m = (DefaultTableModel) turniTable.getModel();
        m.setRowCount(0);

        for (Object[] row : datiTurni) {
            if (isTurnoCorrispondente(row, filtroIdTurno, filtroNome, filtroMatricola, filtroRuolo, filtroReparto, filtroData)) {
                m.addRow(row);
            }
        }
    }
}