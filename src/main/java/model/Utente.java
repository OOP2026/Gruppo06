package model;

public class Utente {

    protected String login;

    protected String password;

    protected String matricola;

    protected String nome;

    protected String cognome;

    /**
     * Instantiates a new Utente.
     *
     * @param login     the login
     * @param password  the password
     * @param matricola the matricola
     * @param nome
     * @param cognome
     */
    public Utente(String login, String password, String matricola, String nome, String cognome) {
        this.login = login;
        this.password = password;
        this.matricola = matricola;
    }

    /**
     * Login boolean.
     *
     * @param login
     * @param matricola
     * @return boolean
     */
    public boolean login(String login, String password, String matricola) {
        return ( login.equals(this.login) && password.equals(this.password) && matricola.equals(this.matricola));
    }
}
