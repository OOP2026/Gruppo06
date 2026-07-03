package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Schermata_Amministratore extends JFrame {

    private JPanel panelHome;
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
    private JTextField DataField;
    private JButton ricercaButton;
    private JPanel AgendaPanel;
    private JTable AgendaTable;
    private JButton NewEventButton;

    // COSTRUTTORE
    public Schermata_Amministratore(String nomeUtente) {

        this.setTitle("Ospedale - Home");
        this.setContentPane(panelHome);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Chiude tutto se chiudi la Home
        this.setSize(1000, 680);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        if (utenteLoggatoLabel != null) {
            utenteLoggatoLabel.setText(" " + nomeUtente);
            utenteLoggatoLabel.setForeground(Color.WHITE);
            utenteLoggatoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        }

        // --- STILE MENU LATERALE ---
        applicaStileMenuLaterale(prestazioniButton);
        applicaStileMenuLaterale(ricoveroButton);
        applicaStileMenuLaterale(turniButton);
        applicaStileMenuLaterale(esciButton);

        //pulsanti dell'agenda nel menu laterale
        applicaStilePulsantiCentrali(ricercaButton);
        applicaStilePulsantiCentrali(NewEventButton);

        // --- STILE PULSANTI CENTRALI ---
        applicaStilePulsantiCentrali(pazientiButton);
        applicaStilePulsantiCentrali(lettiButton);
        applicaStilePulsantiCentrali(dimissioniButton);
        applicaStilePulsantiCentrali(mediciButton);

        // --- POPOLA LA TABELLA DELL'AGENDA ---
        popolaTabellaAgenda();

        // Esempio logica "Nuovo Evento"
        if (NewEventButton != null) {
            NewEventButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "Apertura modulo per un nuovo evento...", "Nuovo Evento", JOptionPane.INFORMATION_MESSAGE);
            });
        }
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
        if (NewEventButton != null) NewEventButton.addActionListener(listener);
    }

    public void aggiornaAgenda(Object[][] dati) {
        if (AgendaTable != null) {
            DefaultTableModel model = (DefaultTableModel) AgendaTable.getModel();
            model.setRowCount(0); // Svuota la tabella dai vecchi dati
            for (Object[] riga : dati) {
                model.addRow(riga);
            }
        }
    }

    /**
     * Metodo dedicato per configurare e popolare la JTable creata dal GUI Builder
     */
    private void popolaTabellaAgenda() {
        if (AgendaTable != null) {
            // Definisce le colonne
            String[] colonne = {"Ora", "Evento"};

            // Crea un modello personalizzato per impedire la modifica diretta del testo nelle celle
            DefaultTableModel model = new DefaultTableModel(new Object[0][0], colonne) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            // Applica il modello
            AgendaTable.setModel(model);
            AgendaTable.setRowHeight(30);

            // Colori per le righe normali
            AgendaTable.setForeground(Color.BLACK); // Forza il testo a nero
            AgendaTable.setBackground(Color.WHITE); // Forza lo sfondo a bianco

            // Colori per la riga selezionata
            AgendaTable.setSelectionBackground(new Color(180, 210, 240));
            AgendaTable.setSelectionForeground(Color.BLACK); // Mantiene il testo nero anche se selezionato

            // Colori e font per l'intestazione (Header)
            AgendaTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            AgendaTable.getTableHeader().setForeground(Color.BLACK); // Forza il testo dell'intestazione a nero
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Avviamo tramite il Controller per agganciare i bottoni!
            controller.Controller ctrl = new controller.Controller();
            ctrl.avviaSchermataAmministratore("Dott. Mario Rossi (TEST)");
        });
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
        Color coloreTestoDefault = Color.BLACK;  // Testo nero per essere leggibile sul bianco
        Color coloreSfondoHover = new Color(70, 132, 197); // Azzurro al passaggio del mouse
        Color coloreTestoHover = Color.WHITE;              // Testo bianco sull'azzurro

        impostaColoriEdEffetti(bottone, coloreSfondoDefault, coloreTestoDefault, coloreSfondoHover, coloreTestoHover);
    }

    private void impostaColoriEdEffetti(JButton bottone, Color sfondoDefault, Color testoDefault, Color sfondoHover, Color testoHover) {
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
}