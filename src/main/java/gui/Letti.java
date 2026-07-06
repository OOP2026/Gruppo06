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

    private static final String[] COLONNE = {
            "ID Letto", "Tipologia Letto", "Reparto",
            "Stanza", "Numero Letto", "Stato"
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
        Login.styleList(repartoList);
        Login.styleList(tipologiaList);
        Login.setupTableStyle(lettiTable);
        Login.applicaStilePulsantiCentrali(cercaButton);
        Login.applicaStilePulsantiCentrali(resetButton);
        Login.applicaStilePulsantiCentrali(assegnaPazienteButton);
        Login.applicaStilePulsantiCentrali(storicoLettiButton);
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
            controller.Controller.impostaSchermata(frame, frame.mainPanel, "Gestione Letti", JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}