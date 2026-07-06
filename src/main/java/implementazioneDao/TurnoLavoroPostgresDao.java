package implementazioneDao;

import dao.Turno_LavoroDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TurnoLavoroPostgresDao implements Turno_LavoroDAO {

    private static final Logger LOGGER = Logger.getLogger(TurnoLavoroPostgresDao.class.getName());

    @Override
    public boolean aggiungiTurno(String matricola, String data, String inizioTurno, String fineTurno, String idAgenda) {
        String query = "INSERT INTO turni_lavoro (id_turno, data, inizio_turno, fine_turno, matricola, id_agenda) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Integer.parseInt(idAgenda));
            stmt.setObject(2, LocalDate.parse(data));
            stmt.setObject(3, LocalDateTime.parse(data + "T" + inizioTurno));
            stmt.setObject(4, LocalDateTime.parse(data + "T" + fineTurno));
            stmt.setString(5, matricola);
            stmt.setInt(6, Integer.parseInt(idAgenda));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiunta del turno", e);
        }
        return false;
    }

    @Override
    public ArrayList<String> getTurno(String matricola, String data, String inizioTurno) {
        String query = "SELECT * FROM turni_lavoro WHERE matricola = ? AND data = ? AND inizio_turno = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matricola);
            stmt.setObject(2, LocalDate.parse(data));
            stmt.setObject(3, LocalDateTime.parse(data + "T" + inizioTurno));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ArrayList<String> turno = new ArrayList<>();
                turno.add(rs.getString("matricola"));

                LocalDate dataDb = rs.getObject("data", LocalDate.class);
                turno.add(dataDb != null ? dataDb.toString() : "");

                LocalDateTime inizioDb = rs.getObject("inizio_turno", LocalDateTime.class);
                turno.add(inizioDb != null ? inizioDb.toString().replace("T", " ") : "");

                LocalDateTime fineDb = rs.getObject("fine_turno", LocalDateTime.class);
                turno.add(fineDb != null ? fineDb.toString().replace("T", " ") : "");

                return turno;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero del turno", e);
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<ArrayList<String>> getTurniByMedico(String matricola) {
        ArrayList<ArrayList<String>> turni = new ArrayList<>();
        String query = "SELECT * FROM turni_lavoro WHERE matricola = ? ORDER BY data ASC, inizio_turno ASC";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matricola);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ArrayList<String> turno = new ArrayList<>();
                turno.add(rs.getString("matricola"));

                LocalDate dataDb = rs.getObject("data", LocalDate.class);
                turno.add(dataDb != null ? dataDb.toString() : "");

                LocalDateTime inizioDb = rs.getObject("inizio_turno", LocalDateTime.class);
                turno.add(inizioDb != null ? inizioDb.toString().replace("T", " ") : "");

                LocalDateTime fineDb = rs.getObject("fine_turno", LocalDateTime.class);
                turno.add(fineDb != null ? fineDb.toString().replace("T", " ") : "");

                turni.add(turno);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dei turni per medico", e);
        }
        return turni;
    }

    @Override
    public boolean aggiornaTurno(String matricola, String data, String inizioTurno, String fineTurno) {
        String query = "UPDATE turni_lavoro SET fine_turno = ? WHERE matricola = ? AND data = ? AND inizio_turno = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, LocalDateTime.parse(data + "T" + fineTurno));
            stmt.setString(2, matricola);
            stmt.setObject(3, LocalDate.parse(data));
            stmt.setObject(4, LocalDateTime.parse(data + "T" + inizioTurno));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento del turno", e);
        }
        return false;
    }

    @Override
    public boolean eliminaTurno(String matricola, String data, String inizioTurno) {
        String query = "DELETE FROM turni_lavoro WHERE matricola = ? AND data = ? AND inizio_turno = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matricola);
            stmt.setObject(2, LocalDate.parse(data));
            stmt.setObject(3, LocalDateTime.parse(data + "T" + inizioTurno));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione del turno", e);
        }
        return false; 
    }
}