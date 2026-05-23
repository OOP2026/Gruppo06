package model;

import java.util.Date;

public class Amministratore extends Utente {
    //Richiamo classi
    protected String nome;
    protected String cognome;
    protected String pin;

    public Amministratore(String login, String password, String matricola, String nome, String cognome) {
        super(login, password, matricola);
        this.nome = nome;
        this.cognome = cognome;
        this.pin = pin;
    }


    public String getMatricola() {
        return this.matricola;
    }

    public void anagraficaPaziente(){
        //
    }

    public void assegnaLetto(){
        //
    }

    public Date setDataOraInizio(){
        return new Date();
    }


    public void registraRicovero(){
        System.out.println("Registro Ricovero...");
    }

    public String calcolaPrognosi(){
        return "Il paziente ha una prognosi di 10 giorni";
    }

    public String dimissioni(){
        return "Il paziente è stato dimesso";
    }


    public void checkDisponibilitaLetto(){ //Metodo non piu in uso
    }

    public void ricercaDimissioni(){
        //
    }

    public void setAssenza(){
        //
    }

    public void checkPeriodoAssenza(){
        //
    }

    public void assegnaTurno(){
        //
    }
}
