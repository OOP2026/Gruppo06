package dao;

import java.util.ArrayList;

/**
 * Interfaccia per le operazioni DAO relative agli amministratori.
 */
public interface AmministratoreDAO {
    /**
     * Verifica se un determinato login (username) è già esistente nel sistema.
     *
     * @param login lo username da verificare
     * @return true se il login esiste già, false altrimenti
     */
    boolean checkLoginEsistente(String login);

    /**
     * Aggiunge un nuovo amministratore al sistema.
     *
     * @param matricola la matricola dell'amministratore
     * @param login     lo username per l'accesso
     * @param password  la password per l'accesso
     * @param nome      il nome dell'amministratore
     * @param cognome   il cognome dell'amministratore
     * @param pin       il PIN di sicurezza
     * @return true se l'aggiunta ha avuto successo, false altrimenti
     */
    boolean aggiungiAmministratore(String matricola, String login, String password, String nome, String cognome, String pin);

    /**
     * Aggiorna i dati di un amministratore esistente.
     *
     * @param matricola la matricola dell'amministratore da aggiornare
     * @param nome      il nuovo nome
     * @param cognome   il nuovo cognome
     * @return true se l'aggiornamento ha avuto successo, false altrimenti
     */
    boolean aggiornaAmministratore(String matricola, String nome, String cognome);

    /**
     * Recupera le informazioni di un amministratore tramite login e password.
     *
     * @param login    lo username dell'amministratore
     * @param password la password dell'amministratore
     * @return una lista contenente i dati dell'amministratore
     */
    ArrayList<String> getAmministratoreByLoginAndPassword(String login, String password);
}