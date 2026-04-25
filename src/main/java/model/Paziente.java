package model;

import java.util.Date;

public class Paziente {
    public String nome;
    public String cognome;
    public String CF;
    public Date data_nascita;
    public char sesso;
    public String residenza;
    public int eta;
    public String diagnosi;

    public Paziente(String nome, String cognome, String CF, Date data_nascita, char sesso, String residenza, int eta, String diagnosi){
        this.nome = nome;
        this.cognome = cognome;
        this.CF = CF;
        this.data_nascita = data_nascita;
        this.sesso = sesso;
        this.residenza = residenza;
        this.eta = eta;
        this.diagnosi = diagnosi;
    }
}
