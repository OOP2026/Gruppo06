package model;

public class Reparto {
    public String nome_reparto;
    public int numero_afferenti;
    public Stanza stanza;
    public Letto letto;
    public Paziente paziente;
    public Ricovero ricovero;

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
