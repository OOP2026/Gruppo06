package model;

public class Utente {

    protected String login;
    protected String matricola;
    protected String ruolo;
    protected String nome;
    protected String cognome;


    /**
     * Instantiates a new Utente.
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


    public String getLogin(){
        return this.login;
    }
    public String getMatricola(){
        return this.matricola;
    }
    public String getRuolo(){
        return this.ruolo;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }


}
