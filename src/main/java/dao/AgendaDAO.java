package dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Interfaccia DAO (Data Access Object) per la gestione delle operazioni
 * di persistenza e interazione con il database relative all'Agenda e agli eventi.
 */
public interface AgendaDAO {
    
    /**
     * Ottiene tutti gli eventi in agenda associati a una determinata matricola.
     *
     * @param matricola la matricola dell'utente (medico o amministratore)
     * @return una lista di liste contenente i dettagli di ciascun evento
     */
    List<ArrayList<String>> getEventiByMatricola(String matricola);

    /**
     * Aggiunge un nuovo evento all'agenda.
     *
     * @param titolo il titolo dell'evento
     * @param matricola la matricola dell'utente a cui è assegnato l'evento
     * @param descrizione la descrizione dettagliata dell'evento
     * @param dataOraInizio la data e l'ora di inizio dell'evento
     * @param dataOraFine la data e l'ora di fine dell'evento
     * @return true se l'inserimento ha successo, false altrimenti
     */
    boolean addEvento(String titolo, String matricola, String descrizione, Timestamp dataOraInizio, Timestamp dataOraFine);

    /**
     * Aggiorna i dati di un evento esistente nell'agenda.
     *
     * @param idEvento l'identificativo univoco dell'evento da modificare
     * @param titolo il nuovo titolo dell'evento
     * @param descrizione la nuova descrizione dell'evento
     * @param dataOraInizio la nuova data e ora di inizio dell'evento
     * @param dataOraFine la nuova data e ora di fine dell'evento
     * @return true se l'aggiornamento ha successo, false altrimenti
     */
    boolean updateEvento(int idEvento, String titolo, String descrizione, Timestamp dataOraInizio, Timestamp dataOraFine);

    /**
     * Elimina un evento specifico dall'agenda.
     *
     * @param idEvento l'identificativo univoco dell'evento da eliminare
     * @return true se l'eliminazione ha successo, false altrimenti
     */
    boolean deleteEvento(int idEvento);

    /**
     * Crea un'agenda di base collegata al nuovo medico registrato.
     *
     * @param matricolaMedico la matricola del medico
     * @return true se la creazione ha successo, false altrimenti
     */
    boolean creaAgendaPerMedico(String matricolaMedico);

    /**
     * Crea un'agenda di base collegata al nuovo amministratore registrato.
     *
     * @param matricolaAmministratore la matricola dell'amministratore
     * @return true se la creazione ha successo, false altrimenti
     */
    boolean creaAgendaPerAmministratore(String matricolaAmministratore);
}