package implementazioneDao;

import dao.AgendaDAO;
import database_connection.ConnessioneDatabase;
import model.Agenda;

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

    // Costanti per i nomi delle colonne
    private static final String COL_ID_AGENDA = "id_agenda";
    private static final String COL_MATRICOLA_MEDICO = "matricola_medico";
    private static final String COL_TITOLO = "titolo";
    private static final String COL_DESCRIZIONE = "descrizione";
    private static final String COL_DATA_ORA_INIZIO = "data_ora_inizio";
    private static final String COL_DATA_ORA_FINE = "data_ora_fine";


    @Override
    public ArrayList<Agenda> getEventiByMedico(String matricolaMedico) {
        ArrayList<Agenda> eventi = new ArrayList<>();

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_EVENTI_BY_MEDICO_QUERY)) {

            stmt.setString(1, matricolaMedico);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    eventi.add(new Agenda(
                            rs.getInt(COL_ID_AGENDA),
                            rs.getString(COL_MATRICOLA_MEDICO),
                            rs.getString(COL_TITOLO),
                            rs.getString(COL_DESCRIZIONE),
                            rs.getTimestamp(COL_DATA_ORA_INIZIO),
                            rs.getTimestamp(COL_DATA_ORA_FINE)
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel recupero degli eventi dal database", e);
        }
        return eventi;
    }

    @Override
    public boolean addEvento(Agenda evento) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ADD_EVENTO_QUERY)) {
            stmt.setInt(1, evento.getIdEvento());
            stmt.setString(2, evento.getTitolo());
            stmt.setString(3, evento.getMatricolaMedico());
            stmt.setString(4, evento.getDescrizione());
            stmt.setTimestamp(5, evento.getDataOraInizio());
            stmt.setTimestamp(6, evento.getDataOraFine());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'inserimento dell'evento nel database", e);
            return false;
        }
    }

    @Override
    public boolean updateEvento(Agenda evento) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_EVENTO_QUERY)) {

            stmt.setString(1, evento.getTitolo());
            stmt.setString(2, evento.getDescrizione());
            stmt.setTimestamp(3, evento.getDataOraInizio());
            stmt.setTimestamp(4, evento.getDataOraFine());
            stmt.setInt(5, evento.getIdEvento());

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
}