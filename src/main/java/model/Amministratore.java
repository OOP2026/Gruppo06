package model;


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
     */
    public Amministratore(String matricola, String nome, String cognome, String login) {
        super(matricola, nome, cognome, login);
    }


}
