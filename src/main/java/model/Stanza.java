package model;

/**
 * Rappresenta una stanza di degenza in un ospedale.
 * Una stanza contiene un certo numero di letti disposti in una griglia.
 */
public class Stanza {
    public int numero_stanza;
    public int numero_letti;

    /**
     * Costruisce un nuovo oggetto Stanza.
     *
     * @param numero_stanza Il numero della stanza.
     * @param numero_letti  Il numero di letti nella stanza.
     */
    public Stanza(int numero_stanza, int numero_letti){
        this.numero_stanza = numero_stanza;
        this.numero_letti =  numero_letti;
    }

}
