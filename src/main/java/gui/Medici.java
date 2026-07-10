package gui;

import javax.swing.*;
import javax.swing.table.*;

/**
 * La classe Medici gestisce l'interfaccia grafica per la visualizzazione,
 * la ricerca e la gestione del personale medico all'interno della struttura.
 * Estende JFrame e fornisce i metodi necessari per interagire con il controller.
 */
public class Medici extends JFrame {
    public JPanel mainPanel;
    private JTextField nomeField;
    private JTextField codiceField;
    private JButton resetButton;
    private JButton cercaButton;
    private JTable mediciTable;
    private JButton newmedicoButton;

    private JList<String> specializzazioneList;
    private JList<String> repartoList;
    private JButton assenzaButton;
    private JButton modificamedicoButton;
    private JRadioButton attivoRadioButton;
    private JRadioButton assenteRadioButton;
    private JRadioButton occupatoRadioButton;
    private JRadioButton tuttiRadioButton;

    private static final String[] COLONNE = {
            "Matricola", "Cognome e Nome", "Specializzazione",
            "Reparto Assegnato", "Stato"
    };

    private static final String[] SPECIALIZZAZIONI_DATA = {
            "Chirurgia Generale", "Cardiologia", "Neurologia",
            "Anestesia", "Chirurgia Toracica", "Ematologia", "Otorinolaringoiatria"
    };

    private static final String[] REPARTI_DATA = {
            "Blocco Operatorio", "Terapia Intensiva", "Neuroradiologia",
            "Chirurgia Toracica", "Laboratorio Analisi", "Pronto Soccorso"
    };

    /**
     * Matrice transitoria contenente i dati dei medici, utilizzata per popolare 
     * e filtrare la tabella in memoria senza incorrere in problemi di serializzazione.
     */
    private transient Object[][] datiMedici = new Object[0][0];

    /**
     * Costruisce una nuova istanza della schermata Medici, inizializzando
     * i componenti grafici, i dati predefiniti e applicando gli stili visivi.
     */
    public Medici() {
        initComponents();
        setupStyles();
        inizializzaComponentiDati();
        setupListeners();

        if (mediciTable != null) {
            loadTableData(null, null, null, null);
        }
    }

    /**
     * Aggiorna il contenuto della tabella dei medici con i nuovi dati forniti.
     *
     * @param dati matrice di oggetti contenente i record dei medici prelevati dal database
     */
    public void aggiornaTabella(Object[][] dati) {
        this.datiMedici = dati != null ? dati : new Object[0][0];
        if (mediciTable != null) {
            loadTableData(null, null, null, null);
        }
    }

    /**
     * Registra un listener per il pulsante di inserimento di un nuovo medico.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addNuovoMedicoListener(java.awt.event.ActionListener listener) {
        if (newmedicoButton != null) newmedicoButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di modifica dei dati di un medico esistente.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addModificaMedicoListener(java.awt.event.ActionListener listener) {
        if (modificamedicoButton != null) modificamedicoButton.addActionListener(listener);
    }

    /**
     * Registra un listener per il pulsante di gestione delle assenze del medico.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addAssenzaListener(java.awt.event.ActionListener listener) {
        if (assenzaButton != null) assenzaButton.addActionListener(listener);
    }

    /**
     * Restituisce la matricola del medico attualmente selezionato nella tabella.
     *
     * @return la matricola del medico come stringa, oppure null se non è stata selezionata alcuna riga
     */
    public String getMatricolaMedicoSelezionato() {
        if (mediciTable == null) return null;

        int selectedRow = mediciTable.getSelectedRow();
        return (selectedRow == -1) ? null : (String) mediciTable.getValueAt(selectedRow, 0);
    }

    /**
     * Inizializza i componenti generati dal GUI Designer.
     */
    private void initComponents() {
    }

    /**
     * Applica gli stili visivi personalizzati a tabelle, liste e pulsanti dell'interfaccia.
     */
    private void setupStyles() {
        if (specializzazioneList != null) Login.styleList(specializzazioneList);
        if (repartoList != null) Login.styleList(repartoList);
        if (mediciTable != null) Login.setupTableStyle(mediciTable);
        if (cercaButton != null) Login.applicaStilePulsantiCentrali(cercaButton);
        if (resetButton != null) Login.applicaStilePulsantiCentrali(resetButton);
        if (modificamedicoButton != null) Login.applicaStilePulsantiCentrali(modificamedicoButton);
        if (newmedicoButton != null) Login.applicaStilePulsantiCentrali(newmedicoButton);
        if (assenzaButton != null) Login.applicaStilePulsantiCentrali(assenzaButton);
    }

    /**
     * Inizializza i modelli di dati per le liste di specializzazioni, reparti, 
     * i radio button di stato e la tabella principale.
     */
    private void inizializzaComponentiDati() {
        if (specializzazioneList != null) specializzazioneList.setListData(SPECIALIZZAZIONI_DATA);
        if (repartoList != null) repartoList.setListData(REPARTI_DATA);
        inizializzaRadioButtons();
        inizializzaTabella();
    }

