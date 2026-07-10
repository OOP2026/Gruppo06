package dao;

import java.util.ArrayList;


public interface PrestazioneDAO {

    /**
     * Aggiunge una nuova prestazione al sistema.
     *
     * @param tipologiaPrestazione la tipologia della prestazione
     * @param esitoPrestazione     l'esito della prestazione
     * @param idTurno              l'ID del turno associato
     * @param cfPaziente           il codice fiscale del paziente
     * @param matricolaMedico      la matricola del medico
     * @param idAgenda             l'ID dell'agenda associata
     * @return true se l'inserimento ha avuto successo, false altrimenti
     */
    boolean aggiungiPrestazione(String tipologiaPrestazione, String esitoPrestazione, String idTurno, String cfPaziente, String matricolaMedico, String idAgenda);

    /**
     * Restituisce tutte le prestazioni.
     *
     * @return una lista contenente tutte le prestazioni
     */
    ArrayList<ArrayList<String>> getAllPrestazioni();

    /**
     * Restituisce le prestazioni associate a un determinato medico.
     *
     * @param matricola la matricola del medico
     * @return una lista delle prestazioni del medico
     */
    ArrayList<ArrayList<String>> getPrestazioniByMedico(String matricola);

    /**
     * Restituisce una prestazione in base al suo ID.
     *
     * @param idPrestazione l'ID della prestazione
     * @return i dettagli della prestazione cercata
     */
    ArrayList<String> getPrestazioneById(String idPrestazione);

    /**
     * Aggiorna i dati di una prestazione esistente.
     *
     * @param idPrestazione l'ID della prestazione da aggiornare
     * @param tipologia     la nuova tipologia
     * @param esito         il nuovo esito
     * @param referto       il nuovo referto
     * @return true se l'aggiornamento ha avuto successo, false altrimenti
     */
    boolean updatePrestazione(int idPrestazione, String tipologia, String esito, String referto);

    /**
     * Elimina una prestazione dal sistema.
     *
     * @param idPrestazione l'ID della prestazione da eliminare
     * @return true se l'eliminazione ha avuto successo, false altrimenti
     */
    boolean eliminaPrestazione(int idPrestazione);
}