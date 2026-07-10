package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

public class SchermataMedico extends JFrame {
    public JPanel mainPanel;
    private JButton prestazioniButton;
    private JButton turniButton;
    private JLabel utenteLoggatoLabel;
    private JButton esciButton;
    private JButton pazientiButton;
    private JButton lettiButton;
    private JButton dimissioniButton;
    private JButton ricoveroButton;

    // Attributi per l'Agenda
    private JPanel agendaPanel;
    private JTextField ricercaField;
    private JButton ricercaButton;
    private  JTable agendaTable ;
    private JButton newEventButton;
    private JButton settimanaleButton;

    public SchermataMedico(String nomeUtente) {
        if (utenteLoggatoLabel != null) {
            utenteLoggatoLabel.setText(" " + nomeUtente);
            utenteLoggatoLabel.setForeground(Color.WHITE);
            utenteLoggatoLabel.setFont(new Font("Arial", Font.BOLD, 14));
            utenteLoggatoLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            utenteLoggatoLabel.setToolTipText("Clicca per visualizzare e modificare il tuo profilo");

            utenteLoggatoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    utenteLoggatoLabel.setForeground(new Color(173, 216, 230)); // Azzurro chiaro
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    utenteLoggatoLabel.setForeground(Color.WHITE);
                }
            });
        }

        if (agendaPanel != null && agendaPanel.getBorder() instanceof javax.swing.border.TitledBorder) {
            ((javax.swing.border.TitledBorder) agendaPanel.getBorder()).setTitleColor(Color.WHITE);
        }


        Login.applicaStileMenuLaterale(prestazioniButton);
        Login.applicaStileMenuLaterale(turniButton);
        Login.applicaStileMenuLaterale(esciButton);


        Login.applicaStilePulsantiCentrali(ricercaButton);
        Login.applicaStilePulsantiCentrali(newEventButton);
        Login.applicaStilePulsantiCentrali(settimanaleButton);


        Login.applicaStilePulsantiCentrali(pazientiButton);
        Login.applicaStilePulsantiCentrali(lettiButton);
        Login.applicaStilePulsantiCentrali(dimissioniButton);
        Login.applicaStilePulsantiCentrali(ricoveroButton);


        Login.setupAgendaTableStyle(agendaTable);
    }

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

    public void addSettimanaleListener(ActionListener listener) {
        if (settimanaleButton != null) settimanaleButton.addActionListener(listener);
    }

    public void addProfiloListener(java.awt.event.MouseAdapter listener) {
        if (utenteLoggatoLabel != null) utenteLoggatoLabel.addMouseListener(listener);
    }

    public void updateUtenteLoggatoLabel(String nomeUtente) {
        if (utenteLoggatoLabel != null) {
            utenteLoggatoLabel.setText(" " + nomeUtente);
        }
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

    /**
     * Restituisce il testo inserito nel campo di ricerca per passarlo al Controller
     */
    public String getTestoRicercaAgenda() {
        return (ricercaField != null) ? ricercaField.getText().trim() : "";
    }

}