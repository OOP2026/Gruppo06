package dao;

import java.sql.Timestamp;
import java.util.ArrayList;

public interface AgendaDAO {
    
    java.util.List<ArrayList<String>> getEventiByMatricola(String matricola);

    boolean addEvento(String titolo, String matricola, String descrizione, Timestamp dataOraInizio, Timestamp dataOraFine);

    boolean updateEvento(int idEvento, String titolo, String descrizione, Timestamp dataOraInizio, Timestamp dataOraFine);

    boolean deleteEvento(int idEvento);

    /**
     * Crea un'agenda di base collegata al nuovo medico registrato.
     * @param matricolaMedico La matricola del medico.
     */
    boolean creaAgendaPerMedico(String matricolaMedico);

    /**
     * Crea un'agenda di base collegata al nuovo amministratore registrato.
     * @param matricolaAmministratore La matricola dell'amministratore.
     */
    boolean creaAgendaPerAmministratore(String matricolaAmministratore);
}