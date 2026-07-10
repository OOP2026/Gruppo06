package dao;

import java.util.ArrayList;

/**
 * Interfaccia per le operazioni DAO relative ai pazienti.
 */
public interface PazienteDAO {
    /**
     * Aggiunge un nuovo paziente nel sistema.
     *
     * @param cf          il codice fiscale del paziente
     * @param nome        il nome del paziente
     * @param cognome     il cognome del paziente
     * @param dataNascita la data di nascita del paziente
     * @param sesso       il sesso del paziente
     * @param residenza   la residenza del paziente
     * @param diagnosi    la diagnosi iniziale del paziente
     * @return true se l'aggiunta ha avuto successo, false altrimenti
     */
    boolean aggiungiPaziente(String cf, String nome, String cognome, String dataNascita, String sesso, String residenza, String diagnosi);

    /**
     * Recupera le informazioni di un paziente tramite il suo codice fiscale.
     *
     * @param cf il codice fiscale del paziente da cercare
     * @return una lista contenente i dettagli del paziente trovato
     */
    ArrayList<String> getPazienteByCf(String cf);

    /**
     * Restituisce la lista di tutti i pazienti registrati.
     *
     * @return una lista contenente tutti i pazienti
     */
    ArrayList<ArrayList<String>> getAllPazienti();

    /**
     * Aggiorna i dati di un paziente esistente nel sistema.
     *
     * @param cf          il codice fiscale del paziente da aggiornare
     * @param nome        il nuovo nome
     * @param cognome     il nuovo cognome
     * @param dataNascita la nuova data di nascita
     * @param sesso       il nuovo sesso
     * @param residenza   la nuova residenza
     * @param diagnosi    la nuova diagnosi
     * @return true se l'aggiornamento ha avuto successo, false altrimenti
     */
    boolean aggiornaPaziente(String cf, String nome, String cognome, String dataNascita, String sesso, String residenza, String diagnosi);

    /**
     * Elimina un paziente dal sistema.
     *
     * @param cf il codice fiscale del paziente da eliminare
     * @return true se l'eliminazione ha avuto successo, false altrimenti
     */
    boolean eliminaPaziente(String cf);
}