package model;

/**
 * Rappresenta il modello di dati per un Letto.
 * Questa classe è un Plain Old Java Object (POJO) che incapsula le informazioni
 * di base di un letto, come il suo identificativo, il reparto di appartenenza
 * e il suo stato di occupazione.
 * Ho rimosso la logica e i campi complessi per renderlo un semplice contenitore
 * di dati, come dovrebbe essere un modello.
 */
public class Letto {
    private String reparto;
    private boolean occupato;

    public Letto( String reparto, boolean occupato) {
        this.reparto = reparto;
        this.occupato = occupato;
    }

    // --- Getters ---

    public String getReparto() {
        return reparto;
    }

    public boolean isOccupato() {
        return occupato;
    }

    // --- Setters ---
    public void setOccupato(boolean occupato) {
        this.occupato = occupato;
    }

    @Override
    public String toString() {
        return "Letto{" +
                ", reparto='" + reparto + '\'' +
                ", occupato=" + occupato +
                '}';
    }
}
