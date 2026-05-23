package model;

public class Utente {

    protected String login;

    protected String password;

    protected String matricola;


    /**
     * Instantiates a new Utente.
     *
     * @param login     the login
     * @param password  the password
     * @param matricola the matricola
     */
    public Utente(String login, String password, String matricola) {
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
