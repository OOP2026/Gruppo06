package model;

import java.util.ArrayList;

/**
 * Rappresenta una stanza di degenza in un ospedale.
 * Una stanza contiene un certo numero di letti disposti in una griglia.
 */
public class Stanza {
    public int numero_stanza;
    public Letto[][] numero_letti;

    /**
     * Costruisce un nuovo oggetto Stanza.
     *
     * @param numero_stanza Il numero della stanza.
     * @param righe         Il numero di righe di letti nella stanza.
     * @param colonne       Il numero di colonne di letti nella stanza.
     */
    public Stanza(int numero_stanza, int righe, int colonne){
        this.numero_stanza = numero_stanza;
        this.numero_letti = new Letto[righe][colonne];
    }

    public void checkPosti(){
        //
    }
}
