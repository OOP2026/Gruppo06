package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe Pazienti gestisce l'interfaccia grafica per la visualizzazione,
 * la ricerca e la gestione dei pazienti all'interno della struttura ospedaliera.
 * Estende JFrame e fornisce i metodi necessari per interagire con il controller.
 */
public class Pazienti extends JFrame {

    public JPanel mainPanel;
    private JTextField nomeField;
    private JTextField codiceField;
    private JList<String> tipologiaList;
    private JButton resetButton;
    private JButton cercaButton;
    private JTable pazientiTable;
    private JButton nuovoPazienteButton;
    private JButton storicoPazienteButton;
    private JTextField residenzaField;
    private JTextField dataField;
    private JTextField ricercaprognosiField;
    private JRadioButton femminaRadioButton;
    private JRadioButton maschioRadioButton;

    private static final String[] COLONNE = {
            "ID Paziente", "Nome e Cognome", "Codice Fiscale", "Data Nascita", "Diagnosi",
            "Sesso", "Residenza", "Stato Ricovero", "Reparto"
    };

    private transient TableRowSorter<DefaultTableModel> sorter;

    /**
     * Costruisce una nuova istanza della schermata Pazienti, inizializzando
     * i componenti grafici e applicando gli stili visivi predefiniti.
     */
    public Pazienti() {
        initComponents();
        setupStyles();
    }

    /**
     * Inizializza i componenti principali dell'interfaccia, configurando
     * il modello della tabella, i listener di base e i dati delle liste di filtraggio.
     */
    private void initComponents() {
        DefaultTableModel model = new DefaultTableModel(new Object[0][0], COLONNE) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pazientiTable.setModel(model);

        sorter = new TableRowSorter<>(model);
        pazientiTable.setRowSorter(sorter);

        if (cercaButton != null) {
            cercaButton.addActionListener(e -> eseguiRicerca());
        }
        if (resetButton != null) {
            resetButton.addActionListener(e -> resettaRicerca());
        }
    }

    /**
     * Applica gli stili visivi personalizzati ai vari elementi grafici
     * come tabelle, liste e pulsanti per garantire coerenza nell'interfaccia.
     */
    private void setupStyles() {
        Login.setupTableStyle(pazientiTable);
        Login.styleList(tipologiaList);
        if(cercaButton != null) Login.applicaStilePulsantiCentrali(cercaButton);
        if(resetButton != null) Login.applicaStilePulsantiCentrali(resetButton);
        if(nuovoPazienteButton != null) Login.applicaStilePulsantiCentrali(nuovoPazienteButton);
        if(storicoPazienteButton != null) Login.applicaStilePulsantiCentrali(storicoPazienteButton);
    }

