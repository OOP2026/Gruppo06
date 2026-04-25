package model;

public class Letto {
    public int numero_letto;
    public boolean occupato = false; //LIBERO
    public Stanza stanza;
    public Reparto reparto;
    public Paziente paziente;
    public Ricovero ricovero;

        public Letto(int numero_letto, boolean occupato, Stanza stanza, Reparto reparto, Paziente paziente, Ricovero ricovero){
            this.stanza = stanza;
            this.reparto = reparto;
            this.paziente = paziente;
            this.ricovero = ricovero;
            this.numero_letto = numero_letto;
            this.occupato = occupato;
    }

    public boolean checkLibero(){
       if(occupato = false){
           System.out.println("Il letto è libero.");
           return true;
        } else {
           System.out.println("Il letto è occupato.");
           return false;
        }
    }
}
