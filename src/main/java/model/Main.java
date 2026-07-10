package model;

import controller.Controller;
import javax.swing.SwingUtilities;

/**
 * Classe principale dell'applicazione.
 */
public class Main {

    /**
     * Punto di ingresso dell'applicazione.
     *
     * @param args gli argomenti passati da riga di comando
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controller app = new Controller();
            app.avvia(); // Questo farà partire la tua schermata di Login!
        });
    }
}
