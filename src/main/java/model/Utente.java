package model;

/**
 * Classe base che rappresenta un utente generico del sistema.
 * Contiene le informazioni comuni a tutti i tipi di utenti, come anagrafica e credenziali.
 */
public class Utente {

    protected String login;
    protected String matricola;
    protected String nome;
    protected String cognome;


    /**
     * Costruisce un nuovo oggetto Utente.
     *
     * @param matricola la matricola
     * @param nome      il nome
     * @param cognome   il cognome
     * @param login     la login
     */
    public Utente(String matricola, String nome, String cognome, String login) {
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.login = login;
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

    /**
     * Imposta il login dell'utente.
     * @param login il nuovo login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Imposta la matricola dell'utente.
     * @param matricola la nuova matricola
     */
    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }



    /**
     * Imposta il nome dell'utente.
     * @param nome il nuovo nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Imposta il cognome dell'utente.
     * @param cognome il nuovo cognome
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

}
