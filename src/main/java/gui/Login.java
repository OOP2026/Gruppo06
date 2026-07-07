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



    public Login() {
        Login.applicaStilePulsantiCentrali(accediButton);
        Login.applicaStileLabelLink(registratiLabel);
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword()).trim();
    }

    public String getPin() {
        if (pinField != null) {
            return new String(pinField.getPassword()).trim();
        }
        return "";
    }

    public void addLoginListener(java.awt.event.ActionListener listener) {
        accediButton.addActionListener(listener);
    }

    public void addRegisterListener(java.awt.event.MouseListener listener) {
        registratiLabel.addMouseListener(listener);
    }

    public void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(mainPanel, message, title, messageType);
    }

    public static void applicaStilePulsantiCentrali(JButton bottone) {
        if (bottone == null) return;
        impostaColoriEdEffetti(bottone, Color.WHITE, Color.BLACK, AZZURRO_HOME, Color.WHITE);
        bottone.setBorder(BorderFactory.createLineBorder(AZZURRO_HOME, 1));
        bottone.setBorderPainted(true);
    }

    public static void applicaStileMenuLaterale(JButton bottone) {
        if (bottone == null) return;
        impostaColoriEdEffetti(bottone, AZZURRO_HOME, Color.WHITE, Color.WHITE, Color.BLACK);
    }

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

    public static void styleList(JList<?> list) {
        if (list == null) return;
        list.setSelectionBackground(AZZURRO_HOME);
        list.setSelectionForeground(Color.WHITE);
        list.setFont(BASE_FONT);
    }

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

    public static void applicaStileLabelLink(JLabel label) {
        if (label == null) return;

        final String testoOriginale = label.getText().replace("<html><u>", "").replace("</u></html>", "");
        label.setForeground(AZZURRO_HOME);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Applica la sottolineatura usando HTML
                label.setText("<html><u>" + testoOriginale + "</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Ripristina il testo originale senza sottolineatura
                label.setText(testoOriginale);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Login");
            controller.Controller.impostaSchermata(frame, new Login().mainPanel, "Login", JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}