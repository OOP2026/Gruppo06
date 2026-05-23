package model;

import java.util.Date;

public class Amministratore extends Utente {
    //Richiamo classi


    public Amministratore(String login, String password, String matricola) {
        super(login, password, matricola);
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

    public void checkDisponibilitaLetto(){
        //return letto.checkLibero();
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
