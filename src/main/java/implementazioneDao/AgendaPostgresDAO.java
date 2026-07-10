package implementazioneDao;

import dao.AgendaDAO;
import database_connection.ConnessioneDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione dell'interfaccia AgendaDAO per la gestione degli eventi dell'agenda
 * su un database PostgreSQL.
 */
public class AgendaPostgresDAO implements AgendaDAO {

    private static final Logger LOGGER = Logger.getLogger(AgendaPostgresDAO.class.getName());

    private static final String GET_EVENTI_BY_MATRICOLA_QUERY = "SELECT id_agenda, titolo, descrizione, matricola_medico, data_ora_inizio, data_ora_fine, matricola_amministratore FROM agenda WHERE matricola_medico = ? OR matricola_amministratore = ? ORDER BY data_ora_inizio DESC";
    private static final String ADD_EVENTO_ADMIN_QUERY = "INSERT INTO agenda (titolo, descrizione, data_ora_inizio, data_ora_fine, matricola_amministratore) VALUES (?, ?, ?, ?, ?)";
    private static final String ADD_EVENTO_MEDICO_QUERY = "INSERT INTO agenda (titolo, descrizione, data_ora_inizio, data_ora_fine, matricola_medico) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_EVENTO_QUERY = "UPDATE agenda SET titolo = ?, descrizione = ?, data_ora_inizio = ?, data_ora_fine = ? WHERE id_agenda = ?";
    private static final String DELETE_EVENTO_QUERY = "DELETE FROM agenda WHERE id_agenda = ?";
    private static final String CREA_AGENDA_QUERY = "INSERT INTO agenda (matricola_medico, titolo, descrizione, data_ora_inizio, data_ora_fine) VALUES (?, 'Agenda Principale', 'Agenda creata automaticamente', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
    private static final String CREA_AGENDA_ADMIN_QUERY = "INSERT INTO agenda (matricola_amministratore, titolo, descrizione, data_ora_inizio, data_ora_fine) VALUES (?, 'Agenda Principale', 'Agenda creata automaticamente', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

    private static final String COL_ID_AGENDA = "id_agenda";
    private static final String COL_MATRICOLA_MEDICO = "matricola_medico";
    private static final String COL_MATRICOLA_AMMINISTRATORE = "matricola_amministratore";
    private static final String COL_TITOLO = "titolo";
    private static final String COL_DESCRIZIONE = "descrizione";
    private static final String COL_DATA_ORA_INIZIO = "data_ora_inizio";
    private static final String COL_DATA_ORA_FINE = "data_ora_fine";

    /**
     * Recupera tutti gli eventi associati a una specifica matricola (medico o amministratore).
     *
     * @param matricola la matricola dell'utente.
     * @return una lista di eventi, dove ogni evento è rappresentato da un'ArrayList di stringhe.
     */
    @Override
    public java.util.List<ArrayList<String>> getEventiByMatricola(String matricola) {
        ArrayList<ArrayList<String>> eventi = new ArrayList<>();

        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(GET_EVENTI_BY_MATRICOLA_QUERY)) {

            stmt.setString(1, matricola);
            stmt.setString(2, matricola);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ArrayList<String> evento = new ArrayList<>();
                    evento.add(rs.getString(COL_ID_AGENDA));
                    evento.add(rs.getString(COL_TITOLO));
                    evento.add(rs.getString(COL_DESCRIZIONE));

                    String matMed = rs.getString(COL_MATRICOLA_MEDICO);
                    String matAdmin = rs.getString(COL_MATRICOLA_AMMINISTRATORE);
                    evento.add(matMed != null ? matMed : matAdmin);

                    evento.add(rs.getString(COL_DATA_ORA_INIZIO));
                    evento.add(rs.getString(COL_DATA_ORA_FINE));
                    eventi.add(evento);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore nel recuperare gli eventi per la matricola: " + matricola);
        }
        return eventi;
    }

    /**
     * Aggiunge un nuovo evento all'agenda di un utente (medico o amministratore).
     *
     * @param titolo        il titolo dell'evento.
     * @param matricola     la matricola dell'utente a cui associare l'evento.
     * @param descrizione   una descrizione dell'evento.
     * @param dataOraInizio il timestamp di inizio dell'evento.
     * @param dataOraFine   il timestamp di fine dell'evento.
     * @return {@code true} se l'evento è stato aggiunto con successo, altrimenti {@code false}.
     */
    @Override
    public boolean addEvento(String titolo, String matricola, String descrizione, Timestamp dataOraInizio, Timestamp dataOraFine) {
        String query;
        boolean isAdmin = matricola != null && matricola.toUpperCase().startsWith("A");

        if (isAdmin) {
            query = ADD_EVENTO_ADMIN_QUERY;
        } else {
            query = ADD_EVENTO_MEDICO_QUERY;
        }

        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, titolo);
            stmt.setString(2, descrizione);
            stmt.setTimestamp(3, dataOraInizio);
            stmt.setTimestamp(4, dataOraFine);
            stmt.setString(5, matricola);

            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, e, () ->"Errore durante l'inserimento dell'evento nel database per la matricola: " + matricola);
            return false;
        }
    }

    /**
     * Aggiorna un evento esistente nel database.
     *
     * @param idEvento      l'ID dell'evento da aggiornare.
     * @param titolo        il nuovo titolo dell'evento.
     * @param descrizione   la nuova descrizione dell'evento.
     * @param dataOraInizio il nuovo timestamp di inizio.
     * @param dataOraFine   il nuovo timestamp di fine.
     * @return {@code true} se l'aggiornamento ha avuto successo, altrimenti {@code false}.
     */
    @Override
    public boolean updateEvento(int idEvento, String titolo, String descrizione, Timestamp dataOraInizio, Timestamp dataOraFine) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_EVENTO_QUERY)) {

            stmt.setString(1, titolo);
            stmt.setString(2, descrizione);
            stmt.setTimestamp(3, dataOraInizio);
            stmt.setTimestamp(4, dataOraFine);
            stmt.setInt(5, idEvento);

            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento dell'evento nel database", e);
            return false;
        }
    }

    /**
     * Elimina un evento dal database.
     *
     * @param idEvento l'ID dell'evento da eliminare.
     * @return {@code true} se l'eliminazione ha avuto successo, altrimenti {@code false}.
     */
    @Override
    public boolean deleteEvento(int idEvento) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(DELETE_EVENTO_QUERY)) {
            stmt.setInt(1, idEvento);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione dell'evento dal database", e);
            return false;
        }
    }

    /**
     * Crea un'agenda di default per un nuovo medico.
     *
     * @param matricolaMedico la matricola del medico.
     * @return {@code true} se la creazione ha avuto successo, altrimenti {@code false}.
     */
    @Override
    public boolean creaAgendaPerMedico(String matricolaMedico) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(CREA_AGENDA_QUERY)) {
            stmt.setString(1, matricolaMedico);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante la creazione automatica dell'agenda per il medico", e);
            return false;
        }
    }

    /**
     * Crea un'agenda di default per un nuovo amministratore.
     *
     * @param matricolaAmministratore la matricola dell'amministratore.
     * @return {@code true} se la creazione ha avuto successo, altrimenti {@code false}.
     */
    @Override
    public boolean creaAgendaPerAmministratore(String matricolaAmministratore) {
        try (Connection conn = ConnessioneDatabase.getInstance();
             PreparedStatement stmt = conn.prepareStatement(CREA_AGENDA_ADMIN_QUERY)) {
            stmt.setString(1, matricolaAmministratore);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Errore durante la creazione automatica dell'agenda per l'amministratore", e);
            return false;
        }
    }
}