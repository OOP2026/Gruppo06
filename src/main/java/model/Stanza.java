package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una stanza de degenza in un ospedale.
 * Una stanza contiene un certo numero di letti disposti in una griglia.
 */
public class Stanza {
    public int numeroStanza;
    public int numeroLetti;
    private List<Letto> quantitaLetti;
    /**
     * Costruisce un nuovo oggetto Stanza.
     *
     * @param numeroStanza Il numero della stanza.
     * @param numeroLetti  Il numero di letti nella stanza.
     */
    public Stanza(int numeroStanza, int numeroLetti){
        this.numeroStanza = numeroStanza;
        this.numeroLetti =  numeroLetti;
        this.quantitaLetti = new ArrayList<>();
    }

    /**
     * Restituisce la lista dei letti nella stanza.
     * @return la lista dei letti.
     */
    public List<Letto> getQuantitaLetti() {
        return quantitaLetti;
    }

    /**
     * Aggiunge un letto alla stanza.
     * @param letto il letto da aggiungere.
     */
    public void addLetto(Letto letto) {
        if (this.quantitaLetti.size() < this.numeroLetti) {
            this.quantitaLetti.add(letto);
        }
    }

}