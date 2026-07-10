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
     */
    public Medico(String matricola, String nome, String cognome, String login) {
        super(matricola, nome, cognome, login);
    }


    /**
     * Imposta la data di iscrizione all'albo.
     * @param iscrizioneAlbo La nuova data di iscrizione.
     */
    public LocalDate setIscrizioneAlbo(LocalDate iscrizioneAlbo) {
        this.iscrizioneAlbo = LocalDate.now(ZoneId.systemDefault());
        return this.iscrizioneAlbo;
    }



    /**
     * Imposta la specializzazione del medico.
     * @param specializzazione La specializzazione da impostare.
     */
    public void setSpecializzazione(String specializzazione) {
        this.specializzazione = specializzazione;
    }


    /**
     * Imposta il reparto di appartenenza del medico.
     * @param repartoDiAppartenenza Il reparto da impostare.
     */
    public void setRepartoDiAppartenenza(String repartoDiAppartenenza) {
        this.repartoDiAppartenenza = repartoDiAppartenenza;
    }

    /**
     * Metodo fittizio per dimostrare l'interazione con la classe Ricovero.
     * Il medico compila o aggiorna la cartella clinica relativa a un ricovero.
     *
     * @param ricovero Il ricovero da esaminare o aggiornare.
     */
    public void compilaCartellaClinica(Ricovero ricovero) {
        if (ricovero != null) {
            System.out.println("Il Dott. " + getCognome() + " del reparto " + repartoDiAppartenenza + 
                    " sta valutando il ricovero iniziato il: " + ricovero.getDataOraInizio() + 
                    " presso il letto: " + (ricovero.getLettoAssegnato() != null ? ricovero.getLettoAssegnato().getReparto() : "Nessuno"));
        }
    }

}
