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
        String query = "INSERT INTO turno_lavorativo (id_turno, data_turno, ora_inizio, ora_fine, matricola_medico, id_agenda) VALUES (?, ?, ?, ?, ?, ?)";
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
        String query = "SELECT * FROM turno_lavorativo WHERE matricola_medico = ? AND data_turno = ? AND ora_inizio = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matricola);
            stmt.setObject(2, LocalDate.parse(data));
            stmt.setObject(3, LocalDateTime.parse(data + "T" + inizioTurno));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ArrayList<String> turno = new ArrayList<>();
                turno.add(rs.getString("matricola_medico"));

                LocalDate dataDb = rs.getObject("data_turno", LocalDate.class);
                turno.add(dataDb != null ? dataDb.toString() : "");

                LocalDateTime inizioDb = rs.getObject("ora_inizio", LocalDateTime.class);
                turno.add(inizioDb != null ? inizioDb.toString().replace("T", " ") : "");

                LocalDateTime fineDb = rs.getObject("ora_fine", LocalDateTime.class);
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
        String query = "SELECT * FROM  turno_lavorativo WHERE matricola_medico = ? ORDER BY data_turno ASC, ora_inizio ASC";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matricola);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ArrayList<String> turno = new ArrayList<>();
                turno.add(rs.getString("matricola_medico"));

                LocalDate dataDb = rs.getObject("data_turno", LocalDate.class);
                turno.add(dataDb != null ? dataDb.toString() : "");

                LocalDateTime inizioDb = rs.getObject("ora_inizio", LocalDateTime.class);
                turno.add(inizioDb != null ? inizioDb.toString().replace("T", " ") : "");

                LocalDateTime fineDb = rs.getObject("ora_fine", LocalDateTime.class);
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
        String query = "UPDATE  turno_lavorativo SET ora_fine = ? WHERE matricola_medico = ? AND data_turno = ? AND ora_inizio = ?";
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
        String query = "DELETE FROM  turno_lavorativo WHERE matricola_medico = ? AND data_turno = ? AND ora_inizio = ?";
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