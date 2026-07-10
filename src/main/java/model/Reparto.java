package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un reparto ospedaliero.
 * Contiene informazioni sul nome e il numero di medici afferenti.
 */
public class Reparto {
    private String nomeReparto;
    private int numeroAfferenti;
    private List<Stanza> stanze;

    /**
     * Costruisce un nuovo oggetto Reparto.
     * @param nomeReparto Il nome del reparto.
     * @param numeroAfferenti Il numero di medici afferenti.
     */
    public Reparto(String nomeReparto, int numeroAfferenti) {
        this.nomeReparto = nomeReparto;
        this.numeroAfferenti = numeroAfferenti;
        this.stanze = new ArrayList<>();
    }

    /**
     * Restituisce il nome del reparto.
     * @return il nome del reparto.
     */
    public String getNomeReparto() {
        return nomeReparto;
    }

    /**
     * Imposta il nome del reparto.
     * @param nomeReparto il nuovo nome del reparto.
     */
    public void setNomeReparto(String nomeReparto) {
        this.nomeReparto = nomeReparto;
    }

    /**
     * Restituisce il numero di medici afferenti al reparto.
     * @return il numero di medici.
     */
    public int getNumeroAfferenti() {
        return numeroAfferenti;
    }

    /**
     * Imposta il numero di medici afferenti al reparto.
     * @param numeroAfferenti il nuovo numero di medici.
     */
    public void setNumeroAfferenti(int numeroAfferenti) {
        this.numeroAfferenti = numeroAfferenti;
    }

    /**
     * Restituisce la lista delle stanze del reparto.
     * @return la lista delle stanze.
     */
    public List<Stanza> getStanze() {
        return stanze;
    }

    /**
     * Aggiunge una stanza al reparto.
     * @param stanza la stanza da aggiungere.
     */
    public void addStanza(Stanza stanza) {
        this.stanze.add(stanza);
    }

    @Override
    public String toString() {
        return "Reparto{" +
                "nomeReparto='" + nomeReparto + '\'' +
                ", numeroAfferenti=" + numeroAfferenti +
                '}';
    }

}