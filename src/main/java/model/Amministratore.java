package model;

import java.util.Date;

public class Amministratore extends Utente {
    //Richiamo classi
    public Medico medico;
    public Paziente paziente;
    public Reparto reparto;
    public Turno_Lavoro turno_lavoro;
    public Letto letto;
    public Stanza stanza;
    public Assenza assenza;
    public Ricovero ricovero;


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

    public boolean checkDisponibilitaLetto(){
        return letto.checkLibero();
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
