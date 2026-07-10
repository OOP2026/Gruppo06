package dao;

import java.util.ArrayList;
import java.util.List;

/**
 * L'interfaccia Letto DAO per la gestione delle operazioni di persistenza
 * e l'interazione con il database per l'entità Letto.
 */
public interface LettoDAO {

    /**
     * Aggiunge un nuovo letto in un reparto specifico.
     *
     * @param idLetto l'identificativo del letto
     * @param reparto il reparto in cui aggiungere il letto
     * @return true se l'operazione ha successo, false altrimenti
     */
    boolean aggiungiLetto(String idLetto, String reparto);

    /**
     * Ottiene le informazioni di un letto specifico tramite il suo ID e il reparto di appartenenza.
     *
     * @param idLetto l'identificativo del letto da cercare
     * @param reparto il reparto in cui cercare il letto
     * @return una lista contenente i dettagli del letto trovato, oppure una lista vuota se non trovato
     */
    ArrayList<String> getLettoById(String idLetto, String reparto);

    /**
     * Ottiene tutti i letti registrati nel sistema.
     *
     * @return una lista di liste, dove ogni sottolista rappresenta i dettagli di un singolo letto
     */
    ArrayList<ArrayList<String>> getAllLetti();

    /**
     * Aggiorna lo stato di occupazione di un letto.
     *
     * @param idLetto  l'identificativo del letto da aggiornare
     * @param reparto  il reparto in cui si trova il letto
     * @param occupato lo stato del letto (true se occupato, false se libero)
     * @return true se l'aggiornamento ha successo, false altrimenti
     */
    boolean aggiornaStatoLetto(String idLetto, String reparto, boolean occupato);

    /**
     * Ottiene la lista di tutti i reparti registrati o disponibili nel sistema.
     *
     * @return una lista contenente i nomi di tutti i reparti
     */
    List<String> getAllReparti();
}