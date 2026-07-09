package model;

import java.time.LocalDateTime;

/**
 * Rappresenta un evento nell'agenda di un medico.
 * Contiene informazioni come titolo, descrizione e intervallo di tempo.
 */
public class Agenda {
    private int idEvento;
    private String matricolaMedico;
    private String titolo;
    private String descrizione;
    private LocalDateTime dataOraInizio;
    private LocalDateTime dataOraFine;

    /**
     * Costruisce un nuovo oggetto Agenda (evento).
     *
     * @param idEvento        L'ID univoco dell'evento.
     * @param matricolaMedico La matricola del medico a cui l'evento è associato.
     * @param titolo          Il titolo dell'evento.
     * @param descrizione     Una descrizione dettagliata dell'evento.
     * @param dataOraInizio   Il timestamp di inizio dell'evento.
     * @param dataOraFine     Il timestamp di fine dell'evento.
     */
    public Agenda(int idEvento, String matricolaMedico, String titolo, String descrizione, LocalDateTime dataOraInizio, LocalDateTime dataOraFine) {
        this.idEvento = idEvento;
        this.matricolaMedico = matricolaMedico;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.dataOraInizio = dataOraInizio;
        this.dataOraFine = dataOraFine;
    }

    /**
     * Restituisce l'ID dell'evento.
     * @return L'ID dell'evento.
     */
    public int getIdEvento() {
        return idEvento;
    }

    /**
     * Restituisce la matricola del medico associato all'evento.
     * @return La matricola del medico.
     */
    public String getMatricolaMedico() {
        return matricolaMedico;
    }

    /**
     * Restituisce il titolo dell'evento.
     * @return Il titolo dell'evento.
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * Restituisce la descrizione dell'evento.
     * @return La descrizione dell'evento.
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Restituisce il timestamp di inizio dell'evento.
     * @return Il timestamp di inizio.
     */
    public LocalDateTime getDataOraInizio() {
        return dataOraInizio;
    }

    /**
     * Restituisce il timestamp di fine dell'evento.
     * @return Il timestamp di fine.
     */
    public LocalDateTime getDataOraFine() {
        return dataOraFine;
    }

    // Setters

    /**
     * Imposta un nuovo titolo per l'evento.
     * @param titolo Il nuovo titolo.
     */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    /**
     * Imposta una nuova descrizione per l'evento.
     * @param descrizione La nuova descrizione.
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * Imposta un nuovo timestamp di inizio per l'evento.
     * @param dataOraInizio Il nuovo timestamp di inizio.
     */
    public void setDataOraInizio(LocalDateTime dataOraInizio) {
        this.dataOraInizio = dataOraInizio;
    }

    /**
     * Imposta un nuovo timestamp di fine per l'evento.
     * @param dataOraFine Il nuovo timestamp di fine.
     */
    public void setDataOraFine(LocalDateTime dataOraFine) {
        this.dataOraFine = dataOraFine;
    }
}