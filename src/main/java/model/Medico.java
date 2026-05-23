package model;

import java.util.Date;

public class Medico extends Utente {
    protected String nome;
    protected String cognome;
    //Gli attributi sottostanti verranno utilizati in un secondo momento
    protected Date iscrizioneAlbo;
    protected String specializzazione;
    protected String repartoDiAppartenenza;


    public Medico(String login, String password, String matricola, String nome, String cognome) {
        super(login, password, matricola, nome, cognome);
        this.nome = nome;
        this.cognome = cognome;
    }

}
