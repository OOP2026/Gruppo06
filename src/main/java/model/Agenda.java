package model;

import java.sql.Timestamp;

/**
 * Rappresenta un evento nell'agenda di un medico.
 * Contiene informazioni come titolo, descrizione e intervallo di tempo.
 */
public class Agenda {
    private int idEvento;
    private String matricolaMedico;
    private String titolo;
    private String descrizione;
    private Timestamp dataOraInizio;
    private Timestamp dataOraFine;

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
    public Agenda(int idEvento, String matricolaMedico, String titolo, String descrizione, Timestamp dataOraInizio, Timestamp dataOraFine) {
        this.idEvento = idEvento;
        this.matricolaMedico = matricolaMedico;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.dataOraInizio = dataOraInizio;
        this.dataOraFine = dataOraFine;
    }
}