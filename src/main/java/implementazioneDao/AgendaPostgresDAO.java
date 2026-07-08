package implementazioneDao;

import dao.AgendaDAO;
import database_connection.ConnessioneDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgendaPostgresDAO implements AgendaDAO {

    private static final Logger LOGGER = Logger.getLogger(AgendaPostgresDAO.class.getName());

    // Centralizzazione delle query SQL come costanti per migliorare la leggibilità e la manutenibilità
    private static final String GET_EVENTI_BY_MEDICO_QUERY = "SELECT id_agenda, matricola_medico, titolo, descrizione, data_ora_inizio, data_ora_fine FROM agenda WHERE matricola_medico = ? ORDER BY data_ora_inizio ASC";
    private static final String ADD_EVENTO_QUERY = "INSERT INTO agenda (id_agenda, titolo, matricola_medico, descrizione, data_ora_inizio, data_ora_fine) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_EVENTO_QUERY = "UPDATE agenda SET titolo = ?, descrizione = ?, data_ora_inizio = ?, data_ora_fine = ? WHERE id_agenda = ?";
    private static final String DELETE_EVENTO_QUERY = "DELETE FROM agenda WHERE id_agenda = ?";
    private static final String CREA_AGENDA_QUERY = "INSERT INTO agenda (id_agenda, matricola_medico, titolo, descrizione, data_ora_inizio, data_ora_fine) VALUES ((SELECT COALESCE(MAX(id_agenda), 0) + 1 FROM agenda), ?, 'Agenda Principale', 'Agenda creata automaticamente', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

    // Costanti per i nomi delle colonne
    private static final String COL_ID_AGENDA = "id_agenda";
    private static final String COL_MATRICOLA_MEDICO = "matricola_medico";
    private static final String COL_TITOLO = "titolo";
    private static final String COL_DESCRIZIONE = "descrizione";
    private static final String COL_DATA_ORA_INIZIO = "data_ora_inizio";
    private static final String COL_DATA_ORA_FINE = "data_ora_fine";


    @Override
    public ArrayList<ArrayList<String>> getEventiByMedico(String matricolaMedico) {
        ArrayList<ArrayList<String>> eventi = new ArrayList<>();

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_EVENTI_BY_MEDICO_QUERY)) {

            stmt.setString(1, matricolaMedico);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ArrayList<String> evento = new ArrayList<>();
                    evento.add(String.valueOf(rs.getInt(COL_ID_AGENDA)));
                    evento.add(rs.getString(COL_MATRICOLA_MEDICO));
                    evento.add(rs.getString(COL_TITOLO));
                    evento.add(rs.getString(COL_DESCRIZIONE));
                    evento.add(rs.getTimestamp(COL_DATA_ORA_INIZIO).toString());
                    evento.add(rs.getTimestamp(COL_DATA_ORA_FINE).toString());
                    eventi.add(evento);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel recupero degli eventi dal database", e);
        }
        return eventi;
    }

    @Override
    public boolean addEvento(int idEvento, String titolo, String matricolaMedico, String descrizione, Timestamp dataOraInizio, Timestamp dataOraFine) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ADD_EVENTO_QUERY)) {
            stmt.setInt(1, idEvento);
            stmt.setString(2, titolo);
            stmt.setString(3, matricolaMedico);
            stmt.setString(4, descrizione);
            stmt.setTimestamp(5, dataOraInizio);
            stmt.setTimestamp(6, dataOraFine);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'inserimento dell'evento nel database", e);
            return false;
        }
    }

    @Override
    public boolean updateEvento(int idEvento, String titolo, String descrizione, Timestamp dataOraInizio, Timestamp dataOraFine) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_EVENTO_QUERY)) {

            stmt.setString(1, titolo);
            stmt.setString(2, descrizione);
            stmt.setTimestamp(3, dataOraInizio);
            stmt.setTimestamp(4, dataOraFine);
            stmt.setInt(5, idEvento);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento dell'evento nel database", e);
            return false;
        }
    }

    @Override
    public boolean deleteEvento(int idEvento) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_EVENTO_QUERY)) {
            stmt.setInt(1, idEvento);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione dell'evento dal database", e);
            return false;
        }
    }

    @Override
    public boolean creaAgendaPerMedico(String matricolaMedico) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREA_AGENDA_QUERY)) {
            stmt.setString(1, matricolaMedico);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante la creazione automatica dell'agenda per il medico", e);
            return false;
        }
    }
}