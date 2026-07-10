package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * La classe Login gestisce l'interfaccia grafica della schermata di autenticazione.
 * Fornisce inoltre una serie di costanti e metodi statici di utilità per
 * applicare uno stile visivo coerente (colori, font, tabelle, bottoni) 
 * in tutta l'applicazione ospedaliera.
 */
public class Login {
    public static final Color AZZURRO_HOME = new Color(70, 132, 197);
    public static final Color SELECTION_BG = new Color(187, 222, 247);
    public static final Color ALT_ROW_BG = new Color(0xf5, 0xf8, 0xfc);
    public static final Font BASE_FONT = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 12);

    private JPasswordField passwordField;
    private JButton accediButton;
    public JPanel mainPanel;
    private JTextField usernameField;
    private JLabel registratiLabel;
    private JPasswordField pinField;

    /**
     * Costruisce una nuova schermata di Login, applicando
     * gli stili visivi ai componenti principali.
     */
    public Login() {
        Login.applicaStilePulsantiCentrali(accediButton);
        Login.applicaStileLabelLink(registratiLabel);
    }

    /**
     * Restituisce lo username inserito dall'utente.
     *
     * @return la stringa contenente lo username (senza spazi bianchi iniziali/finali)
     */
    public String getUsername() {
        return usernameField.getText().trim();
    }

    /**
     * Restituisce la password inserita dall'utente.
     *
     * @return la stringa contenente la password
     */
    public String getPassword() {
        return new String(passwordField.getPassword()).trim();
    }

    /**
     * Restituisce il PIN inserito dall'utente (se il campo è presente).
     *
     * @return la stringa contenente il PIN, oppure una stringa vuota se il campo è nullo
     */
    public String getPin() {
        if (pinField != null) {
            return new String(pinField.getPassword()).trim();
        }
        return "";
    }

    /**
     * Registra un listener per il pulsante di accesso.
     *
     * @param listener il comportamento da eseguire al click
     */
    public void addLoginListener(java.awt.event.ActionListener listener) {
        accediButton.addActionListener(listener);
    }

    /**
     * Registra un listener per l'etichetta (link) di registrazione.
     *
     * @param listener il comportamento da eseguire al click/interazione
     */
    public void addRegisterListener(java.awt.event.MouseListener listener) {
        registratiLabel.addMouseListener(listener);
    }

    /**
     * Mostra una finestra di dialogo (pop-up) con un messaggio per l'utente.
     *
     * @param title       il titolo della finestra di dialogo
     * @param message     il testo del messaggio da mostrare
     * @param messageType il tipo di messaggio (es. JOptionPane.ERROR_MESSAGE)
     */
    public void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(mainPanel, message, title, messageType);
    }

    /**
     * Applica uno stile visivo standard (bordi e colori) ai pulsanti centrali dell'interfaccia.
     *
     * @param bottone il bottone a cui applicare lo stile
     */
    public static void applicaStilePulsantiCentrali(JButton bottone) {
        if (bottone == null) return;
        impostaColoriEdEffetti(bottone, Color.WHITE, Color.BLACK, AZZURRO_HOME, Color.WHITE);
        bottone.setBorder(BorderFactory.createLineBorder(AZZURRO_HOME, 1));
        bottone.setBorderPainted(true);
    }

    /**
     * Applica lo stile visivo standard ai pulsanti del menu di navigazione laterale.
     *
     * @param bottone il bottone a cui applicare lo stile
     */
    public static void applicaStileMenuLaterale(JButton bottone) {
        if (bottone == null) return;
        impostaColoriEdEffetti(bottone, AZZURRO_HOME, Color.WHITE, Color.WHITE, Color.BLACK);
    }

    /**
     * Configura i colori di base, il cursore e gli effetti al passaggio del mouse (hover) per un bottone.
     *
     * @param bottone       il bottone da personalizzare
     * @param sfondoDefault il colore di sfondo a riposo
     * @param testoDefault  il colore del testo a riposo
     * @param sfondoHover   il colore di sfondo al passaggio del mouse
     * @param testoHover    il colore del testo al passaggio del mouse
     */
    public static void impostaColoriEdEffetti(JButton bottone, Color sfondoDefault, Color testoDefault, Color sfondoHover, Color testoHover) {
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

    /**
     * Applica i colori di selezione e il font standard a un componente JList.
     *
     * @param list la lista da personalizzare
     */
    public static void styleList(JList<?> list) {
        if (list == null) return;
        list.setSelectionBackground(AZZURRO_HOME);
        list.setSelectionForeground(Color.WHITE);
        list.setFont(BASE_FONT);
    }

    /**
     * Configura in maniera globale l'aspetto di una JTable, impostando colori per
     * le righe alterne, margini, intestazioni e comportamenti di selezione.
     *
     * @param table la tabella a cui applicare lo stile
     */
    public static void setupTableStyle(JTable table) {
        if (table == null) return;
        table.setRowHeight(26);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(SELECTION_BG);
        table.setSelectionForeground(Color.BLACK);
        table.setFont(BASE_FONT);

        JTableHeader th = table.getTableHeader();
        th.setBackground(AZZURRO_HOME);
        th.setForeground(Color.WHITE);
        th.setFont(HEADER_FONT);
        th.setPreferredSize(new Dimension(th.getWidth(), 30));
        th.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : ALT_ROW_BG);
                    setForeground(Color.BLACK);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return this;
            }
        });
    }

    /**
     * Applica uno stile dedicato e semplificato alla tabella specifica dell'Agenda.
     *
     * @param agendaTable la tabella dell'agenda da stilizzare
     */
    public static void setupAgendaTableStyle(JTable agendaTable) {
        if (agendaTable == null) return;
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

    /**
     * Trasforma visivamente una JLabel in un link interattivo, aggiungendo 
     * l'effetto di sottolineatura testuale al passaggio del mouse.
     *
     * @param label l'etichetta testuale da rendere simile a un link web
     */
    public static void applicaStileLabelLink(JLabel label) {
        if (label == null) return;

        final String testoOriginale = label.getText().replace("<html><u>", "").replace("</u></html>", "");
        label.setForeground(AZZURRO_HOME);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setText("<html><u>" + testoOriginale + "</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setText(testoOriginale);
            }
        });
    }

}