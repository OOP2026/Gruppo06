package model;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Rappresenta un utente di tipo Medico.
 * Estende la classe {@link Utente} aggiungendo informazioni specifiche
 * come la data di iscrizione all'albo, la specializzazione e il reparto.
 */
public class Medico extends Utente {

    protected LocalDate iscrizioneAlbo;
    protected String specializzazione;
    protected String repartoDiAppartenenza;

    /**
     * Costruisce un nuovo oggetto Medico.
     *
     * @param matricola La matricola univoca del medico.
     * @param nome      Il nome del medico.
     * @param cognome   Il cognome del medico.
     * @param login     L'username per l'accesso al sistema.
     * @param ruolo     Il ruolo dell'utente, tipicamente "medico".
     */
    public Medico(String matricola, String nome, String cognome, String login, String ruolo) {
        super(matricola, nome, cognome, login, ruolo);
    }

    /**
     * Imposta la data di iscrizione all'albo alla data corrente e la restituisce.
     * @return La data di iscrizione impostata.
     */
    public LocalDate setIscrizionAlbo() {
        this.iscrizioneAlbo = LocalDate.now(ZoneId.systemDefault()); // Imposta la data di iscrizione all'albo al momento della creazione del medico
        return this.iscrizioneAlbo;
    }

    /**
     * Restituisce la data di iscrizione all'albo.
     * @return La data di iscrizione.
     */
    public LocalDate getIscrizioneAlbo(){
        return iscrizioneAlbo;
    }

    /**
     * Imposta la specializzazione del medico.
     * @param specializzazione La specializzazione da impostare.
     */
    public void setSpecializzazione(String specializzazione) {
        this.specializzazione = specializzazione;
    }
    /**
     * Restituisce la specializzazione del medico.
     * @return La specializzazione.
     */
    public String getSpecializzazione() {
        return specializzazione;
    }

    /**
     * Imposta il reparto di appartenenza del medico.
     * @param repartoDiAppartenenza Il reparto da impostare.
     */
    public void setRepartoDiAppartenenza(String repartoDiAppartenenza) {
        this.repartoDiAppartenenza = repartoDiAppartenenza;
    }
    /**
     * Restituisce il reparto di appartenenza del medico.
     * @return Il reparto di appartenenza.
     */
    public String getRepartoDiAppartenenza() {
        return repartoDiAppartenenza;
    }

}
