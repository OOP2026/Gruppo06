package dao;

import java.util.ArrayList;

/**
 * Interfaccia DAO (Data Access Object) per la gestione delle operazioni 
 * di persistenza relative alle assenze dei medici.
 */
public interface AssenzaDAO {

    /**
     * Aggiunge una nuova assenza per un determinato medico nel sistema.
     *
     * @param matricola   la matricola del medico
     * @param dataInizio  la data di inizio dell'assenza
     * @param dataFine    la data di fine dell'assenza
     * @param motivazione la motivazione dell'assenza
     * @return true se l'inserimento ha successo, false altrimenti
     */
    boolean aggiungiAssenza(String matricola, String dataInizio, String dataFine, String motivazione);
    
    /**
     * Ottiene i dettagli di una specifica assenza di un medico.
     *
     * @param matricola  la matricola del medico
     * @param dataInizio la data di inizio dell'assenza da cercare
     * @return una lista contenente i dettagli dell'assenza, oppure una lista vuota se non trovata
     */
    ArrayList<String> getAssenza(String matricola, String dataInizio);

    /**
     * Ottiene lo storico di tutte le assenze registrate per un determinato medico.
     *
     * @param matricola la matricola del medico di cui recuperare le assenze
     * @return una lista di liste, dove ogni sottolista rappresenta i dettagli di una singola assenza
     */
    ArrayList<ArrayList<String>> getAssenzeByMedico(String matricola);
    
    /**
     * Aggiorna le informazioni (data di fine e motivazione) di un'assenza già esistente.
     *
     * @param matricola   la matricola del medico
     * @param dataInizio  la data di inizio dell'assenza da identificare e aggiornare
     * @param dataFine    la nuova data di fine dell'assenza
     * @param motivazione la nuova motivazione dell'assenza
     * @return true se l'aggiornamento ha successo, false altrimenti
     */
    boolean aggiornaAssenza(String matricola, String dataInizio, String dataFine, String motivazione);

    /**
     * Elimina un'assenza specifica dal sistema.
     *
     * @param matricola  la matricola del medico
     * @param dataInizio la data di inizio dell'assenza da eliminare
     * @return true se l'eliminazione ha successo, false altrimenti
     */
    boolean eliminaAssenza(String matricola, String dataInizio);
}