    /**
     * Registra un listener per il pulsante di registrazione di un nuovo paziente.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addNuovoPazienteListener(java.awt.event.ActionListener listener) {
        if (nuovoPazienteButton != null) {
            nuovoPazienteButton.addActionListener(listener);
        }
    }

    /**
     * Registra un listener per il pulsante di visualizzazione dello storico di un paziente.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addStoricoPazienteListener(java.awt.event.ActionListener listener) {
        if (storicoPazienteButton != null) {
            storicoPazienteButton.addActionListener(listener);
        }
    }

    /**
     * Restituisce il codice fiscale del paziente attualmente selezionato nella tabella.
     * Il codice fiscale è posizionato nella prima colonna.
     *
     * @return il codice fiscale come stringa, oppure null se nessuna riga è selezionata
     */
    public String getCfPazienteSelezionato() {
        int rigaSelezionata = pazientiTable.getSelectedRow();
        if (rigaSelezionata == -1) {
            JOptionPane.showMessageDialog(this, "Per favore, seleziona un paziente dalla tabella.", "Nessun Paziente Selezionato", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (String) pazientiTable.getValueAt(rigaSelezionata, 0);
    }

    /**
     * Aggiorna il contenuto della tabella con i nuovi dati dei pazienti prelevati dal database.
     *
     * @param datiPazienti lista contenente i record dei pazienti
     */
    public void aggiornaTabella(List<ArrayList<String>> datiPazienti) {
        DefaultTableModel model = (DefaultTableModel) pazientiTable.getModel();
        model.setRowCount(0);

        for (java.util.ArrayList<String> p : datiPazienti) {
            String cf = p.get(0);
            String nomeCognome = p.get(1) + " " + p.get(2);
            String dataNascita = p.size() > 3 ? p.get(3) : "";
            String sesso = p.size() > 4 ? p.get(4) : "";
            String residenza = p.size() > 5 ? p.get(5) : "";
            String diagnosi = p.size() > 6 ? p.get(6) : "";

            String statoRicovero = (p.size() > 7 && p.get(7) != null && !p.get(7).isEmpty()) ? "Ricoverato - Letto " + p.get(7) : "Non ricoverato";
            String reparto = (p.size() > 8 && p.get(8) != null && !p.get(8).isEmpty()) ? p.get(8) : "Nessuno";

            model.addRow(new Object[]{cf, nomeCognome, cf, dataNascita, diagnosi, sesso, residenza, statoRicovero, reparto});
        }
    }

    /**
     * Aggiunge un filtro basato su un'espressione regolare a partire dal testo di un JTextField.
     *
     * @param filters     la lista dei filtri accumulati
     * @param field       il campo di testo da cui prelevare il valore di ricerca
     * @param columnIndex l'indice della colonna su cui applicare il filtro
     */
    private void addFilterSeTesto(List<RowFilter<Object, Object>> filters, JTextField field, int columnIndex) {
        if (field != null && !field.getText().trim().isEmpty()) {
            String text = field.getText().trim();
            if (columnIndex == 3) {
                filters.add(RowFilter.regexFilter("^" + text, columnIndex));
            } else {
                filters.add(RowFilter.regexFilter("(?i)" + text, columnIndex));
            }
        }
    }

    /**
     * Aggiunge alla ricerca il filtro basato sulla selezione del sesso.
     *
     * @param filters la lista dei filtri accumulati
     */
    private void addSessoFilter(List<RowFilter<Object, Object>> filters) {
        if (maschioRadioButton != null && maschioRadioButton.isSelected()) {
            filters.add(RowFilter.regexFilter("(?i)^M$", 5));
        } else if (femminaRadioButton != null && femminaRadioButton.isSelected()) {
            filters.add(RowFilter.regexFilter("(?i)^F$", 5));
        }
    }

    /**
     * Aggiunge alla ricerca il filtro basato sulle tipologie di reparto selezionate nella lista.
     *
     * @param filters la lista dei filtri accumulati
     */
    private void addRepartoFilter(List<RowFilter<Object, Object>> filters) {
        if (tipologiaList != null && !tipologiaList.isSelectionEmpty()) {
            List<String> repartiScelti = tipologiaList.getSelectedValuesList();
            if (!repartiScelti.isEmpty()) {
                String regex = "(?i)(" + String.join("|", repartiScelti) + ")";
                filters.add(RowFilter.regexFilter(regex, 8));
            }
        }
    }

    /**
     * Raccoglie tutti i criteri di ricerca inseriti nei campi e li applica alla
     * tabella filtrandone le righe visibili in tempo reale.
     */
    private void eseguiRicerca() {
        if (sorter == null) return;
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        addFilterSeTesto(filters, nomeField, 1);
        addFilterSeTesto(filters, codiceField, 2);
        addFilterSeTesto(filters, dataField, 3);
        addFilterSeTesto(filters, ricercaprognosiField, 4);
        addFilterSeTesto(filters, residenzaField, 6);

        addSessoFilter(filters);
        addRepartoFilter(filters);

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    /**
     * Svuota tutti i campi di input, deseleziona i filtri e ripristina 
     * la tabella mostrando l'elenco completo di tutti i pazienti.
     */
    private void resettaRicerca() {
        if (nomeField != null) nomeField.setText("");
        if (codiceField != null) codiceField.setText("");
        if (residenzaField != null) residenzaField.setText("");
        if (ricercaprognosiField != null) ricercaprognosiField.setText("");
        if (maschioRadioButton != null) maschioRadioButton.setSelected(false);
        if (femminaRadioButton != null) femminaRadioButton.setSelected(false);
        if (tipologiaList != null) tipologiaList.clearSelection();
        if (dataField != null) dataField.setText("");
        if (sorter != null) sorter.setRowFilter(null);
    }

    /**
     * Popola la lista dei reparti con i dati forniti dinamicamente.
     *
     * @param reparti lista di stringhe rappresentanti i nomi dei reparti.
     */
    public void setRepartiListData(java.util.List<String> reparti) {
        // NOTA: Nel GUI designer, la JList dei reparti è stata erroneamente
        // associata alla variabile 'tipologiaList'.
        if (tipologiaList != null) { // Assicuriamoci che il componente esista
            final DefaultListModel<String> model = new DefaultListModel<>();
            if (reparti != null) { // Aggiunto controllo per prevenire NullPointerException
                for (String reparto : reparti) {
                    model.addElement(reparto);
                }
            }
            tipologiaList.setModel(model);
        }
    }
}