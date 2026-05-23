package model;

import java.util.Date;

public class Medico extends Utente {
    protected String nome;
    protected String cognome;
    //Gli attributi sottostanti verranno utilizati in un secondo momento
    protected Date iscrizioneAlbo;
    protected String specializzazione;
    protected String repartoDiAppartenenza;


    public Medico(String login, String password, String matricola, String nome, String cognome) {
        super(login, password, matricola);
        this.nome = nome;
        this.cognome = cognome;
    }

    public Date setIscrizionAlbo() {
        this.iscrizioneAlbo = new Date(); // Imposta la data di iscrizione all'albo al momento della creazione del medico
        return this.iscrizioneAlbo;
    }

    public Date getIscrizioneAlbo(){
        return iscrizioneAlbo;
    }

    public void setSpecializzazione(String specializzazione) {
        this.specializzazione = specializzazione;
    }
    public String getSpecializzazione() {
        return specializzazione;
    }

    public void setRepartoDiAppartenenza(String repartoDiAppartenenza) {
        this.repartoDiAppartenenza = repartoDiAppartenenza;
    }
    public String getRepartoDiAppartenenza() {
        return repartoDiAppartenenza;
    }

}
