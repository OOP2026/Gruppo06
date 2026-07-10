package model;

import controller.Controller;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controller app = new Controller();
            app.avvia(); // Questo farà partire la tua schermata di Login!
        });
    }
}
