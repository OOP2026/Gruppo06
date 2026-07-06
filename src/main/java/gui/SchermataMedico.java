package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

public class SchermataMedico extends JFrame {
    public JPanel mainPanel;
    private JButton ricoveroButton;
    private JButton turniButton;
    private JLabel utenteLoggatoLabel;
    private JButton esciButton;
    private JButton pazientiButton;
    private JButton lettiButton;
    private JButton dimissioniButton;
    private JButton prestazioniButton;
    private JPanel agendaPanel;
    private JTextField dataField;
    private JButton ricercaButton;
    private  JTable agendaTable ;
    private JButton newEventButton;

    public SchermataMedico(String nomeUtente) {
        if (utenteLoggatoLabel != null) {
            utenteLoggatoLabel.setText(" " + nomeUtente);
            utenteLoggatoLabel.setForeground(Color.WHITE);
            utenteLoggatoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        }

        // --- FORZA IL COLORE BIANCO PER IL TITOLO DEL PANNELLO "AGENDA" ---
        if (agendaPanel != null && agendaPanel.getBorder() instanceof javax.swing.border.TitledBorder) {
            ((javax.swing.border.TitledBorder) agendaPanel.getBorder()).setTitleColor(Color.WHITE);
        }

        // --- STILE MENU LATERALE ---
        Login.applicaStileMenuLaterale(ricoveroButton);
        Login.applicaStileMenuLaterale(turniButton);
        Login.applicaStileMenuLaterale(esciButton);

        // Pulsanti dell'agenda nel menu laterale
        Login.applicaStilePulsantiCentrali(ricercaButton);
        Login.applicaStilePulsantiCentrali(newEventButton);

        // --- STILE PULSANTI CENTRALI ---
        Login.applicaStilePulsantiCentrali(pazientiButton);
        Login.applicaStilePulsantiCentrali(lettiButton);
        Login.applicaStilePulsantiCentrali(dimissioniButton);
        Login.applicaStilePulsantiCentrali(prestazioniButton);


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

    public void addDimissioniListener(ActionListener listener) {
        if (dimissioniButton != null) dimissioniButton.addActionListener(listener);
    }

    public void addRicoveroListener(ActionListener listener) {
        if (ricoveroButton != null) ricoveroButton.addActionListener(listener);
    }

    public void addTurniListener(ActionListener listener) {
        if (turniButton != null) turniButton.addActionListener(listener);
    }

    public void addEsciListener(ActionListener listener) {
        if (esciButton != null) esciButton.addActionListener(listener);
    }

    public void addRicercaAgendaListener(ActionListener listener) {
        if (ricercaButton != null) ricercaButton.addActionListener(listener);
    }

    public void addNewEventListener(ActionListener listener) {
        if (newEventButton != null) newEventButton.addActionListener(listener);
    }

    public void aggiornaAgenda(Object[][] dati) {
        if (agendaTable != null) {
            DefaultTableModel model = (DefaultTableModel) agendaTable.getModel();
            model.setRowCount(0); // Svuota la tabella dai vecchi dati
            for (Object[] riga : dati) {
                model.addRow(riga);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SchermataMedico frame = new SchermataMedico("Dott. Luigi Verdi (TEST)");
            controller.Controller.impostaSchermata(frame, frame.mainPanel, "Ospedale - Home Medico", JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}