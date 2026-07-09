package model;

import java.time.LocalDate;

/**
 * Rappresenta un paziente con i suoi dati anagrafici e clinici.
 * Questa classe è un semplice contenitore di dati (POJO) che segue le best practice di incapsulamento.
 */
public class Paziente {
    private String nome;
    private String cognome;
    private String cf;
    private LocalDate dataNascita;
    private char sesso;
    private String residenza;
    private String diagnosi;

    /**
     * Costruisce un nuovo oggetto Paziente.
     *
     * @param nome         Il nome del paziente.
     * @param cognome      Il cognome del paziente.
     * @param cf           Il codice fiscale del paziente.
     * @param dataNascita  La data di nascita del paziente.
     * @param sesso        Il sesso del paziente.
     * @param residenza    L'indirizzo di residenza.
     * @param diagnosi     La diagnosi del paziente.
     */
    public Paziente(String nome, String cognome, String cf, LocalDate dataNascita, char sesso, String residenza, String diagnosi){
        this.nome = nome;
        this.cognome = cognome;
        this.cf = cf;
        this.dataNascita = dataNascita;
        this.sesso = sesso;
        this.residenza = residenza;
        this.diagnosi = diagnosi;
    }

    /**
     * Restituisce il nome del paziente.
     * @return il nome.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Imposta il nome del paziente.
     * @param nome il nuovo nome.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce il cognome del paziente.
     * @return il cognome.
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Imposta il cognome del paziente.
     * @param cognome il nuovo cognome.
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * Restituisce il codice fiscale del paziente.
     * @return il codice fiscale.
     */
    public String getCf() {
        return cf;
    }

    /**
     * Imposta il codice fiscale del paziente.
     * @param cf il nuovo codice fiscale.
     */
    public void setCf(String cf) {
        this.cf = cf;
    }

    /**
     * Restituisce la data di nascita del paziente.
     * @return la data di nascita.
     */
    public LocalDate getDataNascita() {
        return dataNascita;
    }

    /**
     * Imposta la data di nascita del paziente.
     * @param dataNascita la nuova data di nascita.
     */
    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    /**
     * Restituisce il sesso del paziente.
     * @return il sesso.
     */
    public char getSesso() {
        return sesso;
    }

    /**
     * Imposta il sesso del paziente.
     * @param sesso il nuovo sesso.
     */
    public void setSesso(char sesso) {
        this.sesso = sesso;
    }

    /**
     * Restituisce l'indirizzo di residenza del paziente.
     * @return la residenza.
     */
    public String getResidenza() {
        return residenza;
    }

    /**
     * Imposta l'indirizzo di residenza del paziente.
     * @param residenza la nuova residenza.
     */
    public void setResidenza(String residenza) {
        this.residenza = residenza;
    }


    /**
     * Restituisce la diagnosi del paziente.
     * @return la diagnosi.
     */
    public String getDiagnosi() {
        return diagnosi;
    }

    /**
     * Imposta la diagnosi del paziente.
     * @param diagnosi la nuova diagnosi.
     */
    public void setDiagnosi(String diagnosi) {
        this.diagnosi = diagnosi;
    }

    /**
     * Restituisce una rappresentazione testuale dell'oggetto Paziente.
     * @return una stringa contenente i dettagli del paziente.
     */
    @Override
    public String toString() {
        return "Paziente{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", cf='" + cf + '\'' +
                ", dataNascita=" + dataNascita +
                ", sesso=" + sesso +
                ", residenza='" + residenza + '\'' +
                ", diagnosi='" + diagnosi + '\'' +
                '}';
    }
}
