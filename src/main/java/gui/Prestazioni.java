package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Prestazioni {
    public JPanel panel1;
    public JPanel header;
    public JPanel body;
    public JPanel footer;
    public JTextField nomeDescrizioneTextField;
    public JTextField codicePrestazioneTextField;
    public JSpinner spinner1;
    public JButton resetButton;
    public JButton cercaPrestazioniButton;
    public JList list1;
    public JList list2;
    public JTable Medici;

    public Prestazioni() {
        String[] colonne = {"ID Prestaz.", "Codice", "Nome Prestazione", "Tipo", "Reparto Erog.", "Note/Dettagli"};
        DefaultTableModel model = new DefaultTableModel(colonne, 0);
        model.addRow(new Object[]{"ID: P001", "D001", "Intervento Chirurgia Bariatrica", "Chirurgia Gen.", "Blocco Operatorio", "Team spec."});
        model.addRow(new Object[]{"ID: P002", "R005", "RM Cardiaca con Contrasto", "Diagnostica Av.", "Neuroradiologia", "Cardiologo pres."});
        model.addRow(new Object[]{"ID: P003", "E101", "Colonscopia Robotica", "Procedure Endo.", "Blocco Operatorio", "Sedazione"});
        Medici.setModel(model);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Prestazioni");
        Prestazioni p = new Prestazioni();
        frame.setContentPane(p.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setVisible(true);
    }
}