    /**
     * Raggruppa i radio button per il filtraggio in base allo stato del medico,
     * impostando la selezione predefinita su "Tutti".
     */
    private void inizializzaRadioButtons() {
        if (tuttiRadioButton == null || attivoRadioButton == null || assenteRadioButton == null || occupatoRadioButton == null) return;

        ButtonGroup statoGroup = new ButtonGroup();
        statoGroup.add(tuttiRadioButton);
        statoGroup.add(attivoRadioButton);
        statoGroup.add(assenteRadioButton);
        statoGroup.add(occupatoRadioButton);
        tuttiRadioButton.setSelected(true);
    }

    /**
     * Configura il modello della tabella dei medici, definendo le colonne 
     * e rendendo le celle non modificabili dall'utente.
     */
    private void inizializzaTabella() {
        if (mediciTable == null) return;

        DefaultTableModel model = new DefaultTableModel(COLONNE, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        mediciTable.setModel(model);
    }

    /**
     * Configura i listener interni per i pulsanti di ricerca e reset dei filtri.
     */
    private void setupListeners() {
        if (cercaButton != null) cercaButton.addActionListener(e -> eseguiRicerca());
        if (resetButton != null) resetButton.addActionListener(e -> eseguiReset());
    }

    /**
     * Legge i valori dai campi di filtro (nome, matricola, specializzazione, reparto)
     * e invoca il ricaricamento della tabella con i filtri applicati.
     */
    private void eseguiRicerca() {
        String nome = (nomeField != null) ? nomeField.getText().toLowerCase().trim() : "";
        String matricola = (codiceField != null) ? codiceField.getText().toLowerCase().trim() : "";
        String specializzazione = (specializzazioneList != null) ? specializzazioneList.getSelectedValue() : null;
        String reparto = (repartoList != null) ? repartoList.getSelectedValue() : null;

        loadTableData(nome, matricola, specializzazione, reparto);
    }

    /**
     * Svuota tutti i campi di ricerca, resetta le selezioni ai valori predefiniti
     * e ricarica la tabella mostrando l'elenco completo dei medici.
     */
    private void eseguiReset() {
        if (nomeField != null) nomeField.setText("");
        if (codiceField != null) codiceField.setText("");
        if (specializzazioneList != null) specializzazioneList.clearSelection();
        if (repartoList != null) repartoList.clearSelection();
        if (tuttiRadioButton != null) tuttiRadioButton.setSelected(true);

        loadTableData(null, null, null, null);
    }

    /**
     * Filtra i dati memorizzati in base ai parametri specificati e aggiorna visivamente la tabella.
     *
     * @param fNome      il filtro sul nome
     * @param fMatricola il filtro sulla matricola
     * @param fSpec      il filtro sulla specializzazione
     * @param fReparto   il filtro sul reparto
     */
    private void loadTableData(String fNome, String fMatricola, String fSpec, String fReparto) {
        if (mediciTable == null || mediciTable.getModel() == null) return;

        DefaultTableModel m = (DefaultTableModel) mediciTable.getModel();
        m.setRowCount(0);

        String filtroStato = determinaFiltroStatoSelezionato();

        for (Object[] row : datiMedici) {
            if (rigaCorrispondeAiFiltri(row, fNome, fMatricola, fSpec, fReparto, filtroStato)) {
                m.addRow(row);
            }
        }
    }

    /**
     * Determina quale radio button dello stato è attualmente selezionato.
     *
     * @return una stringa che rappresenta lo stato ("attivo", "assente", "occupato"), 
     *         oppure null se è selezionato "Tutti"
     */
    private String determinaFiltroStatoSelezionato() {
        if (attivoRadioButton != null && attivoRadioButton.isSelected()) return "attivo";
        if (assenteRadioButton != null && assenteRadioButton.isSelected()) return "assente";
        if (occupatoRadioButton != null && occupatoRadioButton.isSelected()) return "occupato";
        return null;
    }

    /**
     * Verifica se una singola riga di dati corrisponde ai filtri di ricerca forniti.
     *
     * @param row        la riga di dati del medico da valutare
     * @param fNome      il filtro sul nome
     * @param fMatricola il filtro sulla matricola
     * @param fSpec      il filtro sulla specializzazione
     * @param fReparto   il filtro sul reparto
     * @param fStato     il filtro sullo stato operativo
     * @return true se la riga soddisfa tutti i filtri, false altrimenti
     */
    private boolean rigaCorrispondeAiFiltri(Object[] row, String fNome, String fMatricola, String fSpec, String fReparto, String fStato) {
        if (row == null || row.length < 5) return false;

        String rMatricola = row[0] != null ? ((String) row[0]).toLowerCase() : "";
        String rNome = row[1] != null ? ((String) row[1]).toLowerCase() : "";
        String rSpec = (String) row[2];
        String rReparto = (String) row[3];
        String rStato = row[4] != null ? ((String) row[4]).toLowerCase() : "";

        if (fNome != null && !fNome.isEmpty() && !rNome.contains(fNome)) return false;
        if (fMatricola != null && !fMatricola.isEmpty() && !rMatricola.contains(fMatricola)) return false;
        if (fSpec != null && !fSpec.equals(rSpec)) return false;
        if (fReparto != null && !fReparto.equals(rReparto)) return false;
        if (fStato != null && !fStato.equals(rStato)) return false;

        return true;
    }
}