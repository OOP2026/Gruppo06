package model;

import java.util.Date;

/**
 * Rappresenta un paziente con i suoi dati anagrafici e clinici.
 * Questa classe è un semplice contenitore di dati (POJO).
 */
public class Paziente {
    public String nome;
    public String cognome;
    public String CF;
    public Date data_nascita;
    public char sesso;
    public String residenza;
    public int eta;
    public String diagnosi;

    /**
     * Costruisce un nuovo oggetto Paziente.
     *
     * @param nome         Il nome del paziente.
     * @param cognome      Il cognome del paziente.
     * @param CF           Il codice fiscale del paziente.
     * @param data_nascita La data di nascita del paziente.
     * @param sesso        Il sesso del paziente.
     * @param residenza    L'indirizzo di residenza.
     * @param eta          L'età del paziente.
     * @param diagnosi     La diagnosi del paziente.
     */
    public Paziente(String nome, String cognome, String CF, Date data_nascita, char sesso, String residenza, int eta, String diagnosi){
        this.nome = nome;
        this.cognome = cognome;
        this.CF = CF;
        this.data_nascita = data_nascita;
        this.sesso = sesso;
        this.residenza = residenza;
        this.eta = eta;
        this.diagnosi = diagnosi;
    }
}
