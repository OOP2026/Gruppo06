package model;

/**
 * Rappresenta un reparto ospedaliero.
 * Contiene informazioni sul nome, il numero di medici afferenti e le entità associate.
 */
public class Reparto {
    public String nome_reparto;
    public int numero_afferenti;
    public Stanza stanza;
    public Letto letto;
    public Paziente paziente;
    public Ricovero ricovero;

    /**
     * Costruisce un nuovo oggetto Reparto.
     * @param nome_reparto Il nome del reparto.
     * @param numero_afferenti Il numero di medici afferenti.
     * @param stanza La stanza associata.
     * @param letto Il letto associato.
     * @param paziente Il paziente associato.
     * @param ricovero Il ricovero associato.
     */
    public Reparto(String nome_reparto, int numero_afferenti, Stanza stanza, Letto letto, Paziente paziente, Ricovero ricovero){
        this.nome_reparto = nome_reparto;
        this.numero_afferenti = numero_afferenti;
        this.stanza = stanza;
        this.letto = letto;
        this.paziente = paziente;
        this.ricovero = ricovero;
    }

     public void getNumPazienti(){

     }
}
