package implementazioneDao;

import dao.Turno_LavoroDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TurnoLavoroPostgresDao implements Turno_LavoroDAO {

    private static final Logger LOGGER = Logger.getLogger(TurnoLavoroPostgresDao.class.getName());

    @Override
    public boolean aggiungiTurno(String matricola, String data, String inizioTurno, String fineTurno, String idAgenda) {
        String query = "INSERT INTO turno_lavorativo (data_turno, ora_inizio, ora_fine, matricola_medico, id_agenda) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setObject(1, LocalDate.parse(data));
            stmt.setObject(2, LocalTime.parse(inizioTurno));
            stmt.setObject(3, LocalTime.parse(fineTurno));
            stmt.setString(4, matricola);
            stmt.setInt(5, Integer.parseInt(idAgenda));

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
            stmt.setObject(3, LocalTime.parse(inizioTurno));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ArrayList<String> turno = new ArrayList<>();
                turno.add(rs.getString("id_turno"));
                turno.add(rs.getString("matricola_medico"));

                LocalDate dataDb = rs.getObject("data_turno", LocalDate.class);
                turno.add(dataDb != null ? dataDb.toString() : "");

                LocalTime inizioDb = rs.getObject("ora_inizio", LocalTime.class);
                turno.add(inizioDb != null ? inizioDb.toString() : "");

                LocalTime fineDb = rs.getObject("ora_fine", LocalTime.class);
                turno.add(fineDb != null ? fineDb.toString() : "");

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
                turno.add(rs.getString("id_turno"));
                turno.add(rs.getString("matricola_medico"));

                LocalDate dataDb = rs.getObject("data_turno", LocalDate.class);
                turno.add(dataDb != null ? dataDb.toString() : "");

                LocalTime inizioDb = rs.getObject("ora_inizio", LocalTime.class);
                turno.add(inizioDb != null ? inizioDb.toString() : "");

                LocalTime fineDb = rs.getObject("ora_fine", LocalTime.class);
                turno.add(fineDb != null ? fineDb.toString() : "");

                turni.add(turno);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dei turni per medico", e);
        }
        return turni;
    }

    @Override
    public boolean aggiornaTurno(String matricola, String data, String vecchioInizio, String nuovoInizio, String nuovaFine) {
        String query = "UPDATE  turno_lavorativo SET ora_inizio = ?, ora_fine = ? WHERE matricola_medico = ? AND data_turno = ? AND ora_inizio = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, LocalTime.parse(nuovoInizio));
            stmt.setObject(2, LocalTime.parse(nuovaFine));
            stmt.setString(3, matricola);
            stmt.setObject(4, LocalDate.parse(data));
            stmt.setObject(5, LocalTime.parse(vecchioInizio));
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
            stmt.setObject(3, LocalTime.parse(inizioTurno));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'eliminazione del turno", e);
        }
        return false; 
    }
}