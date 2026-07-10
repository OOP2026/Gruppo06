package dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Interfaccia per le operazioni DAO dell'entità Ricovero.
 */
public interface RicoveroDAO {
    /**
     * Aggiunge un nuovo ricovero nel sistema.
     *
     * @param cfPaziente il codice fiscale del paziente
     * @param idLetto    l'ID del letto
     * @param reparto    il reparto
     * @param dataInizio la data di inizio del ricovero
     * @param motivo     il motivo del ricovero
     * @return true se l'operazione ha successo, false altrimenti
     */
    boolean aggiungiRicovero(String cfPaziente, String idLetto, String reparto, String dataInizio, String motivo);

    /**
     * Aggiorna un ricovero registrando la dimissione.
     *
     * @param idRicovero l'ID del ricovero
     * @param dataFine   la data di fine ricovero
     * @param prognosi   i giorni di prognosi
     * @param esito      l'esito della dimissione
     * @return true se l'operazione ha successo, false altrimenti
     */
    boolean aggiornaRicoveroDimissione(String idRicovero, String dataFine, String prognosi, String esito);

    /**
     * Restituisce il ricovero attualmente attivo per un paziente.
     *
     * @param cfPaziente il codice fiscale del paziente
     * @return i dettagli del ricovero attivo
     */
    ArrayList<String> getRicoveroAttivo(String cfPaziente);

    /**
     * Restituisce lo storico dei ricoveri per un paziente.
     *
     * @param cfPaziente il codice fiscale del paziente
     * @return una lista contenente lo storico dei ricoveri
     */
    ArrayList<ArrayList<String>> getStoricoRicoveri(String cfPaziente);

    /**
     * Restituisce tutte le dimissioni registrate.
     *
     * @return una lista contenente tutte le dimissioni
     */
    ArrayList<ArrayList<String>> getAllDimissioni();

    /**
     * Restituisce l'ultimo ricovero chiuso (dimissione) per un paziente.
     *
     * @param cfPaziente il codice fiscale del paziente
     * @return i dettagli dell'ultimo ricovero chiuso
     */
    ArrayList<String> getUltimoRicoveroChiuso(String cfPaziente);

    /**
     * Restituisce tutti i ricoveri attualmente attivi.
     *
     * @return una lista di tutti i ricoveri attivi
     */
    List<ArrayList<String>> getAllRicoveriAttivi();

    /**
     * Verifica se un determinato letto è attualmente occupato.
     *
     * @param idLetto l'ID del letto
     * @param reparto il reparto
     * @return true se il letto è occupato, false altrimenti
     */
    boolean isLettoAttualmenteOccupato(String idLetto, String reparto);

    /**
     * Recupera lo storico dei ricoveri per un letto e reparto specifici.
     *
     * @param idLetto l'ID del letto
     * @param reparto il reparto
     * @return lo storico dei ricoveri per il letto specificato
     */
    List<ArrayList<String>> getStoricoRicoveriByLetto(String idLetto, String reparto);

    /**
     * Aggiorna il letto assegnato a un ricovero attivo (trasferimento).
     *
     * @param idRicovero   l'ID del ricovero
     * @param nuovoLetto   il nuovo ID del letto
     * @param nuovoReparto il nuovo reparto
     * @return true se l'operazione ha successo, false altrimenti
     */
    boolean aggiornaLettoRicovero(String idRicovero, String nuovoLetto, String nuovoReparto);
}