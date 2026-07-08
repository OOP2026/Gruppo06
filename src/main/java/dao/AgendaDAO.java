package dao;

import java.sql.Timestamp;
import java.util.ArrayList;

public interface AgendaDAO {

    ArrayList<ArrayList<String>> getEventiByMedico(String matricolaMedico);

    boolean addEvento(int idEvento, String titolo, String matricolaMedico, String descrizione, Timestamp dataOraInizio, Timestamp dataOraFine);

    boolean updateEvento(int idEvento, String titolo, String descrizione, Timestamp dataOraInizio, Timestamp dataOraFine);

    boolean deleteEvento(int idEvento);

    /**
     * Crea un'agenda di base collegata al nuovo medico registrato.
     * @param matricolaMedico La matricola del medico.
     */
    boolean creaAgendaPerMedico(String matricolaMedico);
}