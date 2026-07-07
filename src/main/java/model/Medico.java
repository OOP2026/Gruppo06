package model;

import java.time.LocalDate;
import java.time.ZoneId;

public class Medico extends Utente {
    protected String nome;
    protected String cognome;
    //Gli attributi sottostanti verranno utilizati in un secondo momento
    protected LocalDate iscrizioneAlbo;
    protected String specializzazione;
    protected String repartoDiAppartenenza;


    public Medico(String matricola, String nome, String cognome, String login, String ruolo) {
        super(matricola, login, ruolo);
        this.nome = nome;
        this.cognome = cognome;
    }

    public String getNome(){
        return this.nome;
    }

    public String getCognome(){
        return this.cognome;
    }

    public LocalDate setIscrizionAlbo() {
        this.iscrizioneAlbo = LocalDate.now(ZoneId.systemDefault()); // Imposta la data di iscrizione all'albo al momento della creazione del medico
        return this.iscrizioneAlbo;
    }

    public LocalDate getIscrizioneAlbo(){
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
