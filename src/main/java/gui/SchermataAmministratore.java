package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SchermataAmministratore extends JFrame {

    public JPanel mainPanel;
    private JButton pazientiButton;
    private JButton lettiButton;
    private JButton dimissioniButton;
    private JButton mediciButton;
    private JButton prestazioniButton;
    private JButton ricoveroButton;
    private JButton turniButton;
    private JLabel utenteLoggatoLabel;
    private JButton esciButton;

    // Attributi per l'Agenda
    private JTextField dataField;
    private JButton ricercaButton;
    private JPanel agendaPanel;
    private JTable agendaTable;
    private JButton newEventButton;

    // COSTRUTTORE
    public SchermataAmministratore(String nomeUtente) {

        if (utenteLoggatoLabel != null) {
            utenteLoggatoLabel.setText(" " + nomeUtente);
            utenteLoggatoLabel.setForeground(Color.WHITE);
            utenteLoggatoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        }

        // --- STILE MENU LATERALE ---
        Login.applicaStileMenuLaterale(prestazioniButton);
        Login.applicaStileMenuLaterale(ricoveroButton);
        Login.applicaStileMenuLaterale(turniButton);
        Login.applicaStileMenuLaterale(esciButton);

        //pulsanti dell'agenda nel menu laterale
        Login.applicaStilePulsantiCentrali(ricercaButton);
        Login.applicaStilePulsantiCentrali(newEventButton);

        // --- STILE PULSANTI CENTRALI ---
        Login.applicaStilePulsantiCentrali(pazientiButton);
        Login.applicaStilePulsantiCentrali(lettiButton);
        Login.applicaStilePulsantiCentrali(dimissioniButton);
        Login.applicaStilePulsantiCentrali(mediciButton);

        // --- POPOLA LA TABELLA DELL'AGENDA ---
        Login.setupAgendaTableStyle(agendaTable);
    }

    // =========================================================
    // METODI PUBBLICI PER ESPORRE I BOTTONI AL CONTROLLER ESTERNO
    // =========================================================

    public void addPazientiListener(ActionListener listener) {
        if (pazientiButton != null) pazientiButton.addActionListener(listener);
    }

    public void addLettiListener(ActionListener listener) {
        if (lettiButton != null) lettiButton.addActionListener(listener);
    }

    public void addPrestazioniListener(ActionListener listener) {
        if (prestazioniButton != null) prestazioniButton.addActionListener(listener);
    }

    public void addEsciListener(ActionListener listener) {
        if (esciButton != null) esciButton.addActionListener(listener);
    }

    public void addMediciListener(ActionListener listener) {
        if (mediciButton != null) mediciButton.addActionListener(listener);
    }

    public void addDimissioniListener(ActionListener listener) {
        if (dimissioniButton != null) dimissioniButton.addActionListener(listener);
    }

    public void addRicoveroListener(ActionListener listener) {
        if (ricoveroButton != null) ricoveroButton.addActionListener(listener);
    }

    public void addTurniListener(ActionListener listener) {
        if (turniButton != null) turniButton.addActionListener(listener);
    }

    public void addRicercaAgendaListener(ActionListener listener) {
        if (ricercaButton != null) ricercaButton.addActionListener(listener);
    }

    public void addNewEventListener(ActionListener listener) {
        if (newEventButton != null) newEventButton.addActionListener(listener);
    }

    public void aggiornaAgenda(Object[][] dati) {
        if (agendaTable != null && dati != null) {
            DefaultTableModel model = (DefaultTableModel) agendaTable.getModel();
            model.setRowCount(0); // Svuota la tabella dai vecchi dati
            for (Object[] riga : dati) {
                model.addRow(riga);
            }
            // Notifica esplicitamente alla tabella che i dati sono cambiati per forzare il refresh visivo
            model.fireTableDataChanged();
        }
    }

    /**
     * Restituisce il testo inserito nel campo di ricerca per passarlo al Controller
     */
    public String getTestoRicercaAgenda() {
        return (dataField != null) ? dataField.getText().trim() : "";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SchermataAmministratore frame = new SchermataAmministratore("Dott. Mario Rossi (TEST)");
            controller.Controller.impostaSchermata(frame, frame.mainPanel, "Ospedale - Home Amministratore", JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

}