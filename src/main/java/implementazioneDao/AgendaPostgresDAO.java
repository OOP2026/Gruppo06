package implementazioneDao;

import dao.AgendaDAO;
import database_connection.ConnessioneDatabase;
import model.Agenda;

import java.sql.*;
import java.util.ArrayList;

public class AgendaPostgresDAO implements AgendaDAO {

    // Centralizzazione delle query SQL come costanti per migliorare la leggibilità e la manutenibilità
    private static final String GET_EVENTI_BY_MEDICO_QUERY = "SELECT id_agenda, matricola_medico, titolo, descrizione, data_ora_inizio, data_ora_fine FROM agenda WHERE matricola_medico = ? ORDER BY data_ora_inizio ASC";
    private static final String ADD_EVENTO_QUERY = "INSERT INTO agenda (id_agenda, titolo, matricola_medico, descrizione, data_ora_inizio, data_ora_fine) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_EVENTO_QUERY = "UPDATE agenda SET titolo = ?, descrizione = ?, data_ora_inizio = ?, data_ora_fine = ? WHERE id_agenda = ?";
    private static final String DELETE_EVENTO_QUERY = "DELETE FROM agenda WHERE id_agenda = ?";


    @Override
    public ArrayList<Agenda> getEventiByMedico(String matricolaMedico) {
        ArrayList<Agenda> eventi = new ArrayList<>();

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_EVENTI_BY_MEDICO_QUERY)) {

            stmt.setString(1, matricolaMedico);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    eventi.add(new Agenda(
                            rs.getInt("id_agenda"),
                            rs.getString("matricola_medico"),
                            rs.getString("titolo"),
                            rs.getString("descrizione"),
                            rs.getTimestamp("data_ora_inizio"),
                            rs.getTimestamp("data_ora_fine")
                    ));
                }
            }
        } catch (SQLException e) {
            // Lanciare una RuntimeException avvolgendo la SQLException originale
            // permette ai livelli superiori di gestire l'errore in modo appropriato.
            throw new RuntimeException("Errore nel recupero degli eventi dal database", e);
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
            throw new RuntimeException("Errore durante l'inserimento dell'evento nel database", e);
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
            throw new RuntimeException("Errore durante l'aggiornamento dell'evento nel database", e);
        }
    }

    @Override
    public boolean deleteEvento(int idEvento) {
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_EVENTO_QUERY)) {
            stmt.setInt(1, idEvento);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione dell'evento dal database", e);
        }
    }
}