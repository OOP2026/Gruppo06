package dao;

import java.util.ArrayList;

/**
 * Interfaccia per le operazioni DAO relative ai turni di lavoro.
 */
public interface TurnoLavoroDAO {
    /**
     * Aggiunge un nuovo turno di lavoro al sistema.
     *
     * @param matricola   la matricola del medico
     * @param data        la data del turno
     * @param inizioTurno l'orario di inizio del turno
     * @param fineTurno   l'orario di fine del turno
     * @param id_agenda   l'ID dell'agenda associata
     * @return true se l'aggiunta ha successo, false altrimenti
     */
    boolean aggiungiTurno(String matricola, String data, String inizioTurno, String fineTurno, String id_agenda);

    /**
     * Restituisce i dettagli di un turno di lavoro specifico.
     *
     * @param matricola   la matricola del medico
     * @param data        la data del turno
     * @param inizioTurno l'orario di inizio del turno
     * @return una lista contenente i dettagli del turno
     */
    ArrayList<String> getTurno(String matricola, String data, String inizioTurno);

    /**
     * Restituisce tutti i turni assegnati a un determinato medico.
     *
     * @param matricola la matricola del medico
     * @return una lista dei turni assegnati al medico
     */
    ArrayList<ArrayList<String>> getTurniByMedico(String matricola);

    /**
     * Aggiorna gli orari di un turno esistente.
     *
     * @param matricola     la matricola del medico
     * @param data          la data del turno
     * @param vecchioInizio il precedente orario di inizio
     * @param nuovoInizio   il nuovo orario di inizio
     * @param nuovaFine     il nuovo orario di fine
     * @return true se l'aggiornamento ha successo, false altrimenti
     */
    boolean aggiornaTurno(String matricola, String data, String vecchioInizio, String nuovoInizio, String nuovaFine);

    /**
     * Elimina un turno di lavoro dal sistema.
     *
     * @param matricola   la matricola del medico
     * @param data        la data del turno
     * @param inizioTurno l'orario di inizio del turno
     * @return true se l'eliminazione ha successo, false altrimenti
     */
    boolean eliminaTurno(String matricola, String data, String inizioTurno);

    /**
     * Aggiorna il medico assegnato a un determinato turno (sostituzione).
     *
     * @param idTurno        l'ID del turno
     * @param nuovaMatricola la matricola del nuovo medico assegnato
     * @return true se l'aggiornamento ha successo, false altrimenti
     */
    boolean aggiornaMedicoTurno(int idTurno, String nuovaMatricola);
}