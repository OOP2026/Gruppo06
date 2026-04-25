package model;

import java.util.ArrayList;

public class Stanza {
    public int numero_stanza;
    public Letto[][] numero_letti;


    public Stanza(int numero_stanza, int righe, int colonne){
        this.numero_stanza = numero_stanza;
        this.numero_letti = new Letto[righe][colonne];
    }

    public void checkPosti(){
        //
    }
}
