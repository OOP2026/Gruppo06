package dao;

import java.util.ArrayList;

/**
 * L'interfaccia Medico DAO.
 */
public interface MedicoDAO {
    /**
     * Aggiunge un medico al sistema.
     *
     * @param nome             il nome del medico
     * @param cognome          il cognome del medico
     * @param matricola        la matricola del medico
     * @param login            il login del medico
     * @param password         la password del medico
     * @param iscrizioneAlbo   la data di iscrizione all'albo
     * @param specializzazione la specializzazione medica
     * @param reparto          il reparto di assegnazione
     * @return true in caso di successo, false altrimenti
     */
    boolean aggiungiMedico(String nome, String cognome, String matricola, String login, String password, String iscrizioneAlbo, String specializzazione, String reparto);

    /**
     * Ottiene i dati di un medico tramite la sua matricola.
     *
     * @param matricola la matricola del medico da cercare
     * @return la lista contenente i dati del medico associato alla matricola
     */
    ArrayList<String> getMedicoByMatricola(String matricola);

    /**
     * Ottiene la lista di tutti i medici presenti nel sistema.
     *
     * @return la lista di tutti i medici
     */
    ArrayList<ArrayList<String>> getAllMedici();

    /**
     * Aggiorna i dati di un medico esistente.
     *
     * @param nome             il nome del medico
     * @param cognome          il cognome del medico
     * @param matricola        la matricola del medico da aggiornare
     * @param iscrizioneAlbo   la data di iscrizione all'albo
     * @param specializzazione la specializzazione medica
     * @param reparto          il reparto di assegnazione
     * @return true in caso di successo, false altrimenti
     */
    boolean aggiornaMedico(String nome, String cognome, String matricola, String iscrizioneAlbo, String specializzazione, String reparto);

    /**
     * Elimina un medico dal sistema.
     *
     * @param matricola la matricola del medico da eliminare
     * @return true in caso di successo, false altrimenti
     */
    boolean eliminaMedico(String matricola);

    /**
     * Ottiene i dati di un medico verificando le credenziali di accesso.
     *
     * @param login    il login del medico
     * @param password la password del medico
     * @return la lista contenente i dati del medico associati a login e password
     */
    ArrayList<String> getMedicoByLoginAndPassword(String login, String password);

    /**
     * Controlla se un nome utente (login) è già utilizzato nel sistema.
     *
     * @param login il login da verificare
     * @return true se il login esiste già, false altrimenti
     */
    boolean checkLoginEsistente(String login);
}