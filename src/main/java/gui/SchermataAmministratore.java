package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public class SchermataAmministratore extends JFrame {

    public JPanel mainPanel;
    private JButton pazientiButton;
    private JButton lettiButton;
    private JButton dimissioniButton;
    private JButton ricoveroButton;
    private JButton prestazioniButton;
    private JButton mediciButton;
    private JButton turniButton;
    private JLabel utenteLoggatoLabel;
    private JButton esciButton;

    // Attributi per l'Agenda
    private JPanel agendaPanel;
    private JTextField dataField;
    private JButton ricercaButton;
    private JTable agendaTable;
    private JButton settimanaleButton;
    private JButton newEventButton;

    public SchermataAmministratore(String nomeUtente) {

        if (utenteLoggatoLabel != null) {
            utenteLoggatoLabel.setText(" " + nomeUtente);
            utenteLoggatoLabel.setForeground(Color.WHITE);
            utenteLoggatoLabel.setFont(new Font("Arial", Font.BOLD, 14));
            utenteLoggatoLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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

        applicaStiliAiPulsanti();

        Login.setupAgendaTableStyle(agendaTable);
    }


    public void addPazientiListener(ActionListener listener) {
        this.pazientiButton.addActionListener(listener);
    }

    public void addLettiListener(ActionListener listener) {
        this.lettiButton.addActionListener(listener);
    }

    public void addPrestazioniListener(ActionListener listener) {
        this.prestazioniButton.addActionListener(listener);
    }

    public void addEsciListener(ActionListener listener) {
        this.esciButton.addActionListener(listener);
    }

    public void addMediciListener(ActionListener listener) {
        this.mediciButton.addActionListener(listener);
    }

    public void addDimissioniListener(ActionListener listener) {
        this.dimissioniButton.addActionListener(listener);
    }

    public void addRicoveroListener(ActionListener listener) {
        this.ricoveroButton.addActionListener(listener);
    }

    public void addTurniListener(ActionListener listener) {
        this.turniButton.addActionListener(listener);
    }

    public void addRicercaAgendaListener(ActionListener listener) {
        this.ricercaButton.addActionListener(listener);
    }

    public void addNewEventListener(ActionListener listener) {
        this.newEventButton.addActionListener(listener);
    }

    public void addSettimanaleListener(ActionListener listener) {
        this.settimanaleButton.addActionListener(listener);
    }

    public void addProfiloListener(MouseAdapter listener) {
        if (utenteLoggatoLabel != null) utenteLoggatoLabel.addMouseListener(listener);
    }

    public void updateUtenteLoggatoLabel(String nomeUtente) {
        if (utenteLoggatoLabel != null) {
            utenteLoggatoLabel.setText(" " + nomeUtente);
        }
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

    private void applicaStiliAiPulsanti() {
        JButton[] menuLateraleButtons = {prestazioniButton, mediciButton, turniButton, esciButton};
        for (JButton button : menuLateraleButtons) {
            Login.applicaStileMenuLaterale(button);
        }

        JButton[] pulsantiCentraliButtons = {ricercaButton, newEventButton, settimanaleButton, pazientiButton, lettiButton, dimissioniButton, ricoveroButton};
        for (JButton button : pulsantiCentraliButtons) {
            Login.applicaStilePulsantiCentrali(button);
        }
    }
}