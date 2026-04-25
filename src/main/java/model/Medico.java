package model;

import java.util.Date;

public class Medico extends Utente {
    public String nome;
    public String cognome;
    public String agenda;
    public Date iscrizione_albo;
    public String specializzazione;
    public String reparto_di_appartenenza;


    public Medico(String login, String password, String matricola, String nome, String cognome, String agenda, Date iscrizione_albo,String specializzazione, String reparto_di_appartenenza) {
        super(login, password, matricola);
        this.nome = nome;
        this.cognome = cognome;
        this.agenda = agenda;
        this.iscrizione_albo = iscrizione_albo;
        this.specializzazione = specializzazione;
        this.reparto_di_appartenenza = reparto_di_appartenenza;

    }

}
