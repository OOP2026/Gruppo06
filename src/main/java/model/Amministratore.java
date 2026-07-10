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

    /**
     * Metodo fittizio per dimostrare l'interazione con la classe Ricovero.
     * L'amministratore revisiona i dati amministrativi di un ricovero.
     *
     * @param ricovero Il ricovero da revisionare.
     */
    public void revisionaPraticaRicovero(Ricovero ricovero) {
        if (ricovero != null) {
            System.out.println("L'amministratore " + getNome() + " " + getCognome() + 
                    " sta revisionando la pratica di ricovero del paziente con CF: " + 
                    (ricovero.getPaziente() != null ? ricovero.getPaziente().getCf() : "Sconosciuto"));
        }
    }

}
