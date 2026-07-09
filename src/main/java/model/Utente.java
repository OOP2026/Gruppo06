package model;

/**
 * Classe base che rappresenta un utente generico del sistema.
 * Contiene le informazioni comuni a tutti i tipi di utenti, come anagrafica e credenziali.
 */
public class Utente {

    protected String login;
    protected String matricola;
    protected String ruolo;
    protected String nome;
    protected String cognome;


    /**
     * Costruisce un nuovo oggetto Utente.
     *
     * @param matricola the matricola
     * @param nome      the nome
     * @param cognome   the cognome
     * @param login     the login
     * @param ruolo     the ruolo
     */
    public Utente(String matricola, String nome, String cognome, String login, String ruolo) {
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.login = login;
        this.ruolo = ruolo;
    }

    /**
     * Restituisce il login (username) dell'utente.
     * @return il login dell'utente.
     */
    public String getLogin(){
        return this.login;
    }
    /**
     * Restituisce la matricola dell'utente.
     * @return la matricola dell'utente.
     */
    public String getMatricola(){
        return this.matricola;
    }
    /**
     * Restituisce il ruolo dell'utente.
     * @return il ruolo dell'utente.
     */
    public String getRuolo(){
        return this.ruolo;
    }

    /**
     * Restituisce il nome dell'utente.
     * @return il nome dell'utente.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce il cognome dell'utente.
     * @return il cognome dell'utente.
     */
    public String getCognome() {
        return cognome;
    }

}
