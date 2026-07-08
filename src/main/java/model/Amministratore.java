package model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.Scanner;

public class Amministratore extends Utente {
    //Richiamo classi

    public Amministratore(String matricola, String nome, String cognome, String login, String ruolo) {
        super(matricola, nome, cognome, login, ruolo);
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
