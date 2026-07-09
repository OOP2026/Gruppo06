package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionListener;


public class Letti extends JFrame {
    public JPanel mainPanel;
    private JRadioButton tuttiRadioButton;
    private JRadioButton disponibileRadioButton;
    private JRadioButton occupatoRadioButton;
    private JList<String> repartoList;
    private JButton cercaButton;
    private JButton resetButton;
    private JTable lettiTable; // Questa è la tabella che mostra i letti
    private JButton assegnaPazienteButton;
    private JButton storicoLettiButton;
    private JTextField stanzaField;
    private JTextField pazienteField;

    private static final String[] COLONNE = {
            "Numero Letto", "Stanza", "Nome Paziente", "Codice Fiscale", "Reparto", "Stato"
    };

    public Letti() {
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
        Login.styleList(repartoList); // Mantenuto per repartoList
        Login.setupTableStyle(lettiTable);
        Login.applicaStilePulsantiCentrali(cercaButton);
        Login.applicaStilePulsantiCentrali(resetButton);
        Login.applicaStilePulsantiCentrali(assegnaPazienteButton);
        Login.applicaStilePulsantiCentrali(storicoLettiButton);
        
        // Centra il testo nelle colonne della tabella per far risaltare meglio le emoji
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < lettiTable.getColumnCount(); i++) {
            lettiTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    /**
     * Rende il pulsante "Assegna Paziente" accessibile a un controller esterno.
     * @param listener L'ActionListener che verrà eseguito al click del pulsante.
     */
    public void addAssegnaPazienteListener(ActionListener listener) {
        assegnaPazienteButton.addActionListener(listener);
    }

    public void addCercaListener(ActionListener listener) {
        cercaButton.addActionListener(listener);
    }

    public void addResetListener(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    public void addStoricoLettiListener(ActionListener listener) {
        storicoLettiButton.addActionListener(listener);
    }

    public String getSelectedStato() {
        if (disponibileRadioButton.isSelected()) return "Libero";
        if (occupatoRadioButton.isSelected()) return "Occupato";
        return "Tutti";
    }

    public String getSelectedReparto() {
        // Se nessun reparto è selezionato, restituisce null o una stringa vuota per indicare "tutti i reparti"
        return repartoList.getSelectedValue();
    }

    public String getStanza() {
        return stanzaField.getText();
    }

    public String getPaziente() {
        return pazienteField.getText();
    }

    public void resetCampiRicerca() {
        tuttiRadioButton.setSelected(true);
        repartoList.clearSelection();
        stanzaField.setText("");
        pazienteField.setText("");
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
     * Recupera il Reparto del letto attualmente selezionato nella tabella.
     * @return Il Reparto del letto come String, o null se non c'è nessuna selezione.
     */
    public String getRepartoLettoSelezionato() {
        int rigaSelezionata = lettiTable.getSelectedRow();
        if (rigaSelezionata == -1) {
            return null;
        }
        return (String) lettiTable.getValueAt(rigaSelezionata, 4);
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
}