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


    public String getPassword() {
        return this.password;
    }
    public String getLogin(){
        return this.login;
    }
    public String getMatricola(){
        return this.matricola;
    }


}
