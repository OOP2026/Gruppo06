package gui;

import controller.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SchermataMedico extends JFrame {
    private JPanel mainPanel;
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
        this.setTitle("Ospedale - Home Medico");
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(1000, 680);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

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
        applicaStileMenuLaterale(ricoveroButton);
        applicaStileMenuLaterale(turniButton);
        applicaStileMenuLaterale(esciButton);

        // Pulsanti dell'agenda nel menu laterale
        applicaStilePulsantiCentrali(ricercaButton);
        applicaStilePulsantiCentrali(newEventButton);

        // --- STILE PULSANTI CENTRALI ---
        applicaStilePulsantiCentrali(pazientiButton);
        applicaStilePulsantiCentrali(lettiButton);
        applicaStilePulsantiCentrali(dimissioniButton);
        applicaStilePulsantiCentrali(prestazioniButton);

        // --- POPOLA LA TABELLA DELL'AGENDA ---
        popolaTabellaAgenda();
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

    public void addRicercaAgendaListener(ActionListener listener) {
        if (ricercaButton != null) ricercaButton.addActionListener(listener);
    }

    public void addNewEventListener(ActionListener listener) {
        if (newEventButton != null) newEventButton.addActionListener(listener);
    }

    public void addEsciListener(ActionListener listener) {
        if (esciButton != null) esciButton.addActionListener(listener);
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

    private void popolaTabellaAgenda() {
        if (agendaTable != null) {
            String[] colonne = {"Ora", "Evento"};

            DefaultTableModel model = new DefaultTableModel(new Object[0][0], colonne) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            agendaTable.setModel(model);
            agendaTable.setRowHeight(30);
            agendaTable.setForeground(Color.BLACK);
            agendaTable.setBackground(Color.WHITE);
            agendaTable.setSelectionBackground(new Color(180, 210, 240));
            agendaTable.setSelectionForeground(Color.BLACK);
            agendaTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            agendaTable.getTableHeader().setForeground(Color.BLACK);
        }
    }

    private void applicaStileMenuLaterale(JButton bottone) {
        if (bottone == null) return;
        Color coloreSfondoDefault = new Color(70, 132, 197); // Azzurro
        Color coloreTestoDefault = Color.WHITE;
        Color coloreSfondoHover = Color.WHITE;
        Color coloreTestoHover = Color.BLACK;
        impostaColoriEdEffetti(bottone, coloreSfondoDefault, coloreTestoDefault, coloreSfondoHover, coloreTestoHover);
    }

    private void applicaStilePulsantiCentrali(JButton bottone) {
        if (bottone == null) return;
        Color coloreSfondoDefault = Color.WHITE; // Bianco di base
        Color coloreTestoDefault = Color.BLACK;  // Testo nero per essere leggibile
        Color coloreSfondoHover = new Color(70, 132, 197); // Azzurro hover
        Color coloreTestoHover = Color.WHITE;
        impostaColoriEdEffetti(bottone, coloreSfondoDefault, coloreTestoDefault, coloreSfondoHover, coloreTestoHover);
    }

    private void impostaColoriEdEffetti(JButton bottone, Color sfondoDefault, Color testoDefault, Color sfondoHover, Color testoHover) {
        if (bottone == null) return;
        bottone.setBackground(sfondoDefault);
        bottone.setForeground(testoDefault);
        bottone.setFocusPainted(false);
        bottone.setBorderPainted(false);
        bottone.setContentAreaFilled(true);
        bottone.setOpaque(true);
        bottone.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottone.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bottone.setBackground(sfondoHover);
                bottone.setForeground(testoHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bottone.setBackground(sfondoDefault);
                bottone.setForeground(testoDefault);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Avviamo tramite il Controller per agganciare i bottoni!
            Controller ctrl = new Controller();
            ctrl.avviaSchermataMedico("Dott. Luigi Verdi (TEST)");
        });
    }
}