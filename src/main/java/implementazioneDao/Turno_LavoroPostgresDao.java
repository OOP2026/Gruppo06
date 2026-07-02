package implementazioneDao;

import dao.Turno_LavoroDAO;
import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Turno_LavoroPostgresDao implements Turno_LavoroDAO {

    @Override
    public boolean aggiungiTurno(String matricola, String data, String inizioTurno, String fineTurno) {
        String query = "INSERT INTO turni_lavoro (matricola, data, inizio_turno, fine_turno) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, matricola);
            stmt.setDate(2, java.sql.Date.valueOf(data));
            stmt.setTimestamp(3, Timestamp.valueOf(inizioTurno));
            stmt.setTimestamp(4, Timestamp.valueOf(fineTurno));
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ArrayList<String> getTurno(String matricola, String data, String inizioTurno) {
        String query = "SELECT * FROM turni_lavoro WHERE matricola = ? AND data = ? AND inizio_turno = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, matricola);
            stmt.setDate(2, java.sql.Date.valueOf(data));
            stmt.setTimestamp(3, Timestamp.valueOf(inizioTurno));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ArrayList<String> turno = new ArrayList<>();
                turno.add(rs.getString("matricola"));
                
                java.sql.Date dataDb = rs.getDate("data");
                turno.add(dataDb != null ? dataDb.toString() : "");
                
                Timestamp inizioDb = rs.getTimestamp("inizio_turno");
                turno.add(inizioDb != null ? inizioDb.toString() : "");
                
                Timestamp fineDb = rs.getTimestamp("fine_turno");
                turno.add(fineDb != null ? fineDb.toString() : "");
                
                return turno;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
                
                java.sql.Date dataDb = rs.getDate("data");
                turno.add(dataDb != null ? dataDb.toString() : "");
                
                Timestamp inizioDb = rs.getTimestamp("inizio_turno");
                turno.add(inizioDb != null ? inizioDb.toString() : "");
                
                Timestamp fineDb = rs.getTimestamp("fine_turno");
                turno.add(fineDb != null ? fineDb.toString() : "");
                
                turni.add(turno);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return turni;
    }

    @Override
    public boolean aggiornaTurno(String matricola, String data, String inizioTurno, String fineTurno) {
        String query = "UPDATE turni_lavoro SET fine_turno = ? WHERE matricola = ? AND data = ? AND inizio_turno = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(fineTurno));
            stmt.setString(2, matricola);
            stmt.setDate(3, java.sql.Date.valueOf(data));
            stmt.setTimestamp(4, Timestamp.valueOf(inizioTurno));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminaTurno(String matricola, String data, String inizioTurno) {
        String query = "DELETE FROM turni_lavoro WHERE matricola = ? AND data = ? AND inizio_turno = ?";
        try (Connection conn = ConnessioneDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matricola);
            stmt.setDate(2, java.sql.Date.valueOf(data));
            stmt.setTimestamp(3, Timestamp.valueOf(inizioTurno));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; 
    }
}