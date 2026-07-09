package model;

/**
 * Rappresenta un letto all'interno di un reparto ospedaliero.
 * contiene le informazioni di base di un letto, come il reparto e lo stato di occupazione.
 */
public class Letto {
    private String reparto;
    private boolean occupato;

    /**
     * Costruisce un nuovo oggetto Letto.
     *
     * @param reparto  Il nome del reparto a cui il letto appartiene.
     * @param occupato Lo stato di occupazione del letto (true se occupato, false se libero).
     */
    public Letto( String reparto, boolean occupato) {
        this.reparto = reparto;
        this.occupato = occupato;
    }

    /**
     * Restituisce il nome del reparto a cui il letto appartiene.
     * @return il nome del reparto.
     */
    public String getReparto() {
        return reparto;
    }

    /**
     * Verifica se il letto è occupato.
     * @return {@code true} se il letto è occupato, altrimenti {@code false}.
     */
    public boolean isOccupato() {
        return occupato;
    }

    /**
     * Imposta lo stato di occupazione del letto.
     * @param occupato {@code true} per impostare il letto come occupato, {@code false} per liberarlo.
     */
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
