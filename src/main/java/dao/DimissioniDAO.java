package dao;

import java.util.ArrayList;

/**
 * Interfaccia per la gestione delle operazioni sulle dimissioni nel database.
 */
public interface DimissioniDAO {

    /**
     * Crea una nuova dimissione aggiornando un ricovero esistente con i dettagli della dimissione.
     *
     * @param idRicovero L'ID del ricovero da chiudere.
     * @param dataFine La data e ora di fine ricovero.
     * @param prognosi La prognosi post-dimissione.
     * @param esito L'esito della dimissione.
     * @return true se l'operazione ha successo, false altrimenti.
     */
    boolean creaDimissione(String idRicovero, String dataFine, String prognosi, String esito);

    /**
     * Recupera tutte le dimissioni registrate.
     * @return Una lista di liste di stringhe, dove ogni lista interna rappresenta una dimissione.
     */
    ArrayList<ArrayList<String>> getAllDimissioni();

    ArrayList<String> getUltimoRicoveroChiuso(String cfPaziente);
}