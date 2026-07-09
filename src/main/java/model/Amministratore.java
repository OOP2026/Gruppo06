package model;

import java.util.Date;

/**
 * Rappresenta un utente di tipo Amministratore.
 * Estende la classe {@link Utente} e definisce le operazioni specifiche
 * che un amministratore può compiere nel sistema.
 */
public class Amministratore extends Utente {

    /**
     * Costruisce un nuovo oggetto Amministratore.
     *
     * @param matricola La matricola univoca dell'amministratore.
     * @param nome      Il nome dell'amministratore.
     * @param cognome   Il cognome dell'amministratore.
     * @param login     L'username per l'accesso al sistema.
     * @param ruolo     Il ruolo dell'utente, tipicamente "amministratore".
     */
    public Amministratore(String matricola, String nome, String cognome, String login, String ruolo) {
        super(matricola, nome, cognome, login, ruolo);
    }

    /**
     * Metodo per la gestione dell'anagrafica di un nuovo paziente.
     * (Logica da implementare)
     */
    public void anagraficaPaziente(){
        //
    }

    /**
     * Metodo per assegnare un letto a un paziente.
     * (Logica da implementare)
     */
    public void assegnaLetto(){
        //
    }

    /**
     * Imposta e restituisce la data e l'ora correnti.
     *
     * @return Un oggetto {@link Date} rappresentante il momento attuale.
     */
    public Date setDataOraInizio(){
        return new Date();
    }

    /**
     * Metodo per registrare un nuovo ricovero.
     * (Logica da implementare)
     */
    public void registraRicovero(){
        //
    }

    /**
     * Calcola una prognosi standard.
     *
     * @return Una stringa che descrive la prognosi.
     */
    public String calcolaPrognosi(){
        return "Il paziente ha una prognosi di 10 giorni";
    }

    /**
     * Gestisce le dimissioni di un paziente.
     *
     * @return Una stringa che conferma la dimissione.
     */
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

    /**
     * Metodo per assegnare un turno a un medico.
     * (Logica da implementare)
     */
    public void assegnaTurno(){
        //
    }
}
