package implementazioneDao;

import dao.AgendaDAO;
import database_connection.ConnessioneDatabase;
import model.Agenda;

import java.sql.*;
import java.util.ArrayList;

public class AgendaPostgresDAO implements AgendaDAO {

    @Override
    public ArrayList<Agenda> getEventiByMedico(String matricolaMedico) {
        ArrayList<Agenda> eventi = new ArrayList<>();
        String query = "SELECT * FROM agenda WHERE matricola_medico = ? ORDER BY data_ora_inizio ASC";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matricolaMedico);
            ResultSet rs = stmt.executeQuery();

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventi;
    }

    @Override
    public boolean addEvento(Agenda evento) {
        String query = "INSERT INTO agenda ( id_agenda, titolo, matricola_medico, descrizione, data_ora_inizio, data_ora_fine) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, evento.getIdEvento());
            stmt.setString(2, evento.getTitolo());
            stmt.setString(3, evento.getMatricolaMedico());
            stmt.setString(4, evento.getDescrizione());
            stmt.setTimestamp(5, evento.getDataOraInizio());
            stmt.setTimestamp(6, evento.getDataOraFine());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateEvento(Agenda evento) {
        String query = "UPDATE agenda SET titolo = ?, descrizione = ?, data_ora_inizio = ?, data_ora_fine = ? WHERE id_agenda = ?";

        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, evento.getTitolo());
            stmt.setString(2, evento.getDescrizione());
            stmt.setTimestamp(3, evento.getDataOraInizio());
            stmt.setTimestamp(4, evento.getDataOraFine());
            stmt.setInt(5, evento.getIdEvento());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteEvento(int idEvento) {
        String query = "DELETE FROM agenda WHERE id_agenda = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idEvento);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}