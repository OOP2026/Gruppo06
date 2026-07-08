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
    private JPanel agendaPanel;
    private JTextField dataField;
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
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    utenteLoggatoLabel.setForeground(new Color(173, 216, 230)); // Azzurro chiaro
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    utenteLoggatoLabel.setForeground(Color.WHITE);
                }
            });
        }

        // --- FORZA IL COLORE BIANCO PER IL TITOLO DEL PANNELLO "AGENDA" ---
        if (agendaPanel != null && agendaPanel.getBorder() instanceof javax.swing.border.TitledBorder) {
            ((javax.swing.border.TitledBorder) agendaPanel.getBorder()).setTitleColor(Color.WHITE);
        }

        // --- STILE MENU LATERALE ---
        Login.applicaStileMenuLaterale(prestazioniButton);
        Login.applicaStileMenuLaterale(turniButton);
        Login.applicaStileMenuLaterale(esciButton);

        // Pulsanti dell'agenda nel menu laterale
        Login.applicaStilePulsantiCentrali(ricercaButton);
        Login.applicaStilePulsantiCentrali(newEventButton);
        Login.applicaStilePulsantiCentrali(settimanaleButton);

        // --- STILE PULSANTI CENTRALI ---
        Login.applicaStilePulsantiCentrali(pazientiButton);
        Login.applicaStilePulsantiCentrali(lettiButton);
        Login.applicaStilePulsantiCentrali(dimissioniButton);
        Login.applicaStilePulsantiCentrali(ricoveroButton);


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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SchermataMedico frame = new SchermataMedico("Dott. Luigi Verdi (TEST)");
            controller.Controller.impostaSchermata(frame, frame.mainPanel, "Ospedale - Home Medico", JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}