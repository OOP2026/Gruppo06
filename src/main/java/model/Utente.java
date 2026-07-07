package model;

public class Utente {

    protected String login;
    protected String matricola;
    protected String ruolo;


    /**
     * Instantiates a new Utente.
     *
     * @param matricola the matricola
     * @param login     the login
     * @param ruolo     the ruolo
     */
    public Utente(String matricola, String login, String ruolo) {
        this.matricola = matricola;
        this.login = login;
        this.ruolo = ruolo;
    }


    public String getLogin(){
        return this.login;
    }
    public String getMatricola(){
        return this.matricola;
    }
    public String getRuolo(){
        return this.ruolo;
    }


}
