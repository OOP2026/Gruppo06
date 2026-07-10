package model;

import controller.Controller;
import javax.swing.SwingUtilities;

/**
 * The type Main.
 */
public class Main {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controller app = new Controller();
            app.avvia(); // Questo farà partire la tua schermata di Login!
        });
    }
}
