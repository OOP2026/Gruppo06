package model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.Scanner;

public class Amministratore extends Utente {
    //Richiamo classi
    protected String nome;
    protected String cognome;
    protected String pin;

    public Amministratore(String nome, String cognome, String login, String password, String matricola, String pin) {
        super(login, password, matricola);
        this.nome = nome;
        this.cognome = cognome;
        this.pin = pin;
    }


    public String getPin(){
        return this.pin;
    }

    public String getNome(){
        return this.nome;
    }

    public String getCognome(){
        return this.cognome;
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
        System.out.println();
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
