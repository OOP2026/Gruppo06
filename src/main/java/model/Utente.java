package model;

import java.util.ArrayList;

public class Utente {
    private String login;
    private String password;
    private String matricola;

    public Utente(String login, String password, String matricola) {
        this.login = login;
        this.password = password;
        this.matricola = matricola;
    }

    public boolean login(String login, String password, String matricola) {
        return ( login.equals(this.login) && password.equals(this.password) && matricola.equals(this.matricola));
    }
}
