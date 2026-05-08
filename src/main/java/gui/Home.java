package gui;

import javax.swing.*;

public class Home {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Home");
        frame.setContentPane(new Home().panelHome);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(1000, 800); // Imposta le dimensioni desiderate
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // Centra la finestra
        frame.setVisible(true);
    }

    private JPanel panelHome;
    private JButton pazientiButton;
    private JButton lettiButton;
    private JButton dimissioniButton;
    private JButton mediciButton;
    private JButton turniButton;
    private JButton prestazioniButton;
    private JButton ricoveroButton;
}
