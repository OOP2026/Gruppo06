package model;

public class Utente {
    protected String login;
    protected String password;
    protected String matricola;

    public Utente(String login, String password, String matricola) {
        this.login = login;
        this.password = password;
        this.matricola = matricola;
    }

    public boolean login(String login, String password, String matricola) {
        return ( login.equals(this.login) && password.equals(this.password) && matricola.equals(this.matricola));
    }
}
